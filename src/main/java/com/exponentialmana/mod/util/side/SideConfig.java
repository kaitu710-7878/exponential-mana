package com.exponentialmana.mod.util.side;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import java.util.EnumMap;
import java.util.Map;

/**
 * Manages 6-face side configuration and auto-eject setting for a machine.
 */
public class SideConfig {
    private final Map<Direction, SideMode> modes = new EnumMap<>(Direction.class);
    private boolean autoEject = true;

    public SideConfig() {
        for (Direction dir : Direction.values()) {
            modes.put(dir, SideMode.BOTH);
        }
    }

    public SideMode getMode(Direction dir) {
        if (dir == null) return SideMode.BOTH;
        return modes.getOrDefault(dir, SideMode.BOTH);
    }

    public void setMode(Direction dir, SideMode mode) {
        if (dir != null && mode != null) {
            modes.put(dir, mode);
        }
    }

    public SideMode cycleMode(Direction dir) {
        SideMode next = getMode(dir).next();
        setMode(dir, next);
        return next;
    }

    public boolean isAutoEject() {
        return autoEject;
    }

    public void setAutoEject(boolean autoEject) {
        this.autoEject = autoEject;
    }

    public void toggleAutoEject() {
        this.autoEject = !this.autoEject;
    }

    public boolean canInput(Direction dir) {
        SideMode mode = getMode(dir);
        return mode == SideMode.INPUT || mode == SideMode.BOTH;
    }

    public boolean canOutput(Direction dir) {
        SideMode mode = getMode(dir);
        return mode == SideMode.OUTPUT || mode == SideMode.BOTH;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        for (Direction dir : Direction.values()) {
            tag.putString(dir.getSerializedName(), getMode(dir).name());
        }
        tag.putBoolean("auto_eject", autoEject);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        if (tag == null) return;
        for (Direction dir : Direction.values()) {
            if (tag.contains(dir.getSerializedName())) {
                try {
                    modes.put(dir, SideMode.valueOf(tag.getString(dir.getSerializedName())));
                } catch (IllegalArgumentException ignored) {
                    modes.put(dir, SideMode.BOTH);
                }
            }
        }
        if (tag.contains("auto_eject")) {
            autoEject = tag.getBoolean("auto_eject");
        }
    }
}
