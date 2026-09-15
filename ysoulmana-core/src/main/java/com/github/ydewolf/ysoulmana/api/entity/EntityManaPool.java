package com.github.ydewolf.ysoulmana.api.entity;

import com.github.ydewolf.ysoulmana.api.mana.IManaPool;
import com.github.ydewolf.ysoulmana.config.SoulManaConfig;

public class EntityManaPool implements IManaPool {
    protected float maxMana = 100.0f;
    protected float mana = 0.0f;
    private int regenCooldown = 0;

    @Override
    public float getMana() {
        return this.mana;
    }

    @Override
    public void setMana(float mana) {
        this.mana =  Math.min(Math.max(mana, 0.0f), this.maxMana);
    }

    @Override
    public float getMaxMana() {
        return this.maxMana;
    }

    @Override
    public void setMaxMana(float maxMana) {
        this.maxMana = Math.max(0.0f, maxMana);
        if (this.mana > maxMana) {
            this.mana = maxMana;
        }
    }

    @Override
    public void addMana(float amount) {
        this.setMana(this.getMana() + amount);
    }

    @Override
    public boolean consumeMana(float amount) {
        if (canConsumeMana(amount)) {
            this.setMaxMana(this.getMana() - amount);
            return true;
        }
        return false;
    }

    @Override
    public boolean canConsumeMana(float amount) {
        return this.mana >= amount;
    }

    @Override
    public void copyFrom(IManaPool source) {
        this.mana = source.getMaxMana();
        this.maxMana = source.getMaxMana();
    }

    public int getRegenCooldown() {
        return regenCooldown;
    }

    public void setRegenCooldown(int regenCooldown) {
        this.regenCooldown = Math.max(0, regenCooldown);
    }

    public void decrementRegenCooldown() { this.setRegenCooldown(getRegenCooldown() - SoulManaConfig.manaRegenTickRate);}
    public boolean onRegenCooldown() {
        return this.getRegenCooldown() > 0;
    }

    public boolean isRegeneratable() { return true; }
}
