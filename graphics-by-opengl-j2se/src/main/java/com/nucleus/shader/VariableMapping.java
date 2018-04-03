package com.nucleus.shader;

import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.Mesh.BufferIndex;
import com.nucleus.shader.ShaderVariable.VariableType;

/**
 * This interface has the uniform and attribute mapping for a shader program, this is used to find the program specific
 * index and offset of a variable.
 * The offset is used when setting the attribute pointers to GL, ie it is the offset where data for the variable is
 * located in attribute/uniform data.
 * The index is the variable location in the shader program, ie in the array holding active variables.
 * 
 * @author Richard Sahlin
 *
 */
public interface VariableMapping {

    /**
     * Returns the index position of the attribute, this can be used to locate runtime version of variable.
     * 
     * @return Index of variable
     */
    public int getIndex();

    /**
     * Returns the (static) offset for data to the variable.
     * 
     * @return
     */
    public int getOffset();

    /**
     * Returns the type of variable
     * 
     * @return Type of variable
     */
    public VariableType getType();

    /**
     * Returns the buffer index in the mesh.
     * This value can be used to call {@link Mesh#getAttributeBuffer(BufferIndex)}
     * 
     * @param BufferIndex Index to buffer holding variables
     */
    public BufferIndex getBufferIndex();

    /**
     * Returns the name of the variable - this is the name as defined it shall be defined in shader program.
     * 
     * @return
     */
    public String getName();

}
