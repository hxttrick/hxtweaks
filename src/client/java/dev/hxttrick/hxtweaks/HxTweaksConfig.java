package dev.hxttrick.hxtweaks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class HxTweaksConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static File configFile;

    public boolean showLocatorBar = true;
    public double boatSnapping = 2.0;

    public static HxTweaksConfig INSTANCE = new HxTweaksConfig();

    public static void init(File configDir) {
        if (!configDir.exists()) configDir.mkdirs();
        configFile = new File(configDir, "hxtweaks.json");
        load();
    }

    public static void load() {
        if (configFile == null || !configFile.exists()) {
            save();
            return;
        }
        try (FileReader reader = new FileReader(configFile)) {
            HxTweaksConfig loaded = GSON.fromJson(reader, HxTweaksConfig.class);
            if (loaded != null) INSTANCE = loaded;
        } catch (IOException e) {
            HxTweaksClient.LOGGER.error(e.getMessage());
        }
    }

    public static void save() {
        if (configFile == null) return;
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            HxTweaksClient.LOGGER.error(e.getMessage());
        }
    }
}
