package org.ae2PatternTagger.ae2patterntagger.menus.widgets;

import appeng.client.gui.ICompositeWidget;

public interface IScrollViewItem<T> extends ICompositeWidget {
    T getData();

    void setData(T data);

    default int getHeight() {
        return 16;
    }

    default int getWidth() {
        return 36;
    }

//    void onMouseDown(int btn);
//
//    void onMouseUp(int btn);
//
//    void onMouseHover();
}
