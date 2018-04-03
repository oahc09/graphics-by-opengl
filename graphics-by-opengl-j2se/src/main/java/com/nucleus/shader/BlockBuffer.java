package com.nucleus.shader;

import java.nio.Buffer;

import com.nucleus.geometry.BufferObject;
import com.nucleus.shader.ShaderVariable.InterfaceBlock;

/**
 * Storage for a variable block, for instance a uniform block
 * The underlying buffer shall be a java.nio buffer
 *
 */
public abstract class BlockBuffer extends BufferObject {

    protected final Buffer plainBuffer;
    /**
     * The block this buffer belongs to or null
     */
    protected final InterfaceBlock interfaceBlock;

    /**
     * Name of the block as defined in the source
     */
    protected final String blockName;

    public BlockBuffer(Buffer buffer, String blockName, int sizeInBytes) {
        super(sizeInBytes);
        this.blockName = blockName;
        this.plainBuffer = buffer;
        this.interfaceBlock = null;

    }

    /**
     * 
     * @param buffer
     * @param blockName
     * @param interfaceBlock The block this buffer belongs to
     * @throws NullPointerException If interfaceBlock is null
     */
    public BlockBuffer(Buffer buffer, String blockName, InterfaceBlock interfaceBlock) {
        super(interfaceBlock.blockDataSize);
        this.blockName = blockName;
        this.interfaceBlock = interfaceBlock;
        this.plainBuffer = buffer;
    }

    /**
     * Returns the capacity of the buffer
     * 
     * @return
     */
    public int capacity() {
        return plainBuffer.capacity();
    }

    /**
     * Sets the position in the buffer
     * 
     * @param newPosition
     */
    public abstract void position(int newPosition);

    /**
     * Returns the name of the block, as defined in the source
     * 
     * @return
     */
    public String getBlockName() {
        return blockName;
    }

    /**
     * Returns the underlying buffer as an un-typed Buffer
     * 
     * @return
     */
    public Buffer getBuffer() {
        return plainBuffer;
    }

    /**
     * Returns the interface block that this buffer belongs to
     * 
     * @return
     */
    public InterfaceBlock getInterfaceBlock() {
        return interfaceBlock;
    }

}