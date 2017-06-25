package com.nucleus.assets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;

import com.nucleus.io.ExternalReference;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TextureFactory;
import com.nucleus.texturing.TextureType;

/**
 * Loading and unloading assets, mainly textures.
 * It should normally handle resources that are loaded separately from the main json file using an
 * {@link ExternalReference} eg data that does not fit within the main file.
 * 
 * 
 * @author Richard Sahlin
 *
 */
public class AssetManager {
    protected static AssetManager assetManager = null;
    private final static String NO_TEXTURE_SOURCE_ERROR = "No texture source for id: ";
    /**
     * Store textures using the source image name.
     */
    private HashMap<String, Texture2D> textures = new HashMap<String, Texture2D>();
    /**
     * Use to convert from object id (texture reference) to name of source (file)
     */
    private Hashtable<String, ExternalReference> sourceNames = new Hashtable<String, ExternalReference>();

    /**
     * Hide the constructor
     */
    private AssetManager() {
    }

    public static AssetManager getInstance() {
        if (assetManager == null) {
            assetManager = new AssetManager();
        }
        return assetManager;
    }

    public interface Asset {
        /**
         * Loads an instance of the asset into memory, after this method returns the asset SHALL be ready to be used.
         * 
         * @param source The source of the object, it is up to implementations to decide what sources to support.
         * For images the normal usecase is InputStream
         * 
         * @return The id of the asset, this is a counter starting at 1 and increasing.
         * @throws IOException If there is an exception reading from the stream.
         */
        public int load(Object source) throws IOException;

        /**
         * Releases the asset and all allocated memory, after this method returns all memory and objects shall be
         * released.
         */
        public void destroy();
    }

    /**
     * Returns the texture, if the texture has not been loaded it will be loaded and stored in the assetmanager.
     * If already has been loaded the loaded instance will be returned.
     * Treat textures as immutable object
     * 
     * @param renderer
     * @param ref
     * @return The texture
     * @throws IOException
     */
    public Texture2D getTexture(NucleusRenderer renderer, ExternalReference ref) throws IOException {
        return getTexture(renderer, TextureFactory.createTexture(ref));
    }

    /**
     * Returns the texture, if the texture has not been loaded it will be loaded and stored in the assetmanager.
     * If already has been loaded the loaded instance will be returned.
     * Treat textures as immutable object
     * 
     * @param renderer
     * @param source The external ref is used to load a texture
     * @return The texture specifying the external reference to the texture to load and return.
     * @throws IOException
     */
    protected Texture2D getTexture(NucleusRenderer renderer, Texture2D source) throws IOException {
        /**
         * External ref for untextured needs to be "" so it can be store and fetched.
         */
        if (source.getTextureType() == TextureType.Untextured) {
            source.setExternalReference(new ExternalReference(""));
        }
        ExternalReference ref = source.getExternalReference();
        String refSource = ref.getSource();
        Texture2D texture = textures.get(refSource);
        if (texture == null) {
            // Texture not loaded
            texture = TextureFactory.createTexture(renderer.getGLES(), renderer.getImageFactory(), source);
            textures.put(refSource, texture);
        }
        return texture;
    }

    /**
     * Sets the external reference for the object id
     * @param id
     * @param externalReference
     * @throws IllegalArgumentException If a reference with the specified Id already has been set
     */
    private void setExternalReference(String id, ExternalReference externalReference) {
        if (sourceNames.containsKey(id)) {
            throw new IllegalArgumentException("Id already added as external reference:" + id);
        }
        sourceNames.put(id, externalReference);

    }

    /**
     * Returns the source reference for the object with the specified id, this can be used to fetch object
     * source name from a texture reference/id.
     * If the source reference cannot be found it is considered an error and an exception is thrown.
     * 
     * @param Id
     * @return The source (file) reference or null if not found.
     * @throws IllegalArgumentException If a texture source could not be found for the Id.
     */
    public ExternalReference getSourceReference(String Id) {
        ExternalReference ref = sourceNames.get(Id);
        if (ref == null) {
            // Horrendous error - cannot export data!
            // TODO Is there a way to recover?
            throw new IllegalArgumentException(NO_TEXTURE_SOURCE_ERROR + Id);
        }
        return ref;
    }
}
