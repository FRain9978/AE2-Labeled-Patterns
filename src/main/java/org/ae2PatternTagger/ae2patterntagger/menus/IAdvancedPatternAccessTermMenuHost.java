package org.ae2PatternTagger.ae2patterntagger.menus;

import appeng.api.storage.IPatternAccessTermMenuHost;
import org.ae2PatternTagger.ae2patterntagger.items.components.PatternProviderTag;

public interface IAdvancedPatternAccessTermMenuHost extends IPatternAccessTermMenuHost {
    PatternProviderTag getCurrentTag();

    void setCurrentTag(PatternProviderTag tag);
}
