package org.ae2LabeledPatterns.menus;

import org.ae2LabeledPatterns.items.components.PatternProviderLabel;

import java.util.List;

public interface ILabelsProvider {
    List<PatternProviderLabel> getLabels();

    PatternProviderLabel currentLabel();

    default boolean addLabel(PatternProviderLabel label){
        return false;
    }

    default boolean removeLabel(PatternProviderLabel label){
        return false;
    }

    boolean hasLabel(PatternProviderLabel label);

    default boolean canEdit() {;
        return true;
    }

    void setCurrentLabel(PatternProviderLabel label);

    default void deleteLabel(PatternProviderLabel label) {
        removeLabel(label);
    }
}
