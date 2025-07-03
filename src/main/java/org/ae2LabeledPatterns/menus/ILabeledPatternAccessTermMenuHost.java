package org.ae2LabeledPatterns.menus;

import appeng.api.storage.IPatternAccessTermMenuHost;
import org.ae2LabeledPatterns.items.components.PatternProviderLabel;

public interface ILabeledPatternAccessTermMenuHost extends IPatternAccessTermMenuHost {
    PatternProviderLabel getCurrentTag();

    void setCurrentTag(PatternProviderLabel tag);
}
