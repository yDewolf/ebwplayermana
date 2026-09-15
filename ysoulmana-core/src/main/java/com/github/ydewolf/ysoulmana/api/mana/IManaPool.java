package com.github.ydewolf.ysoulmana.api.mana;

import com.github.ydewolf.ysoulmana.config.SoulManaConfig;

public interface IManaPool {
    float getMana();
    void setMana(float mana);

    float getMaxMana();
    void setMaxMana(float maxMana);

    void addMana(float amount);

    boolean consumeMana(float amount);
    boolean canConsumeMana(float amount);

    void copyFrom(IManaPool source);

    int getRegenCooldown();

    void setRegenCooldown(int regenCooldown);
    void decrementRegenCooldown();
    boolean onRegenCooldown();

    boolean isRegeneratable();
}
