package ru.lavafrai.svogame.f5blocker.config

import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.config.ModConfig


object F5BlockerConfig {
    private val SERVER_BUILDER: ForgeConfigSpec.Builder = ForgeConfigSpec.Builder()

    val disableF5View: ForgeConfigSpec.BooleanValue
    val ignoreF5LimitationForOps: ForgeConfigSpec.BooleanValue
    // val SBWEnableF5Driver: ForgeConfigSpec.BooleanValue

    val SERVER_SPEC: ForgeConfigSpec

    init {
        SERVER_BUILDER
            .comment("F5 Disabler mod configuration.")
            .push("gamerules")

        disableF5View = SERVER_BUILDER
            .comment("Controls whether third-person camera usage is restricted.")
            .define("disableF5View", true)

        ignoreF5LimitationForOps = SERVER_BUILDER
            .comment("If this option is enabled, server operators are exempt from the restriction of third-party viewing.")
            .define("ignoreF5LimitationForOps", false)

        /*SBWEnableF5Driver = SERVER_BUILDER
            .comment("If superbwarfare is installed, this rule allows players in the driver's seat of a vehicle to use third-person view.")
            .define("SBWEnableF5Driver", true)*/

        SERVER_BUILDER.pop()
        SERVER_SPEC = SERVER_BUILDER.build()
    }

    fun register() {
        ModLoadingContext.get().registerConfig(
            ModConfig.Type.SERVER,
            SERVER_SPEC
        )
    }
}