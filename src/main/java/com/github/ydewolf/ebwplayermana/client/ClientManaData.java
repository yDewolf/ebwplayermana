package com.github.ydewolf.ebwplayermana.client;

import com.github.ydewolf.ebwplayermana.api.ManaBonusType;
import net.minecraft.util.Mth;

import java.util.Map;

public class ClientManaData {
    private static float maxMana = 100.0f;
    private static Map<ManaBonusType, Float> bonusMap = null;
    private static float playerMana = 0.0f;
    private static float visualMana = 0.0f;

    public static void set(float mana, Map<ManaBonusType, Float> bonuses) {
        playerMana = mana;
        bonusMap = bonuses;

        float calculatedMax = 0.0f;
        if (bonuses != null) {
            for (Float value : bonuses.values()) {
                calculatedMax += value;
            }
        }
        maxMana = Math.max(1.0f, calculatedMax);
        if (visualMana == 0.0f && mana > 0.0f) {
            visualMana = mana;
        }
    }

    public static void updateInterpolation() {
        visualMana = Mth.lerp(0.15f, visualMana, playerMana);
    }

    public static float getMana() { return playerMana; }
    public static float getVisualMana() { return visualMana; }
    public static float getMaxMana() { return maxMana; }
    public static Map<ManaBonusType, Float> getBonusMap() { return bonusMap; }
}