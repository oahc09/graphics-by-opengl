package com.nucleus.opengl.shader;

import java.nio.FloatBuffer;

import com.nucleus.renderer.Pass;
import com.nucleus.shader.Shader.Shading;

/**
 * Generic shader program - use this when a specific shader source shall be specified.
 * Loads from /assets folder and appends 'vertex', 'fragment', 'geometry' after source name.
 *
 */
public class GenericShaderProgram extends GLShaderProgram {

    protected String[] source;

    /**
     * Creates a shader program that will load shaders from default location
     * 
     * @param source Source names for shaders, must match number of shader types in Shader.
     * @param pass
     * @param shading
     * @param category
     * @param shaders
     * {@link ProgramType#VERTEX_FRAGMENT} then this must contain 2 values.
     * @param shaders
     */
    public GenericShaderProgram(String[] source, Pass pass, Shading shading, String category,
            GLShaderProgram.ProgramType shaders) {
        super(pass, shading, category, shaders);
        this.source = source;
    }

    public GenericShaderProgram(Pass pass, Shading shading, String category,
            GLShaderProgram.ProgramType shaders) {
        super(pass, shading, category, shaders);
    }

    public GenericShaderProgram(Categorizer function, GLShaderProgram.ProgramType shaders) {
        super(function, shaders);
    }

    @Override
    protected String getShaderSourceName(ShaderType type) {
        if (source == null) {
            return super.getShaderSourceName(type);
        }
        if (type.index >= source.length) {
            return "";
        }
        return function.getPath(type) + source[type.index];
    }

    @Override
    public void updateUniformData(FloatBuffer destinationUniform) {
    }

    @Override
    public void initUniformData(FloatBuffer destinationUniforms) {
    }

    @Override
    public String getKey() {
        return getClass().getSimpleName() + getShaderSourceName(ShaderType.VERTEX)
                + getShaderSourceName(ShaderType.GEOMETRY) + getShaderSourceName(ShaderType.FRAGMENT) +
                getShaderSourceName(ShaderType.COMPUTE);
    }

}