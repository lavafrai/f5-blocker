package ru.lavafrai.svogame.f5blocker.compatability

import net.minecraft.server.level.ServerPlayer

interface CompatabilityLayer {
    fun register()
    fun isAvailable(): Boolean

    fun shouldIgnoreF5Limitation(player: ServerPlayer): Boolean
}