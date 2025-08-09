package org.ae2LabeledPatterns.menus.subscreens;

import appeng.api.storage.ISubMenuHost;
import appeng.client.Point;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.AESubScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.Color;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.widgets.Scrollbar;
import appeng.client.gui.widgets.TabButton;
import appeng.menu.AEBaseMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import org.ae2LabeledPatterns.menus.GUIText;
import org.ae2LabeledPatterns.menus.ILabelsProvider;
import org.ae2LabeledPatterns.menus.widgets.MActionButton;


public class LabelListScreen<C extends AEBaseMenu & ILabelsProvider, P extends AEBaseScreen<C>>
        extends AESubScreen<C, P> {
    private static final String stylePath = "/screens/label_list.json";
    private final ILabelsProvider labelsProvider;
    private final Color textColor;

    private final Blitter buttonBgNormal;
    private final Blitter buttonBgSelected;

    private final Scrollbar scrollbar;

    private final int maxShownItems = 6;

    private final int ItemHeight = 23;
    private final int TopPadding = 16;
    private final int LeftPadding = 12;

    private final MActionButton selectButton;
    private final MActionButton deleteButton;

    private int currentSelectedIndex = -1;

    public LabelListScreen(P parent, ISubMenuHost subMenuHost) {
        super(parent, stylePath);
        labelsProvider = parent.getMenu();
        this.textColor = style.getColor(PaletteColor.DEFAULT_TEXT_COLOR);

        buttonBgNormal = style.getImage("normal");
        buttonBgSelected = style.getImage("select");

        addBackButton(subMenuHost);
        scrollbar = widgets.addScrollBar("scrollbar", Scrollbar.BIG);
        scrollbar.setRange(0, Math.max(labelsProvider.getLabels().size() - 6, 0), 1);

        selectButton = new MActionButton(Icon.ENTER, (button) -> {
            var labels = labelsProvider.getLabels();
            if (labels.size() > scrollbar.getCurrentScroll() && currentSelectedIndex >= 0) {
                var label = labels.get(currentSelectedIndex);
                labelsProvider.setCurrentLabel(label);
            }
        }, GUIText.LabelListSelect.text());
        addRenderableWidget(selectButton);
        selectButton.visible = false;

        deleteButton = new MActionButton(Icon.CLEAR, (button) -> {
            var labels = labelsProvider.getLabels();
            if (labels.size() > scrollbar.getCurrentScroll() && currentSelectedIndex >= 0) {
                var label = labels.get(currentSelectedIndex);
                labelsProvider.deleteLabel(label);
            }
        }, GUIText.LabelListDelete.text());
        addRenderableWidget(deleteButton);
        deleteButton.visible = false;
    }

    private void addBackButton(ISubMenuHost subMenuHost) {
        var icon = subMenuHost.getMainMenuIcon();
        var label = icon.getHoverName();
        TabButton button = new TabButton(Icon.BACK, label, btn -> returnToParent());
        widgets.add("back", button);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        scrollbar.setRange(0, Math.max(labelsProvider.getLabels().size() - 6, 0), 1);
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        // draw two buttons
        var tags = labelsProvider.getLabels();
        offsetX += LeftPadding;
        offsetY += TopPadding;
        Point mousePos = new Point(mouseX, mouseY);
        var scrollbarValue = scrollbar.getCurrentScroll();
        var hasHovered = false;
        var shownItems = Math.min(maxShownItems, tags.size());
        for(var i = 0; i < shownItems; i++) {
            var tag = tags.get(i + scrollbarValue);
            Rect2i buttonArea = new Rect2i(offsetX, offsetY, 91, ItemHeight);
            var isHover = mousePos.isIn(buttonArea);
            if (isHover){
                selectButton.setPosition(offsetX + 56, offsetY);
                deleteButton.setPosition(offsetX + 76, offsetY);
                currentSelectedIndex = i + scrollbarValue;
                hasHovered = true;
            }
            if (tag.equals(labelsProvider.currentLabel())){
                if (isHover){
                    selectButton.visible = false;
                    deleteButton.visible = true;
                }
            } else{
                if (isHover){
                    selectButton.visible = true;
                    deleteButton.visible = true;
                }
            }
            offsetY += ItemHeight;
        }
        if (!hasHovered){
            selectButton.visible = false;
            deleteButton.visible = false;
            currentSelectedIndex = -1;
        }
    }

    @Override
    public void drawBG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
        // draw background and text
        super.drawBG(guiGraphics, offsetX, offsetY, mouseX, mouseY, partialTicks);
        var tags = labelsProvider.getLabels();
        var defaultFont = Minecraft.getInstance().font;
        offsetX += LeftPadding;
        offsetY += TopPadding;
        var scrollbarValue = scrollbar.getCurrentScroll();
        var shownItems = Math.min(maxShownItems, tags.size());
        for(var i = 0; i < shownItems; i++) {
            var tag = tags.get(i + scrollbarValue);
            if (tag.equals(labelsProvider.currentLabel())){
                buttonBgSelected.dest(offsetX, offsetY).blit(guiGraphics);
            } else{
                buttonBgNormal.dest(offsetX, offsetY).blit(guiGraphics);
            }
            guiGraphics.drawString(defaultFont, Component.literal(tag.name()), offsetX + 2, offsetY + 5, textColor.toARGB(), false);
            offsetY += ItemHeight;
        }
    }
}
