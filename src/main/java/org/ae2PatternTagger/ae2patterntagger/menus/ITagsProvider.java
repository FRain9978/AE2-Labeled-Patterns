package org.ae2PatternTagger.ae2patterntagger.menus;

import org.ae2PatternTagger.ae2patterntagger.items.components.PatternProviderTag;

import java.util.List;

public interface ITagsProvider {
    List<PatternProviderTag> getTags();

    PatternProviderTag currentTag();

    boolean addTag(PatternProviderTag tag);

    boolean removeTag(PatternProviderTag tag);

    boolean hasTag(PatternProviderTag tag);

    default boolean canEdit() {;
        return true;
    }
}
