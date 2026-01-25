package ru.lavafrai.svogame.f5blocker.compatability

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
import com.atsuishio.superbwarfare.item.Monitor
import net.minecraft.client.CameraType
import net.minecraft.client.Minecraft
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.GameRules
import net.minecraftforge.fml.ModList
import ru.lavafrai.svogame.f5blocker.F5BlockerMod

class SuperbWarfareCompatability: CompatabilityLayer {
    companion object {
        private lateinit var IGNORE_F5_LIMITATION_FOR_SBW_DRIVER: GameRules.Key<GameRules.BooleanValue>
    }

    override fun isAvailable(): Boolean {
        val sbwLoaded = ModList.get().isLoaded("superbwarfare")
        return sbwLoaded
    }

    override fun serverTick(player: ServerPlayer) {}

    override fun clientTick(player: Player) {
        if (isControllingDrone(player)) {
            if (Minecraft.getInstance().options.cameraType != CameraType.THIRD_PERSON_BACK) {
                Minecraft.getInstance().options.cameraType = CameraType.THIRD_PERSON_BACK
            }
        }
    }

    override fun register() {
        registerGameRules()
    }

    private fun registerGameRules() {
        IGNORE_F5_LIMITATION_FOR_SBW_DRIVER = GameRules.register(
            "ignoreF5LimitationForSBWDriver",
            GameRules.Category.PLAYER,
            GameRules.BooleanValue.create(false) { server, value ->
                val rules = server.gameRules
                F5BlockerMod.getInstance().updatePlayersState(rules)
            }
        )
    }

    private fun isDriver(player: Player): Boolean {
        val vehicle = player.vehicle
        val playerIsDriver = (vehicle as? VehicleEntity)?.firstPassenger === player
        return playerIsDriver
    }

    private fun isControllingDrone(player: Player): Boolean {
        val stack = player.mainHandItem
        if (stack.item is Monitor) {
            return stack.orCreateTag.getBoolean("Using")
        }
        return false
    }

    override fun shouldIgnoreF5Limitation(player: ServerPlayer): Boolean {
        val driverRuleEnabled = player.level().gameRules.getBoolean(IGNORE_F5_LIMITATION_FOR_SBW_DRIVER)

        val playerIsDriver = isDriver(player)
        val isControllingDrone = isControllingDrone(player)

        return (driverRuleEnabled && playerIsDriver) || isControllingDrone
    }
}