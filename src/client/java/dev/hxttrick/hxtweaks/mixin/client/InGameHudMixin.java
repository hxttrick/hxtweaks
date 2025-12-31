package dev.hxttrick.hxtweaks.mixin.client;

import dev.hxttrick.hxtweaks.HxTweaksClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud.BarType;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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


    @Shadow @Final private MinecraftClient client;

    private static final Identifier EFFECT_BACKGROUND_TEXTURE = Identifier.ofVanilla("hud/effect_background");
    private static final ItemStack CLOCK_ITEM_STACK = new ItemStack(Items.CLOCK);

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"))
    private void drawClockAsEffect(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!shouldDrawClockEffect()) return;

        //---------- config ----------//
        int effectBackgroundSize = 24;
        int effectItemSize = 16;
        int marginX = 1;
        int marginY = 1;
        //----------------------------//

        int x = context.getScaledWindowWidth() - (effectBackgroundSize + marginX);
        int y = client.isDemo() ? 15 + marginY : marginY;

        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, EFFECT_BACKGROUND_TEXTURE, x, y, effectBackgroundSize, effectBackgroundSize);
        int itemOffset = (effectBackgroundSize - effectItemSize) / 2;
        context.drawItem(CLOCK_ITEM_STACK, x + itemOffset, y + itemOffset);
    }

    @ModifyVariable(method = "renderStatusEffectOverlay", at = @At("STORE"), ordinal = 0) // beneficial effects counter
    private int offsetBeneficialEffects(int original) {
        return shouldDrawClockEffect() ? Math.min(1, original + 1) : original;
    }

    private boolean shouldDrawClockEffect() {
        return client.player != null && client.player.getInventory().contains(CLOCK_ITEM_STACK);
    }
}