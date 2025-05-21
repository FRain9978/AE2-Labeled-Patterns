package org.ae2PatternTagger.ae2patterntagger.menus;

import appeng.api.storage.ISubMenuHost;
import appeng.client.Point;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.AESubScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.Color;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.widgets.AE2Button;
import appeng.client.gui.widgets.Scrollbar;
import appeng.client.gui.widgets.TabButton;
import appeng.menu.AEBaseMenu;
import appeng.menu.interfaces.KeyTypeSelectionMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import org.ae2PatternTagger.ae2patterntagger.menus.widgets.MActionButton;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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

    private List<Button> buttons;
    private final MActionButton selectButton;
    private final MActionButton deleteButton;

    private int currentSelectedIndex = -1;

    public TagListScreen(P parent, ISubMenuHost subMenuHost) {
        super(parent, stylePath);
        tagsProvider = parent.getMenu();
        this.textColor = style.getColor(PaletteColor.DEFAULT_TEXT_COLOR);

        buttonBgNormal = style.getImage("normal");
        buttonBgSelected = style.getImage("select");

        addBackButton(subMenuHost);
        scrollbar = widgets.addScrollBar("scrollbar", Scrollbar.BIG);
        scrollbar.setRange(0, Math.max(tagsProvider.getTags().size() - 6, 0), 1);

        selectButton = new MActionButton(Icon.VIEW_MODE_CRAFTING, (button) -> {
            var tags = tagsProvider.getTags();
            if (tags.size() > scrollbar.getCurrentScroll() && currentSelectedIndex >= 0) {
                var tag = tags.get(currentSelectedIndex);
                tagsProvider.setCurrentTag(tag);
            }
        }, GUIText.TaggerListSelect.text());
        addRenderableWidget(selectButton);
        selectButton.visible = false;
        deleteButton = new MActionButton(Icon.VIEW_MODE_ALL, (button) -> {
            var tags = tagsProvider.getTags();
            if (tags.size() > scrollbar.getCurrentScroll() && currentSelectedIndex >= 0) {
                var tag = tags.get(currentSelectedIndex);
                tagsProvider.removeTag(tag);
            }
        }, GUIText.TaggerListDelete.text());
        addRenderableWidget(deleteButton);
        deleteButton.visible = false;
//        buttons = new ArrayList<>();
//        for (int i = 0; i < 6; i++) {
//            var btn = getTagButton(i);
//            addRenderableWidget(btn);
////            int finalI = i;
////            var btn = widgets.addButton("btn" + i, Component.empty(),
////                    (button) -> {
////                        var tags = tagsProvider.getTags();
////                        if (tags.size() > finalI + scrollbar.getCurrentScroll()) {
////                            var tag = tags.get(finalI + scrollbar.getCurrentScroll());
////                            tagsProvider.setCurrentTag(tag);
////                        }
////                    });
//            buttons.add(btn);
//        }
    }

    private @NotNull AE2Button getTagButton(int i) {
//        var btnBuilder = new Button.Builder(Component.empty(),
//                (button) -> {
//                    var tags = tagsProvider.getTags();
//                    if (tags.size() > i + scrollbar.getCurrentScroll()) {
//                        var tag = tags.get(i + scrollbar.getCurrentScroll());
//                        tagsProvider.setCurrentTag(tag);
//                    }
//                });

//        var btn = new TagButton(Component.literal("this is btn" + i), (button) -> {
//            var tags = tagsProvider.getTags();
//            if (tags.size() > i + scrollbar.getCurrentScroll()) {
//                var tag = tags.get(i + scrollbar.getCurrentScroll());
//                tagsProvider.setCurrentTag(tag);
//            }
//        });
//        var btn = btnBuilder
////                .pos(LeftMargin + this.leftPos, TopMargin + this.topPos + ItemHeight * i)
//                .size(91, ItemHeight)
//                .build();
        var btn = new AE2Button(Component.empty(),
                (button) -> {
                    var tags = tagsProvider.getTags();
                    if (tags.size() > i + scrollbar.getCurrentScroll()) {
                        var tag = tags.get(i + scrollbar.getCurrentScroll());
                        tagsProvider.setCurrentTag(tag);
                    }
                });
//            widgets.add("btn" + i, btn);
//        btn.setPosition(LeftMargin, TopMargin + ItemHeight * i);
        return btn;
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
        Point mousePos = new Point(mouseX, mouseY);
        var scrollbarValue = scrollbar.getCurrentScroll();
        var hasHovered = false;
        for(var i = 0; i < maxShownItems; i++) {
            var tag = tags.get(i + scrollbarValue);
//            var btn = buttons.get(i);
//            btn.setPosition(offsetX + this.leftPos, offsetY + this.topPos);
//            btn.setMessage(Component.literal(tag.name()));
//            btn.setSize(91, ItemHeight);
//            if (tag.equals(tagsProvider.currentTag())){
//                btn.setFocused(true);
//            }else {
//                btn.setFocused(false);
//            }
            Rect2i buttonArea = new Rect2i(offsetX + this.leftPos, offsetY + this.topPos, 91, ItemHeight);
            var isHover = mousePos.isIn(buttonArea);
            if (isHover){
                selectButton.setPosition(offsetX + this.leftPos + 56, offsetY + this.topPos);
                deleteButton.setPosition(offsetX + this.leftPos + 76, offsetY + this.topPos);
                currentSelectedIndex = i + scrollbarValue;
                hasHovered = true;
            }
            if (tag.equals(tagsProvider.currentTag())){
                buttonBgSelected.dest(offsetX, offsetY).blit(guiGraphics);
                if (isHover){
                    selectButton.visible = false;
                    deleteButton.visible = true;
                }
            } else{
                buttonBgNormal.dest(offsetX, offsetY).blit(guiGraphics);
                if (isHover){
                    selectButton.visible = true;
                    deleteButton.visible = true;
                }
            }
            guiGraphics.drawString(defaultFont, Component.literal(tag.name()), offsetX + 2, offsetY + 5, textColor.toARGB(), false);
            offsetY += ItemHeight;
        }
        if (!hasHovered){
            selectButton.visible = false;
            deleteButton.visible = false;
            currentSelectedIndex = -1;
        }
    }

    private class TagButton extends MActionButton{
        public TagButton(Component text, OnPress onPress) {
            super(Icon.COG, onPress, text);
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
            super.renderWidget(guiGraphics, mouseX, mouseY, partial);
        }
    }
}
