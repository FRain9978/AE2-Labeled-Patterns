package org.ae2LabeledPatterns.menus;

import appeng.api.storage.IPatternAccessTermMenuHost;
import appeng.api.storage.ITerminalHost;
import org.ae2LabeledPatterns.items.components.PatternProviderLabel;

public interface ILabeledPatternAccessTermMenuHost extends IPatternAccessTermMenuHost, ITerminalHost {
    PatternProviderLabel getCurrentTag();

    void setCurrentTag(PatternProviderLabel tag);
}
