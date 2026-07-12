package CCPCT.ElytraUtils.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class configScreen extends Screen {

    protected configScreen() {
        super(Component.literal("Totem Utils Config"));
    }

    public static Screen getConfigScreen(Screen parent) {
        ModConfig.load();
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Totem Utils Config"))
                .setSavingRunnable(ModConfig::save);

        ConfigCategory generalTab = builder.getOrCreateCategory(Component.literal("General"));
        ConfigCategory screenTab = builder.getOrCreateCategory(Component.literal("Overlay"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Auto Totem toggle

        generalTab.addEntry(entryBuilder.startBooleanToggle(Component.literal("Chat feedback"),ModConfig.get().chatfeedback)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Send chat such as durability warning on chat"))
                .setSaveConsumer(newValue -> ModConfig.get().chatfeedback = newValue)
                .build());

        generalTab.addEntry(entryBuilder.startBooleanToggle(Component.literal("Disable Firework on wall"),ModConfig.get().disableFireworkOnWall)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Boost instead of placing firework on wall/ interact with entities in flight"))
                .setSaveConsumer(newValue -> ModConfig.get().disableFireworkOnWall = newValue)
                .build());

        generalTab.addEntry(entryBuilder.startBooleanToggle(Component.literal("Elytra durability alert"),ModConfig.get().durabilityAlert)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Component when elytra have low durability (<=10)"))
                .setSaveConsumer(newValue -> ModConfig.get().durabilityAlert = newValue)
                .build());

        generalTab.addEntry(entryBuilder.startBooleanToggle(Component.literal("Replace breaking elytra"),ModConfig.get().replaceBreakingElytra)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Auto replace elytra to healthy elytra if able"))
                .setSaveConsumer(newValue -> ModConfig.get().replaceBreakingElytra = newValue)
                .build());

        generalTab.addEntry(entryBuilder.startBooleanToggle(Component.literal("Auto adjust flight delay after spear boost"),ModConfig.get().autoDelayFly)
                .setDefaultValue(true)
                .setTooltip(Component.literal("recommended enabling this option unless unable to fly after spear boost, which set delay manually with slider below"))
                .setSaveConsumer(newValue -> ModConfig.get().autoDelayFly = newValue)
                .build());

        generalTab.addEntry(entryBuilder.startIntSlider(Component.literal("Delay flight after spear boost"),ModConfig.get().delayFly,0,10)
                .setDefaultValue(1)
                        .setMin(0).setMax(10)
                .setTooltip(Component.literal("Prevent lag dropping flight packet by adding delay (in ticks)\n0/1 works well in single player,\nand 2+ works better in multiplayer, (can increase with instability of server connection/ mspt)"))
                .setSaveConsumer(newValue -> ModConfig.get().delayFly = newValue)
                .build());

        generalTab.addEntry(entryBuilder.startBooleanToggle(Component.literal("Debug"),ModConfig.get().debug)
                .setDefaultValue(false)
                .setTooltip(Component.literal("get debug chat for dev. Not recommended for normal use"))
                .setSaveConsumer(newValue -> ModConfig.get().debug = newValue)
                .build());

        screenTab.addEntry(entryBuilder.startBooleanToggle(Component.literal("Enable Overlay"),ModConfig.get().flightOverlay)
                .setDefaultValue(false)
                .setTooltip(Component.literal("Show transparent screen outline in flight"))
                .setSaveConsumer(newValue -> ModConfig.get().flightOverlay = newValue)
                .build());

        screenTab.addEntry(entryBuilder.startColorField(Component.literal("Overlay Color"), ModConfig.get().flightOverlayColour)
                        .setTooltip(Component.literal("Colour of overlay"))
                .setAlphaMode(true)
                .setDefaultValue(0x15FFFFFF) // ARGB format (e.g., opaque green)
                .setSaveConsumer(newValue -> ModConfig.get().flightOverlayColour = newValue)
                .build()
        );

        screenTab.addEntry(entryBuilder.startIntField(Component.literal("Box Width"), ModConfig.get().flightOverlayWidth)
                .setDefaultValue(100)
                .setSaveConsumer(newValue -> ModConfig.get().flightOverlayWidth = newValue)
                .build()
        );

        screenTab.addEntry(entryBuilder.startBooleanToggle(Component.literal("Enable Firework count"),ModConfig.get().fireworkCount)
                .setDefaultValue(false)
                .setTooltip(Component.literal("Show elytra icon in flight"))
                .setSaveConsumer(newValue -> ModConfig.get().fireworkCount = newValue)
                .build());

        screenTab.addEntry(entryBuilder.startColorField(Component.literal("Firework count colour"), ModConfig.get().fireworkCountColour)
                .setAlphaMode(true)
                .setDefaultValue(0xFF000000) // ARGB
                .setSaveConsumer(newValue -> ModConfig.get().fireworkCountColour = newValue)
                .build()
        );

        screenTab.addEntry(entryBuilder.startIntField(Component.literal("X coordinate of Component"), ModConfig.get().fireworkCountx)
                .setDefaultValue(10)
                .setSaveConsumer(newValue -> ModConfig.get().fireworkCountx = newValue)
                .build()
        );

        screenTab.addEntry(entryBuilder.startIntField(Component.literal("Y coordinate of Component"), ModConfig.get().fireworkCounty)
                .setDefaultValue(10)
                .setSaveConsumer(newValue -> ModConfig.get().fireworkCounty = newValue)
                .build()
        );

        return builder.build();
    }
}
