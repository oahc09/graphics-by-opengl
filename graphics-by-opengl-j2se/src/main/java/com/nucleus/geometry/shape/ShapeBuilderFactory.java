package com.nucleus.geometry.shape;

import com.nucleus.geometry.shape.RectangleShapeBuilder.RectangleConfiguration;
import com.nucleus.vecmath.Rectangle;
import com.nucleus.vecmath.Shape;

/**
 * Creates shape builders
 *
 */
public class ShapeBuilderFactory {

    /**
     * Generic shapebuilder create method, will create the correct builder instance based on the shape.
     * 
     * @param shape
     * @param offsets
     * @param count
     * @param startVertex
     * @return
     */
    public static ShapeBuilder createBuilder(Shape shape, float[] offsets, int count, int startVertex) {
        switch (shape.getType()) {
            case rect:
                return createBuilder((Rectangle) shape, offsets, count, startVertex);
            default:
                throw new IllegalArgumentException("Not implemented for: " + shape.getType());

        }
    }

    public static ShapeBuilder createBuilder(Rectangle shape, float[] offsets, int count, int startVertex) {
        RectangleConfiguration config = new RectangleShapeBuilder.RectangleConfiguration(shape,
                offsets[0], count, 0);
        config.enableVertexIndex(true);
        return new RectangleShapeBuilder(config);
    }

}