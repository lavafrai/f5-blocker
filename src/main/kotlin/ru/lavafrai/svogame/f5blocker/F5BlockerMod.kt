@file:Suppress("DEPRECATION", "removal")

package ru.lavafrai.svogame.f5blocker

import com.mojang.logging.LogUtils
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.GameRules
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import ru.lavafrai.svogame.f5blocker.compatability.CompatabilityLayer
import ru.lavafrai.svogame.f5blocker.compatability.SuperbWarfareCompatability
import ru.lavafrai.svogame.f5blocker.config.F5BlockerConfig
import ru.lavafrai.svogame.f5blocker.config.F5GameRules
import ru.lavafrai.svogame.f5blocker.network.F5Network
import ru.lavafrai.svogame.f5blocker.network.UpdateClientStatePacket
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import java.util.*


@Mod(F5BlockerMod.MODID)
class F5BlockerMod {
    lateinit var server: MinecraftServer
    private val compatabilityLayers: List<CompatabilityLayer> = listOf(
        SuperbWarfareCompatability()
    )
    val enabledCompatabilityLayers: MutableList<CompatabilityLayer> = mutableListOf()
    val userStatesCache: MutableMap<UUID, Boolean> = mutableMapOf()

    companion object {
        const val MODID = "f5blocker"
        val LOGGER = LogUtils.getLogger()
        private lateinit var instance: F5BlockerMod

        fun getInstance(): F5BlockerMod {
            return instance
        }
    }

    init {
        initializeMod()
    }

    fun initializeMod() {
        instance = this
        MOD_BUS.addListener(this::commonSetup)

        MinecraftForge.EVENT_BUS.register(this)

        F5BlockerConfig.register()
        F5GameRules.register()
    }

    private fun commonSetup(event: FMLCommonSetupEvent) {
        LOGGER.info("Template mod started.")

        for (layer in compatabilityLayers) {
            if (layer.isAvailable()) {
                LOGGER.info("[F5 Blocker] Enabling compatability layer: ${layer.javaClass.simpleName}")
                layer.register()
                enabledCompatabilityLayers.add(layer)
            }
        }

        F5Network.register()
    }

    @SubscribeEvent
    fun onServerStarting(event: ServerStartingEvent) {
        val server = event.getServer()
        this.server = server

        F5GameRules.applyFromConfig(server)
    }

    @SubscribeEvent
    fun onPlayerLogin(event: PlayerEvent.PlayerLoggedInEvent) {
        val player = event.entity
        if (player is ServerPlayer) {
            updatePlayerState(player, server.gameRules)
        }
    }

    @SubscribeEvent
    fun onPlayerLogout(event: PlayerEvent.PlayerLoggedOutEvent) {
        val player = event.entity
        userStatesCache.remove(player.uuid)
    }

    @SubscribeEvent
    fun onPlayerTick(event: TickEvent.PlayerTickEvent) {
        if (event.side.isClient && event.phase == TickEvent.Phase.END) {
            enabledCompatabilityLayers.forEach {
                it.clientTick(event.player)
            }
        }
        if (event.side.isServer && event.phase == TickEvent.Phase.END) {
            val player = event.player as? ServerPlayer
            if (player != null) enabledCompatabilityLayers.forEach {
                it.serverTick(player)
            }
        }

        if (event.side.isServer && event.phase == TickEvent.Phase.END) {
            val player = event.player as? ServerPlayer ?: return
            updatePlayerState(player, server.gameRules)
        }
    }

    private fun updatePlayerState(player: ServerPlayer, gameRules: GameRules) {
        val server = player.server

        val isPlayerOp = server.playerList.isOp(player.gameProfile)
        val isF5Blocked = F5GameRules.isF5ViewDisabled(gameRules)
        val isOpIgnored = F5GameRules.isIgnoreF5LimitationForOps(gameRules) && isPlayerOp

        var shouldBlockF5ForPlayer = isF5Blocked
        enabledCompatabilityLayers.forEach {
            if (it.shouldIgnoreF5Limitation(player)) {
                shouldBlockF5ForPlayer = false
            }
        }

        if (isPlayerOp && isOpIgnored) shouldBlockF5ForPlayer = false

        val cachedValue = userStatesCache[player.uuid]
        if (cachedValue != null && cachedValue == shouldBlockF5ForPlayer) return

        userStatesCache[player.uuid] = shouldBlockF5ForPlayer
        F5Network.sendToClient(
            UpdateClientStatePacket(shouldBlockF5ForPlayer),
            player
        )
    }

    fun updatePlayersState(gameRules: GameRules) {
        for (player in server.playerList.players) {
            updatePlayerState(player, gameRules)
        }
    }
}
