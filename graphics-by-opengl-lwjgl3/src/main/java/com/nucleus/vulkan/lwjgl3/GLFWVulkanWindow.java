package com.nucleus.vulkan.lwjgl3;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.MemoryUtil;

import com.nucleus.Backend;
import com.nucleus.Backend.BackendFactory;
import com.nucleus.CoreApp;
import com.nucleus.CoreApp.CoreAppStarter;
import com.nucleus.lwjgl3.GLFWWindow;
import com.nucleus.renderer.NucleusRenderer.Renderers;
import com.nucleus.renderer.SurfaceConfiguration;

/**
 * Window for Vulkan support
 *
 */
public class GLFWVulkanWindow extends GLFWWindow {

    public GLFWVulkanWindow(BackendFactory factory, CoreAppStarter coreAppStarter, Configuration config) {
        super(factory, coreAppStarter, config);
    }

    @Override
    public void init() {
        GLFWErrorCallback.createPrint().set();
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize glfw");
        }
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_NO_API);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        window = GLFW.glfwCreateWindow(configuration.width, configuration.height, "", MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        backend = initFW(window);
        initInput();
    }

    @Override
    protected Backend initFW(long GLFWWindow) {
        return factory.createBackend(Renderers.VULKAN11, window, null);
    }

}
