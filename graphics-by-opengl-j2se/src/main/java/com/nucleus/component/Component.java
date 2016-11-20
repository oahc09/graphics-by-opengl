package com.nucleus.component;

import com.google.gson.annotations.SerializedName;
import com.nucleus.actor.ComponentNode;
import com.nucleus.io.BaseReference;
import com.nucleus.io.ResourcesData;
import com.nucleus.renderer.NucleusRenderer;

/**
 * The component part of behavior, this holds the data needed to perform actions.
 * Component describes what actions are possible on an entity by defining the data.
 * This holds the data needed to process the behavior, this processing shall be done by the system handling the
 * component.
 * This is to ensure that behavior is data driven and uses composition rather than inheritance.
 * In order to be compatible with hardware acceleration such as OpenCL it is very important NOT to break down into too
 * small components.
 */
public abstract class Component extends BaseReference {

    public final static String INVALID_DATACOUNT_ERROR = "Invalid datacount";

    @SerializedName("type")
    private String type;

    /**
     * Used to create a new instance of a component
     * 
     * @return
     */
    public abstract Component createInstance();

    /**
     * Creates the data and objects needed for the component - when this method returns the component shall be ready to
     * be used.
     * 
     * @param renderer
     * @param resources
     * @param parent
     * @throws ComponentException If there is an error preventing the component to be created
     */
    public abstract void create(NucleusRenderer renderer, ResourcesData resources, ComponentNode parent)
            throws ComponentException;

    public Component copy() {
        Component copy = createInstance();
        copy.set(this);
        return copy;
    }

    public void set(Component source) {
        this.type = source.type;
        this.setId(source.getId());
    }

}