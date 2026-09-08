package com.github.ydewolf.ebwplayermana;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = EBWManaMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PlayerManaConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

//    private static final ForgeConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER.comment("Whether to log the dirt block on common setup").define("logDirtBlock", true);

    private static final ForgeConfigSpec.IntValue INITIAL_MANA_PERCENT = BUILDER.comment("Initial Mana Percentage").defineInRange("initialManaPercent", 30, 0, 100);
    private static final ForgeConfigSpec.DoubleValue MANA_COST_TO_MAX_MANA = BUILDER.comment("Mana Cost to Max Mana Rate").defineInRange("manaCostToMaxMana", 0.003, 0.0, 1.0);
    private static final ForgeConfigSpec.DoubleValue MANA_COST_TO_MAX_MANA_CONTINUOUS = BUILDER.comment("Mana Cost to Max Mana Rate for Continuous Spells").defineInRange("manaCostToMaxManaContinuous", 0.0005, 0.0, 1.0);

//    public static final ForgeConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER.comment("What you want the introduction message to be for the magic number").define("magicNumberIntroduction", "The magic number is... ");

//    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER.comment("A list of items to log on common setup.").defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), PlayerManaConfig::validateItemName);

    static final ForgeConfigSpec SPEC = BUILDER.build();

//    public static boolean logDirtBlock;
    public static float initialManaPercent;
    public static double manaCostToMaxManaRate;
    public static double manaCostToMaxManaRateContinuous;
//    public static String magicNumberIntroduction;
//    public static Set<Item> items;

//    private static boolean validateItemName(final Object obj) {
//        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
//    }
//
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
//        logDirtBlock = LOG_DIRT_BLOCK.get();
        initialManaPercent = ((float) INITIAL_MANA_PERCENT.get() / 100);
        manaCostToMaxManaRate = MANA_COST_TO_MAX_MANA.get();
        manaCostToMaxManaRateContinuous = MANA_COST_TO_MAX_MANA_CONTINUOUS.get();

//        magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();
//
//        // convert the list of strings into a set of items
//        items = ITEM_STRINGS.get().stream().map(itemName -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName))).collect(Collectors.toSet());
    }
}
