package dev.hxttrick.hxtweaks.mixin.client;

import dev.hxttrick.hxtweaks.HxTweaksClient;
import dev.hxttrick.hxtweaks.HxTweaksConfig;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoatItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerEntityInteractionManagerMixin {

    @Unique private float savedYaw;
    @Unique private float savedHeadYaw;
    @Unique private boolean isSpoofing;

    @Inject(method = "interactItem", at = @At("HEAD"))
    private void beforeInteractItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        double boatSnapping = HxTweaksConfig.INSTANCE.boatSnapping;
        if (boatSnapping == 0) return;

        ItemStack stack = player.getStackInHand(hand);
        if (!(stack.getItem() instanceof BoatItem)) return;

        Float snapped = getSnappedYaw(player.getYaw(), (float) boatSnapping);
        if (snapped == null) return;

        isSpoofing = true;
        savedYaw = player.getYaw();
        savedHeadYaw = player.getHeadYaw();

        player.setYaw(snapped);
        player.setHeadYaw(snapped);
    }

    @Inject(method = "interactItem", at = @At("TAIL"))
    private void afterInteractItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!isSpoofing) return;

        player.setYaw(savedYaw);
        player.setHeadYaw(savedHeadYaw);
        isSpoofing = false;
    }

    @Unique
    private Float getSnappedYaw(float yaw, float threshold) {
        yaw = MathHelper.wrapDegrees(yaw);

        float[] cardinals = { 0f, 90f, 180f, -90f };

        Float bestCardinal = null;
        float bestDiff = threshold + 1e-4f;

        for (float c : cardinals) {
            float diff = Math.abs(MathHelper.wrapDegrees(yaw - c));
            if (diff < bestDiff) {
                bestDiff = diff;
                bestCardinal = c;
            }
        }

        return bestCardinal;
    }
}
