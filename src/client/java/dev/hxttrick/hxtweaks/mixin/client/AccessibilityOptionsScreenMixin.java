package dev.hxttrick.hxtweaks.mixin.client;

import dev.hxttrick.hxtweaks.HxTweaksClient;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(AccessibilityOptionsScreen.class)
public class AccessibilityOptionsScreenMixin {
	@Inject(at = @At("RETURN"), method = "getOptions", cancellable = true)
	private static void modifyOptions(GameOptions gameOptions, CallbackInfoReturnable<SimpleOption<?>[]> cir) {
		SimpleOption<?>[] vanilla = cir.getReturnValue();

		List<SimpleOption<?>> list = new ArrayList<>(Arrays.asList(vanilla));

		list.add(HxTweaksClient.getShowLocatorBar());

		cir.setReturnValue(list.toArray(new SimpleOption[0]));
	}
}