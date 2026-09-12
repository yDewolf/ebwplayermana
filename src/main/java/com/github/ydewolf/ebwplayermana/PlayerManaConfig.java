package com.github.ydewolf.ebwplayermana;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = EBWManaMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PlayerManaConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue DISABLE_MANA_SYSTEM = BUILDER.comment("Disables Player Mana System Completely").define("disableManaSystem", false);
    private static final ForgeConfigSpec.BooleanValue INCREMENT_ON_MANA_USE = BUILDER.comment("Should increment mana every time a player casts a spell (base_mana + spell_cost * rate_multiplier)").define("incrementManaOnUse", false);
    private static final ForgeConfigSpec.BooleanValue CONSUME_FROM_WAND = BUILDER.comment("Consumes Wand's Mana if the player is out of mana").define("consumeWandMana", true);

    private static final ForgeConfigSpec.DoubleValue MANA_COST_TO_MAX_MANA = BUILDER.comment("Mana Cost to Max Mana Rate").defineInRange("manaCostToMaxMana", 0.003, 0.0, 1.0);
    private static final ForgeConfigSpec.DoubleValue MANA_COST_TO_MAX_MANA_CONTINUOUS = BUILDER.comment("Mana Cost to Max Mana Rate for Continuous Spells").defineInRange("manaCostToMaxManaContinuous", 0.0005, 0.0, 1.0);
    private static final ForgeConfigSpec.DoubleValue MANA_COST_TO_MANA_REGEN = BUILDER.comment("Mana Cost to Mana Regen increment rate").defineInRange("manaCostToManaRegen", 0.000001, 0.0, 1.0);

    private static final ForgeConfigSpec.IntValue BASE_MAX_MANA = BUILDER.comment("Mana player starts with").defineInRange("baseMaxMana", 75, 10, 1024);
    private static final ForgeConfigSpec.DoubleValue MIN_MANA_REGEN = BUILDER.comment("Minimum Mana a player regens per second").defineInRange("minManaRegen", 0.5, 0.01, 2);

    private static final ForgeConfigSpec.IntValue MAX_MANA_BONUS = BUILDER.comment("Max Mana Bonus").defineInRange("maxManaBonus", 2500, 0, 5000);
    private static final ForgeConfigSpec.DoubleValue MANA_INCREASE_RATE = BUILDER.comment("Rate which Mana Bonus increases (bonus = maxManaBonus * (1 - e^(-rate * totalManaUsed)))").defineInRange("manaIncreaseRate", 1e-6, 1e-9, 0.5);

    private static final ForgeConfigSpec.IntValue MAX_REGEN_BONUS = BUILDER.comment("Max Mana Regen Bonus").defineInRange("maxManaRegenBonus", 2500, 0, 5000);
    private static final ForgeConfigSpec.DoubleValue MANA_REGEN_INCREASE_RATE = BUILDER.comment("Rate which Mana Regen Bonus increases (bonus = maxManaBonus * (1 - e^(-rate * totalManaUsed)))").defineInRange("manaRegenIncreaseRate", 1e-7, 1e-15, 0.1);
    private static final ForgeConfigSpec.IntValue MANA_REGEN_COOLDOWN = BUILDER.comment("Cooldown before mana starts regenerating after casting a spell (cooldown * (10) -> ticks)").defineInRange("manaRegenCooldown", 2, 0, 10);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean disablePlayerMana;
    public static boolean consumeWandIfNoPlayerMana;

    public static double manaCostToMaxManaRate;
    public static double manaCostToMaxManaRateContinuous;
    public static double manaCostToManaRegen;
    public static int baseMana;
    public static double baseManaRegen;
    public static boolean incrementOnManaUse;

    public static double manaIncreaseRate;
    public static int maxManaBonus;

    public static double maxRegenBonus;
    public static double manaRegenIncreaseRate;

    public static int manaRegenTickRate = 10;
    public static int manaRegenCooldownAfterSpell;

    public static float initialMaxManaPercentOnSpawn = 0.3f;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        disablePlayerMana = DISABLE_MANA_SYSTEM.get();

        manaCostToMaxManaRate = MANA_COST_TO_MAX_MANA.get();
        manaCostToMaxManaRateContinuous = MANA_COST_TO_MAX_MANA_CONTINUOUS.get();
        manaCostToManaRegen = MANA_COST_TO_MANA_REGEN.get();
        baseMana = BASE_MAX_MANA.get();
        baseManaRegen = MIN_MANA_REGEN.get();
        incrementOnManaUse = INCREMENT_ON_MANA_USE.get();

        manaIncreaseRate = MANA_INCREASE_RATE.get();
        maxManaBonus = MAX_MANA_BONUS.get();

        manaRegenIncreaseRate = MANA_REGEN_INCREASE_RATE.get();
        maxRegenBonus = MAX_REGEN_BONUS.get();

        manaRegenCooldownAfterSpell = MANA_REGEN_COOLDOWN.get();

        consumeWandIfNoPlayerMana = CONSUME_FROM_WAND.get();
    }
}
