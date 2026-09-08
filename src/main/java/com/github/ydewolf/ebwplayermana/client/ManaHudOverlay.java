package com.github.ydewolf.ebwplayermana.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class ManaHudOverlay {
    public static final IGuiOverlay HUD_MANA = (gui, guiGraphics, partialTick, width, height) -> {
        int x = 10;
        int y = 10;

        float mana = ClientManaData.getMana();
        float maxMana = ClientManaData.getMaxMana();
        if (maxMana <= 0) maxMana = 100;

        int barWidth = 120;
        int barHeight = 10;
        int progress = (int) ((mana / maxMana) * barWidth);

        boolean isFull = mana >= maxMana;
        int alpha = isFull ? 0x80 : 0xFF;

        int frameColor = (alpha << 24) | 0x000000;
        int bgColor = (alpha << 24) | 0x333333;
        int barColor = (alpha << 24) | 0x0077FF;
        int textColor = (alpha << 24) | 0xFFFFFF;

        guiGraphics.fill(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, frameColor);
        guiGraphics.fill(x, y, x + barWidth, y + barHeight, bgColor);
        guiGraphics.fill(x, y, x + progress, y + barHeight, barColor);

        String text = String.format("Mana: %.0f / %.0f", mana, maxMana);
        Font font = Minecraft.getInstance().font;

        int textX = x + (barWidth - font.width(text)) / 2;
        int textY = y + 1;

        guiGraphics.drawString(font, text, textX, textY, textColor, true);
    };
}