package com.github.ydewolf.ysoulmana.api.entity;

import com.github.ydewolf.ysoulmana.api.mana.IManaPool;
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

public class EntityManaProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<IManaPool> MANA_POOL = CapabilityManager.get(new CapabilityToken<>() {});
    private final LazyOptional<IManaPool> optional = LazyOptional.of(this::createEntityMana);
    private EntityManaPool mana = null;


    private EntityManaPool createEntityMana() {
        if (this.mana == null) {
            this.mana = new EntityManaPool();
        }
        return this.mana;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == MANA_POOL) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createEntityMana();
        nbt.putFloat("mana", this.mana.getMana());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createEntityMana();
        if (nbt.contains("mana", Tag.TAG_FLOAT)) {
            this.mana.setMana(nbt.getFloat("mana"));
        }
    }
}
