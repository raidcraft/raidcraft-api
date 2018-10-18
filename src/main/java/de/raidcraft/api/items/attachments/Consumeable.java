package de.raidcraft.api.items.attachments;

import io.ebean.annotation.EnumValue;

public interface Consumeable {

    Type getConsumeableType();

    String getResourceName();

    double getResourceGain();

    double getDuration();

    long getInterval();

    boolean isPercentage();

    default boolean isInstant() {
        return getDuration() <= 0;
    }

    enum Type {

        @EnumValue("HEALTH")
        HEALTH,
        @EnumValue("RESOURCE")
        RESOURCE,
        @EnumValue("ATTRIBUTE")
        ATTRIBUTE
    }
}
