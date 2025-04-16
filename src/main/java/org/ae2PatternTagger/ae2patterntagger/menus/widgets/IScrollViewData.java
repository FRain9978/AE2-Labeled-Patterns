package org.ae2PatternTagger.ae2patterntagger.menus.widgets;

import java.util.List;

public interface IScrollViewData<T> {
    List<T> getData();

    int getCount();

    void setData(List<T> data);

    public class ScrollViewData<T> implements IScrollViewData<T>{
        private List<T> data;
        private int count;

        public ScrollViewData(List<T> data) {
            this.data = data;
            this.count = data.size();
        }

        public ScrollViewData() {
        }

        @Override
        public List<T> getData() {
            return data;
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public void setData(List<T> data) {
            this.data = data;
            this.count = data.size();
        }
    }
}
