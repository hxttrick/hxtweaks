package dev.hxttrick.hxtweaks;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.option.SimpleOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HxTweaksClient implements ClientModInitializer {

	public static final String MOD_ID = "hxtweaks";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final SimpleOption<Boolean> showLocatorBar = SimpleOption.ofBoolean("options.showLocatorBar", true);

	@Override
	public void onInitializeClient() {
		LOGGER.info("HxTweaks initialized.");
	}

	public static SimpleOption<Boolean> getShowLocatorBar() {
		return showLocatorBar;
	}
}