package org.ae2PatternTagger.ae2patterntagger.menus.widgets;

public final class ScrollViewItemBuilder<T> {
    private final int height;
    private final int width;
    private final ScrollViewItemFactory<T> factory;

    public ScrollViewItemBuilder(ScrollViewItemFactory<T> factory, int height, int width) {
        this.height = height;
        this.width = width;
        this.factory = factory;
    }

    public int getHeight(){
        return height;
    }

    public int getWidth(){
        return width;
    }

    public IScrollViewItem<T> build() {
        return factory.create(height, width);
    }

    public IScrollViewItem<T> build(T data) {
        return factory.create(height, width).setData(data);
    }

    public static <T> ScrollViewItemBuilder<T> create(ScrollViewItemFactory<T> factory, int height, int width) {
        return new ScrollViewItemBuilder<>(factory, height, width);
    }

    @FunctionalInterface
    public interface ScrollViewItemFactory<T> {
        IScrollViewItem<T> create(int height, int width);
    }
}
