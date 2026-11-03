package com.exponentialmana.mod.network;

import com.exponentialmana.mod.util.side.ISidedMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketToggleSideConfig {
    private final BlockPos pos;
    private final int directionIndex; // 0-5 for Direction.values()[i], -1 for autoEject toggle

    public PacketToggleSideConfig(BlockPos pos, Direction dir) {
        this.pos = pos;
        this.directionIndex = dir != null ? dir.get3DDataValue() : -1;
    }

    public PacketToggleSideConfig(BlockPos pos) {
        this.pos = pos;
        this.directionIndex = -1; // Auto-eject toggle
    }

    public PacketToggleSideConfig(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.directionIndex = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(directionIndex);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;
            Level level = player.level();
            if (!level.isLoaded(pos)) return;

            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ISidedMachine machine) {
                if (directionIndex >= 0 && directionIndex < 6) {
                    Direction dir = Direction.from3DDataValue(directionIndex);
                    machine.getSideConfig().cycleMode(dir);
                } else if (directionIndex == -1) {
                    machine.getSideConfig().toggleAutoEject();
                }
                be.setChanged();
                level.sendBlockUpdated(pos, be.getBlockState(), be.getBlockState(), 3);
            }
        });
        return true;
    }
}
