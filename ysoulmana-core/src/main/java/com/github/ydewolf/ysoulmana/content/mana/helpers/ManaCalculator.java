package com.github.ydewolf.ysoulmana.content.mana.helpers;

import com.github.ydewolf.ysoulmana.SoulManaConfig;

public class ManaCalculator {

    private static final double MAX_MANA_BONUS = SoulManaConfig.maxManaBonus;
    private static final double MANA_INCREASE_FACTOR = SoulManaConfig.manaIncreaseRate;

    private static final double MAX_REGEN_BOUNS = SoulManaConfig.maxRegenBonus;
    private static final double REGEN_INCREASE_FACTOR = SoulManaConfig.manaRegenIncreaseRate;

    public static double calculateManaBonus(float totalManaUsed) {
        // ManaBase + Teto * (1 - e^(-k * totalManaUsed))
        return MAX_MANA_BONUS * (1.0D - Math.exp(-MANA_INCREASE_FACTOR * totalManaUsed));
    }

    public static double calculateRegenBonus(float totalManaUsed) {
        return MAX_REGEN_BOUNS * (1.0D - Math.exp(-REGEN_INCREASE_FACTOR * totalManaUsed));
    }

    public static double calculateMaxMana(double baseMaxMana, float totalManaUsed) {
        if (totalManaUsed <= 0) {
            return baseMaxMana;
        }

        return baseMaxMana + calculateManaBonus(totalManaUsed);
    }

    public static double calculateManaRegen(double baseManaRegen, float totalManaUsed) {
        if (totalManaUsed <= 0) {
            return baseManaRegen;
        }

        return baseManaRegen + calculateRegenBonus(totalManaUsed);
    }
}