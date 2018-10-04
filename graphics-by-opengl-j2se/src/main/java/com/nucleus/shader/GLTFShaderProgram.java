package com.nucleus.shader;

import com.nucleus.light.GlobalLight;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.opengl.GLException;
import com.nucleus.renderer.Pass;
import com.nucleus.scene.gltf.AccessorDictionary;
import com.nucleus.scene.gltf.Material;
import com.nucleus.scene.gltf.Mesh;
import com.nucleus.scene.gltf.Primitive;
import com.nucleus.scene.gltf.Primitive.Attributes;
import com.nucleus.texturing.Texture2D.Shading;

public class GLTFShaderProgram extends GenericShaderProgram {

    transient protected ShaderVariable color0Uniform;
    transient protected ShaderVariable light0Uniform;

    /**
     * The dictionary created from linked program
     */
    protected AccessorDictionary<String> accessorDictionary = new AccessorDictionary<>();

    protected Mesh[] meshes;

    public GLTFShaderProgram(Mesh[] meshes, Pass pass, Shading shading, String category, ProgramType shaders) {
        super(pass, shading, category, shaders);
        this.meshes = meshes;
    }

    /**
     * Returns the program accessor dictionary, this is created after linking the program and stores accessors
     * using shader variable name.
     * 
     * @return
     */
    public AccessorDictionary<String> getAccessorDictionary() {
        return accessorDictionary;
    }

    @Override
    public void updateUniformData(float[] destinationUniform) {
        if (color0Uniform == null) {
            color0Uniform = getUniformByName(Attributes.COLOR_0.name());
            light0Uniform = getUniformByName(Attributes._LIGHT_0.name());
        }
        setUniformData(light0Uniform, GlobalLight.getInstance().getLightPosition(), 0);
    }

    public void updatePrimitiveUniforms(GLES20Wrapper gles, Primitive primitive) throws GLException {
        Material material = primitive.getMaterial();
        setUniformData(color0Uniform, material.getPbrMetallicRoughness().getBaseColorFactor(), 0);
        uploadUniforms(gles, uniforms, activeUniforms);
    }

}
