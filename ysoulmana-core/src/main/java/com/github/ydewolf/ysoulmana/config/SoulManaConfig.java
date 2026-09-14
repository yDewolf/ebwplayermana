package com.github.ydewolf.ysoulmana.config;

import com.github.ydewolf.ysoulmana.SoulManaMod;
import com.github.ydewolf.ysoulmana.content.mana.helpers.ManaUsageHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = SoulManaMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SoulManaConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // Spec Values
    private static final ForgeConfigSpec.BooleanValue DISABLE_MANA_SYSTEM;
    private static final ForgeConfigSpec.BooleanValue INCREMENT_ON_MANA_USE;
    private static final ForgeConfigSpec.BooleanValue CONSUME_FROM_WAND;

    private static final ForgeConfigSpec.DoubleValue MANA_COST_TO_MAX_MANA;
    private static final ForgeConfigSpec.DoubleValue MANA_COST_TO_MAX_MANA_CONTINUOUS;
    private static final ForgeConfigSpec.DoubleValue MANA_COST_TO_MANA_REGEN;

    private static final ForgeConfigSpec.IntValue BASE_MAX_MANA;
    private static final ForgeConfigSpec.DoubleValue MIN_MANA_REGEN;

    private static final ForgeConfigSpec.IntValue MAX_MANA_BONUS;
    private static final ForgeConfigSpec.DoubleValue MANA_INCREASE_RATE;

    private static final ForgeConfigSpec.DoubleValue MAX_REGEN_BONUS;
    private static final ForgeConfigSpec.DoubleValue MANA_REGEN_INCREASE_RATE;
    private static final ForgeConfigSpec.IntValue MANA_REGEN_COOLDOWN;

    public static boolean disablePlayerMana;
    public static boolean allowItemManaConsumption;
    public static boolean incrementOnManaUse;

    public static double manaCostToMaxManaRate;
    public static double manaCostToMaxManaRateContinuous;
    public static double manaCostToManaRegen;

    public static int baseMana;
    public static double baseManaRegen;

    public static double manaIncreaseRate;
    public static int maxManaBonus;

    public static double maxRegenBonus;
    public static double manaRegenIncreaseRate;

    public static int manaRegenTickRate = 10;
    public static int manaRegenCooldownAfterCast;

    public static float initialMaxManaPercentOnSpawn = 0.3f;

    static {
        BUILDER.push("Modules");

        BUILDER.comment("Disables Player Mana System Completely");
        DISABLE_MANA_SYSTEM = BUILDER.worldRestart().define("disableManaSystem", false);

        BUILDER.comment("Consumes Wand/Item Mana if the player is out of mana");
        CONSUME_FROM_WAND = BUILDER.define("consumeWandMana", true);

        BUILDER.pop();


        BUILDER.push("Progression");

        BUILDER.comment("Should increment mana every time a player casts a spell (base_mana + spell_cost * rate_multiplier)");
        INCREMENT_ON_MANA_USE = BUILDER.define("incrementManaOnUse", false);

        BUILDER.comment("Mana Cost to Max Mana Rate");
        MANA_COST_TO_MAX_MANA = BUILDER.defineInRange("manaCostToMaxMana", 0.003, 0.0, 1.0);

        BUILDER.comment("Mana Cost to Max Mana Rate for Continuous Spells");
        MANA_COST_TO_MAX_MANA_CONTINUOUS = BUILDER.defineInRange("manaCostToMaxManaContinuous", 5e-4, 0.0, 1.0);

        BUILDER.comment("Mana Cost to Mana Regen increment rate");
        MANA_COST_TO_MANA_REGEN = BUILDER.defineInRange("manaCostToManaRegen", 1e-6, 0.0, 1.0);

        BUILDER.comment("Max Mana Bonus");
        MAX_MANA_BONUS = BUILDER.defineInRange("maxManaBonus", 2500, 0, 5000);

        BUILDER.comment("Rate which Mana Bonus increases (bonus = maxManaBonus * (1 - e^(-rate * totalManaUsed)))");
        MANA_INCREASE_RATE = BUILDER.defineInRange("manaIncreaseRate", 1e-6, 1e-9, 0.5);

        BUILDER.comment("Max Mana Regen Bonus");
        MAX_REGEN_BONUS = BUILDER.defineInRange("maxManaRegenBonus", 2500.0, 0.0, 5000.0);

        BUILDER.comment("Rate which Mana Regen Bonus increases (bonus = maxManaBonus * (1 - e^(-rate * totalManaUsed)))");
        MANA_REGEN_INCREASE_RATE = BUILDER.defineInRange("manaRegenIncreaseRate", 1e-7, 1e-15, 0.1);

        BUILDER.pop();


        BUILDER.push("ManaDefaults");

        BUILDER.comment("Mana player starts with");
        BASE_MAX_MANA = BUILDER.defineInRange("baseMaxMana", 75, 10, Integer.MAX_VALUE);

        BUILDER.comment("Minimum Mana a player regens per second");
        MIN_MANA_REGEN = BUILDER.defineInRange("minManaRegen", 0.5, 0.01, 2.0);

        BUILDER.comment("Cooldown before mana starts regenerating after casting a spell (cooldown * 10 -> ticks)");
        MANA_REGEN_COOLDOWN = BUILDER.defineInRange("manaRegenCooldown", 2, 0, 10);

        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    protected static void updateAllVariables() {
        disablePlayerMana = DISABLE_MANA_SYSTEM.get();
        allowItemManaConsumption = CONSUME_FROM_WAND.get();
        incrementOnManaUse = INCREMENT_ON_MANA_USE.get();

        manaCostToMaxManaRate = MANA_COST_TO_MAX_MANA.get();
        manaCostToMaxManaRateContinuous = MANA_COST_TO_MAX_MANA_CONTINUOUS.get();
        manaCostToManaRegen = MANA_COST_TO_MANA_REGEN.get();

        baseMana = BASE_MAX_MANA.get();
        baseManaRegen = MIN_MANA_REGEN.get();

        maxManaBonus = MAX_MANA_BONUS.get();
        manaIncreaseRate = MANA_INCREASE_RATE.get();

        maxRegenBonus = MAX_REGEN_BONUS.get();
        manaRegenIncreaseRate = MANA_REGEN_INCREASE_RATE.get();

        manaRegenCooldownAfterCast = MANA_REGEN_COOLDOWN.get();
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        updateAllVariables();
    }

    @SubscribeEvent
    static void onConfigEvent(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            updateAllVariables();
        }
    }

    @SubscribeEvent
    static void onConfigReload(final ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            onConfigEvent(event);

            if (net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer() != null) {
                net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(ManaUsageHelper::reapplyManaAttributes);
            }
        }
    }
}