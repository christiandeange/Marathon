package com.deange.marathonapp.model;

import com.deange.marathonapp.controller.GsonController;

public abstract class BaseModel {

    public String serialize() {
        return GsonController.getInstance().toJson(this);
    }

    public static <T extends BaseModel> T deserialize(final String serializedData, final Class<T> clazz) {
        return GsonController.getInstance().fromJson(serializedData, clazz);
    }

}
