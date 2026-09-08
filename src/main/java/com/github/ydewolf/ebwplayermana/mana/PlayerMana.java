package com.github.ydewolf.ebwplayermana.mana;

public class PlayerMana implements IPlayerMana {
    private float mana = 0.0f;
    private float maxMana = 100.0f;

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
    public void addMana(float amount) {
        setMana(this.mana + amount);
    }

    @Override
    public boolean consumeMana(float amount) {
        if (this.mana >= amount) {
            setMana(this.mana - amount);
            return true;
        }
        return false;
    }

    @Override
    public void copyFrom(IPlayerMana source) {
        this.maxMana = source.getMaxMana();
        this.mana = source.getMana();
    }
}