package com.github.ydewolf.ebwplayermana.client;

public class ClientManaData {
    private static float maxMana = 100.0f;
    private static  float initialPlayerMana = maxMana * 1/3;
    private static float playerMana = initialPlayerMana;

    public static void set(float mana, float max) {
        playerMana = mana;
        maxMana = max;
    }

    public static float getMana() { return playerMana; }
    public static float getMaxMana() { return maxMana; }
}