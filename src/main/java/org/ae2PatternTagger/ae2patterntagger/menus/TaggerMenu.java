package org.ae2PatternTagger.ae2patterntagger.menus;

import appeng.api.config.YesNo;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.client.gui.style.Color;
import appeng.items.contents.NetworkToolMenuHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.me.networktool.NetworkToolMenu;
import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.ae2PatternTagger.ae2patterntagger.Ae2patterntagger;
import org.ae2PatternTagger.ae2patterntagger.items.TaggerItem;
import org.ae2PatternTagger.ae2patterntagger.items.components.ComponentRegisters;
import org.ae2PatternTagger.ae2patterntagger.items.components.PatternProviderTag;
import org.ae2PatternTagger.ae2patterntagger.items.components.TaggerSetting;
import org.jetbrains.annotations.Nullable;

public class TaggerMenu extends AEBaseMenu {

    public static final MenuType<TaggerMenu> TYPE = MenuTypeBuilder
            .create(TaggerMenu::new, ItemMenuHost.class)
            .buildUnregistered(Ae2patterntagger.makeId("tagger"));

    private static final String ACTION_SET_TAG = "setTag";
    private static final String ACTION_SET_SETTING = "setSetting";
    public PatternProviderTag tag;
    public TaggerSetting setting;

    private final ItemStack iHost;

    public TaggerMenu(int id, Inventory playerInventory, ItemMenuHost<?> host) {
        super(TYPE, id, playerInventory, host);

        iHost = host.getItemStack();
        tag = iHost.getOrDefault(ComponentRegisters.PATTERN_PROVIDER_TAG, new PatternProviderTag());
        setting = iHost.getOrDefault(ComponentRegisters.TAGGER_SETTING, new TaggerSetting());

        registerClientAction(ACTION_SET_TAG, PatternProviderTag.class, this::setTag);
        registerClientAction(ACTION_SET_SETTING, TaggerSetting.class, this::setSetting);
    }

    public void setTag(PatternProviderTag tag) {
        LogUtils.getLogger().debug("setTag:{}", tag);
        this.tag = tag;
        if (isClientSide()) {
            sendClientAction(ACTION_SET_TAG, tag);
        }else {
            iHost.set(ComponentRegisters.PATTERN_PROVIDER_TAG, tag);
        }
    }

    public void setSetting(TaggerSetting taggerSetting) {
        LogUtils.getLogger().debug("TaggerSetting:{}", taggerSetting);
        setting = taggerSetting;
        if (isClientSide()) {
            sendClientAction(ACTION_SET_SETTING, taggerSetting);
        }else {
            iHost.set(ComponentRegisters.TAGGER_SETTING, taggerSetting);
        }
    }
}

