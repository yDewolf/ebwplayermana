package com.github.ydewolf.ebwplayermana.content.mana;

import com.github.ydewolf.ebwplayermana.PlayerManaConfig;

public class ManaCalculator {

    private static final double MAX_MANA_BONUS = PlayerManaConfig.maxManaBonus;
    private static final double MANA_INCREASE_FACTOR = PlayerManaConfig.manaIncreaseRate;

    private static final double MAX_REGEN_BOUNS = PlayerManaConfig.maxRegenBonus;
    private static final double REGEN_INCREASE_FACTOR = PlayerManaConfig.manaRegenIncreaseRate;

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