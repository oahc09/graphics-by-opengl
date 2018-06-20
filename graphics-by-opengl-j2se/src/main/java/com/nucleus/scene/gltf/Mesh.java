package com.nucleus.scene.gltf;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * The Mesh as it is loaded using the glTF format.
 * 
 * mesh
 * A set of primitives to be rendered. A node can contain one mesh. A node's transform places the mesh in the scene.
 * 
 * Properties
 * 
 * Type Description Required
 * primitives primitive [1-*] An array of primitives, each defining geometry to be rendered with a material. ✅ Yes
 * weights number [1-*] Array of weights to be applied to the Morph Targets. No
 * name string The user-defined name of this object. No
 * extensions object Dictionary object with extension-specific objects. No
 * extras any Application-specific data. No
 * 
 *
 */
public class Mesh {

    private static final String PRIMITIVES = "primitives";
    private static final String WEIGHTS = "weights";
    private static final String NAME = "name";

    @SerializedName(PRIMITIVES)
    private Primitive[] primitives;
    @SerializedName(WEIGHTS)
    private int[] weights;
    @SerializedName(NAME)
    private String name;

    public Primitive[] getPrimitives() {
        return primitives;
    }

    public int[] getWeights() {
        return weights;
    }

    public String getName() {
        return name;
    }

}