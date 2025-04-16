package org.ae2PatternTagger.ae2patterntagger.menus.widgets;

import appeng.client.Point;
import appeng.client.gui.ICompositeWidget;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.Scrollbar;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import org.ae2PatternTagger.ae2patterntagger.Ae2patterntagger;

import java.util.List;

public class ScrollView<T> implements ICompositeWidget {

    private final IScrollViewData<T> data;
    private final IScrollViewItem<T> itemMaker;
    private int row = 1;
    private int column = 1;

    private final Scrollbar scrollbar;
    private final Blitter background;

    private Rect2i bounds = new Rect2i(0, 0, 0, 0);

    public ScrollView(IScrollViewData<T> data, IScrollViewItem<T> itemMaker, Scrollbar scrollbar, ScreenStyle style) {
        this.data = data;
        this.itemMaker = itemMaker;
        this.scrollbar = scrollbar;
        background = style.getImage("scrollview_background");
    }

    @Override
    public void setPosition(Point position) {
        this.bounds = new Rect2i(position.getX(), position.getY(), bounds.getWidth(), bounds.getHeight());
    }

    @Override
    public void setSize(int column, int row) {
        this.row = row;
        this.column = column;
        this.bounds = new Rect2i(bounds.getX(), bounds.getY(), column * itemMaker.getWidth(), row * itemMaker.getHeight());
        refresh();
    }

    @Override
    public Rect2i getBounds() {
        return this.bounds;
    }

    public void setData(List<T> newData){
        data.setData(newData);
        scrollbar.setRange(0, Math.max(0, newData.size() / column - row), 1);
    }

    public void refresh() {
        scrollbar.setRange(0, Math.max(0, data.getCount() / column - row), 1);
    }

//    @Override
//    public boolean onMouseDown(Point mousePos, int button) {
//        for(int i = 0; i < row; i++) {
//            for (int j = 0; j < column; j++) {
//                int x = bounds.getX() + this.bounds.getX() + j * itemMaker.getWidth();
//                int y = bounds.getY() + this.bounds.getY() + i * itemMaker.getHeight();
//                if (mousePos.getX() >= x && mousePos.getX() <= x + itemMaker.getWidth() &&
//                        mousePos.getY() >= y && mousePos.getY() <= y + itemMaker.getHeight()) {
//                    itemMaker.onMouseDown(mousePos, button);
//                }
//            }
//        }
//
//        return false;
//    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse){
        var x = bounds.getX() + this.bounds.getX();
        var y = bounds.getY() + this.bounds.getY();
//        background.dest(
//                x,
//                y,
//                this.bounds.getWidth(),
//                this.bounds.getHeight()).blit(guiGraphics);
        guiGraphics.blitSprite(ResourceLocation.fromNamespaceAndPath(Ae2patterntagger.MODID, "dropmenu_bg"),
                x,
                y,
                this.bounds.getWidth(),
                this.bounds.getHeight());

        if (data.getCount() == 0) {
            return;
        }
        int currentScroll = scrollbar.getCurrentScroll();
//        if (currentScroll + row * column > data.getData().size()) {
//            currentScroll = data.getData().size() - row * column;
//        }

        int index = currentScroll;
        var data = this.data.getData();
        for(int i = 0; i < row; i++){
            for(int j = 0; j < column; j++){
                itemMaker.setData(data.get(index));
                itemMaker.setPosition(new Point(x + j * itemMaker.getWidth(), y + i * itemMaker.getHeight()));
                itemMaker.drawBackgroundLayer(guiGraphics, this.bounds, mouse);
                index++;
            }
        }
    }

    @Override
    public void drawForegroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        if (data.getCount() == 0) {
            return;
        }
        var x = bounds.getX() + this.bounds.getX();
        var y = bounds.getY() + this.bounds.getY();

        int currentScroll = scrollbar.getCurrentScroll();
//        if (currentScroll + row * column > data.getData().size()) {
//            currentScroll = data.getData().size() - row * column;
//        }

        int index = currentScroll;
        var data = this.data.getData();
        for(int i = 0; i < row; i++){
            for(int j = 0; j < column; j++){
                itemMaker.setData(data.get(index));
                itemMaker.setPosition(new Point(x + j * itemMaker.getWidth(), y + i * itemMaker.getHeight()));
                itemMaker.drawForegroundLayer(guiGraphics, this.bounds, mouse);
                index++;
            }
        }
    }
}
