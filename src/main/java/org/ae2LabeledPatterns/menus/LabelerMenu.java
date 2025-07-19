package org.ae2LabeledPatterns.menus;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.ae2LabeledPatterns.AE2LabeledPatterns;
import org.ae2LabeledPatterns.items.components.ComponentRegisters;
import org.ae2LabeledPatterns.items.components.LabelerSetting;
import org.ae2LabeledPatterns.items.components.PatternProviderLabel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LabelerMenu extends AEBaseMenu implements ILabelsProvider {

    public static final MenuType<LabelerMenu> TYPE = MenuTypeBuilder
            .create(LabelerMenu::new, ItemMenuHost.class)
            .buildUnregistered(AE2LabeledPatterns.makeId("labeler"));

    private static final String ACTION_SET_LABEL = "setLabel";
    private static final String ACTION_SET_SETTING = "setSetting";
    private static final String ACTION_SAVE_LABEL = "saveLabel";
    private static final String ACTION_DELETE_LABEL = "deleteLabel";
    public PatternProviderLabel label;
    public List<PatternProviderLabel> saved_labels;
    public LabelerSetting setting;

    private final ItemStack iHost;
    private final ItemMenuHost<?> host;

    public LabelerMenu(int id, Inventory playerInventory, ItemMenuHost<?> host) {
        super(TYPE, id, playerInventory, host);

        this.host = host;
        iHost = host.getItemStack();
        label = iHost.getOrDefault(ComponentRegisters.PATTERN_PROVIDER_LABEL, new PatternProviderLabel());
        saved_labels = iHost.getOrDefault(ComponentRegisters.SAVED_LABELS, new ArrayList<>());
        setting = iHost.getOrDefault(ComponentRegisters.LABELER_SETTING, new LabelerSetting());

        registerClientAction(ACTION_SET_LABEL, PatternProviderLabel.class, this::setCurrentLabel);
        registerClientAction(ACTION_SET_SETTING, LabelerSetting.class, this::setSetting);
        registerClientAction(ACTION_SAVE_LABEL, PatternProviderLabel.class, this::saveLabel);
        registerClientAction(ACTION_DELETE_LABEL, PatternProviderLabel.class, this::deleteLabel);
    }

    public void setSetting(LabelerSetting labelerSetting) {
        LogUtils.getLogger().debug("LabelerSetting:{}", labelerSetting);
        setting = labelerSetting;
        if (isClientSide()) {
            sendClientAction(ACTION_SET_SETTING, labelerSetting);
        }else {
            iHost.set(ComponentRegisters.LABELER_SETTING, labelerSetting);
        }
    }

    public void saveLabel(PatternProviderLabel label) {
        LogUtils.getLogger().debug("saveLabel:{}", label);
        this.label = label;
        iHost.set(ComponentRegisters.PATTERN_PROVIDER_LABEL, label);
        addLabel(label);
        if (isClientSide()) {
            sendClientAction(ACTION_SAVE_LABEL, label);
        }else {
        }
    }

    public ItemMenuHost<?> getHost() {
        return host;
    }

    @Override
    public List<PatternProviderLabel> getLabels() {
        return saved_labels;
    }

    @Override
    public PatternProviderLabel currentLabel() {
        return iHost.getOrDefault(ComponentRegisters.PATTERN_PROVIDER_LABEL, new PatternProviderLabel());
    }

    @Override
    public boolean addLabel(PatternProviderLabel label) {
        if (label == null) return false;
        List<PatternProviderLabel> labels = iHost.getOrDefault(ComponentRegisters.SAVED_LABELS, new LinkedList<>());
        if (labels.contains(label)) return false;
        labels = new LinkedList<>(labels);
        labels.add(label);
        iHost.set(ComponentRegisters.SAVED_LABELS, labels);
        saved_labels = labels;
        return true;
    }

    @Override
    public boolean removeLabel(PatternProviderLabel label) {
        if (label == null) return false;
        List<PatternProviderLabel> labels = iHost.getOrDefault(ComponentRegisters.SAVED_LABELS, new LinkedList<>());
        if (!labels.contains(label)) return false;
        labels = new LinkedList<>(labels);
        labels.remove(label);
        iHost.set(ComponentRegisters.SAVED_LABELS, labels);
        saved_labels = labels;
        return true;
    }

    @Override
    public boolean hasLabel(PatternProviderLabel label) {
        if (label == null) return false;
        List<PatternProviderLabel> labels = iHost.getOrDefault(ComponentRegisters.SAVED_LABELS, new LinkedList<>());
        return labels.contains(label);
    }

    @Override
    public void setCurrentLabel(PatternProviderLabel label) {
        LogUtils.getLogger().debug("setLabel:{}", label);
        this.label = label;
        iHost.set(ComponentRegisters.PATTERN_PROVIDER_LABEL, label);
        if (isClientSide()) {
            sendClientAction(ACTION_SET_LABEL, label);
        }else {

        }
    }

    @Override
    public void deleteLabel(PatternProviderLabel label) {
        LogUtils.getLogger().debug("deleteLabel:{}", label);
        removeLabel(label);
        if (isClientSide()) {
            sendClientAction(ACTION_DELETE_LABEL, label);
        }else {
        }
    }
}

