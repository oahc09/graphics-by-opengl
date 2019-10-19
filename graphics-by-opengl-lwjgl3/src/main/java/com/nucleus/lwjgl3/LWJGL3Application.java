package com.nucleus.lwjgl3;

import com.nucleus.CoreApp.ClientApplication;
import com.nucleus.J2SEWindow;
import com.nucleus.J2SEWindowApplication;
import com.nucleus.common.Type;
import com.nucleus.opengl.lwjgl3.GLFWGLESWindow;
import com.nucleus.opengl.lwjgl3.LWJGLEGLWindow;
import com.nucleus.renderer.NucleusRenderer.Renderers;
import com.nucleus.vulkan.lwjgl3.GLFWVulkanWindow;

/**
 * Entry point for an application using lwjgl3 library
 */
public class LWJGL3Application extends J2SEWindowApplication {

    protected static final WindowType DEFAULT_WINDOW_TYPE = WindowType.GLFW;

    private boolean running = false;

    /**
     * Creates a new application starter with the specified renderer and client main class implementation.
     * 
     * @param args
     * @param version
     * @param clientClass Implementing class for {@link ClientApplication}, must implement {@link ClientApplication}
     * interface
     * @throws IllegalArgumentException If clientClass is null
     */
    public LWJGL3Application(String[] args, Renderers version, Type<Object> clientClass) {
        super(args, version, clientClass);
        switch (windowConfiguration.getWindowType()) {
            case GLFW:
                createCoreApp(windowConfiguration.getWidth(), windowConfiguration.getHeight());
                ((GLFWWindow) j2seWindow).swapBuffers();
                coreApp.contextCreated(windowConfiguration.getWidth(), windowConfiguration.getHeight());
                break;
            default:
                // Do nothing - create context based on callback
        }
    }

    @Override
    protected void setProperties(String[] args) {
        windowConfiguration.windowType = DEFAULT_WINDOW_TYPE;
        super.setProperties(args);
    }

    @Override
    protected J2SEWindow createWindow(Renderers version) {
        switch (version) {
            case GLES20:
            case GLES30:
            case GLES31:
            case GLES32:
                createGLESWindow(version);
                break;
            case VULKAN10:
            case VULKAN11:
                createVulkanWindow(version);
                break;
            default:
                throw new IllegalArgumentException("Not implemented for " + version);
        }
        return j2seWindow;
    }

    protected void createGLESWindow(Renderers version) {
        switch (windowConfiguration.windowType) {
            case GLFW:
                j2seWindow = new GLFWGLESWindow(new LWJGLWrapperFactory(), this, windowConfiguration);
                j2seWindow.init();
                j2seWindow.setVisible(true);
                break;
            case JAWT:
                j2seWindow = new JAWTWindow(new LWJGLWrapperFactory(), this, windowConfiguration);
                j2seWindow.init();
                j2seWindow.setVisible(true);
                break;
            case EGL:
                j2seWindow = new LWJGLEGLWindow(new LWJGLWrapperFactory(), this, windowConfiguration );
                break;
            default:
                throw new IllegalArgumentException("Not implemented for " + windowConfiguration.windowType);
        }
    }

    protected void createVulkanWindow(Renderers version) {
        switch (windowConfiguration.windowType) {
            case GLFW:
                j2seWindow = new GLFWVulkanWindow(new LWJGLWrapperFactory(), this, windowConfiguration );
                break;
            default:
                throw new IllegalArgumentException("Not implemented for " + windowConfiguration.windowType);
        }
    }

    @Override
    public void resize(int x, int y, int width, int height) {
        if (coreApp != null) {
            coreApp.getRenderer().resizeWindow(x, y, width, height);
        }
    }

    /**
     * Call this method after instantiation to drive rendering if it is not driven by paint() method from window.
     * Will automatically exit if window type is one that drives rendering via paint()
     */

    public void run() {
        switch (windowConfiguration.windowType) {
            case GLFW:
                running = true;
                while (running) {
                    j2seWindow.drawFrame();
                }
                break;
            default:
        }
    }

}
