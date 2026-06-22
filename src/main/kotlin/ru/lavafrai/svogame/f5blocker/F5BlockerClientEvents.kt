package ru.lavafrai.svogame.f5blocker

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.ClientPlayerNetworkEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import ru.lavafrai.svogame.f5blocker.config.F5ClientState

@Mod.EventBusSubscriber(modid = F5BlockerMod.MODID, value = [Dist.CLIENT])
object F5BlockerClientEvents {
    @SubscribeEvent
    fun onLoggingOut(event: ClientPlayerNetworkEvent.LoggingOut) {
        F5ClientState.setF5Blocked(false)
    }
}
