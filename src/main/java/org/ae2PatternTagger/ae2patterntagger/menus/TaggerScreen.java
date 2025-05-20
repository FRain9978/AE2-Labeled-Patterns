package org.ae2PatternTagger.ae2patterntagger.menus;

import appeng.api.config.YesNo;
import appeng.api.storage.ISubMenuHost;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.style.BackgroundGenerator;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.ae2PatternTagger.ae2patterntagger.items.components.PatternProviderTag;
import org.ae2PatternTagger.ae2patterntagger.items.components.TaggerSetting;
import org.ae2PatternTagger.ae2patterntagger.menus.widgets.*;

import java.util.ArrayList;
import java.util.List;

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
                this.menu.setTag(new PatternProviderTag(text));
            }
        }, GUIText.TaggerEditConfirm.text());

        saveButton = new MActionButton(Icon.VIEW_MODE_ALL, (button) -> {
            if (this.menu.setting.isLockEdit() == YesNo.YES) return;
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


//        scrollbar = widgets.addScrollBar("scrollbar");
//        scrollView = new ScrollView<>(new IScrollViewData.ScrollViewData<>(),
//                ScrollViewItemBuilder.create(TaggerSVItem::new, 24, 60),
//                scrollbar, style);
//        var sv_data = new ArrayList<PatternProviderTag>();
//        sv_data.add(new PatternProviderTag("111"));
//        sv_data.add(new PatternProviderTag("222"));
//        sv_data.add(new PatternProviderTag("333"));
//        sv_data.add(new PatternProviderTag("444"));
//        sv_data.add(new PatternProviderTag("555"));
//        sv_data.add(new PatternProviderTag("666"));
//        scrollView.setData(sv_data);
//        widgets.add("scrollview", scrollView);
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


}
