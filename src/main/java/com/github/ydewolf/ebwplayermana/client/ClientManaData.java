package com.github.ydewolf.ebwplayermana.client;

public class ClientManaData {
    private static float playerMana = 0.0f;
    private static float maxMana = 100.0f;

    public static void set(float mana, float max) {
        playerMana = mana;
        maxMana = max;
    }

    public static float getMana() { return playerMana; }
    public static float getMaxMana() { return maxMana; }
}