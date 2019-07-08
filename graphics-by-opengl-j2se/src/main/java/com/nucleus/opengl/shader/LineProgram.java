package com.nucleus.opengl.shader;

import java.nio.FloatBuffer;

import com.nucleus.geometry.AttributeUpdater.BufferIndex;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.renderer.Pass;
import com.nucleus.shader.ShaderVariable.VariableType;

/**
 * Program for rendering lines and similar.
 *
 */
public class LineProgram extends GLShaderProgram {

    public static class LineProgramIndexer extends VariableIndexer {

        protected final static Property[] PROPERTY = new Property[] { Property.VERTEX,
                Property.EMISSIVE };
        protected final static int[] OFFSETS = new int[] { 0, 3 };
        protected final static VariableType[] TYPES = new VariableType[] { VariableType.ATTRIBUTE,
                VariableType.ATTRIBUTE };

        public LineProgramIndexer() {
            super();
            createArrays(PROPERTY, OFFSETS, TYPES, 7, BufferIndex.ATTRIBUTES);
        }

    }

    public static final String CATEGORY = "line";

    public LineProgram(Shading shading) {
        super(null, shading, CATEGORY, GLShaderProgram.ProgramType.VERTEX_FRAGMENT);
        setIndexer(new LineProgramIndexer());
    }

    @Override
    public GLShaderProgram getProgram(NucleusRenderer renderer, Pass pass, Shading shading) {
        switch (pass) {
            case UNDEFINED:
            case ALL:
            case MAIN:
                return this;
            default:
                throw new IllegalArgumentException("Invalid pass " + pass);
        }
    }

    @Override
    public void updateUniformData(FloatBuffer destinationUniform) {
    }

    @Override
    public void initUniformData(FloatBuffer destinationUniforms) {
    }

}