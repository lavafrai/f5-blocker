@file:Suppress("DEPRECATION", "removal")

package ru.lavafrai.svogame.metrics


@Mod(TemplateMod.MODID)
class TemplateMod {
    companion object {
        const val MODID = "template"
        private val LOGGER = LogUtils.getLogger()
        private lateinit var instance: Metrics
    }

    init {
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            initializeMod()
        } else {
            LOGGER.info("TemplateMod mod is disabled on client side.")
        }
    }

    fun initializeMod() {
        instance = this
        MOD_BUS.addListener(this::commonSetup)
    }

    private fun commonSetup(event: FMLCommonSetupEvent) {
        LOGGER.info("Template mod started.")
        // MinecraftForge.EVENT_BUS.register(Listener())
        // All listeners
    }
}
