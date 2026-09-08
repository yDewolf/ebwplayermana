package com.github.ydewolf.ebwplayermana.mana;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerManaProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    // Objeto único de identificação da Capability no Forge
    public static Capability<IPlayerMana> PLAYER_MANA = CapabilityManager.get(new CapabilityToken<>() {});

    private PlayerMana mana = null;
    private final LazyOptional<IPlayerMana> optional = LazyOptional.of(this::createPlayerMana);

    private PlayerMana createPlayerMana() {
        if (this.mana == null) {
            this.mana = new PlayerMana();
        }
        return this.mana;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLAYER_MANA) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createPlayerMana();
        nbt.putFloat("mana", this.mana.getMana());
        nbt.putFloat("max_mana", this.mana.getMaxMana());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createPlayerMana();
        this.mana.setMaxMana(nbt.getFloat("max_mana"));
        this.mana.setMana(nbt.getFloat("mana"));
    }
}