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
                .setSaveConsumer(newValue -> {
                    ModConfig.get().chatfeedback = newValue;
                })
                .build());

        generalTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Disable Firework on wall"),ModConfig.get().disableFireworkOnWall)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Boost instead of placing firework on wall in flight"))
                .setSaveConsumer(newValue -> {
                    ModConfig.get().disableFireworkOnWall = newValue;
                })
                .build());

        generalTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Elytra durability alert"),ModConfig.get().durabilityAlert)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Text when elytra have low durability (<=10)"))
                .setSaveConsumer(newValue -> {
                    ModConfig.get().durabilityAlert = newValue;
                })
                .build());

        generalTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Replace breaking elytra"),ModConfig.get().replaceBreakingElytra)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Text when elytra have low durability (<=10)"))
                .setSaveConsumer(newValue -> {
                    ModConfig.get().replaceBreakingElytra = newValue;
                })
                .build());

        screenTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enable Overlay"),ModConfig.get().flightOverlay)
                .setDefaultValue(false)
                .setTooltip(Text.literal("Show transparent screen outline in flight"))
                .setSaveConsumer(newValue -> {
                    ModConfig.get().flightOverlay = newValue;
                })
                .build());

        screenTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enable Icon"),ModConfig.get().flightIcon)
                .setDefaultValue(false)
                .setTooltip(Text.literal("Show elytra icon in flight"))
                .setSaveConsumer(newValue -> {
                    ModConfig.get().flightIcon = newValue;
                })
                .build());

        return builder.build();
    }
}
