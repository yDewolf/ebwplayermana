package com.github.ydewolf.ysoulmana.content.mana;

import com.github.ydewolf.ysoulmana.api.entity.EntityManaProvider;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerManaProvider extends EntityManaProvider {
    public static Capability<IPlayerMana> PLAYER_MANA = CapabilityManager.get(new CapabilityToken<>() {});

    private PlayerMana playerMana = null;
    private final LazyOptional<IPlayerMana> playerOptional = LazyOptional.of(this::getOrCreatePlayerMana);

    private PlayerMana getOrCreatePlayerMana() {
        if (this.playerMana == null) {
            this.playerMana = new PlayerMana();
        }
        return this.playerMana;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLAYER_MANA) {
            return playerOptional.cast();
        }

        if (cap == MANA_POOL) {
            return playerOptional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();

        getOrCreatePlayerMana();
        nbt.putFloat("manaUsed", this.playerMana.getTotalManaUsed());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);

        getOrCreatePlayerMana();
        if (nbt.contains("manaUsed", Tag.TAG_FLOAT)) {
            this.playerMana.setTotalManaUsed(nbt.getFloat("manaUsed"));
        }
    }
}