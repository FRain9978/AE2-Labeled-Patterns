package org.ae2LabeledPatterns.menus.widgets;

import appeng.client.gui.Icon;
import appeng.client.gui.widgets.IconButton;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MActionButton extends MIconButton {
    private Component titleMessage;
    private List<Component> extraTooltip;

    public MActionButton(@NotNull Icon icon , OnPress onPress) {
        super(onPress, icon);
    }

    public MActionButton(@NotNull Icon icon , OnPress onPress, @NotNull Component titleMessage) {
        this(icon, onPress);
        this.titleMessage = titleMessage;
    }

    public void setExtraTooltip(List<Component> extraTooltip) {
        this.extraTooltip = extraTooltip;
    }

    @Override
    public List<Component> getTooltipMessage() {
        // combine titleMessage and extraTooltip
        List<Component> tooltip = new ArrayList<>();
        if (titleMessage != null) {
            tooltip.add(titleMessage);
        }
        if (extraTooltip != null) {
            tooltip.addAll(extraTooltip);
        }
        return tooltip;

    }
}
