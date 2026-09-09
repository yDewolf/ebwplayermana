package com.github.ydewolf.ebwplayermana.client;

import com.github.ydewolf.ebwplayermana.api.ManaBonusType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.Map;

public class ManaHudOverlay {

    private static final int BAR_WIDTH = 120;
    private static final int BAR_HEIGHT = 10;
    private static final int DEFAULT_X = 10;
    private static final int DEFAULT_Y = 10;

    public static final IGuiOverlay HUD_MANA = (gui, guiGraphics, partialTick, width, height) -> {
        float mana = ClientManaData.getMana();
        float visualMana = ClientManaData.getVisualMana();
        float maxMana = ClientManaData.getMaxMana();
        Map<ManaBonusType, Float> bonusMap = ClientManaData.getBonusMap();

        int alpha = getAlpha(mana, maxMana);
        int frameColor = getColorWithAlpha(0x000000, alpha);
        int textColor = getColorWithAlpha(0xFFFFFF, alpha);

        renderFrame(guiGraphics, DEFAULT_X, DEFAULT_Y, frameColor);

        if (bonusMap == null || bonusMap.isEmpty()) {
            renderDefaultBar(guiGraphics, DEFAULT_X, DEFAULT_Y, visualMana, maxMana, alpha);
        } else {
            renderSegmentedBar(guiGraphics, DEFAULT_X, DEFAULT_Y, visualMana, maxMana, bonusMap, alpha, frameColor);
        }

        renderManaText(guiGraphics, DEFAULT_X, DEFAULT_Y, mana, maxMana, textColor);
    };

//  render

    private static void renderFrame(GuiGraphics guiGraphics, int x, int y, int frameColor) {
        guiGraphics.fill(x - 1, y - 1, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, frameColor);
    }

    private static void renderDefaultBar(GuiGraphics guiGraphics, int x, int y, float visualMana, float maxMana, int alpha) {
        int defaultBg = getColorWithAlpha(0x333333, alpha);
        int defaultFill = getColorWithAlpha(0x0077FF, alpha);
        int progress = Mth.floor((visualMana / maxMana) * BAR_WIDTH);

        guiGraphics.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, defaultBg);
        guiGraphics.fill(x, y, x + progress, y + BAR_HEIGHT, defaultFill);
    }

    private static void renderSegmentedBar(GuiGraphics guiGraphics, int x, int y, float visualMana, float maxMana,
                                           Map<ManaBonusType, Float> bonusMap, int alpha, int frameColor) {
        int currentSegmentX = x;
        int remainingFillWidth = Mth.floor((visualMana / maxMana) * BAR_WIDTH);

        int totalSegments = bonusMap.size();
        int currentSegmentIndex = 0;

        for (Map.Entry<ManaBonusType, Float> entry : bonusMap.entrySet()) {
            currentSegmentIndex++;
            ManaBonusType type = entry.getKey();
            float bonusValue = entry.getValue();

            if (bonusValue <= 0) continue;

            int segmentWidth = Mth.floor((bonusValue / maxMana) * BAR_WIDTH);
            if (segmentWidth <= 0) continue;

            remainingFillWidth = renderSegmentLayer(guiGraphics, currentSegmentX, y, segmentWidth, type, remainingFillWidth, alpha);
            if (currentSegmentIndex < totalSegments) {
                renderDivider(guiGraphics, currentSegmentX + segmentWidth - 1, y, frameColor);
            }

            currentSegmentX += segmentWidth;
        }
    }

    private static int renderSegmentLayer(GuiGraphics guiGraphics, int segmentX, int y, int segmentWidth,
                                          ManaBonusType type, int remainingFillWidth, int alpha) {
        int bgColor = getColorWithAlpha(type.getBgColor(), alpha);
        int fillColor = getColorWithAlpha(type.getFillColor(), alpha);

        guiGraphics.fill(segmentX, y, segmentX + segmentWidth, y + BAR_HEIGHT, bgColor);
        int fillInThisSegment = Math.min(remainingFillWidth, segmentWidth);
        if (fillInThisSegment > 0) {
            guiGraphics.fill(segmentX, y, segmentX + fillInThisSegment, y + BAR_HEIGHT, fillColor);
            return remainingFillWidth - fillInThisSegment;
        }

        return remainingFillWidth;
    }

    private static void renderDivider(GuiGraphics guiGraphics, int dividerX, int y, int frameColor) {
        guiGraphics.fill(dividerX, y, dividerX + 1, y + BAR_HEIGHT, frameColor);
    }

    private static void renderManaText(GuiGraphics guiGraphics, int x, int y, float mana, float maxMana, int textColor) {
        String text = String.format("Mana: %.0f / %.0f", mana, maxMana);
        Font font = Minecraft.getInstance().font;

        int textX = x + (BAR_WIDTH - font.width(text)) / 2;
        int textY = y + 1;

        guiGraphics.drawString(font, text, textX, textY, textColor, true);
    }

//    Color utils

    private static int getAlpha(float mana, float maxMana) {
        return (mana >= maxMana) ? 0x80 : 0xFF;
    }

    private static int getColorWithAlpha(int rgbColor, int alpha) {
        return (alpha << 24) | (rgbColor & 0xFFFFFF);
    }
}