package com.github.ydewolf.ysoulmana.content.mana;

import com.github.ydewolf.ysoulmana.api.mana.IManaPool;

public interface IPlayerMana extends IManaPool {
    float getTotalManaUsed();
    void setTotalManaUsed(float mana);
    void addTotalManaUsed(float mana);

    void copyFrom(IPlayerMana source);

    int getRegenCooldown();
    void setRegenCooldown(int regenCooldown);

    void decrementRegenCooldown();
    boolean onRegenCooldown();
}