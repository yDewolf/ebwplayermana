package com.github.ydewolf.ebwplayermana.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class ManaHudOverlay {
    public static final IGuiOverlay HUD_MANA = (gui, guiGraphics, partialTick, width, height) -> {
        int x = 10;
        int y = height - 25; // Canto inferior esquerdo, acima do inventário

        float mana = ClientManaData.getMana();
        float maxMana = ClientManaData.getMaxMana();
        if (maxMana <= 0) maxMana = 100;

        int barWidth = 100;
        int barHeight = 10;
        int progress = (int) ((mana / maxMana) * barWidth);

        // Fundo da barra (Cinza Escuro)
        guiGraphics.fill(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, 0xFF000000);
        guiGraphics.fill(x, y, x + barWidth, y + barHeight, 0xFF333333);

        // Barra de Progresso (Azul)
        guiGraphics.fill(x, y, x + progress, y + barHeight, 0xFF0077FF);

        // Texto "Mana: 50 / 100"
        String text = String.format("Mana: %.0f / %.0f", mana, maxMana);
        Font font = Minecraft.getInstance().font;

        // Centraliza o texto na barra
        int textX = x + (barWidth - font.width(text)) / 2;
        int textY = y + 1;
        guiGraphics.drawString(font, text, textX, textY, 0xFFFFFFFF, true);
    };
}