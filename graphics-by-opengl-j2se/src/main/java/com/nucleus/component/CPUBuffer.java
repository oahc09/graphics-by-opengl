package com.nucleus.component;

import com.nucleus.geometry.AttributeBuffer;

public class CPUBuffer extends ComponentBuffer {

    private float[] data;

    public CPUBuffer(int entityCount, int sizePerEntity) {
        super(entityCount, sizePerEntity);
        data = new float[entityCount * sizePerEntity];
    }

    @Override
    public void get(int entity, float[] destination) {
        int index = entity * sizePerEntity;
        System.arraycopy(data, index, destination, 0, sizePerEntity);
    }

    @Override
    public void put(int entity, int offset, float[] data, int srcOffset, int count) {
        int index = entity * sizePerEntity;
        System.arraycopy(this.data, index + offset, data, srcOffset, count);
    }

    @Override
    public void get(int entity, AttributeBuffer destination) {
        int index = entity * sizePerEntity;
        destination.getBuffer().put(data, index, sizePerEntity);
        destination.setDirty(true);
    }

}
