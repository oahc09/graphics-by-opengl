package com.nucleus.scene;

import com.nucleus.camera.ViewFrustum;
import com.nucleus.common.Type;
import com.nucleus.convolution.ConvolutionProgram;
import com.nucleus.geometry.Mesh;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.shader.ShaderProgram;

/**
 * Implementation of RootNode, this can be used to construct simple nodetrees
 * The standard way of creating a scene is to load from file, sometimes a simple tree is needed and in those
 * cases the builder can be used.
 *
 */
public class BaseRootNode extends RootNode {

    public static class Builder {

        ViewFrustum viewFrustum;
        Type<Node> nodeType;
        ShaderProgram program;
        NodeFactory nodeFactory;
        Mesh.Builder builder;
        NucleusRenderer renderer;

        public Builder(NucleusRenderer renderer) {
            if (renderer == null) {
                throw new IllegalArgumentException("Renderer may not be null");
            }
            this.renderer = renderer;
        }

        public Builder setNodeFactory(NodeFactory nodeFactory) {
            this.nodeFactory = nodeFactory;
            return this;
        }

        public Builder setMeshBuilder(Mesh.Builder meshBuilder) {
            this.builder = meshBuilder;
            return this;
        }

        public Builder setViewFrustum(ViewFrustum viewFrustum) {
            this.viewFrustum = viewFrustum;
            return this;
        }

        /**
         * Sets the node (class) to create
         * 
         * @param classResolver Type and class for the node.
         * @return
         * @throws IllegalArgumentException If nodeclass is not instance of Node, or nodeClass can not be instantiated
         */
        public Builder setNode(Type<Node> classResolver) {
            this.nodeType = classResolver;
            return this;
        }

        public Builder setProgram(ShaderProgram program) {
            this.program = program;
            return this;
        }

        public RootNode create() throws NodeException {
            validate();
            BaseRootNode root = new BaseRootNode();
            Node created = nodeFactory.create(renderer, new ConvolutionProgram(), builder, nodeType, root);
            ViewFrustum vf = new ViewFrustum();
            vf.setOrthoProjection(-0.5f, 0.5f, -0.5f, 0.5f, 0, 10);
            created.setViewFrustum(vf);
            root.setScene(created);
            return root;
        }

        private void validate() {
            if (builder == null || nodeType == null || nodeFactory == null) {
                throw new IllegalArgumentException(
                        "Parameter not set: " + builder + ", " + nodeType + ", " + nodeFactory);
            }
        }

    }


    @Override
    public RootNode createInstance() {
        BaseRootNode root = new BaseRootNode();
        return root;
    }

}
