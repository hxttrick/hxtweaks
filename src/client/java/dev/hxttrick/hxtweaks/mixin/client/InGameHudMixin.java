package dev.hxttrick.hxtweaks.mixin.client;

import dev.hxttrick.hxtweaks.HxPeripherals;
import dev.hxttrick.hxtweaks.HxTweaksClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud.BarType;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    /** Locator bar **/

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



    /** Peripherals **/

    @Shadow protected abstract void renderHotbarItem(DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack, int seed);

    @Inject(method = "renderHotbar", at = @At("HEAD"))
    private void renderPeripherals(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {

        if (!(MinecraftClient.getInstance().getCameraEntity() instanceof ClientPlayerEntity player)) return;

        HxPeripherals.renderPeripherals(context, player, slot -> {
            renderHotbarItem(
                    context,
                    slot.x + slot.itemOffsetX,
                    slot.y + slot.itemOffsetY,
                    tickCounter,
                    player,
                    slot.peripheral,
                    0
            );
        });
    }
}