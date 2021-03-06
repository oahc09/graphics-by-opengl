package com.nucleus.android.egl10;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import com.nucleus.SimpleLogger;
import com.nucleus.egl.EGLUtils;
import com.nucleus.renderer.SurfaceConfiguration;

/**
 * EGL Utils based on old JSR 239 EGL (EGL 1.0 and 1.1)
 *
 */
public class EGL10Utils {

    public final static String TAG = "EGLUtils";

    public final static int EGL_OPENGL_ES2_BIT = 4;

    /**
     * Calls getEGLConfigAttrib and returns the value for the specified attribute.
     * 
     * @param egl
     * @param eglDisplay
     * @param config
     * @param configAttrib
     * @return
     */
    public static int getEGLConfigAttrib(EGL10 egl, EGLDisplay eglDisplay, EGLConfig config, int configAttrib) {
        int[] attribs = new int[1];
        egl.eglGetConfigAttrib(eglDisplay, config, configAttrib, attribs);
        return attribs[0];
    }

    /**
     * Reads the configuration and stores in surfaceConfig
     * 
     * @param egl
     * @param eglDisplay
     * @param config
     * @param surfaceConfig
     */
    public static void readSurfaceConfig(EGL10 egl, EGLDisplay eglDisplay, EGLConfig config,
            SurfaceConfiguration surfaceConfig) {
        surfaceConfig.setRedBits(getEGLConfigAttrib(egl, eglDisplay, config, EGL10.EGL_RED_SIZE));
        surfaceConfig.setGreenBits(getEGLConfigAttrib(egl, eglDisplay, config, EGL10.EGL_GREEN_SIZE));
        surfaceConfig.setBlueBits(getEGLConfigAttrib(egl, eglDisplay, config, EGL10.EGL_BLUE_SIZE));
        surfaceConfig.setAlphaBits(getEGLConfigAttrib(egl, eglDisplay, config, EGL10.EGL_ALPHA_SIZE));
        surfaceConfig.setDepthBits(getEGLConfigAttrib(egl, eglDisplay, config, EGL10.EGL_DEPTH_SIZE));
        surfaceConfig.setSamples(getEGLConfigAttrib(egl, eglDisplay, config, EGL10.EGL_SAMPLES));
        surfaceConfig.setSurfaceType(getEGLConfigAttrib(egl, eglDisplay, config, EGL10.EGL_SURFACE_TYPE));
    }

    /**
     * Sets the egl info
     * 
     * @param eglDisplay
     */
    public static void setEGLInfo(EGL10 egl, EGLDisplay eglDisplay, SurfaceConfiguration surfaceConfig) {
        surfaceConfig.setInfo(egl.eglQueryString(eglDisplay, EGL10.EGL_VERSION),
                egl.eglQueryString(eglDisplay, EGL10.EGL_VENDOR),
                egl.eglQueryString(eglDisplay, EGL10.EGL_EXTENSIONS));
    }

    /**
     * Select the config that is what we asked for
     * 
     * @param egl
     * @param eglDisplay
     * @param configs
     * @param count
     * @param wantedConfig
     * @return The exact matching EGLConfig or null
     */
    public final static EGLConfig selectConfig(EGL10 egl, EGLDisplay eglDisplay, EGLConfig[] configs, int count,
            SurfaceConfiguration wantedConfig) {
        if (wantedConfig.getSamples() > 1) {
            SimpleLogger.d(EGL10Utils.class, "Select config with >= " + wantedConfig.getSamples());
            // Fetch a list with configs that have at least two samples
            ArrayList<EGLConfig> sortedlist = filterByAttributeGreaterEqual(egl, eglDisplay, configs, EGL10.EGL_SAMPLES,
                    2);
            SimpleLogger.d(EGL10Utils.class, "Found " + sortedlist.size() + " configs with samples.");
            EGLConfig chosen = null;
            for (EGLConfig conf : sortedlist) {
                int currentSamples = getEGLConfigAttrib(egl, eglDisplay, conf, EGL10.EGL_SAMPLES);
                if (currentSamples == wantedConfig.getSamples()) {
                    return conf;
                }
                if (chosen == null) {
                    chosen = conf;
                } else {
                    int chosenSamples = getEGLConfigAttrib(egl, eglDisplay, chosen, EGL10.EGL_SAMPLES);
                    if (chosenSamples < currentSamples || chosenSamples > wantedConfig.getSamples()) {
                        if (currentSamples < wantedConfig.getSamples()) {
                            chosen = conf;
                            SimpleLogger.d(EGL10Utils.class, "Picking config with " + currentSamples + " samples");
                        }
                    }
                }
            }
            if (chosen != null) {
                return chosen;
            }
        }
        return configs[0];

    }

    public final static String getError(int error) {

        switch (error) {
            case EGL10.EGL_BAD_ACCESS:
                return "EGL_BAD_ACCESS";
            case EGL10.EGL_BAD_ALLOC:
                return "EGL_BAD_ALLOC";
            case EGL10.EGL_BAD_ATTRIBUTE:
                return "EGL_BAD_ATTRIBUTE";
            case EGL10.EGL_BAD_CONFIG:
                return "EGL_BAD_CONFIG";
            case EGL10.EGL_BAD_CONTEXT:
                return "EGL_BAD_CONTEXT";
            case EGL10.EGL_BAD_CURRENT_SURFACE:
                return "EGL_BAD_CURRENT_SURFACE";
            case EGL10.EGL_BAD_DISPLAY:
                return "EGL_BAD_DISPLAY";
            case EGL10.EGL_BAD_MATCH:
                return "EGL_BAD_MATCH";
            case EGL10.EGL_BAD_NATIVE_PIXMAP:
                return "EGL_BAD_PIXMAP";
            case EGL10.EGL_BAD_NATIVE_WINDOW:
                return "EGL_BAD_NATIVE_WINDOW";
            case EGL10.EGL_BAD_PARAMETER:
                return "EGL_BAD_PARAMETER";
            case EGL10.EGL_BAD_SURFACE:
                return "EGL_BAD_SURFACE";
            case EGL10.EGL_NOT_INITIALIZED:
                return "EGL_NOT_INITIALIZED";
        }

        return "UNKNOWN (" + error + ")";
    }

    /**
     * Selects the closest matching config - some properties such as samples may not be fulfilled.
     * Create a SurfaceConfig from the returned EGLConfig to check what the configuration is.
     * 
     * @param egl
     * @param display
     * @param wantedConfig
     * @return
     */
    public static EGLConfig selectConfig(EGL10 egl, EGLDisplay display, SurfaceConfiguration wantedConfig) {
        int[] configSpec = EGLUtils.createConfig(wantedConfig);
        EGLConfig[] configs = new EGLConfig[20];
        int[] num_config = new int[1];
        egl.eglChooseConfig(display, configSpec, configs, 20, num_config);
        if (num_config[0] == 0) {
            // Really bad!
            return null;
        }
        // Filter on config attributes that may not be present
        return EGL10Utils.selectConfig(egl, display, configs, num_config[0], wantedConfig);
    }

    /**
     * Filters the list of configs based on attribute
     * 
     * @param egl
     * @param eglDisplay
     * @param configs
     * @param attribute The attribute to filter by
     * @param min Min value for attribute, if equal or greater the config is included.
     * @return List of egl configs filtered by attribute sorted in ascending value
     */
    public static ArrayList<EGLConfig> filterByAttributeGreaterEqual(EGL10 egl, EGLDisplay eglDisplay,
            EGLConfig[] configs, int attribute, int min) {
        ArrayList<EGLConfig> result = new ArrayList<>();

        for (EGLConfig config : configs) {
            if (config == null) {
                return result;
            }
            int value = getEGLConfigAttrib(egl, eglDisplay, config, attribute);
            if (value >= min) {
                int index = 0;
                boolean added = false;
                for (EGLConfig c : result) {
                    int v = getEGLConfigAttrib(egl, eglDisplay, c, attribute);
                    if (value < v) {
                        result.add(index, config);
                        added = true;
                        break;
                    }
                    index++;
                }
                if (!added) {
                    result.add(config);
                }
            }
        }
        return result;
    }

}
