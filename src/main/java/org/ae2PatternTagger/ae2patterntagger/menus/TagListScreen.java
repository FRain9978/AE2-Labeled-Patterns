package org.ae2PatternTagger.ae2patterntagger.menus;

import appeng.api.storage.ISubMenuHost;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.AESubScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.Color;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.widgets.Scrollbar;
import appeng.client.gui.widgets.TabButton;
import appeng.menu.AEBaseMenu;
import appeng.menu.interfaces.KeyTypeSelectionMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class TagListScreen<C extends AEBaseMenu & ITagsProvider, P extends AEBaseScreen<C>>
        extends AESubScreen<C, P> {
    private static final String stylePath = "/screens/tag_list.json";
    private final ITagsProvider tagsProvider;
    private final Color textColor;

    private final Blitter buttonBgNormal;
    private final Blitter buttonBgSelected;

    private final Scrollbar scrollbar;

    private final int maxShownItems = 6;

    private final int ItemHeight = 23;
    private final int TopMargin = 16;
    private final int LeftMargin = 12;

    public TagListScreen(P parent, ISubMenuHost subMenuHost) {
        super(parent, stylePath);
        tagsProvider = parent.getMenu();
        this.textColor = style.getColor(PaletteColor.DEFAULT_TEXT_COLOR);

        buttonBgNormal = style.getImage("normal");
        buttonBgSelected = style.getImage("select");

        addBackButton(subMenuHost);
        scrollbar = widgets.addScrollBar("scrollbar", Scrollbar.BIG);
        scrollbar.setRange(0, Math.max(tagsProvider.getTags().size() - 6, 0), 1);
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
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        var tags = tagsProvider.getTags();
        var defaultFont = Minecraft.getInstance().font;
        offsetX = LeftMargin;
        offsetY = TopMargin;
        var scrollbarValue = scrollbar.getCurrentScroll();
        for(var i = 0; i < maxShownItems; i++) {
            var tag = tags.get(i + scrollbarValue);
            if (tag.equals(tagsProvider.currentTag())){
                buttonBgSelected.dest(offsetX, offsetY).blit(guiGraphics);
            } else{
                buttonBgNormal.dest(offsetX, offsetY).blit(guiGraphics);
            }
            guiGraphics.drawString(defaultFont, Component.literal(tag.name()), offsetX + 2, offsetY + 5, textColor.toARGB(), false);
            offsetY += ItemHeight;
        }
    }
}
