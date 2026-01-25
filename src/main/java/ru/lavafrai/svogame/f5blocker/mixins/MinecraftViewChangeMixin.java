package ru.lavafrai.svogame.f5blocker.mixins;

import net.minecraft.client.CameraType;
import net.minecraft.client.Options;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.lavafrai.svogame.f5blocker.config.F5ClientState;


@Mixin(Minecraft.class)
public abstract class MinecraftViewChangeMixin {
    @Shadow
    @Final
    public Options options;

    @Inject(
        method = {"handleKeybinds"},
        at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z")}
    )
    private void onThirdPersonToggle(CallbackInfo ci) {
        if (F5ClientState.INSTANCE.isF5Allowed()) return;

        if (options.getCameraType() != CameraType.FIRST_PERSON) {
            options.setCameraType(CameraType.FIRST_PERSON);
        }
    }
}
