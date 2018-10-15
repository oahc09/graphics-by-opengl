package com.nucleus.renderer;

import java.util.ArrayDeque;

import com.nucleus.common.Environment;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.opengl.GLException;
import com.nucleus.opengl.GLUtils;
import com.nucleus.profiling.FrameSampler;
import com.nucleus.renderer.NucleusRenderer.Matrices;
import com.nucleus.scene.GLTFNode;
import com.nucleus.scene.gltf.Accessor;
import com.nucleus.scene.gltf.Buffer;
import com.nucleus.scene.gltf.BufferView;
import com.nucleus.scene.gltf.Camera;
import com.nucleus.scene.gltf.Camera.Perspective;
import com.nucleus.scene.gltf.GLTF;
import com.nucleus.scene.gltf.Material;
import com.nucleus.scene.gltf.Mesh;
import com.nucleus.scene.gltf.Node;
import com.nucleus.scene.gltf.Primitive;
import com.nucleus.scene.gltf.Primitive.Attributes;
import com.nucleus.scene.gltf.Scene;
import com.nucleus.scene.gltf.Texture;
import com.nucleus.shader.GLTFShaderProgram;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.texturing.TextureUtils;

public class GLTFNodeRenderer implements NodeRenderer<GLTFNode> {

    transient protected FrameSampler timeKeeper = FrameSampler.getInstance();
    private Pass currentPass;
    protected ArrayDeque<float[]> modelMatrixStack = new ArrayDeque<float[]>(10);
    protected float[] modelMatrix;
    protected int currentProgram = -1;

    /**
     * Internal method to handle matrix stack, push a matrix on the stack
     * 
     * @param stack The stack to push onto
     * @param matrix
     */
    protected void pushMatrix(ArrayDeque<float[]> stack, float[] matrix) {
        stack.push(matrix);
    }

    /**
     * Internal method to handle matrix stack - pops the latest matrix off the stack
     * 
     * @param stack The stack to pop from
     * @return The poped matrix
     */
    protected float[] popMatrix(ArrayDeque<float[]> stack) {
        return stack.pop();
    }

    @Override
    public boolean renderNode(NucleusRenderer renderer, GLTFNode node, Pass currentPass, float[][] matrices)
            throws GLException {
        currentProgram = -1;
        this.currentPass = currentPass;
        GLES20Wrapper gles = renderer.getGLES();
        // Set view matrix from previous render of this gltfNode
        node.getSavedViewMatrix(matrices[Matrices.VIEW.index]);
        GLTF glTF = node.getGLTF();
        Scene scene = glTF.getDefaultScene();
        setProjection(scene, matrices);
        // Render the default scene.
        renderScene(gles, glTF, scene, currentPass, matrices);
        // Save current viewmatrix to next render of this gltf
        node.saveViewMatrix(matrices[Matrices.VIEW.index]);
        return true;
    }

    protected void renderScene(GLES20Wrapper gles, GLTF glTF, Scene scene, Pass currentPass, float[][] matrices)
            throws GLException {
        // Traverse the nodes and render each.
        Node[] sceneNodes = scene.getNodes();
        if (sceneNodes != null) {
            for (int i = 0; i < sceneNodes.length; i++) {
                renderNode(gles, glTF, sceneNodes[i], matrices);
            }
        }
    }

    /**
     * Sets the projection matrix
     * 
     * @param scene
     * @param matrices
     */
    protected void setProjection(Scene scene, float[][] matrices) {
        if (!scene.isCameraDefined()) {
            // Setup a default projection if none is specified in model - this is to get the right axes and winding.
            Perspective p = new Perspective(1.5f, 0.66f, 10000, 1);
            matrices[Matrices.PROJECTION.index] = p.calculateMatrix();
        } else {
            // For now choose first used camera.
            Node cameraNode = scene.getCameraNodes().get(0);
            matrices[Matrices.PROJECTION.index] = cameraNode.getCamera().getProjectionMatrix();
        }
    }

    protected void setView(Node node, Camera camera, float[][] matrices) {
        matrices[Matrices.VIEW.index] = camera.concatCameraMatrix(node, matrices[Matrices.MODEL.index]);
    }

    /**
     * Renders the node and then childnodes by calling {@link #renderNodes(GLTF, Node[])}
     * This will render the Node using depth first search
     * 
     * @param gles
     * @param glTF
     * @param node
     * @param matrices
     */
    protected void renderNode(GLES20Wrapper gles, GLTF glTF, Node node, float[][] matrices)
            throws GLException {
        pushMatrix(modelMatrixStack, matrices[Matrices.MODEL.index]);
        // Check for camera
        Camera c = node.getCamera();
        if (c != null) {
            setView(node, c, matrices);
        }
        float[] nodeMatrix = node.concatMatrix(matrices[Matrices.MODEL.index]);
        matrices[Matrices.MODEL.index] = nodeMatrix;
        renderMesh(gles, glTF, node.getMesh(), matrices);
        // Render children.
        renderNodes(gles, glTF, node.getChildren(), matrices);
        matrices[Matrices.MODEL.index] = popMatrix(modelMatrixStack);
    }

    protected void renderMesh(GLES20Wrapper gles, GLTF glTF, Mesh mesh, float[][] matrices) throws GLException {
        if (mesh != null) {
            Primitive[] primitives = mesh.getPrimitives();
            if (primitives != null) {
                for (Primitive p : primitives) {
                    renderPrimitive(gles, glTF, p, matrices);
                }
            }
        }
    }

    /**
     * Renders the primitive
     * 
     * @param gles
     * @param glTF
     * @param program
     * @param primitive
     * @param matrices
     * @throws GLException
     */
    protected void renderPrimitive(GLES20Wrapper gles, GLTF glTF, Primitive primitive, float[][] matrices)
            throws GLException {
        GLTFShaderProgram program = (GLTFShaderProgram) getProgram(gles, primitive, currentPass);
        if (currentProgram != program.getProgram()) {
            currentProgram = program.getProgram();
            gles.glUseProgram(currentProgram);
            GLUtils.handleError(gles, "glUseProgram " + currentProgram);
            // TODO - is this the best place for this check - remember, this should only be done in debug cases.
            if (Environment.getInstance().isProperty(com.nucleus.common.Environment.Property.DEBUG, false)) {
                program.validateProgram(gles);
            }
        }
        // Can be optimized to update uniforms under the following conditions:
        // The program has changed OR the matrices have changed, ie another parent node.
        program.updateUniforms(gles, matrices);
        Material material = primitive.getMaterial();
        if (material != null) {
            Texture texture = glTF.getTexture(material.getPbrMetallicRoughness());
            if (texture != null) {
                TextureUtils.prepareTexture(gles, texture, glTF.getTexCoord(material.getPbrMetallicRoughness()));
            }
        }
        Accessor indices = glTF.getAccessor(primitive.getIndicesIndex());
        program.updatePrimitiveUniforms(gles, primitive);
        if (indices != null) {
            // Indexed mode - use glDrawElements
            BufferView indicesView = indices.getBufferView();
            Buffer buffer = indicesView.getBuffer();
            gles.glVertexAttribPointer(glTF, program, primitive);
            if (buffer.getBufferName() > 0) {
                gles.glBindBuffer(indicesView.getTarget().value, buffer.getBufferName());
                gles.glDrawElements(primitive.glMode, indices.getCount(), indices.getComponentType().value,
                        indices.getByteOffset() + indicesView.getByteOffset());
            } else {
                gles.glDrawElements(primitive.glMode, indices.getCount(), indices.getComponentType().value,
                        indicesView.getBuffer().getBuffer()
                                .position(indices.getByteOffset() + indicesView.getByteOffset()));
            }
            timeKeeper.addDrawElements(indices.getCount(), primitive.getAccessor(Attributes.POSITION).getCount());
        } else {
            // Non indexed mode - use glDrawArrays
            throw new IllegalArgumentException("Not implemented yet");
        }
    }

    /**
     * Renders an array of nodes - each node will be rendered by calling {@link #renderNode(GLTF, Node)}
     * This means rendering will be depth first.
     * 
     * @param gles
     * @param glTF
     * @param children
     * @param matrices
     */
    protected void renderNodes(GLES20Wrapper gles, GLTF glTF, Node[] children,
            float[][] matrices) throws GLException {
        if (children != null && children.length > 0) {
            for (Node n : children) {
                renderNode(gles, glTF, n, matrices);
            }
        }
    }

    /**
     * 
     * @param gles
     * @param primitive
     * @param pass The currently defined pass
     * @return
     */
    protected ShaderProgram getProgram(GLES20Wrapper gles, Primitive primitive, Pass pass) {
        ShaderProgram program = primitive.getProgram();
        if (program == null) {
            throw new IllegalArgumentException("No program for primitive ");
        }
        return program.getProgram(gles, pass, program.getShading());
    }

}
