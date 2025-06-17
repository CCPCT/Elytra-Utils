package CCPCT.ElytraUtils.config;

import net.fabricmc.loader.api.FabricLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {
    public boolean autoTotem = false;
    public boolean chatfeedback = true;

    public boolean customSound = true;
    public String customSoundName = "minecraft:item.shield.break";
    public Float customSoundVolume = 1.0f;

    public boolean totemPopScreen = true;
    public int totemPopScreenColour = 0xFF0000;
    public int totemPopScreenAlpha = 255;
    public int totemPopScreenDuration = 20;
    public int totemPopScreenWidth = 100;

    public int totemCountTime = 0;
    public int totemCountx = 10;
    public int totemCounty = 10;
    public int totemCountColour = 0xFFFFFF;
    public int totemCountAlpha = 255;



    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("totemutils-config.json");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static ModConfig INSTANCE;

    public static ModConfig get() {
        if (INSTANCE == null) {
            load();
        }
        return INSTANCE;
    }

    public static void load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                INSTANCE = GSON.fromJson(Files.newBufferedReader(CONFIG_PATH), ModConfig.class);
            } else {
                INSTANCE = new ModConfig();
                save();
            }
        } catch (IOException e) {
            e.printStackTrace();
            INSTANCE = new ModConfig();
        }
    }

    public static void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(get()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
