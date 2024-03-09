package com.iostate.orca.metadata;

import java.util.Arrays;

/**
 * A container of flags to know if something is cascaded
 */
public class CascadeConfig {

    private final boolean[] flags = new boolean[CascadeType.values().length];

    public CascadeConfig(CascadeType[] cascadeTypes) {
        if (cascadeTypes == null) {
            return;
        }

        for (CascadeType each : cascadeTypes) {
            if (each == CascadeType.ALL) {
                Arrays.fill(flags, true);
                break;
            } else {
                flags[each.ordinal()] = true;
            }
        }
    }

    public boolean isPersist() {
        return flags[CascadeType.PERSIST.ordinal()];
    }

    public boolean isMerge() {
        return flags[CascadeType.MERGE.ordinal()];
    }

    public boolean isRefresh() {
        return flags[CascadeType.REFRESH.ordinal()];
    }

    public boolean isRemove() {
        return flags[CascadeType.REMOVE.ordinal()];
    }

    public boolean isDetach() {
        return flags[CascadeType.DETACH.ordinal()];
    }
}