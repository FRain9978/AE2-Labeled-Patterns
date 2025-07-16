package org.ae2LabeledPatterns.menus;

import appeng.api.config.YesNo;
import appeng.api.storage.ISubMenuHost;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.AESubScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.style.BackgroundGenerator;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.ae2LabeledPatterns.items.components.LabelerSetting;
import org.ae2LabeledPatterns.items.components.PatternProviderLabel;
import org.ae2LabeledPatterns.menus.widgets.MActionButton;
import org.ae2LabeledPatterns.menus.widgets.LabelListButton;

public class LabelerScreen extends AEBaseScreen<LabelerMenu> {

    private final AETextField inputField;
    private final ToggleButton lockButton;
//    private final IconButton confirmButton;
    private final IconButton saveButton;

    public LabelerScreen(LabelerMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.inputField = widgets.addTextField("input");

        this.inputField.setPlaceholder(GUIText.LabelerInputPlaceHolder.text());
        if (menu.label != null && !menu.label.name().isBlank()) {
            this.inputField.setValue(menu.label.name());
        }

        lockButton = new ToggleButton(Icon.LOCKED, Icon.UNLOCKED, GUIText.LabelerEditLock.text(), GUIText.LabelerEditLockHint.text(),
                (isLocked) -> this.menu.setSetting(new LabelerSetting(isLocked? YesNo.YES : YesNo.NO, this.menu.setting.mode())));
        this.addToLeftToolbar(lockButton);

//        confirmButton = new MActionButton(Icon.VIEW_MODE_CRAFTING, (button) -> {
//            if (this.menu.setting.isLockEdit() == YesNo.YES) return;
//            var text = this.inputField.getValue();
//            if (!text.isBlank()) {
//                this.menu.setCurrentLabel(new PatternProviderLabel(text));
//            }
//        }, GUIText.LabelerEditConfirm.text());

        saveButton = new MActionButton(Icon.STORAGE_FILTER_EXTRACTABLE_NONE, (button) -> {
            var text = this.inputField.getValue();
            if (!text.isBlank()) {
                this.menu.saveLabel(new PatternProviderLabel(text));
            }
        }, GUIText.LabelerEditSave.text());

//        widgets.add("confirm", confirmButton);

        var host = this.menu.getHost();
        if (host instanceof ISubMenuHost){
            addToLeftToolbar(LabelListButton.create(this, (ISubMenuHost) host , GUIText.LabelerOpenList.text()));
//            addToLeftToolbar(saveButton);
            widgets.add("save", saveButton);
        }
    }

    @Override
    protected void init() {
        super.init();
        this.setInitialFocus(this.inputField);
    }

    @Override
    public void updateBeforeRender() {
        super.updateBeforeRender();
        lockButton.setState(this.menu.setting.isLockEdit() == YesNo.YES);
        var isLocked = this.menu.setting.isLockEdit() == YesNo.YES;
        inputField.setEditable(!isLocked);
        if(isLocked){
            inputField.setFocused(false);
        }
//        confirmButton.active = !isLocked;
    }

    @Override
    public void drawBG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY,
                       float partialTicks) {

        var generatedBackground = style.getGeneratedBackground();
        if (generatedBackground != null) {
            int x = (width - generatedBackground.getWidth()) / 2;
            int y = (height - generatedBackground.getHeight()) / 2;
            BackgroundGenerator.draw(
                    generatedBackground.getWidth(),
                    generatedBackground.getHeight(),
                    guiGraphics,
                    x,
                    y);
        }

        var background = style.getBackground();
        if (background != null) {
            background.dest(offsetX, offsetY).blit(guiGraphics);
        }

    }

    @Override
    protected <P extends AEBaseScreen<LabelerMenu>> void onReturnFromSubScreen(AESubScreen<LabelerMenu, P> subScreen) {
        super.onReturnFromSubScreen(subScreen);
        if (subScreen instanceof LabelListScreen) {
            if (menu.label != null && !menu.label.name().isBlank()) {
                this.inputField.setValue(menu.label.name());
            }
        }
    }
}
