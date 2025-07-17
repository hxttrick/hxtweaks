package dev.hxttrick.hxtweaks.mixin.client;

import dev.hxttrick.hxtweaks.HxTweaksClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.nbt.NbtCompound;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
    @Unique private static final Logger LOGGER = HxTweaksClient.LOGGER;

    @Inject(method = "update", at = @At("TAIL"))
    private void onUpdate(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        if (nbt.contains("showLocatorBar")) {
            SimpleOption<Boolean> option = HxTweaksClient.getShowLocatorBar();
            boolean value = Boolean.parseBoolean(nbt.getString("showLocatorBar").get());
            option.setValue(value);
        }
    }

    @Shadow @Final private java.io.File optionsFile;

    @Inject(method = "write", at = @At("TAIL"))
    private void onWrite(CallbackInfo ci) {
        boolean val = HxTweaksClient.getShowLocatorBar().getValue();
        try {
            Files.write(
                    optionsFile.toPath(),
                    List.of("showLocatorBar:" + val),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
