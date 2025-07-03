package org.ae2LabeledPatterns.parts;

import appeng.api.config.Settings;
import appeng.api.config.ShowPatternProviders;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.storage.ILinkStatus;
import appeng.api.util.IConfigManager;
import appeng.core.AppEng;
import appeng.items.parts.PartModels;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.PartModel;
import appeng.parts.reporting.AbstractDisplayPart;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.ae2LabeledPatterns.MSettings;
import org.ae2LabeledPatterns.items.components.PatternProviderLabel;
import org.ae2LabeledPatterns.menus.LabeledPatternAccessTerminalMenu;
import org.ae2LabeledPatterns.menus.ILabeledPatternAccessTermMenuHost;
import org.ae2LabeledPatterns.menus.MoveConvenience;

public class LabeledPatternAccessTerminalPart extends AbstractDisplayPart implements ILabeledPatternAccessTermMenuHost {
    @PartModels
    public static final ResourceLocation MODEL_OFF = AppEng.makeId("part/pattern_access_terminal_off");
    @PartModels
    public static final ResourceLocation MODEL_ON = AppEng.makeId("part/pattern_access_terminal_on");
    public static final IPartModel MODELS_OFF;
    public static final IPartModel MODELS_ON;
    public static final IPartModel MODELS_HAS_CHANNEL;
    private final IConfigManager configManager;

    private PatternProviderLabel currentTag = PatternProviderLabel.Empty;

    public LabeledPatternAccessTerminalPart(IPartItem<?> partItem) {
        super(partItem, true);
        this.configManager = IConfigManager.builder(() -> this.getHost().markForSave())
                .registerSetting(Settings.TERMINAL_SHOW_PATTERN_PROVIDERS, ShowPatternProviders.VISIBLE)
                .registerSetting(MSettings.TERMINAL_MOVE_CONVENIENCE, MoveConvenience.NONE)
                .build();
    }

    public boolean onUseWithoutItem(Player player, Vec3 pos) {
        if (!super.onUseWithoutItem(player, pos) && !this.isClientSide()) {
            MenuOpener.open(LabeledPatternAccessTerminalMenu.TYPE, player, MenuLocators.forPart(this));
        }

        return true;
    }

    public IPartModel getStaticModels() {
        return this.selectModel(MODELS_OFF, MODELS_ON, MODELS_HAS_CHANNEL);
    }

    public IConfigManager getConfigManager() {
        return this.configManager;
    }

    public void writeToNBT(CompoundTag tag, HolderLookup.Provider registries) {
        super.writeToNBT(tag, registries);
        this.configManager.writeToNBT(tag, registries);
        if (this.currentTag != null && !this.currentTag.isEmpty()) {
            this.currentTag.writeToNBT(tag);
        }
    }

    public void readFromNBT(CompoundTag tag, HolderLookup.Provider registries) {
        super.readFromNBT(tag, registries);
        this.configManager.readFromNBT(tag, registries);
        this.currentTag = PatternProviderLabel.readFromNBT(tag);
    }

    public ILinkStatus getLinkStatus() {
        return ILinkStatus.ofManagedNode(this.getMainNode());
    }

    static {
        MODELS_OFF = new PartModel(MODEL_BASE, MODEL_OFF, MODEL_STATUS_OFF);
        MODELS_ON = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
        MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_HAS_CHANNEL);
    }

    @Override
    public PatternProviderLabel getCurrentTag() {
        return currentTag;
    }

    @Override
    public void setCurrentTag(PatternProviderLabel tag) {
        if (tag != null && !tag.equals(currentTag)) {
            this.currentTag = tag;
            this.getHost().markForSave();
        }
    }
}
