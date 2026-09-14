package com.github.ydewolf.ysoulmana.content.mana.helpers;

import com.github.ydewolf.ysoulmana.config.SoulManaConfig;

public class ManaCalculator {

    private static double getMaxManaBonus() { return SoulManaConfig.maxManaBonus; }
    private static double getManaIncreaseFactor() { return SoulManaConfig.manaIncreaseRate; }

    private static double getMaxRegenBonus() { return SoulManaConfig.maxRegenBonus; }
    private static double getRegenIncreaseFactor() { return SoulManaConfig.manaRegenIncreaseRate; }

    public static double calculateManaBonus(float totalManaUsed) {
        return getMaxManaBonus() * (1.0D - Math.exp(-getManaIncreaseFactor() * totalManaUsed));
    }

    public static double calculateRegenBonus(float totalManaUsed) {
        return getMaxRegenBonus() * (1.0D - Math.exp(-getRegenIncreaseFactor() * totalManaUsed));
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

    /**
     * Calculates amount of mana used needed to get a given bonus
     */
    public static float calculateManaUsedForManaBonus(double targetBonus) {
        double maxBonus = getMaxManaBonus();
        double rate = getManaIncreaseFactor();

        if (targetBonus <= 0 || rate <= 0) return 0.0f;
        if (targetBonus >= maxBonus) targetBonus = maxBonus - 0.0001D;

        return (float) (-Math.log(1.0D - (targetBonus / maxBonus)) / rate);
    }

    /**
     * Calculates amount of mana used needed to get a given bonus
     */
    public static float calculateManaUsedForRegenBonus(double targetBonus) {
        double maxBonus = getMaxRegenBonus();
        double rate = getRegenIncreaseFactor();

        if (targetBonus <= 0 || rate <= 0) return 0.0f;
        if (targetBonus >= maxBonus) targetBonus = maxBonus - 0.0001D;

        return (float) (-Math.log(1.0D - (targetBonus / maxBonus)) / rate);
    }
}