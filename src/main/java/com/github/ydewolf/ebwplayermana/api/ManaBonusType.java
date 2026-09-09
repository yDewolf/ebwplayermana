package com.github.ydewolf.ebwplayermana.api;

public enum ManaBonusType {
    BASE("base", 0x29B6F6, 0x1A237E),
    PROGRESSION("progression", 0xAB47BC, 0x4A148C),
    EQUIPMENT("equipment", 0x26A69A, 0x004D40),
    BUFF("buff", 0xFFCA28, 0xFF6F00);

    private final String id;
    private final int fillColor;
    private final int bgColor;

    ManaBonusType(String id, int fillColor, int bgColor) {
        this.id = id;
        this.fillColor = fillColor;
        this.bgColor = bgColor;
    }

    public String getId() { return id; }
    public int getFillColor() { return fillColor; }
    public int getBgColor() { return bgColor; }
}