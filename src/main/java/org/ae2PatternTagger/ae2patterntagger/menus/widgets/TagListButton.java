package org.ae2PatternTagger.ae2patterntagger.menus.widgets;

import appeng.api.storage.ISubMenuHost;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.widgets.IconButton;
import appeng.menu.AEBaseMenu;
import net.minecraft.network.chat.Component;
import org.ae2PatternTagger.ae2patterntagger.menus.ITagsProvider;
import org.ae2PatternTagger.ae2patterntagger.menus.TagListScreen;

import java.util.List;

public class TagListButton extends IconButton {
    public static <C extends AEBaseMenu & ITagsProvider, P extends AEBaseScreen<C>> TagListButton create(
            P parentScreen, ISubMenuHost subMenuHost, Component tooltip) {
        return new TagListButton(
                () ->{
                    parentScreen.switchToScreen(new TagListScreen<>(parentScreen, subMenuHost));
                },
                tooltip);
    }

    private final Component tooltip;

    public TagListButton(Runnable onPress, Component tooltip) {
        super(btn -> onPress.run());
        this.tooltip = tooltip;
    }

    @Override
    public List<Component> getTooltipMessage() {
        return List.of(tooltip);
    }

    @Override
    protected Icon getIcon() {
        return Icon.COG;
    }
}
