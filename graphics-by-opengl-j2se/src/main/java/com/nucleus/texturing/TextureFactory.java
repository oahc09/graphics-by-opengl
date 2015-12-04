package com.nucleus.texturing;

import com.nucleus.io.ExternalReference;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.opengl.GLESWrapper.GLES20;
import com.nucleus.opengl.GLException;

/**
 * Used to create texture objects, constructor data is abstracted in a separate class to make it easy to de-couple file
 * loading.
 * 
 * @author Richard Sahlin
 *
 */
public class TextureFactory {

    /**
     * Creates a texture for the specified image, the texture will be scaled according to target resolution (for the
     * image) and current height of screen.
     * The texture will be uploaded to GL using the specified texture object name, if several mip-map levels are
     * supplied they will be used.
     * If the device current resolution is lower than the texture target resolution then a lower mip-map level is used.
     * 
     * @return
     */
    public static Texture2D createTexture(GLES20Wrapper gles, ImageFactory imageFactory, Texture2DData source) {
        Texture2D texture = null;
        if (source instanceof TiledTexture2DData) {
            TiledTexture2DData tiledSource = (TiledTexture2DData) source;
            texture = new TiledTexture2D(source.getId(), source.getResolution(), new TextureParameter(
                    source.getTexparameter()), tiledSource.getTile());
        } else {
            texture = new Texture2D(source.getId(), source.getResolution(), new TextureParameter(
                    source.getTexparameter()));
        }
        prepareTexture(gles, texture, imageFactory, source.getExternalReference(), source.getMipmap());
        return texture;

    }

    /**
     * Internal method to create a texture based on the texture setup source, the texture will be set with data from
     * texture setup and uploaded to GL.
     * 
     * @param gles
     * @param texture The texture destination
     * @param imageFactory
     * @param textureSource The texture image to load
     * @param mipmaps Number of mipmap levels
     */
    private static void prepareTexture(GLES20Wrapper gles, Texture2D texture, ImageFactory imageFactory,
            ExternalReference textureSource, int mipmaps) {
        int[] textures = new int[1];
        gles.glGenTextures(1, textures, 0);

        int textureID = textures[0];
        Image[] textureImg = TextureUtils
                .loadTextureMIPMAP(imageFactory, textureSource.getSource(), 1, mipmaps);

        try {
            TextureUtils.uploadTextures(gles, GLES20.GL_TEXTURE0, textureID, textureImg);
        } catch (GLException e) {
            throw new IllegalArgumentException(e);
        }
        texture.setup(textureID, textureImg);
    }
}
