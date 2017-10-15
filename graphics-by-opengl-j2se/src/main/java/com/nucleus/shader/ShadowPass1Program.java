package com.nucleus.shader;

import com.nucleus.geometry.AttributeUpdater.Property;
import com.nucleus.SimpleLogger;
import com.nucleus.geometry.Mesh;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.opengl.GLException;
import com.nucleus.vecmath.Matrix;

/**
 * First shadow pass, render all geometry using vertex shader and light matrix then output distance to light in depth buffer
 *
 */
public class ShadowPass1Program extends ShaderProgram {

    /**
     * Name of this shader - TODO where should this be defined?
     */
    protected static final String VERTEX_NAME = "transform";
    protected static final String FRAGMENT_NAME = "shadow1";
    
    
    public ShadowPass1Program() {
        super(ShaderVariables.values());
        vertexShaderName = PROGRAM_DIRECTORY + VERTEX_NAME + VERTEX + SHADER_SOURCE_SUFFIX;
        fragmentShaderName = PROGRAM_DIRECTORY + FRAGMENT_NAME + FRAGMENT + SHADER_SOURCE_SUFFIX;
    }

    @Override
    public void bindUniforms(GLES20Wrapper gles, float[] modelviewMatrix, float[] projectionMatrix, Mesh mesh)
            throws GLException {
        
        //Modelview is facing into screen.
        float[] lightPOV = new float[16];
        float[] result = new float[16];
        Matrix.setIdentityM(lightPOV, 0);
        float[] lightVector = globalLight.getLightVector();
        Matrix.setRotateEulerM(result, 0, lightVector[0], lightVector[1], lightVector[2]);
        Matrix.orthoM(lightPOV, 0, -0.8889f,0.8889f,-0.5f,0.5f,-10f,10f);
        Matrix.mul4(result, lightPOV);
        // Refresh the uniform matrix
        // TODO prefetch the offsets for the shader variables and store in array.
        System.arraycopy(modelviewMatrix, 0, mesh.getUniforms(), shaderVariables[ShaderVariables.uMVMatrix.index].getOffset(),
                Matrix.MATRIX_ELEMENTS);
        System.arraycopy(result, 0, mesh.getUniforms(),
                shaderVariables[ShaderVariables.uProjectionMatrix.index].getOffset(),
                Matrix.MATRIX_ELEMENTS);
        bindUniforms(gles, uniforms, mesh.getUniforms());
        
    }

    @Override
    public int getPropertyOffset(Property property) {
        ShaderVariable v = null;
        switch (property) {
        case TRANSLATE:
            v = shaderVariables[ShaderVariables.aTranslate.index];
            break;
        case ROTATE:
            v = shaderVariables[ShaderVariables.aRotate.index];
            break;
        case SCALE:
            v = shaderVariables[ShaderVariables.aScale.index];
            break;
        default:
        }
        if (v != null) {
            return v.getOffset();
        } else {
            SimpleLogger.d(getClass(), "No ShaderVariable for " + property);
            
        }
        return -1;
    }

}
