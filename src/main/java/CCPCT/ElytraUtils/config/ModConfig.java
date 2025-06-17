package CCPCT.ElytraUtils.config;

import net.fabricmc.loader.api.FabricLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {
    public boolean chatfeedback = true;
    public boolean disableFireworkOnWall = true;
    public boolean durabilityAlert = true;
    public boolean middleClickQuickFirework = true;
    public boolean replaceBreakingElytra = true;
    public boolean debug = false;

    public boolean flightOverlay = false;
    public int flightOverlayColour = 0x15FFFFFF;
    public int flightOverlayWidth = 100;

    public boolean fireworkCount = false;
    public int fireworkCountColour = 0x000000;
    public int fireworkCountx = 20;
    public int fireworkCounty = 20;



    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("elytrautils-config.json");

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
