package com.github.ydewolf.ebwplayermana.client;

public class ClientManaData {
    private static float playerMana = 0;
    private static float maxMana = 2^16;

    public static void set(float mana, float max) {
        playerMana = mana;
        maxMana = max;
    }

    public static float getMana() { return playerMana; }
    public static float getMaxMana() { return maxMana; }
}