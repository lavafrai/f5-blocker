package ru.lavafrai.svogame.f5blocker.compatability

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

interface CompatabilityLayer {
    fun register()
    fun isAvailable(): Boolean

    fun serverTick(player: ServerPlayer)
    fun clientTick(player: Player)

    fun shouldIgnoreF5Limitation(player: ServerPlayer): Boolean
}