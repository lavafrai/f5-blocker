package ru.lavafrai.svogame.f5blocker.config

import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.GameRules
import ru.lavafrai.svogame.f5blocker.F5BlockerMod


object F5GameRules {
    private lateinit var DISABLE_F5_VIEW: GameRules.Key<GameRules.BooleanValue>

    private lateinit var IGNORE_F5_LIMITATION_FOR_OPS: GameRules.Key<GameRules.BooleanValue>

    fun register() {
        DISABLE_F5_VIEW = GameRules.register(
            "disableF5View",
            GameRules.Category.PLAYER,
            GameRules.BooleanValue.create(true) { server, value ->
                F5BlockerConfig.disableF5View.set(value.get())

                val rules = server.gameRules
                F5BlockerMod.getInstance().updatePlayersState(rules)
            }
        )

        IGNORE_F5_LIMITATION_FOR_OPS = GameRules.register(
            "ignoreF5LimitationForOps",
            GameRules.Category.PLAYER,
            GameRules.BooleanValue.create(false) { server, value ->
                F5BlockerConfig.ignoreF5LimitationForOps.set(value.get())

                val rules = server.gameRules
                F5BlockerMod.getInstance().updatePlayersState(rules)
            }
        )
    }

    fun applyFromConfig(server: MinecraftServer) {
        val rules = server.gameRules

        val disableF5ViewConfig = F5BlockerConfig.disableF5View.get()
        val ignoreF5LimitationForOpsConfig = F5BlockerConfig.ignoreF5LimitationForOps.get()

        rules.getRule(DISABLE_F5_VIEW).set(disableF5ViewConfig, server)
        rules.getRule(IGNORE_F5_LIMITATION_FOR_OPS).set(ignoreF5LimitationForOpsConfig, server)
    }

    fun isF5ViewDisabled(rules: GameRules) = rules.getRule(DISABLE_F5_VIEW).get()
    fun isIgnoreF5LimitationForOps(rules: GameRules) = rules.getRule(IGNORE_F5_LIMITATION_FOR_OPS).get()
}