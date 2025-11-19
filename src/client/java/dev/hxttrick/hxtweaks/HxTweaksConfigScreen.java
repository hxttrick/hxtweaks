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

    /*
    @Override
    protected void initHeader() {
        this.layout.addHeader(this.title, this.textRenderer);
    }

    @Override
    protected void initBody() {
        int btnWidth = 150;
        int btnHeight = 20;

        GridWidget grid = new GridWidget();
        grid.getMainPositioner().margin(4, 2).alignHorizontalCenter();

        GridWidget.Adder adder = grid.createAdder(2);

        // Show Locator Bar ON/OFF
        adder.add(
                CyclingButtonWidget.onOffBuilder(this.showLocatorBar)
                        .build(0, 0, btnWidth, btnHeight,
                                Text.translatable("hx.config.showLocatorBar"),
                                (button, value) -> {
                                    this.showLocatorBar = value;
                                    applyAndSave();
                                })
        );

        // Boat snapping slider
        adder.add(
                new BoatSnappingSlider(
                        0, 0,
                        btnWidth, btnHeight,
                        this
                )
        );

        this.layout.addBody(grid);
    }

    @Override
    protected void addOptions() {

    }

    private void applyAndSave() {
        HxTweaksConfig cfg = HxTweaksConfig.INSTANCE;
        cfg.boatSnappingThreshold = this.boatSnappingThreshold;
        cfg.showLocatorBar = this.showLocatorBar;
        HxTweaksClient.LOGGER.info("Saving config...");
    }

    @Override
    public void close() {
        applyAndSave();
        MinecraftClient.getInstance().setScreen(parent);
    }

    private static class BoatSnappingSlider extends SliderWidget {
        static double STEP = 0.5;
        static double MIN = 0.5 - 1*STEP;
        static double MAX = 10.0;

        private final HxTweaksConfigScreen parent;

        public BoatSnappingSlider(int x, int y, int width, int height, HxTweaksConfigScreen parent) {
            super(x, y, width, height, Text.empty(), normalize(parent.boatSnappingThreshold));
            this.parent = parent;
            updateMessage();
        }

        private static double normalize(double deg) {
            double clamped = Math.max(MIN, Math.min(MAX, deg));
            return (clamped - MIN) / MAX;
        }

        private static double denormalize(double normalized) {
            return MIN + normalized * MAX;
        }

        private double snap(double v) {
            return Math.round(v / STEP) * STEP;
        }

        @Override
        protected void updateMessage() {
            double snapped = snap(denormalize(this.value));
            String value = (snapped == MIN) ? Text.translatable("options.off").getString() : Double.toString(snapped) + "°";
            this.setMessage(Text.translatable("hx.config.boatSnapping", value));
        }

        @Override
        protected void applyValue() {
            double snapped = snap(denormalize(this.value));
            parent.boatSnappingThreshold = snapped;
            this.value = normalize(snapped);
        }

        @Override
        public void onRelease(Click click) {
            super.onRelease(click);
            parent.applyAndSave();
        }
    }*/
}
