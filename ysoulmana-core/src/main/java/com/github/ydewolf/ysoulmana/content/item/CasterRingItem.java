package com.github.ydewolf.ysoulmana.content.item;

import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.spell.SpellTier;
import com.binaris.wizardry.content.item.WandItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Optional;

// TODO
public class CasterRingItem extends WandItem implements ICurioItem {

    public CasterRingItem(SpellTier tier, Element element) {
        super(tier, element);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
    }
    
    private static CasterRingItem getEquippedInCuriosInv(ICurioStacksHandler slotInv) {
        IDynamicStackHandler stacks = slotInv.getStacks();
        for (int idx = 0; idx < stacks.getSlots(); idx++) {
            ItemStack stack = stacks.getStackInSlot(idx);
            if (stack.getItem() instanceof CasterRingItem ringItem) {
                return ringItem;
            }
        }
        return null;
    }

    public static CasterRingItem getEquipped(Player player) {
        ICuriosItemHandler curiosInv = CuriosApi.getCuriosInventory(player).resolve().get();
        Optional<ICurioStacksHandler> slotInv = curiosInv.getStacksHandler("ring");
        return slotInv.map(CasterRingItem::getEquippedInCuriosInv).orElse(null);
    }
}