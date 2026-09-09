package com.github.ydewolf.ebwplayermana.client;

import com.github.ydewolf.ebwplayermana.api.ManaBonusType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.Map;

// Tmj Gemini
public class ManaHudOverlay {

    public static final IGuiOverlay HUD_MANA = (gui, guiGraphics, partialTick, width, height) -> {
        int x = 10;
        int y = 10;

        float mana = ClientManaData.getMana();
        float visualMana = ClientManaData.getVisualMana();
        float maxMana = ClientManaData.getMaxMana();
        Map<ManaBonusType, Float> bonusMap = ClientManaData.getBonusMap();

        int barWidth = 120;
        int barHeight = 10;

        boolean isFull = mana >= maxMana;
        int alpha = isFull ? 0x80 : 0xFF;

        int frameColor = (alpha << 24);
        int textColor  = (alpha << 24) | 0xFFFFFF;
        int defaultBg  = (alpha << 24) | 0x333333;
        int defaultFill= (alpha << 24) | 0x0077FF;

        // 1. Moldura externa da barra
        guiGraphics.fill(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, frameColor);

        // Se o mapa estiver nulo ou vazio, renderiza a barra azul padrão
        if (bonusMap == null || bonusMap.isEmpty()) {
            int progress = (int) ((visualMana / maxMana) * barWidth);
            guiGraphics.fill(x, y, x + barWidth, y + barHeight, defaultBg);
            guiGraphics.fill(x, y, x + progress, y + barHeight, defaultFill);
        } else {
            // 2. Renderização Dinâmica por Segmentos (Fundos + Preenchimento)
            int currentSegmentX = x;
            int remainingFillWidth = Mth.floor((visualMana / maxMana) * barWidth);

            for (Map.Entry<ManaBonusType, Float> entry : bonusMap.entrySet()) {
                ManaBonusType type = entry.getKey();
                float bonusValue = entry.getValue();

                if (bonusValue <= 0) continue;

                // Calcula o tamanho em pixels deste segmento específico
                int segmentWidth = Mth.floor((bonusValue / maxMana) * barWidth);
                if (segmentWidth <= 0) continue;

                // Aplica a opacidade nas cores configuradas no Enum
                int bgColor   = (alpha << 24) | (type.getBgColor() & 0xFFFFFF);
                int fillColor = (alpha << 24) | (type.getFillColor() & 0xFFFFFF);

                // Desenha o fundo do segmento
                guiGraphics.fill(currentSegmentX, y, currentSegmentX + segmentWidth, y + barHeight, bgColor);

                // Desenha o preenchimento da mana atual sobre este segmento
                int fillInThisSegment = Math.min(remainingFillWidth, segmentWidth);
                if (fillInThisSegment > 0) {
                    guiGraphics.fill(currentSegmentX, y, currentSegmentX + fillInThisSegment, y + barHeight, fillColor);
                    remainingFillWidth -= fillInThisSegment;
                }

                currentSegmentX += segmentWidth;
            }
        }

        // 3. Texto informativo centralizado
        String text = String.format("Mana: %.0f / %.0f", mana, maxMana);
        Font font = Minecraft.getInstance().font;

        int textX = x + (barWidth - font.width(text)) / 2;
        int textY = y + 1;

        guiGraphics.drawString(font, text, textX, textY, textColor, true);
    };
}