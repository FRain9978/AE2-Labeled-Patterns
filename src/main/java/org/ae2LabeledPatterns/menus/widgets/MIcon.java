package org.ae2LabeledPatterns.menus.widgets;

import appeng.client.gui.style.Blitter;
import net.minecraft.resources.ResourceLocation;
import org.ae2LabeledPatterns.AE2LabeledPatterns;

public enum MIcon {
    RENAME_ON(0, 0),
    RENAME_OFF(16, 0),
    ;

    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public static final ResourceLocation TEXTURE = AE2LabeledPatterns.makeId("textures/guis/states.png");
    public static final int TEXTURE_WIDTH = 64;
    public static final int TEXTURE_HEIGHT = 64;
    MIcon(int x, int y) {
        this(x, y, 16, 16);
    }

    MIcon(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Blitter getBlitter() {
        return Blitter.texture(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT).src(x, y, width, height);
    }
}
