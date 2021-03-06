package com.nucleus.vulkan;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import com.nucleus.Backend;
import com.nucleus.common.FileUtils;
import com.nucleus.renderer.NucleusRenderer.Renderers;
import com.nucleus.shader.ShaderBinary;
import com.nucleus.vulkan.Vulkan10.Result;
import com.nucleus.vulkan.structs.QueueFamilyProperties;

/**
 * The Vulkan wrapper -all things related to Vulkan functionality that is shared
 * independently of version
 *
 */
public abstract class VulkanWrapper extends Backend {

    public interface VulkanDeviceSelector {
        /**
         * Vulkan implementation agnostic method to select the device to use
         * 
         * @param devices
         * @return The device to use or null if no device that can be used
         */
        public PhysicalDevice selectDevice(PhysicalDevice[] devices);

        /**
         * Used to select the queue instance of the device
         * 
         * @param device
         */
        public QueueFamilyProperties selectQueueInstance(PhysicalDevice device);

    }

    protected static VulkanDeviceSelector deviceSelector;

    /**
     * Sets the vulkan device selector - use this to override the default selector
     * behavior
     * 
     * @param deviceSelector The device selector to be called when Vulkan wrapper is
     * created, or null to remove.
     */
    public static void setVulkanDeviceSelector(VulkanDeviceSelector deviceSelector) {
        VulkanWrapper.deviceSelector = deviceSelector;
    }

    /**
     * The Vulkan API version #define VK_VERSION_MAJOR(version) ((int)(version) >>
     * 22) #define VK_VERSION_MINOR(version) (((int)(version) >> 12) & 0x3ff)
     * #define VK_VERSION_PATCH(version) ((int)(version) & 0xfff)
     *
     */
    public class APIVersion {
        public final int major;
        public final int minor;
        public final int patch;

        public APIVersion(int api) {
            this.major = api >> 22;
            this.minor = (api >> 12) & 0x3ff;
            this.patch = (api & 0x0fff);
        }

        @Override
        public String toString() {
            return major + "." + minor + " patch " + patch;
        }

    }

    protected VulkanWrapper(Renderers version) {
        super(version);
        try {
            initVulkanBackend();
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    protected void initVulkanBackend() throws IOException, URISyntaxException {
        String path = ShaderBinary.PROGRAM_DIRECTORY + ShaderBinary.V450 + "/";
        // Returns subfolders in path - remember to include current dir - ie empty folder name.
        ArrayList<String> folders = FileUtils.getInstance().listResourceFolders(path);
        // Insert empty element for current dir.
        folders.add(0, "");
        GLSLCompiler.getInstance().compileShaders(path, folders);
    }

    /**
     * Checks that the resultcode is VK_SUCCESS, if not RuntimeException is thrown.
     * 
     * @param value
     */
    public void assertResult(int value) {
        Result r = Result.getResult(value);
        {
            if (r == null || r != Result.VK_SUCCESS) {
                throw new RuntimeException("Failed with error: " + r);
            }
        }
    }

}
