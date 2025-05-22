package org.ae2PatternTagger.ae2patterntagger.menus;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.storage.ISubMenuHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.ae2PatternTagger.ae2patterntagger.Ae2patterntagger;
import org.ae2PatternTagger.ae2patterntagger.items.components.ComponentRegisters;
import org.ae2PatternTagger.ae2patterntagger.items.components.PatternProviderTag;
import org.ae2PatternTagger.ae2patterntagger.items.components.TaggerSetting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TaggerMenu extends AEBaseMenu implements ITagsProvider {

    public static final MenuType<TaggerMenu> TYPE = MenuTypeBuilder
            .create(TaggerMenu::new, ItemMenuHost.class)
            .buildUnregistered(Ae2patterntagger.makeId("tagger"));

    private static final String ACTION_SET_TAG = "setTag";
    private static final String ACTION_SET_SETTING = "setSetting";
    private static final String ACTION_SAVE_TAG = "saveTag";
    private static final String ACTION_DELETE_TAG = "deleteTag";
    public PatternProviderTag tag;
    public List<PatternProviderTag> saved_tags;
    public TaggerSetting setting;

    private final ItemStack iHost;
    private final ItemMenuHost<?> host;

    public TaggerMenu(int id, Inventory playerInventory, ItemMenuHost<?> host) {
        super(TYPE, id, playerInventory, host);

        this.host = host;
        iHost = host.getItemStack();
        tag = iHost.getOrDefault(ComponentRegisters.PATTERN_PROVIDER_TAG, new PatternProviderTag());
        saved_tags = iHost.getOrDefault(ComponentRegisters.SAVED_TAGS, new ArrayList<>());
        setting = iHost.getOrDefault(ComponentRegisters.TAGGER_SETTING, new TaggerSetting());

        registerClientAction(ACTION_SET_TAG, PatternProviderTag.class, this::setCurrentTag);
        registerClientAction(ACTION_SET_SETTING, TaggerSetting.class, this::setSetting);
        registerClientAction(ACTION_SAVE_TAG, PatternProviderTag.class, this::saveTag);
        registerClientAction(ACTION_DELETE_TAG, PatternProviderTag.class, this::deleteTag);
    }

//    public void setTag(PatternProviderTag tag) {
//        LogUtils.getLogger().debug("setTag:{}", tag);
//        this.tag = tag;
//        iHost.set(ComponentRegisters.PATTERN_PROVIDER_TAG, tag);
//        if (isClientSide()) {
//            sendClientAction(ACTION_SET_TAG, tag);
//        }else {
//
//        }
//    }

    public void setSetting(TaggerSetting taggerSetting) {
        LogUtils.getLogger().debug("TaggerSetting:{}", taggerSetting);
        setting = taggerSetting;
        if (isClientSide()) {
            sendClientAction(ACTION_SET_SETTING, taggerSetting);
        }else {
            iHost.set(ComponentRegisters.TAGGER_SETTING, taggerSetting);
        }
    }

    public void saveTag(PatternProviderTag tag) {
        LogUtils.getLogger().debug("saveTag:{}", tag);
        this.tag = tag;
        iHost.set(ComponentRegisters.PATTERN_PROVIDER_TAG, tag);
        addTag(tag);
        if (isClientSide()) {
            sendClientAction(ACTION_SAVE_TAG, tag);
        }else {
        }
    }

//    public void deleteTag(PatternProviderTag tag) {
//        LogUtils.getLogger().debug("deleteTag:{}", tag);
//        removeTag(tag);
//        if (isClientSide()) {
//            sendClientAction(ACTION_DELETE_TAG, tag);
//        }else {
//        }
//    }

    public ItemMenuHost<?> getHost() {
        return host;
    }

    @Override
    public List<PatternProviderTag> getTags() {
        return saved_tags;
    }

    @Override
    public PatternProviderTag currentTag() {
        return iHost.getOrDefault(ComponentRegisters.PATTERN_PROVIDER_TAG, new PatternProviderTag());
    }

    @Override
    public boolean addTag(PatternProviderTag tag) {
        if (tag == null) return false;
        List<PatternProviderTag> tags = iHost.getOrDefault(ComponentRegisters.SAVED_TAGS, new LinkedList<>());
        if (tags.contains(tag)) return false;
        tags = new LinkedList<>(tags);
        tags.add(tag);
        iHost.set(ComponentRegisters.SAVED_TAGS, tags);
        saved_tags = tags;
        return true;
    }

    @Override
    public boolean removeTag(PatternProviderTag tag) {
        if (tag == null) return false;
        List<PatternProviderTag> tags = iHost.getOrDefault(ComponentRegisters.SAVED_TAGS, new LinkedList<>());
        if (!tags.contains(tag)) return false;
        tags = new LinkedList<>(tags);
        tags.remove(tag);
        iHost.set(ComponentRegisters.SAVED_TAGS, tags);
        saved_tags = tags;
        return true;
    }

    @Override
    public boolean hasTag(PatternProviderTag tag) {
        if (tag == null) return false;
        List<PatternProviderTag> tags = iHost.getOrDefault(ComponentRegisters.SAVED_TAGS, new LinkedList<>());
        return tags.contains(tag);
    }

    @Override
    public void setCurrentTag(PatternProviderTag tag) {
        LogUtils.getLogger().debug("setTag:{}", tag);
        this.tag = tag;
        iHost.set(ComponentRegisters.PATTERN_PROVIDER_TAG, tag);
        if (isClientSide()) {
            sendClientAction(ACTION_SET_TAG, tag);
        }else {

        }
    }

    @Override
    public void deleteTag(PatternProviderTag tag) {
        LogUtils.getLogger().debug("deleteTag:{}", tag);
        removeTag(tag);
        if (isClientSide()) {
            sendClientAction(ACTION_DELETE_TAG, tag);
        }else {
        }
    }
}

