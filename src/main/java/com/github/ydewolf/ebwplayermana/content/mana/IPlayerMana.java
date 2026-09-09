package com.github.ydewolf.ebwplayermana.content.mana;

public interface IPlayerMana {
    float getMana();
    void setMana(float mana);
    float getMaxMana();
    void setMaxMana(float maxMana);

    float getTotalManaUsed();
    void setTotalManaUsed(float mana);
    void addTotalManaUsed(float mana);

    void addMana(float amount);
    boolean consumeMana(float amount);

    void copyFrom(IPlayerMana source);


    int getRegenCooldown();
    void setRegenCooldown(int regenCooldown);

    void decrementRegenCooldown();
    boolean onRegenCooldown();
}