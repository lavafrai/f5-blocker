package ru.lavafrai.svogame.f5blocker.mixins;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.lavafrai.svogame.f5blocker.config.F5ClientState;


@Mixin(Options.class)
public abstract class OptionsViewChangeMixin {
    @Inject(
        method = {"setCameraType"},
        at = {@At("HEAD")},
        cancellable = true
    )
    private void preventThirdPersonView(CameraType cameraType, CallbackInfo ci) {
        if (F5ClientState.INSTANCE.isF5Blocked() && cameraType != CameraType.FIRST_PERSON) {
            ci.cancel();
        }
    }
}
