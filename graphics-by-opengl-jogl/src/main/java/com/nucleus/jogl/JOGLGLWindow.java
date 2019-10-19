package com.nucleus.jogl;

import java.awt.Frame;
import java.awt.event.WindowListener;
import java.lang.reflect.Field;
import java.util.Hashtable;

import com.jogamp.common.os.Platform;
import com.jogamp.nativewindow.util.Dimension;
import com.jogamp.nativewindow.util.InsetsImmutable;
import com.jogamp.newt.event.InputEvent;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.event.WindowUpdateEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import com.nucleus.Backend.BackendFactory;
import com.nucleus.CoreApp;
import com.nucleus.CoreApp.CoreAppStarter;
import com.nucleus.J2SEWindowApplication.WindowType;
import com.nucleus.J2SEWindow;
import com.nucleus.SimpleLogger;
import com.nucleus.mmi.Key.Action;
import com.nucleus.mmi.Pointer.PointerAction;
import com.nucleus.mmi.Pointer.Type;
import com.nucleus.renderer.NucleusRenderer.Renderers;
import com.nucleus.renderer.SurfaceConfiguration;

/**
 * 
 * @author Richard Sahlin
 * The JOGL abstract native window, use by subclasses to render GL content
 * The window shall drive rendering by calling {@link CoreApp#drawFrame()} on a thread that
 * has GL access. This is normally done in the {@link #display(GLAutoDrawable)} method.
 *
 */
public abstract class JOGLGLWindow extends J2SEWindow
        implements GLEventListener, MouseListener, com.jogamp.newt.event.WindowListener, KeyListener, WindowListener {

    private Dimension windowSize;
    private boolean alwaysOnTop = false;
    private boolean mouseVisible = true;
    private boolean mouseConfined = false;
    private boolean autoSwapBuffer = false;
    protected volatile boolean contextCreated = false;
    protected GLCanvas canvas;
    protected Frame frame;
    protected GLWindow glWindow;
    Animator animator;
    private Hashtable<Integer, Integer> AWTKeycodes;

    /**
     * Creates a new JOGL window with the specified {@link CoreAppStarter} and swapinterval
     * 
     * @throws IllegalArgumentException If coreAppStarter is null
     */
    public JOGLGLWindow(BackendFactory factory, CoreAppStarter coreAppStarter, Configuration configuration) {
        super(factory, coreAppStarter, configuration);
    }

    @Override
    public void init() {
        if (coreAppStarter == null) {
            throw new IllegalArgumentException("CoreAppStarter is null");
        }
        windowSize = new Dimension(configuration.width, configuration.height);
        GLProfile profile = getProfile(version);
        switch (configuration.windowType) {
            case NEWT:
                createNEWTWindow(configuration.width, configuration.height, profile);
                break;
            case JAWT:
                createAWTWindow(configuration.width, configuration.height, profile);
                break;
                default:
                    throw new IllegalArgumentException("Invalid windowtype for JOGL: " + configuration.windowType);
        }

        /**
         * Fetch jogamp.newt fields that start with VK_ and store keycodes in array to convert to AWT values.
         */
        AWTKeycodes = getAWTFields();
    }

    private Hashtable<Integer, Integer> getAWTFields() {
        Hashtable<Integer, Integer> awtFields = new Hashtable<>();
        for (Field newtField : com.jogamp.newt.event.KeyEvent.class.getDeclaredFields()) {
            if (java.lang.reflect.Modifier.isStatic(newtField.getModifiers())) {
                String fieldName = newtField.getName();
                if (fieldName.startsWith("VK_")) {
                    try {
                        Field awtField = java.awt.event.KeyEvent.class.getField(fieldName);
                        int newtKeyCode = newtField.getShort(null) & 0xffff;
                        int awtKeyCode = awtField.getInt(null);
                        awtFields.put(newtKeyCode, awtKeyCode);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        SimpleLogger.d(getClass(), e.toString());
                    }
                }
            }
        }
        return awtFields;
    }

    protected GLProfile getProfile(Renderers version) {
        SimpleLogger.d(getClass(), "os.and.arch: " + Platform.os_and_arch);
        GLProfile defaultProfile = null;
        try {
            defaultProfile = GLProfile.getDefault();
            if (defaultProfile != null) {
                SimpleLogger.d(getClass(), "Default profile implName: " + defaultProfile.getImplName() + ", name: "
                        + defaultProfile.getName());
            } else {
                SimpleLogger.d(getClass(), "Default profile is NULL");
            }
        } catch (Throwable t) {
            // Not much to do
            SimpleLogger.d(getClass(), "Internal error when fetching default profile");
        }
        GLProfile profile = null;
        switch (version) {
            case GLES20:
                if (defaultProfile != null && (defaultProfile.isGLES2() || defaultProfile.isGL2ES2())) {
                    profile = defaultProfile;
                } else {
                    profile = GLProfile.get(GLProfile.GL2ES2);
                }
                break;
            case GLES30:
            case GLES31:
            case GLES32:
                if (defaultProfile != null && (defaultProfile.isGLES3() || defaultProfile.isGL4ES3())) {
                    profile = defaultProfile;
                } else {
                    profile = GLProfile.get(GLProfile.GL4ES3);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid version " + version);
        }
        return profile;
    }

    /**
     * Creates the JOGL display and OpenGLES
     * 
     * @param width
     * @param height
     * @param profile
     * @version
     */
    private void createNEWTWindow(int width, int height, GLProfile profile) {
        GLProfile.initSingleton();
        SurfaceConfiguration config = configuration.surfaceConfig;
        GLCapabilities glCapabilities = new GLCapabilities(profile);
        glCapabilities.setSampleBuffers(config.getSamples() > 0);
        glCapabilities.setNumSamples(config.getSamples());
        glCapabilities.setBackgroundOpaque(true);
        glCapabilities.setAlphaBits(0);
        glWindow = GLWindow.create(glCapabilities);
        glWindow.setUndecorated(configuration.windowUndecorated);
        InsetsImmutable insets = glWindow.getInsets();
        glWindow.setSize(configuration.windowUndecorated ? windowSize.getWidth() : windowSize.getWidth() + insets.getTotalWidth(),
                configuration.windowUndecorated ? windowSize.getHeight() : windowSize.getHeight() + insets.getTotalHeight());
        glWindow.setAlwaysOnTop(alwaysOnTop);
        glWindow.setFullscreen(configuration.fullscreen);
        glWindow.setPointerVisible(mouseVisible);
        glWindow.confinePointer(mouseConfined);
        glWindow.addMouseListener(this);
        glWindow.addWindowListener(this);
        glWindow.addKeyListener(this);
        glWindow.addWindowListener(this);
        glWindow.addGLEventListener(this);
        animator = new Animator();
        animator.add(glWindow);
        animator.start();
        glWindow.setAutoSwapBufferMode(autoSwapBuffer);
    }

    private void createAWTWindow(int width, int height, GLProfile glProfile) {
        GLProfile.initSingleton();
        GLCapabilities caps = new GLCapabilities(glProfile);
        caps.setBackgroundOpaque(true);
        caps.setAlphaBits(0);
        // glWindow = GLWindow.create(caps);
        frame = new java.awt.Frame("Nucleus");
        frame.setSize(width, height);
        frame.setLayout(new java.awt.BorderLayout());
        canvas = new GLCanvas(caps);
        canvas.addGLEventListener(this);
        frame.add(canvas, java.awt.BorderLayout.CENTER);
        frame.validate();
        frame.addWindowListener(this);
        // frame.addMouseListener(this);
        animator = new Animator();
        animator.add(canvas);
        animator.start();
        canvas.setAutoSwapBufferMode(autoSwapBuffer);
        
    }

    @Override
    public void setWindowTitle(String title) {
        if (frame != null) {
            frame.setTitle(title);
        }
        if (glWindow != null) {
            glWindow.setTitle(title);
        }
    }

    public void setGLEVentListener() {
        if (glWindow != null) {
            glWindow.addGLEventListener(this);
        }
        if (canvas != null) {
            canvas.addGLEventListener(this);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (glWindow != null) {
            glWindow.setVisible(visible);
        }
        if (frame != null) {
            frame.setVisible(visible);
        }

    }

    @Override
    public void init(GLAutoDrawable drawable) {
        internalCreateCoreApp(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
        drawable.swapBuffers();
        drawable.getGL().setSwapInterval(configuration.swapInterval);
        internalContextCreated(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        SimpleLogger.d(getClass(), "reshape: x,y= " + x + ", " + y + " width,height= " + width + ", " + height);
        windowSize.setWidth(width);
        windowSize.setHeight(height);
        resize(x, y, width, height);
    }

    /**
     * Returns the width as reported by the {@link #reshape(GLAutoDrawable, int, int, int, int)} method.
     * 
     * @return
     */
    public int getWidth() {
        return windowSize.getWidth();
    }

    /**
     * Returns the height as reported by the {@link #reshape(GLAutoDrawable, int, int, int, int)} method
     * 
     * @return
     */
    public int getHeight() {
        return windowSize.getHeight();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        drawable.getGL().setSwapInterval(configuration.swapInterval);
        if (!autoSwapBuffer) {
            coreApp.renderFrame();
            if (glWindow != null) {
                glWindow.swapBuffers();
            } else if (canvas != null) {
                canvas.swapBuffers();
            }
        }
    }

    protected void handleMouseEvent(MouseEvent e, PointerAction action) {
        int[] xpos = e.getAllX();
        int[] ypos = e.getAllY();
        int count = e.getPointerCount();
        Type type = Type.STYLUS;
        for (int i = 0; i < count; i++) {
            switch (e.getButton()) {
                case MouseEvent.BUTTON1:
                    type = Type.MOUSE;
                    break;
                case MouseEvent.BUTTON2:
                    type = Type.MOUSE;
                    break;
                case MouseEvent.BUTTON3:
                    type = Type.MOUSE;
                    break;
                case MouseEvent.BUTTON4:
                    type = Type.MOUSE;
                    break;
                case MouseEvent.BUTTON5:
                    type = Type.MOUSE;
                    break;
                case MouseEvent.BUTTON6:
                    type = Type.MOUSE;
                    break;
            }
            handleMouseEvent(action, type, xpos[i], ypos[i], e.getPointerId(i), e.getWhen());
        }
    }

    protected void handleKeyEvent(KeyEvent event) {
        /**
         * com.jogamp.newt.event.KeyEvent keycodes are the same as the AWT KeyEvent keycodes.
         */
        SimpleLogger.d(getClass(), "KeyEvent " + event.getEventType() + " : " + event.getKeyCode());
        switch (event.getEventType()) {
            case KeyEvent.EVENT_KEY_PRESSED:
                super.handleKeyEvent(new com.nucleus.mmi.Key(Action.PRESSED,
                        AWTKeycodes.get((int) event.getKeyCode())));
                switch (event.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE:
                        exit();
                }
                break;
            case KeyEvent.EVENT_KEY_RELEASED:
                super.handleKeyEvent(new com.nucleus.mmi.Key(Action.RELEASED,
                        AWTKeycodes.get((int) event.getKeyCode())));
                break;
            default:
                // Do nothing
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
        handleMouseEvent(e, PointerAction.DOWN);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        handleMouseEvent(e, PointerAction.UP);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        handleMouseEvent(e, PointerAction.MOVE);
    }

    @Override
    public void mouseWheelMoved(MouseEvent e) {
        mouseWheelMoved(e.getRotation()[1], e.getWhen());
    }

    @Override
    public void windowResized(WindowEvent e) {
    }

    @Override
    public void windowMoved(WindowEvent e) {
    }

    @Override
    public void windowDestroyNotify(WindowEvent e) {
        windowClosed();
    }

    @Override
    public void windowDestroyed(WindowEvent e) {
        windowClosed();
    }

    @Override
    public void windowGainedFocus(WindowEvent e) {
    }

    @Override
    public void windowLostFocus(WindowEvent e) {
    }

    @Override
    public void windowRepaint(WindowUpdateEvent e) {
    }

    @Override
    public void windowOpened(java.awt.event.WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowClosing(java.awt.event.WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowClosed(java.awt.event.WindowEvent e) {
    }

    @Override
    public void windowIconified(java.awt.event.WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowDeiconified(java.awt.event.WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowActivated(java.awt.event.WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowDeactivated(java.awt.event.WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if ((e.getModifiers() & InputEvent.AUTOREPEAT_MASK) == 0) {
            handleKeyEvent(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if ((e.getModifiers() & InputEvent.AUTOREPEAT_MASK) == 0) {
            handleKeyEvent(e);
        }
    }

    @Override
    protected void setFullscreenMode(boolean fullscreen) {
        configuration.fullscreen = fullscreen;
        glWindow.setFullscreen(fullscreen);
        if (!fullscreen) {
            glWindow.setFullscreen(false);
            glWindow.setPosition(glWindow.getWidth() / 2, glWindow.getHeight() / 2);
        }
    }

    @Override
    protected void destroy() {
        if (animator != null) {
            animator.stop();
        }
    }

}
