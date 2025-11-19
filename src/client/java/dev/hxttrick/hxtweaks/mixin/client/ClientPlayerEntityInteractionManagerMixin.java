package dev.hxttrick.hxtweaks.mixin.client;

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

        ItemStack stack = player.getStackInHand(hand);
        if (!(stack.getItem() instanceof BoatItem)) return;

        Float snapped = getSnappedYaw(player.getYaw());
        if (snapped == null) return;

        isSpoofing = true;
        savedYaw = player.getYaw();
        savedHeadYaw = player.getHeadYaw();

        //HxTweaksClient.LOGGER.info("Spoofing yaw to " + snapped);

        player.setYaw(snapped);
        player.setHeadYaw(snapped);

        //HxTweaksClient.LOGGER.info("Current yaw (player, head): " + player.getYaw() + ", " + player.getHeadYaw());
    }

    @Inject(method = "interactItem", at = @At("TAIL"))
    private void afterInteractItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!isSpoofing) return;

        player.setYaw(savedYaw);
        player.setHeadYaw(savedHeadYaw);
        isSpoofing = false;
    }

    @Unique
    private Float getSnappedYaw(float yaw) {
        yaw = MathHelper.wrapDegrees(yaw);

        float[] cardinals = { 0f, 90f, 180f, -90f };
        float threshold = 2.0f; // degrees

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
