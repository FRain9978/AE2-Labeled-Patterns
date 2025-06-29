package org.ae2PatternTagger.menus;

import org.ae2PatternTagger.items.components.PatternProviderTag;

import java.util.List;

public interface ITagsProvider {
    List<PatternProviderTag> getTags();

    PatternProviderTag currentTag();

    default boolean addTag(PatternProviderTag tag){
        return false;
    }

    default boolean removeTag(PatternProviderTag tag){
        return false;
    }

    boolean hasTag(PatternProviderTag tag);

    default boolean canEdit() {;
        return true;
    }

    void setCurrentTag(PatternProviderTag tag);

    default void deleteTag(PatternProviderTag tag) {
        removeTag(tag);
    }
}
