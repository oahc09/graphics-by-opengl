package com.nucleus.scene.gltf;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.SerializedName;
import com.nucleus.SimpleLogger;
import com.nucleus.opengl.GLESWrapper.GLES20;
import com.nucleus.scene.gltf.Accessor.ComponentType;
import com.nucleus.scene.gltf.Accessor.Type;
import com.nucleus.scene.gltf.BufferView.Target;
import com.nucleus.scene.gltf.GLTF.GLTFException;
import com.nucleus.scene.gltf.GLTF.RuntimeResolver;
import com.nucleus.shader.GLTFShaderProgram;
import com.nucleus.vecmath.Vec2;
import com.nucleus.vecmath.Vec3;

/**
 * The Primitive as it is loaded using the glTF format.
 * 
 * primitive
 * Geometry to be rendered with the given material.
 * 
 * Related WebGL functions: drawElements() and drawArrays()
 * 
 * Properties
 * 
 * Type Description Required
 * attributes object A dictionary object, where each key corresponds to mesh attribute semantic and each value is the
 * index of the accessor containing attribute's data. ✅ Yes
 * indices integer The index of the accessor that contains the indices. No
 * material integer The index of the material to apply to this primitive when rendering. No
 * mode integer The type of primitives to render. No, default: 4
 * targets object [1-*] An array of Morph Targets, each Morph Target is a dictionary mapping attributes (only POSITION,
 * NORMAL, and TANGENT supported) to their deviations in the Morph Target. No
 * extensions object Dictionary object with extension-specific objects. No
 * extras any Application-specific data. No
 *
 */
public class Primitive implements RuntimeResolver {

    /**
     * Name of the tangent/bitangent buffer
     */
    public static final String TANGENT_BITANGENT = "TangentBitangent";
    public static final String BITANGENT = "Bitangent";

    public abstract class BufferToArray<T, U> {
        public abstract U copyBuffer(T buffer);
    }

    public class FloatBufferToArray extends BufferToArray<FloatBuffer, float[]> {
        @Override
        public float[] copyBuffer(FloatBuffer buffer) {
            return null;
        }
    }

    public class Triangles {

        float[] verticeArray;
        float[] uvArray;
        float[] normalArray;
        float[] tangentArray;
        short[] indexArray;

        /**
         * Creates the array buffers needed to calculate normals/tangents/bitanges
         */
        public void createBuffers(GLTF gltf) {
            verticeArray = createFloatArray(Attributes.POSITION);
            uvArray = createFloatArray(Attributes.TEXCOORD_0);
            tangentArray = createFloatArray(Attributes.TANGENT);
            indexArray = createIndexArray();
            normalArray = createFloatArray(Attributes.NORMAL);
            if (normalArray == null) {
                normalArray = createNormals();
                BufferView normals = gltf.createBufferView(BITANGENT,
                        (normalArray.length << 2) * ComponentType.FLOAT.size, 0,
                        Type.VEC3.size * ComponentType.FLOAT.size, Target.ARRAY_BUFFER);
                Buffer buffer = normals.getBuffer();
                buffer.put(normalArray, 0);
                Accessor normalAccessor = new Accessor(normals, 0, ComponentType.FLOAT, normalArray.length, Type.VEC3);
                accessorList.add(normalAccessor);
                attributeList.add(Attributes.NORMAL);

            }
        }

        private float[] createNormals() {
            float[] normals = new float[verticeArray.length];
            // Iterate each triangle
            float[] normal = new float[3];
            float[] vec = new float[9];
            int index = 0;
            int v1Index = 0;
            int v2Index = 0;
            int v3Index = 0;
            while (index < indexArray.length) {
                v1Index = indexArray[index++] * 3;
                v2Index = indexArray[index++] * 3;
                v3Index = indexArray[index++] * 3;
                Vec3.toVector(verticeArray, v1Index, verticeArray, v2Index, vec, 0);
                Vec3.toVector(verticeArray, v2Index, verticeArray, v3Index, vec, 3);
                Vec3.toVector(verticeArray, v3Index, verticeArray, v1Index, vec, 6);
                Vec3.normalize(Vec3.cross3(vec, 6, 0, normal, 0), 0);
                Vec3.normalize(Vec3.add(normal, 0, normals, v1Index, normals, v1Index), v1Index);

                Vec3.normalize(Vec3.cross3(vec, 0, 3, normal, 0), 0);
                Vec3.normalize(Vec3.add(normal, 0, normals, v2Index, normals, v2Index), v2Index);

                Vec3.normalize(Vec3.cross3(vec, 3, 6, normal, 0), 0);
                Vec3.normalize(Vec3.add(normal, 0, normals, v3Index, normals, v3Index), v3Index);

            }
            return normals;
        }

        private float[] createFloatArray(Attributes attribute) {
            Accessor accessor = getAccessor(attribute);
            if (accessor != null) {
                switch (accessor.getComponentType()) {
                    case FLOAT:
                        float[] arrayCopy = new float[accessor.getCount() * accessor.getType().size];
                        accessor.copy(arrayCopy, 0);
                        return arrayCopy;
                    default:
                        throw new IllegalArgumentException();

                }
            }
            return null;
        }

        private short[] createIndexArray() {
            Accessor indices = getIndices();
            if (indices != null) {
                short[] indicesCopy = new short[indices.getCount() * indices.getType().size];
                indices.copy(indicesCopy, 0);
                return indicesCopy;
            }
            return null;
        }

        public float[][] calculateTangentBiTangent() {
            float[][] tangents = new float[2][verticeArray.length];
            float[][] output = new float[2][(verticeArray.length / 3) * 4];
            int verticeSize = 3;
            float[] vec1 = new float[3];
            float[] vec2 = new float[3];
            float[] st1 = new float[] { 1, 1 };
            float[] st2 = new float[] { 1, 1 };
            float[] sdir = new float[3];
            float[] tdir = new float[3];
            float[] cross = new float[3];
            float[] t = new float[4];
            int uvSize = 2;

            for (int i = 0; i < indexArray.length; i += 3) {
                int index0 = indexArray[i];
                int index1 = indexArray[i + 1];
                int index2 = indexArray[i + 2];

                int v0Index = index0 * verticeSize;
                int v1Index = index1 * verticeSize;
                int v2Index = index2 * verticeSize;

                int uv0Index = index0 * uvSize;
                int uv1Index = index1 * uvSize;
                int uv2Index = index2 * uvSize;

                Vec3.toVector(verticeArray, v0Index, verticeArray, v1Index, vec1, 0);
                Vec3.toVector(verticeArray, v0Index, verticeArray, v2Index, vec2, 0);

                float reciprocal = 1f;
                if (uvArray != null) {
                    Vec2.toVector(uvArray, uv0Index, uvArray, uv1Index, st1, 0);
                    Vec2.toVector(uvArray, uv0Index, uvArray, uv2Index, st2, 0);
                    reciprocal = 1.0f / (st1[0] * st2[1] - st1[1] * st2[0]);
                    sdir[0] = (st2[1] * vec1[0] - st1[1] * vec2[0]) * reciprocal;
                    sdir[1] = (st2[1] * vec1[1] - st1[1] * vec2[1]) * reciprocal;
                    sdir[2] = (st2[1] * vec1[2] - st1[1] * vec2[2]) * reciprocal;

                    tdir[0] = (st1[0] * vec2[0] - st2[0] * vec1[0]) * reciprocal;
                    tdir[1] = (st1[0] * vec2[1] - st2[0] * vec1[1]) * reciprocal;
                    tdir[2] = (st1[0] * vec2[2] - st2[0] * vec1[2]) * reciprocal;
                } else {
                    sdir[0] = (st2[1] * vec1[0] - st1[1] * vec2[0]);
                    sdir[1] = (st2[1] * vec1[1] - st1[1] * vec2[1]);
                    sdir[2] = (st2[1] * vec1[2] - st1[1] * vec2[2]);

                    tdir[0] = (st1[0] * vec2[0] - st2[0] * vec1[0]);
                    tdir[1] = (st1[0] * vec2[1] - st2[0] * vec1[1]);
                    tdir[2] = (st1[0] * vec2[2] - st2[0] * vec1[2]);

                }

                Vec3.add(tangents[0], v0Index, sdir, 0, tangents[0], v0Index);
                Vec3.add(tangents[0], v1Index, sdir, 0, tangents[0], v1Index);
                Vec3.add(tangents[0], v2Index, sdir, 0, tangents[0], v2Index);

                Vec3.add(tangents[1], v0Index, tdir, 0, tangents[1], v0Index);
                Vec3.add(tangents[1], v1Index, tdir, 0, tangents[1], v1Index);
                Vec3.add(tangents[1], v2Index, tdir, 0, tangents[1], v2Index);
            }

            int outputIndex = 0;
            for (int i = 0; i < tangents[0].length; i += 3) {
                // const Vector3D& n = normal[a];
                // const Vector3D& t = tan1[a];
                // Gram-Schmidt orthogonalize
                // result[0]
                // tangent[a] = (t - n * Dot(n, t)).Normalize();
                Vec3.mul(normalArray, i, Vec3.dot(normalArray, i, tangents[0], i), t, 0);
                Vec3.subtract(tangents[0], i, t, 0, t, 0);
                Vec3.normalize(t, 0);
                Vec3.set(t, 0, output[0], outputIndex);
                // Calculate handedness
                // tangent[a].w = (Dot(Cross(n, t), tan2[a]) < 0.0F) ? -1.0F : 1.0F;
                Vec3.cross(normalArray, i, tangents[0], i, t, 0);
                float tw = Vec3.dot(t, 0, tangents[1], i) < 0f ? -1 : 1;
                // Calculate bitangent
                // bitangent vector B is then given by B = (N x T) * Tw.
                Vec3.cross(normalArray, i, output[0], i, cross, 0);
                Vec3.mul(cross, 0, tw, output[1], outputIndex);
                output[0][outputIndex + 3] = tw;
                output[1][outputIndex + 3] = tw;
                outputIndex += 4;
            }
            SimpleLogger.d(getClass(), "Created TANGENTS and BITANGENTs for " + tangents[0].length / 3 + " vertices");
            return output;
        }

        /**
         * Creates the bitangent
         * Implementation note:
         * When normals and tangents are specified, client implementations should compute the bitangent
         * by taking the cross product of the normal and tangent xyz vectors and multiplying against the w component
         * of the tangent: bitangent = cross(normal, tangent.xyz) * tangent.w
         * 
         * @return Bitangents in Vec4 format
         */
        public float[] createBiTangent() {
            float[] t = new float[4];
            Accessor position = getAccessor(Attributes.POSITION);
            final int count = position.getCount();
            float[] output = new float[count * 4];
            int outputIndex = 0;
            int nIndex = 0;
            int tIndex = 0;
            for (int i = 0; i < count; i++) {
                Vec3.cross(normalArray, nIndex, tangentArray, tIndex, t, 0);
                Vec3.mul(t, 0, tangentArray[tIndex + 3], output, outputIndex);
                outputIndex += 4;
                nIndex += 3;
                tIndex += 4;
            }
            SimpleLogger.d(getClass(), "Created BITANGENTs for " + count + " vertices");
            return output;

        }

    }

    private static final int DEFAULT_MODE = 4;

    private static final String ATTRIBUTES = "attributes";
    private static final String INDICES = "indices";
    private static final String MATERIAL = "material";
    private static final String MODE = "mode";
    private static final String TARGETS = "targets";

    public enum Attributes {
        POSITION(),
        NORMAL(),
        TANGENT(),
        BITANGENT(),
        TEXCOORD_0(),
        TEXCOORD_1(),
        TEXCOORD_2(),
        TEXCOORD_3(),
        TEXCOORD_4(),
        TEXCOORD_5(),
        COLOR_0(),
        COLOR_1(),
        WEIGHTS_0(),
        WEIGHTS_1(),
        /**
         * Custom Attributes
         */
        _ROTATE(),
        _SCALE(),
        _TRANSLATE(),
        _FRAME(),
        _ALBEDO(),
        _EMISSIVE(),
        _BOUNDS(),
        _PBRDATA(),
        _LIGHT_0(),
        _TEXCOORDNORMAL();

        private final static Attributes[] TEXCOORDS = new Attributes[] { TEXCOORD_0, TEXCOORD_1, TEXCOORD_2, TEXCOORD_3,
                TEXCOORD_4, TEXCOORD_5 };

        /**
         * Returns the texture coord attribute for the specified texture coord, ie 0 will return TEXCOORD_0
         * 
         * @param texCoord
         * @return
         */
        public final static Attributes getTextureCoord(int texCoord) {
            return TEXCOORDS[texCoord];
        }

    }

    public enum Mode {
        POINTS(0, GLES20.GL_POINTS),
        LINES(1, GLES20.GL_LINES),
        LINE_LOOP(2, GLES20.GL_LINE_LOOP),
        LINE_STRIP(3, GLES20.GL_LINE_STRIP),
        TRIANGLES(4, GLES20.GL_TRIANGLES),
        TRIANGLE_STRIP(5, GLES20.GL_TRIANGLE_STRIP),
        TRIANGLE_FAN(6, GLES20.GL_TRIANGLE_FAN);

        public final int index;
        /**
         * The OpenGL value
         */
        public final int value;

        private Mode(int index, int value) {
            this.index = index;
            this.value = value;
        }

        public static final Mode getMode(int index) {
            for (Mode m : values()) {
                if (m.index == index) {
                    return m;
                }
            }
            return null;
        }

    }

    @SerializedName(ATTRIBUTES)
    private HashMap<Attributes, Integer> attributes;
    @SerializedName(INDICES)
    private int indicesIndex = -1;
    @SerializedName(MATERIAL)
    private int material = -1;
    /**
     * Allowed values:
     * 0 POINTS
     * 1 LINES
     * 2 LINE_LOOP
     * 3 LINE_STRIP
     * 4 TRIANGLES
     * 5 TRIANGLE_STRIP
     * 6 TRIANGLE_FAN
     */
    @SerializedName(MODE)
    private int modeIndex = DEFAULT_MODE;

    transient private ArrayList<Accessor> accessorList;
    transient private ArrayList<Attributes> attributeList;
    transient private ArrayList<Buffer> bufferList;
    transient private Material materialRef;
    /**
     * Program to use when rendering this primitive
     */
    @Deprecated
    transient private GLTFShaderProgram program;
    transient private Accessor indices;
    transient private Mode mode;

    public Primitive() {

    }

    /**
     * Creates a new instance of a primitive with references to the specified values.
     * Objects are NOT copied.
     * 
     * @param attributeList
     * @param accessorList
     * @param indices
     * @param material
     * @param mode
     */
    public Primitive(ArrayList<Attributes> attributeList, ArrayList<Accessor> accessorList, Accessor indices,
            Material material, Mode mode) {
        this.attributeList = attributeList;
        this.accessorList = accessorList;
        this.indices = indices;
        this.materialRef = material;
        this.mode = mode;
    }

    /**
     * Returns the dictionary (HashMap) with Attributes
     * 
     * @return
     */
    public HashMap<Attributes, Integer> getAttributes() {
        return attributes;
    }

    public ArrayList<Accessor> getAccessorArray() {
        return accessorList;
    }

    /**
     * Returns the array containing the Attributes that are defined in this primitive
     * 
     * @return
     */
    public ArrayList<Attributes> getAttributesArray() {
        return attributeList;
    }

    /**
     * Returns the array containing the {@link Buffer} objects in this primmitive
     * 
     * @return
     */
    public ArrayList<Buffer> getBufferArray() {
        return bufferList;
    }

    /**
     * Returns the Accessor for the attribute, or null if not defined.
     * 
     * @param glTF
     * @param attribute
     * @return
     */
    @Deprecated
    public Accessor getAccessor(GLTF glTF, Attributes attribute) {
        Integer index = attributes.get(attribute);
        if (index != null) {
            return glTF.getAccessor(index);
        }
        return null;
    }

    /**
     * Returns the Accessor for the attribute if defined, otherwise null
     * 
     * @param attribute
     * @return
     */
    public Accessor getAccessor(Attributes attribute) {
        if (attributeList != null) {
            for (int i = 0; i < attributeList.size(); i++) {
                if (attributeList.get(i) == attribute) {
                    return accessorList.get(i);
                }
            }
        }
        return null;
    }

    /**
     * Returns the index of the accessor that contains the indices.
     * 
     * @return Index of indices or -1 if no indices.
     */
    public int getIndicesIndex() {
        return indicesIndex;
    }

    /**
     * Returns the accessor containing the indices, if this primitive is not indexed null is returned.
     * 
     * @return The indices, or null if drawArrays should be used.
     */
    public Accessor getIndices() {
        return indices;
    }

    /**
     * Sets the index of the accessor that contains the indices
     * 
     * @param indices
     */
    public void setIndicesIndex(int indices) {
        this.indicesIndex = indices;
    }

    /**
     * Returns the index of the material to apply when rendering this primitive
     * 
     * @return
     */
    public int getMaterialIndex() {
        return material;
    }

    public Material getMaterial() {
        return materialRef;
    }

    public Mode getMode() {
        return mode;
    }

    @Override
    public void resolve(GLTF asset) throws GLTFException {
        mode = Mode.getMode(modeIndex);
        createAttributeList(asset);
        if (material >= 0) {
            this.materialRef = asset.getMaterials()[material];
        }
        indices = asset.getAccessor(indicesIndex);
    }

    private void createAttributeList(GLTF asset) {
        if (attributes != null && attributes.size() > 0) {
            Set<Buffer> bufferSet = new HashSet<>();
            accessorList = new ArrayList<>();
            attributeList = new ArrayList<>();
            for (Attributes a : attributes.keySet()) {
                attributeList.add(a);
                Accessor accessor = asset.getAccessor(attributes.get(a));
                accessorList.add(accessor);
                bufferSet.add(asset.getBuffer(accessor));
            }
            bufferList = new ArrayList<>();
            for (Buffer b : bufferSet) {
                bufferList.add(b);
            }
        }
    }

    /**
     * Builds the Normal/Tangent/Binormal buffers as needed
     * Must be called after buffers are loaded so that the INDICES, POSITION and NORMAL (optional) buffers are
     * available.
     * The result buffer must be released when this primitive is not used anymore.
     * 
     */
    public void calculateTBN(GLTF gltf) {
        if (indices == null) {
            throw new IllegalArgumentException("Arrayed mode not supported");
        }
        Triangles triangles = new Triangles();
        triangles.createBuffers(gltf);
        Accessor tangent = getAccessor(Attributes.TANGENT);
        if (tangent != null) {
            buildBitangentBuffer(gltf, triangles);
        } else {
            buildTBNBuffers(gltf, triangles);
        }
    }

    /**
     * Have normal and tangent buffer - build biTangent buffer
     * 
     * @param gltf
     * @param triangles
     */
    private void buildBitangentBuffer(GLTF gltf, Triangles triangles) {
        switch (mode) {
            case TRIANGLES:
                buildBiTangentTriangles(gltf, triangles);
                break;
            default:
                throw new IllegalArgumentException("Not implemented for " + mode);
        }
    }

    private void buildTBNBuffers(GLTF gltf, Triangles triangles) {

        switch (mode) {
            case TRIANGLES:
                buildTBNTriangles(gltf, triangles);
                break;
            default:
                throw new IllegalArgumentException("Not implemented for " + mode);
        }

    }

    /**
     * Builds the tangent buffer for this primitive using TRIANGLES mode.
     * 
     * @param gltf
     * @param triangles
     */
    private void buildBiTangentTriangles(GLTF gltf, Triangles triangles) {
        float[] tangentArray = triangles.createBiTangent();
        int count = tangentArray.length >>> 2; // Tangents are in Vec4 format
        BufferView Bitangentbv = gltf.createBufferView(BITANGENT, (count << 2) * ComponentType.FLOAT.size, 0,
                Type.VEC4.size * ComponentType.FLOAT.size, Target.ARRAY_BUFFER);
        Buffer buffer = Bitangentbv.getBuffer();
        buffer.put(tangentArray, 0);
        Accessor Ba = new Accessor(Bitangentbv, 0, ComponentType.FLOAT, count, Type.VEC4);
        accessorList.add(Ba);
        attributeList.add(Attributes.BITANGENT);
    }

    /**
     * Builds the tangent and binormal buffers for this primitive using TRIANGLES mode.
     * 
     * @param gltf
     * @param triangles
     */
    private void buildTBNTriangles(GLTF gltf, Triangles triangles) {
        float[][] TBArray = triangles.calculateTangentBiTangent();
        int l = TBArray[0].length; // Length of one buffer in number of floats - type is VEC4
        BufferView Tbv = gltf.createBufferView(TANGENT_BITANGENT, l * 4 * 2, 0, 16, Target.ARRAY_BUFFER);
        Buffer buffer = Tbv.getBuffer();
        BufferView Bbv = gltf.createBufferView(buffer, l * 4, 16, Target.ARRAY_BUFFER);
        buffer.put(TBArray[0], 0);
        buffer.put(TBArray[1], l);
        int count = l / 4;
        Accessor Ta = new Accessor(Tbv, 0, ComponentType.FLOAT, count, Type.VEC4);
        Accessor Ba = new Accessor(Bbv, 0, ComponentType.FLOAT, count, Type.VEC4);
        int tangentIndex = attributeList.indexOf(Attributes.TANGENT);
        if (tangentIndex >= 0) {
            throw new IllegalArgumentException("Tangents already present");
        }
        accessorList.add(Ta);
        attributeList.add(Attributes.TANGENT);
        accessorList.add(Ba);
        attributeList.add(Attributes.BITANGENT);
    }

    /**
     * @deprecated Primitive should not have a reference to ShaderProgram, use index instead and fetch from
     * AssetManager.
     * @param program
     */
    @Deprecated
    public void setProgram(GLTFShaderProgram program) {
        this.program = program;
    }

    /**
     * Returns the program to be used to render this primitive
     * 
     * @return
     */
    @Deprecated
    public GLTFShaderProgram getProgram() {
        return program;
    }

}
