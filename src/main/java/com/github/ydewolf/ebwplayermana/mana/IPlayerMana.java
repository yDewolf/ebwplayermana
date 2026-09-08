package com.github.ydewolf.ebwplayermana.mana;

public interface IPlayerMana {
    float getMana();
    void setMana(float mana);
    float getMaxMana();
    void setMaxMana(float maxMana);

    void addMana(float amount);
    boolean consumeMana(float amount);

    // Método utilitário para clonar os dados ao morrer/trocar de dimensão
    void copyFrom(IPlayerMana source);
}