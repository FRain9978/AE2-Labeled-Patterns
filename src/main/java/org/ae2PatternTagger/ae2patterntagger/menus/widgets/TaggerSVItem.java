package org.ae2PatternTagger.ae2patterntagger.menus.widgets;

import appeng.client.Point;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import org.ae2PatternTagger.ae2patterntagger.items.components.PatternProviderTag;
import org.apache.commons.logging.LogFactory;

public final class TaggerSVItem<T extends PatternProviderTag> implements IScrollViewItem<T> {
    private final int height;
    private final int width;

    public TaggerSVItem(int height, int width) {
        button = Button.builder(Component.empty(), (button) -> onPress()).size(getWidth(), getHeight()).build();
        this.height = height;
        this.width = width;
    }

    private T data;

    private Rect2i bounds = new Rect2i(0, 0, 0, 0);

    Button button;

    private void setButtonPosition(int x, int y) {
        button.setX(x);
        button.setY(y);
    }

    @Override
    public T getData() {
        return data;
    }

    @Override
    public void setData(T data) {
        this.data = data;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setPosition(Point position) {
        this.bounds = new Rect2i(position.getX(), position.getY(), getWidth(), getHeight());
        setButtonPosition(position.getX(), position.getY());
    }

    @Override
    public void setSize(int width, int height) {
        return;
    }

    @Override
    public Rect2i getBounds() {
        return bounds;
    }

    @Override
    public void drawForegroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        button.render(guiGraphics, mouse.getX(), mouse.getY(), 0);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(data.name()),
                this.bounds.getX() + 5,
                this.bounds.getY() + 5, 0xFFFFFF);

    }

    private void onPress(){
        LogUtils.getLogger().info("data: {}", data.name());
    }
}
