package com.nucleus.jogl;

import java.awt.Frame;
import java.awt.event.WindowListener;

import com.jogamp.common.os.Platform;
import com.jogamp.nativewindow.util.Dimension;
import com.jogamp.nativewindow.util.InsetsImmutable;
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
import com.nucleus.CoreApp;
import com.nucleus.CoreApp.CoreAppStarter;
import com.nucleus.J2SEWindow;
import com.nucleus.SimpleLogger;
import com.nucleus.mmi.PointerData;
import com.nucleus.mmi.PointerData.PointerAction;
import com.nucleus.mmi.PointerData.Type;
import com.nucleus.opengl.GLESWrapper.Renderers;
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

    /**
     * A zoom on the wheel equals 1 / 5 screen height
     */
    private final static float ZOOM_FACTOR = 100f;

    private Dimension windowSize;
    private boolean undecorated = false;
    private boolean alwaysOnTop = false;
    private boolean fullscreen = false;
    private boolean mouseVisible = true;
    private boolean mouseConfined = false;
    private int swapInterval = 0;
    private boolean autoSwapBuffer = false;
    protected volatile boolean contextCreated = false;
    protected GLCanvas canvas;
    protected Frame frame;
    protected GLWindow glWindow;
    protected Renderers version;
    Animator animator;

    /**
     * Creates a new JOGL window with the specified {@link CoreAppStarter} and swapinterval
     * 
     * @param version
     * @param coreAppStarter
     * @param config Surface configuration
     * @param width
     * @param height
     * @param undecorated
     * @param fullscreen
     * @param swapInterval
     * @throws IllegalArgumentException If coreAppStarter is null
     */
    public JOGLGLWindow(Renderers version, CoreAppStarter coreAppStarter, SurfaceConfiguration config, int width,
            int height, boolean undecorated,
            boolean fullscreen, int swapInterval) {
        super(coreAppStarter, width, height, config);
        this.swapInterval = swapInterval;
        this.undecorated = undecorated;
        this.fullscreen = fullscreen;
        this.version = version;
        create(version, coreAppStarter, width, height);
    }

    private void create(Renderers version, CoreAppStarter coreAppStarter, int width, int height) {
        if (coreAppStarter == null) {
            throw new IllegalArgumentException("CoreAppStarter is null");
        }
        this.coreAppStarter = coreAppStarter;
        windowSize = new Dimension(width, height);
        GLProfile profile = getProfile(version);
        // createAWTWindow(width, height, profile);
        createNEWTWindow(width, height, profile);
    }

    protected GLProfile getProfile(Renderers version) {
        SimpleLogger.d(getClass(), "os.and.arch: " + Platform.os_and_arch);
        GLProfile defaultProfile = GLProfile.getDefault();
        if (defaultProfile != null) {
            SimpleLogger.d(getClass(), "Default profile implName: " + defaultProfile.getImplName() + ", name: "
                    + defaultProfile.getName());
        } else {
            SimpleLogger.d(getClass(), "Default profile is NULL");
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
                if (defaultProfile.isGLES3() || defaultProfile.isGL4ES3()) {
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
        GLCapabilities glCapabilities = new GLCapabilities(profile);
        glCapabilities.setSampleBuffers(true);
        glCapabilities.setNumSamples(config.getSamples());
        glCapabilities.setBackgroundOpaque(config.getAlphaBits() == 0);
        glCapabilities.setAlphaBits(config.getAlphaBits());
        glWindow = GLWindow.create(glCapabilities);
        glWindow.setUndecorated(undecorated);
        InsetsImmutable insets = glWindow.getInsets();
        glWindow.setSize(undecorated ? windowSize.getWidth() : windowSize.getWidth() + insets.getTotalWidth(),
                undecorated ? windowSize.getHeight() : windowSize.getHeight() + insets.getTotalHeight());
        glWindow.setAlwaysOnTop(alwaysOnTop);
        glWindow.setFullscreen(fullscreen);
        glWindow.setPointerVisible(mouseVisible);
        glWindow.confinePointer(mouseConfined);
        glWindow.addMouseListener(this);
        glWindow.addWindowListener(this);
        glWindow.addKeyListener(this);
        glWindow.addWindowListener(this);
        glWindow.addGLEventListener(this);
        GLProfile.initSingleton();
        animator = new Animator();
        animator.add(glWindow);
        animator.start();
        glWindow.setVisible(true);
        glWindow.setAutoSwapBufferMode(autoSwapBuffer);
    }

    private void createAWTWindow(int width, int height, GLProfile glProfile) {
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
        frame.setVisible(true);
    }

    public void setTitle(String title) {
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
        drawable.getGL().setSwapInterval(swapInterval);
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
        drawable.getGL().setSwapInterval(swapInterval);
        coreApp.renderFrame();
        if (glWindow != null) {
            glWindow.swapBuffers();
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
                    type = Type.ERASER;
                    break;
                case MouseEvent.BUTTON3:
                    type = Type.FINGER;
                    break;

            }
            handleMouseEvent(action, type, xpos[i], ypos[i], e.getPointerId(i), e.getWhen());
        }
    }

    protected void handleKeyEvent(KeyEvent event) {
        switch (event.getEventType()) {
            case KeyEvent.EVENT_KEY_PRESSED:
                switch (event.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE:
                        backPressed();
                }
                break;
            case KeyEvent.EVENT_KEY_RELEASED:
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
        float factor = ZOOM_FACTOR;
        coreApp.getInputProcessor().pointerEvent(PointerAction.ZOOM, PointerData.Type.MOUSE, e.getWhen(),
                PointerData.POINTER_1, new float[] {
                        e.getRotation()[1] * factor, e.getRotation()[1] * factor },
                0);
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
        windowClosed();
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
        handleKeyEvent(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        handleKeyEvent(e);
    }

    private void backPressed() {
        SimpleLogger.d(getClass(), "backPressed()");
        if (fullscreen) {
            fullscreen = false;
            glWindow.setFullscreen(false);
            glWindow.setPosition(glWindow.getWidth() / 2, glWindow.getHeight() / 2);
        } else {
            if (coreApp.onBackPressed()) {
                coreApp.setDestroyFlag();
                glWindow.setVisible(false);
                animator.stop();
                System.exit(0);
            }
        }
    }

}
