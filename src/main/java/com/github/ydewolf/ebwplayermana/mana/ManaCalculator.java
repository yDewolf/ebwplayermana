package com.github.ydewolf.ebwplayermana.mana;

import com.github.ydewolf.ebwplayermana.PlayerManaConfig;

public class ManaCalculator {

    private static final double MAX_BONUS = PlayerManaConfig.maxManaBonus;
    private static final double K_FACTOR = PlayerManaConfig.manaIncreaseRate;

    public static double calculateMaxMana(double baseMaxMana, float totalManaUsed) {
        if (totalManaUsed <= 0) {
            return baseMaxMana;
        }

        // ManaBase + Teto * (1 - e^(-k * totalManaUsed))
        double bonus = MAX_BONUS * (1.0D - Math.exp(-K_FACTOR * totalManaUsed));
        return baseMaxMana + bonus;
    }
}