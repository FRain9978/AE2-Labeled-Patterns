package org.ae2PatternTagger.menus;

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
import org.ae2PatternTagger.items.components.PatternProviderTag;
import org.ae2PatternTagger.items.components.TaggerSetting;
import org.ae2PatternTagger.menus.widgets.MActionButton;
import org.ae2PatternTagger.menus.widgets.TagListButton;

public class TaggerScreen extends AEBaseScreen<TaggerMenu> {

    private final AETextField inputField;
    private final ToggleButton lockButton;
    private final IconButton confirmButton;
    private final IconButton saveButton;
//    private final ScrollView<PatternProviderTag> scrollView;
//    private final Scrollbar scrollbar;

    public TaggerScreen(TaggerMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.inputField = widgets.addTextField("input");

        this.inputField.setPlaceholder(GUIText.TaggerInputPlaceHolder.text());
        if (menu.tag != null && !menu.tag.name().isBlank()) {
            this.inputField.setValue(menu.tag.name());
        }

        lockButton = new ToggleButton(Icon.LOCKED, Icon.UNLOCKED, GUIText.TaggerEditLock.text(), GUIText.TaggerEditLockHint.text(),
                (isLocked) -> this.menu.setSetting(new TaggerSetting(isLocked? YesNo.YES : YesNo.NO)));
        this.addToLeftToolbar(lockButton);

        confirmButton = new MActionButton(Icon.VIEW_MODE_CRAFTING, (button) -> {
            if (this.menu.setting.isLockEdit() == YesNo.YES) return;
            var text = this.inputField.getValue();
            if (!text.isBlank()) {
                this.menu.setCurrentTag(new PatternProviderTag(text));
            }
        }, GUIText.TaggerEditConfirm.text());

        saveButton = new MActionButton(Icon.VIEW_MODE_ALL, (button) -> {
            var text = this.inputField.getValue();
            if (!text.isBlank()) {
                this.menu.saveTag(new PatternProviderTag(text));
            }
        }, GUIText.TaggerEditSave.text());

        widgets.add("confirm", confirmButton);
        var host = this.menu.getHost();
        if (host instanceof ISubMenuHost){
            addToLeftToolbar(TagListButton.create(this, (ISubMenuHost) host , Component.literal("Open Tag List")));
            addToLeftToolbar(saveButton);
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
        confirmButton.active = !isLocked;
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
    protected <P extends AEBaseScreen<TaggerMenu>> void onReturnFromSubScreen(AESubScreen<TaggerMenu, P> subScreen) {
        super.onReturnFromSubScreen(subScreen);
        if (subScreen instanceof TagListScreen) {
            if (menu.tag != null && !menu.tag.name().isBlank()) {
                this.inputField.setValue(menu.tag.name());
            }
        }
    }
}
