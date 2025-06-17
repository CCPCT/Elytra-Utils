package CCPCT.ElytraUtils.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class configScreen extends Screen {

    protected configScreen() {
        super(Text.literal("Totem Utils Config"));
    }

    public static Screen getConfigScreen(Screen parent) {
        ModConfig.load();
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("Totem Utils Config"))
                .setSavingRunnable(ModConfig::save);

        ConfigCategory generalTab = builder.getOrCreateCategory(Text.literal("General"));
        ConfigCategory screenTab = builder.getOrCreateCategory(Text.literal("Overlay"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Auto Totem toggle

        generalTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Chat feedback"),ModConfig.get().chatfeedback)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Send chat such as durability warning on chat"))
                .setSaveConsumer(newValue -> ModConfig.get().chatfeedback = newValue)
                .build());

        generalTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Disable Firework on wall"),ModConfig.get().disableFireworkOnWall)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Boost instead of placing firework on wall in flight"))
                .setSaveConsumer(newValue -> ModConfig.get().disableFireworkOnWall = newValue)
                .build());

        generalTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Use Middle click to boost"),ModConfig.get().middleClickQuickFirework)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Dont set keybind in options to middle click, as will override pick block :( and i dont see a better solution"))
                .setSaveConsumer(newValue -> ModConfig.get().middleClickQuickFirework = newValue)
                .build());

        generalTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Elytra durability alert"),ModConfig.get().durabilityAlert)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Text when elytra have low durability (<=10)"))
                .setSaveConsumer(newValue -> ModConfig.get().durabilityAlert = newValue)
                .build());

        generalTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Replace breaking elytra"),ModConfig.get().replaceBreakingElytra)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Auto replace elytra to healthy elytra if able"))
                .setSaveConsumer(newValue -> ModConfig.get().replaceBreakingElytra = newValue)
                .build());

        generalTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Debug"),ModConfig.get().debug)
                .setDefaultValue(false)
                .setTooltip(Text.literal("get debug chat for dev. Not recommended for normal use"))
                .setSaveConsumer(newValue -> ModConfig.get().debug = newValue)
                .build());

        screenTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enable Overlay"),ModConfig.get().flightOverlay)
                .setDefaultValue(false)
                .setTooltip(Text.literal("Show transparent screen outline in flight"))
                .setSaveConsumer(newValue -> ModConfig.get().flightOverlay = newValue)
                .build());

        screenTab.addEntry(entryBuilder.startColorField(Text.literal("Overlay Color"), ModConfig.get().flightOverlayColour)
                        .setTooltip(Text.literal("Colour of overlay"))
                .setAlphaMode(true)
                .setDefaultValue(0x15FFFFFF) // ARGB format (e.g., opaque green)
                .setSaveConsumer(newValue -> ModConfig.get().flightOverlayColour = newValue)
                .build()
        );

        screenTab.addEntry(entryBuilder.startIntField(Text.literal("Box Width"), ModConfig.get().flightOverlayWidth)
                .setDefaultValue(100)
                .setSaveConsumer(newValue -> ModConfig.get().flightOverlayWidth = newValue)
                .build()
        );

        screenTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enable Firework count"),ModConfig.get().fireworkCount)
                .setDefaultValue(false)
                .setTooltip(Text.literal("Show elytra icon in flight"))
                .setSaveConsumer(newValue -> ModConfig.get().fireworkCount = newValue)
                .build());

        screenTab.addEntry(entryBuilder.startColorField(Text.literal("Firework count colour"), ModConfig.get().fireworkCountColour)
                .setAlphaMode(false)
                .setDefaultValue(0x100000) // RGB
                .setSaveConsumer(newValue -> ModConfig.get().fireworkCountColour = newValue)
                .build()
        );

        screenTab.addEntry(entryBuilder.startIntField(Text.literal("X coordinate of text"), ModConfig.get().fireworkCountx)
                .setDefaultValue(10)
                .setSaveConsumer(newValue -> ModConfig.get().fireworkCountx = newValue)
                .build()
        );

        screenTab.addEntry(entryBuilder.startIntField(Text.literal("Y coordinate of text"), ModConfig.get().fireworkCounty)
                .setDefaultValue(10)
                .setSaveConsumer(newValue -> ModConfig.get().fireworkCounty = newValue)
                .build()
        );

        return builder.build();
    }
}
