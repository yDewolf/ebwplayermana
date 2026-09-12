package com.github.ydewolf.ebwplayermana.content.mana.helpers;

import com.binaris.wizardry.api.content.item.ICastItem;
import com.binaris.wizardry.api.content.item.IManaItem;

public record ManaCastItem(ICastItem castItem, IManaItem manaItem) {
}
