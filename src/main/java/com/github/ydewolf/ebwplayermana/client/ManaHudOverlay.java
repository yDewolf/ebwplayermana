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

        int barWidth = 120;
        int barHeight = 10;
        int progress = (int) ((mana / maxMana) * barWidth);

        guiGraphics.fill(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, 0xFF000000);
        guiGraphics.fill(x, y, x + barWidth, y + barHeight, 0xFF333333);

        guiGraphics.fill(x, y, x + progress, y + barHeight, 0xFF0077FF);

        String text = String.format("Mana: %.0f / %.0f", mana, maxMana);
        Font font = Minecraft.getInstance().font;

        int textX = x + (barWidth - font.width(text)) / 2;
        int textY = y + 1;
        guiGraphics.drawString(font, text, textX, textY, 0xFFFFFFFF, true);
    };
}