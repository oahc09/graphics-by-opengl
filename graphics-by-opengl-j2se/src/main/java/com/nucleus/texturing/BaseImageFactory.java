package com.nucleus.texturing;

import java.nio.ByteBuffer;

import com.nucleus.ErrorMessage;
import com.nucleus.texturing.Convolution.Kernel;
import com.nucleus.texturing.Image.ImageFormat;

/**
 * Implementation of platform agnostic image methods using the Nucleus {@link Image} class;
 * Some platforms will provide native functions for these methods.
 * 
 * @author Richard Sahlin
 *
 */
public abstract class BaseImageFactory implements ImageFactory {

    protected final static String ILLEGAL_PARAMETER = "Illegal parameter: ";
    protected final static String NULL_PARAMETER = "Null parameter";

    @Override
    public Image createScaledImage(Image source, int width, int height, ImageFormat format) {
        if (format == null) {
            throw new IllegalArgumentException(NULL_PARAMETER);
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException(ILLEGAL_PARAMETER + width + ", " + height);
        }

        int scale = (source.getWidth() / width + source.getHeight() / height) / 2;
        Convolution c = null;
        switch (scale) {
        case 1:
        case 2:
            c = new Convolution(Kernel.SIZE_2X2);
            c.set(new float[] { 1, 1, 1, 1 }, 0, 0, Kernel.SIZE_2X2.size);
            break;
        case 3:
            c = new Convolution(Kernel.SIZE_3X3);
            c.set(new float[] { 1, 4, 1, 4, 4, 4, 1, 4, 1 }, 0, 0, Kernel.SIZE_3X3.size);
            break;
        case 4:
            c = new Convolution(Kernel.SIZE_4X4);
            c.set(new float[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, 0, 0, Kernel.SIZE_4X4.size);
            break;
        case 5:
            c = new Convolution(Kernel.SIZE_5X5);
            c.set(new float[] { 1, 2, 3, 2, 1,
                    2, 3, 4, 3, 2,
                    3, 4, 5, 4, 3,
                    2, 3, 4, 3, 2,
                    1, 2, 3, 2, 1 }, 0, 0, Kernel.SIZE_5X5.size);
            break;
        case 6:
        case 7:
        case 8:
            c = new Convolution(Kernel.SIZE_8X8);
            c.set(new float[] {
                    1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 2, 2, 1, 1, 1,
                    1, 1, 2, 3, 3, 2, 1, 1,
                    1, 2, 3, 4, 4, 3, 2, 1,
                    1, 2, 3, 4, 4, 3, 2, 1,
                    1, 1, 2, 3, 3, 2, 1, 1,
                    1, 1, 1, 2, 2, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1

            }, 0, 0, Kernel.SIZE_8X8.size);
            break;
        default:
            c = new Convolution(Kernel.SIZE_8X8);

        }

        c.normalize(false);
        Image destination = new Image(width, height, ImageFormat.RGBA);
        c.process(source, destination);
        return destination;
    }

    @Override
    public Image createImage(int width, int height, ImageFormat format) {
        if (width <= 0 || height <= 0 || format == null) {
            throw new IllegalArgumentException(ILLEGAL_PARAMETER + width + ", " + height + " : " + format);
        }
        return new Image(width, height, format);
    }

    /**
     * Copies pixel data from the byte array source to the destination.
     * The type (format) is
     * 
     * @param source
     * @param sourceFormat The source type
     * @param destination
     */
    protected void copyPixels(byte[] source, ImageFormat sourceFormat, Image destination) {

        ByteBuffer buffer = (ByteBuffer) destination.getBuffer().rewind();
        switch (sourceFormat) {
        case TYPE_4BYTE_ABGR:
            switch (destination.getFormat()) {
            case RGBA:
                copyPixels_4BYTE_ABGR_TO_RGBA(source, buffer);
                break;
            case LUMINANCE_ALPHA:
                copyPixels_4BYTE_ABGR_TO_LUMINANCE_ALPHA(source, buffer);
                break;
            case RGB565:
                copyPixels_4BYTE_ABGR_TO_RGB565(source, buffer);
                break;
            // default:
            // throw new IllegalArgumentException(ErrorMessage.NOT_IMPLEMENTED.message + type);
            }
            break;
        case RGBA:
            switch (destination.getFormat()) {
            case RGBA:
                copyPixels_4BYTE_RGBA_TO_RGBA(source, buffer);
                break;
            case LUMINANCE_ALPHA:
                copyPixels_4BYTE_RGBA_TO_LUMINANCE_ALPHA(source, buffer);
                break;
            case RGB565:
                copyPixels_4BYTE_RGBA_TO_RGB565(source, buffer);
                break;
            }
            break;
        default:
            throw new IllegalArgumentException(ErrorMessage.NOT_IMPLEMENTED.message + sourceFormat);
        }
    }


    /**
     * Copies the 4 byte RGBA to 16 bit luminance alpha
     * 
     * @param source
     * @param destination
     */
    protected void copyPixels_4BYTE_RGBA_TO_LUMINANCE_ALPHA(byte[] source, ByteBuffer destination) {
        byte[] la = new byte[2];
        int length = source.length;
        for (int index = 0; index < length;) {
            la[0] = source[index++];
            la[1] = source[index++];
            index += 2;
            destination.put(la, 0, 2);
        }
    }

    /**
     * Copies the 4 byte RGBA to 16 bit RGB565
     * 
     * @param source
     * @param destination
     */
    protected void copyPixels_4BYTE_RGBA_TO_RGB565(byte[] source, ByteBuffer destination) {
        byte[] rgb = new byte[2];
        int length = source.length;
        int r, g, b, a;
        int rgbint;
        for (int index = 0; index < length;) {
            a = source[index++];
            r = source[index++];
            g = source[index++];
            b = source[index++];
            rgbint = (r >>> 3) | ((g >>> 2) << 5) | ((b >>> 3) << 11);
            rgb[0] = (byte) (rgbint & 0xff);
            rgb[1] = (byte) (rgbint >>> 8);
            destination.put(rgb, 0, 2);
        }
    }

    /**
     * Straight copy from source to destination
     * 
     * @param source
     * @param destination
     */
    protected void copyPixels_4BYTE_RGBA_TO_RGBA(byte[] source, ByteBuffer destination) {
        byte[] rgba = new byte[4];
        int length = source.length;
        for (int index = 0; index < length;) {
            rgba[0] = source[index++];
            rgba[1] = source[index++];
            rgba[2] = source[index++];
            rgba[3] = source[index++];
            destination.put(rgba, 0, 4);
        }
    }

    /**
     * Straight copy from source to destination - just swap ABGR to RGBA
     * 
     * @param source
     * @param destination
     */
    protected void copyPixels_4BYTE_ABGR_TO_RGBA(byte[] source, ByteBuffer destination) {
        byte[] rgba = new byte[4];
        int length = source.length;
        for (int index = 0; index < length;) {
            rgba[3] = source[index++];
            rgba[2] = source[index++];
            rgba[1] = source[index++];
            rgba[0] = source[index++];
            destination.put(rgba, 0, 4);
        }
    }

    /**
     * Copies the 4 byte ABGR to 16 bit luminance alpha
     * 
     * @param source
     * @param destination
     */
    protected void copyPixels_4BYTE_ABGR_TO_LUMINANCE_ALPHA(byte[] source, ByteBuffer destination) {
        byte[] la = new byte[2];
        int length = source.length;
        for (int index = 0; index < length;) {
            la[0] = source[index++];
            la[1] = source[index++];
            index += 2;
            destination.put(la, 0, 2);
        }
    }

    /**
     * Copies the 4 byte ABGR to 16 bit RGB565
     * 
     * @param source
     * @param destination
     */
    protected void copyPixels_4BYTE_ABGR_TO_RGB565(byte[] source, ByteBuffer destination) {
        byte[] rgb = new byte[2];
        int length = source.length;
        int r, g, b, a;
        int rgbint;
        for (int index = 0; index < length;) {
            a = source[index++];
            r = source[index++];
            g = source[index++];
            b = source[index++];
            rgbint = (r >>> 3) | ((g >>> 2) << 5) | ((b >>> 3) << 11);
            rgb[0] = (byte) (rgbint & 0xff);
            rgb[1] = (byte) (rgbint >>> 8);
            destination.put(rgb, 0, 2);
        }
    }

}
