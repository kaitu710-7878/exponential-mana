package com.exponentialmana.mod.network;

import com.exponentialmana.mod.ExponentialMana;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ExponentialMana.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(packetId++,
                PacketToggleSideConfig.class,
                PacketToggleSideConfig::toBytes,
                PacketToggleSideConfig::new,
                PacketToggleSideConfig::handle);
    }

    public static void sendToServer(Object msg) {
        CHANNEL.sendToServer(msg);
    }
}
