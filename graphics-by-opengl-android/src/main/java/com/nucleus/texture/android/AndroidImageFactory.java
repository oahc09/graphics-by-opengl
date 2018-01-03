package com.nucleus.texture.android;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.nucleus.profiling.FrameSampler;
import com.nucleus.texturing.BaseImageFactory;
import com.nucleus.texturing.Image;
import com.nucleus.texturing.Image.ImageFormat;
import com.nucleus.texturing.ImageFactory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class AndroidImageFactory extends BaseImageFactory implements ImageFactory {

    @Override
    public Image createImage(String name, ImageFormat format) throws IOException {
        long start = System.currentTimeMillis();
        ClassLoader classLoader = getClass().getClassLoader();
        Bitmap b = BitmapFactory.decodeStream(classLoader.getResourceAsStream(name));
        long loaded = System.currentTimeMillis();
        FrameSampler.getInstance().logTag(FrameSampler.Samples.LOAD_IMAGE, start, loaded);
        if (b == null) {
            throw new IOException("Could not load " + name);
        }
        byte[] bytePixels = new byte[b.getWidth() * b.getHeight() * 4];
        ByteBuffer bb = ByteBuffer.wrap(bytePixels);
        b.copyPixelsToBuffer(bb);
        Image image = new Image(b.getWidth(), b.getHeight(), format);
        copyPixels(bytePixels, ImageFormat.RGBA, image);
        b.recycle();
        FrameSampler.getInstance().logTag(FrameSampler.Samples.COPY_IMAGE, " " + image.getFormat().toString(), loaded,
                System.currentTimeMillis());
        return image;
    }
}