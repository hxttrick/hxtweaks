package dev.hxttrick.hxtweaks;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class HxTweaksConfigScreen extends GameOptionsScreen {

    private boolean showLocatorBar;
    private double boatSnapping;

    protected HxTweaksConfigScreen(Screen previous) {
        super(previous, MinecraftClient.getInstance().options, Text.translatable("hx.config.title"));
    }

    @Override
    protected void addOptions() {
        double MIN, MAX;

        MIN = 0.0; MAX = 10.0;
        this.body.addSingleOptionEntry(
                new SimpleOption<Double>(
                        "hx.config.boatSnapping",
                        SimpleOption.emptyTooltip(),
                        (text, value) -> {
                            String suffix = (value == 0) ? Text.translatable("options.off").getString() : value + "°";
                            return Text.translatable("hx.config.boatSnapping", suffix);
                        },
                        SimpleOption.DoubleSliderCallbacks.INSTANCE.withModifier(
                                progress -> Util.snap(progress * MAX, .5),
                                value -> Util.snap(value, .5) / MAX
                        ),
                        HxTweaksConfig.INSTANCE.boatSnapping,
                        newValue -> boatSnapping = Util.snap(newValue, .5)
                )
        );

        this.body.addSingleOptionEntry(
                HxTweaksClient.getShowLocatorBar()
                /*new SimpleOption<Boolean>(
                        "hx.config.showLocatorBar",
                        SimpleOption.emptyTooltip(),
                        (text, value) -> value
                                ? Text.translatable("options.on")
                                : Text.translatable("options.off"),
                        SimpleOption.BOOLEAN,
                        HxTweaksConfig.INSTANCE.showLocatorBar,
                        newValue -> showLocatorBar = !showLocatorBar
                )*/
        );
    }

    @Override
    public void removed() {
        applyAndSave();
    }

    private void applyAndSave() {
        HxTweaksConfig cfg = HxTweaksConfig.INSTANCE;
        cfg.boatSnapping = this.boatSnapping;
        cfg.showLocatorBar = this.showLocatorBar;
        HxTweaksConfig.save();
        HxTweaksClient.LOGGER.info("Saving config...");
    }

    private static class Util {
        static double snap(double value, double step) {
            return Math.round(value / step) * step;
        }
    }

}
