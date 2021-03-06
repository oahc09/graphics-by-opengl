package com.nucleus.opengl.shader;

import com.nucleus.geometry.AttributeUpdater.BufferIndex;
import com.nucleus.shader.GenericShaderProgram;
import com.nucleus.shader.Shader;
import com.nucleus.shader.ShaderVariable.VariableType;

/**
 * Program for rendering lines and similar.
 *
 */
public class LineProgram extends GenericShaderProgram {

    public static class LineProgramIndexer extends NamedVariableIndexer {

        protected final static Property[] PROPERTY = new Property[] { Property.VERTEX,
                Property.EMISSIVE };
        protected final static int[] OFFSETS = new int[] { 0, 3 };
        protected final static VariableType[] TYPES = new VariableType[] { VariableType.ATTRIBUTE,
                VariableType.ATTRIBUTE };
        protected final static BufferIndex[] BUFFERINDEXES = new BufferIndex[] { BufferIndex.ATTRIBUTES,
                BufferIndex.ATTRIBUTES };

        public LineProgramIndexer() {
            super();
            createArrays(PROPERTY, OFFSETS, TYPES, new int[] { 7 }, BUFFERINDEXES);
        }

    }

    public static final String CATEGORY = "line";

    public LineProgram(Shading shading) {
        init(null, null, shading, CATEGORY, Shader.ProgramType.VERTEX_FRAGMENT);
        setIndexer(new LineProgramIndexer());
    }

    @Override
    public void updateUniformData() {
    }

    @Override
    public void initUniformData() {
    }

}
