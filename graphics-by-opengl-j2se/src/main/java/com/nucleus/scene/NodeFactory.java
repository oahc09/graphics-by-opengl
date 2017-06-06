package com.nucleus.scene;

import java.io.IOException;

import com.nucleus.geometry.MeshFactory;
import com.nucleus.io.ResourcesData;
import com.nucleus.renderer.NucleusRenderer;

/**
 * Factory methods for node creation, implements support for new nodes in sublcasses.
 * This is used by the scene factory implementations to create nodes.
 * If the node uses Mesh(es) these must be created, allocating buffers, programs and fetching textures as needed.
 * It shall be possible to render/process the returned node.
 * 
 * @author Richard Sahlin
 *
 */
public interface NodeFactory {

    /**
     * Creates a new instance of the source node, allocating buffers, programs and fetching textures as needed.
     * If implementations do not recognize the node to be created they must call super
     * {@link #create(NucleusRenderer, Node, RootNode)}
     * TODO Shall this method take the rootnode as property and always set in the created node?
     * 
     * @param renderer
     * @param meshFactory The mesh factory to use when creating mesh
     * @param resources The scene data
     * @param source The source node, the returned node shall be the same type
     * @param root The rootnode of the created node
     * @return A new instance of the source node, ready to be rendered/processed
     * @throws IOException If there is an error creating the Node, could be due to problem reading resource or compiling
     * shaders
     */
    public Node create(NucleusRenderer renderer, MeshFactory meshFactory, ResourcesData resource, Node source,
            RootNode root)
            throws NodeException;

    /**
     * Creates the child nodes in from the source node, adding the created children to the parent node.
     * The rootnode of the parent must be copied
     * 
     * @param resources
     * @param source The source node containing the children to create
     * @param parent The destination where the created child nodes will be added.
     * @param root The rootnode of the created node
     * shaders
     */
    public void createChildNodes(NucleusRenderer renderer, MeshFactory meshFactory, ResourcesData resources,
            Node source, Node parent) throws NodeException;


}
