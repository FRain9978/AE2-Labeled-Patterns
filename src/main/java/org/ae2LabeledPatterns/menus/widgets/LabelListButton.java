package org.ae2LabeledPatterns.menus.widgets;

import appeng.api.storage.ISubMenuHost;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.widgets.IconButton;
import appeng.menu.AEBaseMenu;
import net.minecraft.network.chat.Component;
import org.ae2LabeledPatterns.menus.ILabelsProvider;
import org.ae2LabeledPatterns.menus.LabelListScreen;

import java.util.List;

public class LabelListButton extends IconButton {
    public static <C extends AEBaseMenu & ILabelsProvider, P extends AEBaseScreen<C>> LabelListButton create(
            P parentScreen, ISubMenuHost subMenuHost, Component tooltip) {
        return new LabelListButton(
                () ->{
                    parentScreen.switchToScreen(new LabelListScreen<>(parentScreen, subMenuHost));
                },
                tooltip);
    }

    private final Component tooltip;

    public LabelListButton(Runnable onPress, Component tooltip) {
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
