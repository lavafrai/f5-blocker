package ru.lavafrai.svogame.f5blocker.network

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.network.simple.SimpleChannel
import ru.lavafrai.svogame.f5blocker.F5BlockerMod



object F5Network {
    private const val PROTOCOL_VERSION = "1"

    private val NETWORK_CHANNEL: SimpleChannel = NetworkRegistry.newSimpleChannel(
        ResourceLocation(F5BlockerMod.MODID, "f5_blocker_channel"),
        { PROTOCOL_VERSION },
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    )

    @Suppress("INVISIBLE_MEMBER", "INFERRED_INVISIBLE_RETURN_TYPE_WARNING")
    fun register() {
        NETWORK_CHANNEL.registerMessage(
            0,
            UpdateClientStatePacket::class.java,
            UpdateClientStatePacket::encode,
            ::UpdateClientStatePacket,
            UpdateClientStatePacket::handle
        )
    }

    fun sendToClient(packet: UpdateClientStatePacket, player: ServerPlayer) {
        NETWORK_CHANNEL.sendTo(
            packet,
            player.connection.connection,
            NetworkDirection.PLAY_TO_CLIENT
        )
    }
}