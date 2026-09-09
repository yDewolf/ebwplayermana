package com.github.ydewolf.ebwplayermana;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = EBWManaMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PlayerManaConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

//    private static final ForgeConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER.comment("Whether to log the dirt block on common setup").define("logDirtBlock", true);

    private static final ForgeConfigSpec.BooleanValue DISABLE_MANA_SYSTEM = BUILDER.comment("Disables Player Mana System Completely").define("disableManaSystem", false);
    private static final ForgeConfigSpec.BooleanValue INCREMENT_ON_MANA_USE = BUILDER.comment("Should increment mana every time a player casts a spell (base_mana + spell_cost * rate_multiplier)").define("incrementManaOnUse", false);

    private static final ForgeConfigSpec.IntValue INITIAL_MANA_PERCENT = BUILDER.comment("Initial Mana Percentage").defineInRange("initialManaPercent", 30, 0, 100);

    private static final ForgeConfigSpec.DoubleValue MANA_COST_TO_MAX_MANA = BUILDER.comment("Mana Cost to Max Mana Rate").defineInRange("manaCostToMaxMana", 0.003, 0.0, 1.0);
    private static final ForgeConfigSpec.DoubleValue MANA_COST_TO_MAX_MANA_CONTINUOUS = BUILDER.comment("Mana Cost to Max Mana Rate for Continuous Spells").defineInRange("manaCostToMaxManaContinuous", 0.0005, 0.0, 1.0);
    private static final ForgeConfigSpec.DoubleValue MANA_COST_TO_MANA_REGEN = BUILDER.comment("Mana Cost to Mana Regen increment rate").defineInRange("manaCostToManaRegen", 0.000001, 0.0, 1.0);

    private static final ForgeConfigSpec.IntValue BASE_MAX_MANA = BUILDER.comment("Mana player starts with").defineInRange("baseMaxMana", 75, 10, 1024);
    private static final ForgeConfigSpec.DoubleValue MIN_MANA_REGEN = BUILDER.comment("Minimum Mana a player regens per second").defineInRange("minManaRegen", 0.5, 0.01, 2);

    private static final ForgeConfigSpec.IntValue MAX_MANA_BONUS = BUILDER.comment("Max Mana Bonus").defineInRange("maxManaBonus", 2500, 0, 5000);
    private static final ForgeConfigSpec.DoubleValue MANA_INCREASE_RATE = BUILDER.comment("Rate which Mana Bonus increases (bonus = maxManaBonus * (1 - e^(-rate * totalManaUsed)))").defineInRange("manaIncreaseRate", 0.001, 0.0001, 0.9);
//    public static final ForgeConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER.comment("What you want the introduction message to be for the magic number").define("magicNumberIntroduction", "The magic number is... ");

//    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER.comment("A list of items to log on common setup.").defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), PlayerManaConfig::validateItemName);

    static final ForgeConfigSpec SPEC = BUILDER.build();

//    public static boolean logDirtBlock;
    public static boolean disablePlayerMana;
    public static float initialManaPercent;
    public static double manaCostToMaxManaRate;
    public static double manaCostToMaxManaRateContinuous;
    public static double manaCostToManaRegen;
    public static int baseMana;
    public static double minManaRegen;
    public static boolean incrementOnManaUse;

    public static double manaIncreaseRate;
    public static int maxManaBonus;
//    public static String magicNumberIntroduction;
//    public static Set<Item> items;

//    private static boolean validateItemName(final Object obj) {
//        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
//    }
//
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
//        logDirtBlock = LOG_DIRT_BLOCK.get();
        disablePlayerMana = DISABLE_MANA_SYSTEM.get();

        initialManaPercent = ((float) INITIAL_MANA_PERCENT.get() / 100);
        manaCostToMaxManaRate = MANA_COST_TO_MAX_MANA.get();
        manaCostToMaxManaRateContinuous = MANA_COST_TO_MAX_MANA_CONTINUOUS.get();
        manaCostToManaRegen = MANA_COST_TO_MANA_REGEN.get();
        baseMana = BASE_MAX_MANA.get();
        minManaRegen = MIN_MANA_REGEN.get();
        incrementOnManaUse = INCREMENT_ON_MANA_USE.get();

        manaIncreaseRate = MANA_INCREASE_RATE.get();
        maxManaBonus = MAX_MANA_BONUS.get();

//        magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();
//
//        // convert the list of strings into a set of items
//        items = ITEM_STRINGS.get().stream().map(itemName -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName))).collect(Collectors.toSet());
    }
}
