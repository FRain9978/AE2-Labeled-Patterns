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

import java.util.ArrayList;
import java.util.List;

public class ScrollView<T> implements ICompositeWidget {

    private final IScrollViewData<T> data;
    private final ScrollViewItemBuilder<T> itemBuilder;
    private List<IScrollViewItem<T>> items = new ArrayList<>();
    private int row = 1;
    private int column = 1;

    private final Scrollbar scrollbar;
    private final Blitter background;

    private Rect2i bounds = new Rect2i(0, 0, 0, 0);

    public ScrollView(IScrollViewData<T> data, ScrollViewItemBuilder<T> itemBuilder, Scrollbar scrollbar, ScreenStyle style) {
        this.data = data;
        this.itemBuilder = itemBuilder;
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
        this.bounds = new Rect2i(bounds.getX(), bounds.getY(), column * itemBuilder.getWidth(), row * itemBuilder.getHeight());
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
        refreshItems();
    }

    public void refreshItems(){
        items.clear();
        // create items according to row and column
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                var item = itemBuilder.build();
                items.add(item);
            }
        }
    }

//    @Override
//    public boolean onMouseDown(Point mousePos, int button) {
//        for(int i = 0; i < row; i++) {
//            for (int j = 0; j < column; j++) {
//                int x = bounds.getX() + this.bounds.getX() + j * items.getWidth();
//                int y = bounds.getY() + this.bounds.getY() + i * items.getHeight();
//                if (mousePos.getX() >= x && mousePos.getX() <= x + items.getWidth() &&
//                        mousePos.getY() >= y && mousePos.getY() <= y + items.getHeight()) {
//                    items.onMouseDown(mousePos, button);
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

        int index = currentScroll;
        int itemIndex = 0;
        var data = this.data.getData();
        for(int i = 0; i < row; i++){
            for(int j = 0; j < column; j++){
                var item = items.get(itemIndex);
                item.setData(data.get(index));
                item.setPosition(new Point(x + j * item.getWidth(), y + i * item.getHeight()));
                item.drawBackgroundLayer(guiGraphics, this.bounds, mouse);
                index++;
                itemIndex++;
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

        int index = currentScroll;
        int itemIndex = 0;
        var data = this.data.getData();
        for(int i = 0; i < row; i++){
            for(int j = 0; j < column; j++){
                var item = items.get(itemIndex);
                item.setData(data.get(index));
                item.setPosition(new Point(x + j * item.getWidth(), y + i * item.getHeight()));
                item.drawForegroundLayer(guiGraphics, this.bounds, mouse);
                index++;
                itemIndex++;
            }
        }
    }
}
