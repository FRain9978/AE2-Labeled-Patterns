package org.ae2PatternTagger.ae2patterntagger.menus.widgets;

import appeng.client.gui.Icon;
import appeng.client.gui.widgets.IconButton;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class MActionButton extends IconButton {
    private final Icon icon;

    public MActionButton(@NotNull Icon icon , OnPress onPress) {
        super(onPress);
        this.icon = icon;
    }

    public MActionButton(@NotNull Icon icon , OnPress onPress, @NotNull Component message) {
        this(icon, onPress);
        this.setMessage(message);
    }

    @Override
    protected Icon getIcon() {
        return icon;
    }
}
