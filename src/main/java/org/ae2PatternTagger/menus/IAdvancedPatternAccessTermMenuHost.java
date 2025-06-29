package org.ae2PatternTagger.menus;

import appeng.api.storage.IPatternAccessTermMenuHost;
import org.ae2PatternTagger.items.components.PatternProviderTag;

public interface IAdvancedPatternAccessTermMenuHost extends IPatternAccessTermMenuHost {
    PatternProviderTag getCurrentTag();

    void setCurrentTag(PatternProviderTag tag);
}
