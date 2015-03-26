package com.nucleus.renderer;

import java.util.ArrayList;

import com.nucleus.camera.ViewFrustum;
import com.nucleus.geometry.ElementBuffer;
import com.nucleus.geometry.Material;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.VertexBuffer;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.opengl.GLES20Wrapper.GLES20;
import com.nucleus.opengl.GLException;
import com.nucleus.opengl.GLUtils;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.texturing.Texture2D;

/**
 * Platform agnostic renderer, this handles pure render related methods.
 * It uses a GL wrapper to access GL functions, the caller should not need to know the specifics of OpenGLES.
 * The goal of this class is to have a low level renderer that can be used to draw objects on screen without having
 * to access OpenGLES methods directly.
 * This class does not create thread to drive rendering, that shall be done separately.
 * 
 * @author Richard Sahlin
 */
public abstract class BaseRenderer {

    public final static String NULL_GLESWRAPPER_ERROR = "GLES wrapper is null";

    protected ViewFrustum viewFrustum = new ViewFrustum();
    protected GLES20Wrapper gles;
    /**
     * Implementations shall set the width of the target display
     */
    protected int width;
    /**
     * Implementations shall set the height of the target display
     */
    protected int height;

    /**
     * Creates a new renderer using the specified GLES20Wrapper
     * 
     * @param gles
     * @throws IllegalArgumentException If gles is null
     */
    protected BaseRenderer(GLES20Wrapper gles) {
        if (gles == null) {
            throw new IllegalArgumentException(NULL_GLESWRAPPER_ERROR);
        }
        this.gles = gles;
    }

    /**
     * Sets the projection matrix to be used by the renderer based on the setting in the viewFrustum
     * 
     * @param viewFrustum
     * 
     */
    public abstract void setProjectionMatrix(ViewFrustum viewFrustum);

    /**
     * Called when the GL context is created for a render surface, GL is now active and can be used to create objects,
     * textures and buffers.
     * If this method is called again - it means that the GL context has been lost and is re-created, all textures,
     * objects and buffers must be recreated.
     * 
     */
    public abstract void GLContextCreated();

    /**
     * Signals the start of a frame, implement if needed in subclasses.
     */
    public void beginFrame() {

    }

    /**
     * Signals the end of a frame - rendering is considered to be finished and implementations should call
     * EGL.swapBuffers() if needed
     */
    public void endFrame() {

    }

    /**
     * The main render method, all drawing shall take place here.
     * Call this method when a new frame shall be produced by GL
     * Note that implementations shall not swap buffer in this method, this method shall ONLY render to the currently
     * attached framebuffer.
     */
    public void render() {
        // For now always set the viewport
        // TODO: Add dirty flag in viewport and only set when updated.
        int[] viewport = viewFrustum.getViewPort();
        gles.glViewport(viewport[ViewFrustum.VIEWPORT_X], viewport[ViewFrustum.VIEWPORT_Y],
                viewport[ViewFrustum.VIEWPORT_WIDTH], viewport[ViewFrustum.VIEWPORT_HEIGHT]);
        setProjectionMatrix(viewFrustum);
    }

    /**
     * Renders all the meshes in the list, render order is guaranteed to be the same as the list, ie position 0 is
     * rendered first.
     * 
     * @param meshes
     */
    public void renderMeshes(ArrayList<Mesh> meshes) throws GLException {

        for (Mesh mesh : meshes) {

            renderMesh(mesh);

        }

    }

    /**
     * Sets the attribute and uniform pointers to gl and renders the vertex buffer.
     * 
     */
    public void renderMesh(Mesh mesh) throws GLException {

        Material material = mesh.getMaterial();
        ShaderProgram program = material.getProgram();
        VertexBuffer vertices = mesh.getVerticeBuffer(0);
        ElementBuffer indices = mesh.getElementBuffer();
        gles.glUseProgram(program.getProgram());

        if (mesh.getTexture(0) != null) {
            int textureID = mesh.getTexture(Texture2D.TEXTURE_0).getName();
            gles.glActiveTexture(GLES20.GL_TEXTURE0);
            gles.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
        }

        program.bindAttributes(gles, this, mesh);
        program.bindUniforms(gles, this, mesh);

        gles.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        gles.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_NEAREST);

        gles.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_REPEAT);
        gles.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_REPEAT);

        if (indices == null) {
            gles.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices.getVerticeCount());
        } else {
            gles.glDrawElements(indices.getMode().mode, indices.getCount(), indices.getType().type, indices.getBuffer());
        }

        GLUtils.handleError(gles, "glDrawArrays ");
    }

    /**
     * Returns the view frustum
     * 
     * @return
     */
    public ViewFrustum getViewFrustum() {
        return viewFrustum;
    }

}
