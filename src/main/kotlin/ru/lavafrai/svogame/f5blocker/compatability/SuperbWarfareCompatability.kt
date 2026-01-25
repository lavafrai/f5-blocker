package ru.lavafrai.svogame.f5blocker.compatability

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
import net.minecraft.server.level.ServerPlayer
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

    override fun shouldIgnoreF5Limitation(player: ServerPlayer): Boolean {
        val gameRuleEnabled = player.level().gameRules.getBoolean(IGNORE_F5_LIMITATION_FOR_SBW_DRIVER)
        if (!gameRuleEnabled) return false

        val vehicle = player.vehicle
        val playerIsDriver = (vehicle as? VehicleEntity)?.firstPassenger === player
        return playerIsDriver
    }
}