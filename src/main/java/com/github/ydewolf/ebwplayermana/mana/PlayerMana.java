package com.github.ydewolf.ebwplayermana.mana;

import com.github.ydewolf.ebwplayermana.PlayerManaConfig;

public class PlayerMana implements IPlayerMana {
    private float maxMana = 100.0f;
    private float mana = maxMana * (PlayerManaConfig.initialManaPercent);
    private float totalManaUsed = 0f;

    @Override
    public float getMana() {
        return this.mana;
    }

    @Override
    public void setMana(float mana) {
        this.mana = Math.min(Math.max(mana, 0.0f), this.maxMana);
    }

    @Override
    public float getMaxMana() {
        return this.maxMana;
    }

    @Override
    public void setMaxMana(float maxMana) {
        this.maxMana = maxMana;
        if (this.mana > maxMana) {
            this.mana = maxMana;
        }
    }

    @Override
    public float getTotalManaUsed() {
        return this.totalManaUsed;
    }

    @Override
    public void setTotalManaUsed(float mana) {
        this.totalManaUsed = mana;
    }

    @Override
    public void addTotalManaUsed(float amount) {
        this.totalManaUsed += amount;
    }

    @Override
    public void addMana(float amount) {
        setMana(this.mana + amount);
    }

    @Override
    public boolean consumeMana(float amount) {
        if (this.mana >= amount) {
            addTotalManaUsed(amount);
            setMana(this.mana - amount);
            return true;
        }
        return false;
    }

    @Override
    public void copyFrom(IPlayerMana source) {
        this.mana = source.getMana();
    }
}