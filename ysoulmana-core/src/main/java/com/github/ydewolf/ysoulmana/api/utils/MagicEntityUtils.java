package com.github.ydewolf.ysoulmana.api.utils;

import com.github.ydewolf.ysoulmana.api.entity.EntityManaProvider;
import com.github.ydewolf.ysoulmana.api.mana.IManaPool;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;

public class MagicEntityUtils {
    public static boolean tryConsumeEntityMana(LivingEntity entity, float amount) {
        LazyOptional<IManaPool> manaOpt = entity.getCapability(EntityManaProvider.MANA_POOL);

        if (manaOpt.isPresent()) {
            IManaPool manaPool = manaOpt.orElseThrow(IllegalStateException::new);
            if (manaPool.canConsumeMana(amount)) {
                return manaPool.consumeMana(amount);
            }
        }
        return false;
    }
}
