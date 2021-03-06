package com.nucleus.shader;

import java.io.IOException;

import com.nucleus.Backend;
import com.nucleus.common.FileUtils;
import com.nucleus.renderer.NucleusRenderer.Renderers;
import com.nucleus.shader.Shader.Categorizer;
import com.nucleus.shader.Shader.ShaderType;

/**
 * Holds shader source (binary or bytecode) and data related to the source for a
 * shader This is either pre-compiled binary or byte-code (SPIR-V or similar)
 *
 */
public abstract class ShaderBinary {

    public static final String PROGRAM_DIRECTORY = "assets/";
    public final static String FILE_SUFFIX_SEPARATOR = ".";

    /**
     * Use for shader source names that are versioned 200
     */
    public static final String V200 = "v200";
    /**
     * Use for shader source names that are versioned 300
     */
    public static final String V300 = "v300";
    /**
     * Use for shader source names that are versioned 310
     */
    public static final String V310 = "v310";
    /**
     * Use for shader source names that are versioned 320
     */
    public static final String V320 = "v320";
    /**
     * Use for shader source names that are versioned 450
     */
    public static final String V450 = "v450";

    private String sourceName;

    private String path;

    private String suffix;

    /**
     * Shader type
     */
    public final ShaderType type;

    /**
     * Creates a shadersource from sourcename and optional suffix
     * 
     * 
     * @param path
     * @param sourcename if sourcename is excluding file suffix then {@link #suffix}
     * is used.
     * @param suffix Optional file suffix including separator char ('.') - used
     * if sourcename does not include suffix.
     * @param type
     * @throws IllegalArgumentException If any of the parameters other than
     * {@link #suffix} are null, or if sourcename
     * does not include suffix and {@link #suffix}
     * is null
     */
    public ShaderBinary(String path, String sourcename, String suffix, ShaderType type) {
        if (path == null || type == null) {
            throw new IllegalArgumentException("null parameter");
        }
        this.path = path;
        int s = sourcename.indexOf(FILE_SUFFIX_SEPARATOR);
        if (s > -1) {
            this.suffix = sourcename.substring(s);
            this.sourceName = sourcename.substring(0, s);
        } else {
            this.sourceName = sourcename;
            this.suffix = suffix;
        }
        if (suffix == null) {
            throw new IllegalArgumentException("Suffix is null");
        }
        this.type = type;
    }

    /**
     * Returns the full sourcename of the asset to be loaded.
     * 
     * @return
     */
    public String getFullSourceName() {
        return path + sourceName + suffix;
    }

    public String getPath() {
        return path;
    }

    public String getSuffix() {
        return suffix;
    }

    /**
     * Loads the source data for this shader binary, this shall call
     * {@link #getFullSourceName()} to fetch the asset name.
     * 
     * @param backend
     * @param function
     * @throws IOException
     */
    public abstract void loadShader(Backend backend, Categorizer function) throws IOException;

    @Override
    public String toString() {
        return "Type: " + type + ", Sourcename: " + getFullSourceName();
    }

    /**
     * This is used to be able to load different shader sources depending on shading
     * language version. Returns the common denominator for shader versions, ie 200
     * for GLES20 300 for GLES 300, 310, 320 450 for Vulkan 10, 11
     * 
     * @param version Highest shading language version that is supported
     * @return Empty string "", or shader version to append to source name if
     * different shader source shall be used for a specific shader version.
     */
    public static String getSourceNameVersion(Renderers version) {
        switch (version) {
            case GLES20:
                return ShaderBinary.V200 + FileUtils.DIRECTORY_SEPARATOR;
            case GLES30:
            case GLES31:
            case GLES32:
                return ShaderBinary.V300 + FileUtils.DIRECTORY_SEPARATOR;
            case VULKAN10:
            case VULKAN11:
                return ShaderBinary.V450 + FileUtils.DIRECTORY_SEPARATOR;
            default:
                throw new IllegalArgumentException("Not implemented for " + version);
        }
    }

}
