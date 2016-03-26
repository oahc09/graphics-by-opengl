package com.nucleus.bounds;

import com.google.gson.annotations.SerializedName;

/**
 * Base class for bounds.
 * The bounds can reference a source position to make it possible to track an object that is moving.
 * If source position changes, call {@link #updated()} to flag to implementations that data may need to be recalculated,
 * for instance in case of rectangular bounds.
 * 
 * This class can be serialized using GSON
 * 
 * @author Richard Sahlin
 *
 */
public abstract class Bounds {

    public enum SerializeNames {
        /**
         * Must be aligned with the type SerializedName
         */
        type(),
        /**
         * Must be aligned with the bounds SerializedName
         */
        bounds();
    }

    /**
     * The type of bounds
     * 
     * @author Richard Sahlin
     *
     */
    public enum Type {
        CIRCULAR(1),
        RECTANGULAR(2);

        private final int value;

        private Type(int value) {
            this.value = value;
        }

    }

    /**
     * Set to true when the bounds have been updated/moved, for some bounds this means that
     * internal data needs to be recalculated.
     */
    transient protected boolean updated = true;

    /**
     * Ref to position in object space.
     */
    transient protected float[] position;

    /**
     * The type of bounds object
     */
    @SerializedName("type")
    protected Type type;
    /**
     * The bounds data, this is implementation specifiec.
     * For a circular bounds this is one value.
     * For rectangle bounds it is 8 values, this is the bounds without position.
     */
    @SerializedName("bounds")
    protected float[] bounds;

    /**
     * Checks if a single point is within the bounds.
     * @param position Position array, must contain 3 values in case of 3 dimensional bound.
     * @param index Index into position array where position is
     * @return True if the point is within the bounds
     */
    public abstract boolean isPointInside(float[] position, int index);

    /**
     * Checks if a circular bounds is within the bounds.
     * @param bounds The bounds to check against this bound.
     * @return True if the bounds are within this bound, ie some parts are touching. Returns
     * false if there is no contact.
     */
    public abstract boolean isCircularInside(CircularBounds bounds);

    /**
     * Checks if a rectangle bounds is inside the bounds.
     * @param bounds The bounds to check against this bound.
     * @return True if the bounds are within this bound, ie some parts are touching. Returns
     * false if there is no contact.
     */
    public abstract boolean isRectangleInside(RectangularBounds bounds);

    /**
     * Returns the type of bounds.
     * @return The type of bounds for this class.
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the bound data for this bounds.
     * @return Array containing the bounds values, this is implementation specific.
     */
    public float[] getBounds() {
        return bounds;
    }

    /**
     * Rotate the bounds on the specified axis.
     * 
     * @param axis The axis, X, Y or Z (0,1 or 2)
     * @param angle
     */
    public abstract void rotate(int axis, float angle);

    /**
     * Flag that this bounds may need updating, this is for instance when the source position has changed.
     * 
     */
    public void updated() {
        updated = true;
    }

    /**
     * Sets a reference to the position, this must be set before calling intersection methods.
     * The position is NOT copied to this class, a reference is kept meaning that it just needs to be set once.
     * Call {@link #updated()} when the position changes to inform implementations that data may need to be recalculated
     * 
     * @param position Reference to the position of this bounds.
     * @throws IllegalArgumentException If position is null.
     */
    public void setPosition(float[] position) {
        if (position == null) {
            throw new IllegalArgumentException("Position is null.");
        }
        this.position = position;
        updated = true;
    }

    /**
     * Returns the position for the bounds - this is a ref to the object position, do not modify
     * this position!
     * @return The position of the bounds.
     */
    public float[] getPosition() {
        return position;
    }

    /**
     * Performs a quick check if the object is outside the bounds, use this for quick on screen checks or similar.
     * @param bounds Array with x, y, width height
     * @param radius The radius of the object to check.
     * @return True if this object is touches (is inside) the bounds, false if it is fully outside.
     */
    public static boolean isCulled(float[] position, int[] bounds, float radius) {

        if (position[0] + radius < bounds[0] || position[0] - radius > bounds[0] + bounds[2]) {
            return true;
        }
        if (position[1] + radius < bounds[1] || position[1] - radius > bounds[1] + bounds[3]) {
            return true;
        }

        return false;
    }

}