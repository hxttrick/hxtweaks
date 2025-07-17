package dev.hxttrick.hxtweaks.mixin.client;

import dev.hxttrick.hxtweaks.HxTweaksClient;
import net.minecraft.client.gui.hud.InGameHud.BarType;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    // Should always show experience bar over locator bar
    @Inject(at = @At("RETURN"), method = "shouldShowExperienceBar", cancellable = true)
    private void onShouldShowExperienceBar(CallbackInfoReturnable<Boolean> cir) {
        if (!shouldShowLocatorBar()) cir.setReturnValue(true);
    }

    // Should always show jump bar over locator bar
    @Inject(at = @At("RETURN"), method = "shouldShowJumpBar", cancellable = true)
    private void onShouldShowJumpBar(CallbackInfoReturnable<Boolean> cir) {
        if (!shouldShowLocatorBar()) cir.setReturnValue(true);
    }

    // Hide the bar if it still tries displaying the locator bar
    @Inject(at = @At("RETURN"), method = "getCurrentBarType", cancellable = true)
    private void onGetCurrentBarType(CallbackInfoReturnable<BarType> cir) {
        if (!shouldShowLocatorBar() && cir.getReturnValue() == BarType.LOCATOR) cir.setReturnValue(BarType.EMPTY);
    }

    // Helper function for readability
    @Unique private boolean shouldShowLocatorBar() {
        return HxTweaksClient.getShowLocatorBar().getValue();
    }
}