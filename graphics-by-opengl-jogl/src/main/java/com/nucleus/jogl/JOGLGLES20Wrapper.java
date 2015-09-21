package com.nucleus.jogl;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.renderer.NucleusRenderer;

/**
 * JOGL based GLES2 wrapper, this is the wrapper that lets the {@link NucleusRenderer} use GLES2
 * 
 * @author Richard Sahlin
 *
 */
public class JOGLGLES20Wrapper extends GLES20Wrapper {

    private final static int INFO_BUFFERSIZE = 4096;
    private final static String GLES_NULL = "GLES20 is null";

    /**
     * Used in methods that fetch data from GL - since this wrapper is not threadsafe (GL must be accessed from one
     * thread)
     */
    private IntBuffer length = IntBuffer.allocate(1);
    /**
     * Used in methods that fetch data from GL - since this wrapper is not threadsafe (GL must be accessed from one
     * thread)
     */
    private ByteBuffer buffer = ByteBuffer.allocate(INFO_BUFFERSIZE);

    GL2ES2 gles;
    private int[] tempVBO = new int[] { -1 };

    /**
     * Creates a new instance of the GLES20 wrapper for JOGL
     * 
     * @param gles The JOGL GLES20 instance
     * @throws IllegalArgumentException If gles is null
     */
    public JOGLGLES20Wrapper(GL2ES2 gles) {
        if (gles == null) {
            throw new IllegalArgumentException(GLES_NULL);
        }
        this.gles = gles;
        gles.glGenBuffers(1, tempVBO, 0);
    }

    @Override
    public void glAttachShader(int program, int shader) {
        gles.glAttachShader(program, shader);
    }

    @Override
    public void glLinkProgram(int program) {
        gles.glLinkProgram(program);

    }

    @Override
    public void glShaderSource(int shader, String shaderSource) {
        gles.glShaderSource(shader, 1, new String[] { shaderSource }, null);

    }

    @Override
    public void glCompileShader(int shader) {
        gles.glCompileShader(shader);
    }

    @Override
    public int glCreateShader(int type) {
        return gles.glCreateShader(type);
    }

    @Override
    public int glCreateProgram() {
        return gles.glCreateProgram();
    }

    @Override
    public void glGetShaderiv(int shader, int pname, IntBuffer params) {
        gles.glGetShaderiv(shader, pname, params);

    }

    @Override
    public void glUseProgram(int program) {
        gles.glUseProgram(program);

    }

    @Override
    public void glGetProgramiv(int program, int pname, int[] params, int offset) {
        gles.glGetProgramiv(program, pname, params, offset);

    }

    @Override
    public void glGetActiveAttrib(int program, int index, int bufsize, int[] length, int lengthOffset, int[] size,
            int sizeOffset, int[] type, int typeOffset, byte[] name, int nameOffset) {
        gles.glGetActiveAttrib(program, index, bufsize, length, lengthOffset, size, sizeOffset, type, typeOffset,
                name, nameOffset);

    }

    @Override
    public void glGetActiveUniform(int program, int index, int bufsize, int[] length, int lengthOffset, int[] size,
            int sizeOffset, int[] type, int typeOffset, byte[] name, int nameOffset) {
        gles.glGetActiveUniform(program, index, bufsize, length, lengthOffset, size, sizeOffset, type, typeOffset,
                name, nameOffset);

    }

    @Override
    public int glGetUniformLocation(int program, String name) {
        return gles.glGetUniformLocation(program, name);
    }

    @Override
    public int glGetAttribLocation(int program, String name) {
        return gles.glGetAttribLocation(program, name);
    }

    @Override
    public int glGetError() {
        return gles.glGetError();
    }

    @Override
    public void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, Buffer ptr) {
        gles.glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, tempVBO[0]);
        int numBytes = buffer.limit();
        gles.glBufferData(GL2ES2.GL_ARRAY_BUFFER, numBytes, ptr.position(0), GL.GL_STATIC_DRAW);
        gles.glVertexAttribPointer(index, size, type, normalized, stride, 0);
    }

    @Override
    public void glEnableVertexAttribArray(int index) {
        gles.glEnableVertexAttribArray(index);

    }

    @Override
    public void glUniformMatrix4fv(int location, int count, boolean transpose, float[] v, int offset) {
        gles.glUniformMatrix4fv(location, count, transpose, v, offset);

    }

    @Override
    public void glDrawArrays(int mode, int first, int count) {
        gles.glDrawArrays(mode, first, count);

    }

    @Override
    public void glDrawElements(int mode, int count, int type, Buffer indices) {
        gles.glBindBuffer(GL2ES2.GL_ELEMENT_ARRAY_BUFFER, tempVBO[0]);
        int numBytes = indices.limit();
        gles.glBufferData(GL2ES2.GL_ELEMENT_ARRAY_BUFFER, numBytes, indices.position(0), GL.GL_STATIC_DRAW);
        gles.glDrawElements(mode, count, type, 0);
    }

    @Override
    public void glBindAttribLocation(int program, int index, String name) {
        gles.glBindAttribLocation(program, index, name);

    }

    @Override
    public void glViewport(int x, int y, int width, int height) {
        gles.glViewport(x, y, width, height);
    }

    @Override
    public String glGetShaderInfoLog(int shader) {
        gles.glGetShaderInfoLog(shader, INFO_BUFFERSIZE, length, buffer);
        return new String(buffer.array(), 0, length.get(0));
    }

    @Override
    public String glGetProgramInfoLog(int program) {
        gles.glGetProgramInfoLog(program, INFO_BUFFERSIZE, length, buffer);
        return new String(buffer.array(), 0, length.get(0));
    }

    @Override
    public void glGenTextures(int count, int[] textures, int offset) {
        gles.glGenTextures(count, textures, offset);

    }

    @Override
    public void glActiveTexture(int texture) {
        gles.glActiveTexture(texture);

    }

    @Override
    public void glBindTexture(int target, int texture) {
        gles.glBindTexture(target, texture);

    }

    @Override
    public String glGetString(int name) {
        return gles.glGetString(name);
    }

    @Override
    public void glGetIntegerv(int pname, int[] params, int offset) {
        gles.glGetIntegerv(pname, params, offset);

    }

    @Override
    public void glUniform4fv(int location, int count, float[] v, int offset) {
        gles.glUniform4fv(location, count, v, offset);

    }

    @Override
    public void glUniform3fv(int location, int count, float[] v, int offset) {
        gles.glUniform3fv(location, count, v, offset);

    }

    @Override
    public void glUniform2fv(int location, int count, float[] v, int offset) {
        gles.glUniform2fv(location, count, v, offset);

    }

    @Override
    public void glTexParameterf(int target, int pname, float param) {
        gles.glTexParameterf(target, pname, param);

    }

    @Override
    public void glTexParameteri(int target, int pname, int param) {
        gles.glTexParameteri(target, pname, param);

    }

    @Override
    public void glClearColor(float red, float green, float blue, float alpha) {
        gles.glClearColor(red, green, blue, alpha);

    }

    @Override
    public void glClear(int mask) {
        gles.glClear(mask);

    }

    @Override
    public void glDisable(int cap) {
        gles.glDisable(cap);

    }

    @Override
    public void glEnable(int cap) {
        gles.glEnable(cap);

    }

    @Override
    public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format,
            int type, Buffer pixels) {
        gles.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);

    }

    @Override
    public void glBlendEquationSeparate(int modeRGB, int modeAlpha) {
        gles.glBlendEquationSeparate(modeRGB, modeAlpha);

    }

    @Override
    public void glBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        gles.glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);

    }

}
