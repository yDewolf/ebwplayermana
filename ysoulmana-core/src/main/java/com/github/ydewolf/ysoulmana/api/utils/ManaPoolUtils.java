package com.github.ydewolf.ysoulmana.api.utils;

import com.github.ydewolf.ysoulmana.api.mana.IManaPool;

public class ManaPoolUtils {
    /**
     * Tries to consume mana from a IManaPool
     */
    public static boolean tryConsumeFromPool(IManaPool pool, float amount) {
        if (pool.canConsumeMana(amount)) {
            return pool.consumeMana(amount);
        }
        return false;
    }
}
