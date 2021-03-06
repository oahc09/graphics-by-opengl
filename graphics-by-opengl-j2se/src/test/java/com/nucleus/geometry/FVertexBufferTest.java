package com.nucleus.geometry;

import org.junit.Assert;
import org.junit.Test;

import com.nucleus.BaseTestCase;

public class FVertexBufferTest extends BaseTestCase {

    final static int TRIANGLE_COUNT = 3;
    final static int COMPONENT_COUNT = 3;
    final static int SIZE_PER_VERTEX = 10;
    final static int DATA_SIZE32 = 4;

    @Test
    public void testCreateVertexBuffer() {
        AttributeBuffer vb = new AttributeBuffer(TRIANGLE_COUNT * 3, SIZE_PER_VERTEX);
        Assert.assertEquals(TRIANGLE_COUNT * 3, vb.getVerticeCount());
        Assert.assertEquals(SIZE_PER_VERTEX * DATA_SIZE32, vb.getByteStride());
        Assert.assertEquals(SIZE_PER_VERTEX * TRIANGLE_COUNT * COMPONENT_COUNT, vb.getBuffer().capacity());
    }

}
