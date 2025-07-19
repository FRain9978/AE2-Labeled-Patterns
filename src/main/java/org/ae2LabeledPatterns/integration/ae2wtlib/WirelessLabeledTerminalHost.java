package org.ae2LabeledPatterns.integration.ae2wtlib;

import appeng.api.networking.IGridNode;
import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import net.minecraft.world.entity.player.Player;
import org.ae2LabeledPatterns.items.components.ComponentRegisters;
import org.ae2LabeledPatterns.items.components.PatternProviderLabel;
import org.ae2LabeledPatterns.menus.ILabeledPatternAccessTermMenuHost;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class WirelessLabeledTerminalHost extends WTMenuHost implements ILabeledPatternAccessTermMenuHost {
    public WirelessLabeledTerminalHost(ItemWT item, Player player, ItemMenuHostLocator locator, BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator, returnToMainMenu);
    }

    @Override
    public @Nullable IGridNode getGridNode() {
        return this.getActionableNode();
    }

    @Override
    public PatternProviderLabel getCurrentTag() {
        return getItemStack().getOrDefault(ComponentRegisters.PATTERN_PROVIDER_LABEL, PatternProviderLabel.Empty);
    }

    @Override
    public void setCurrentTag(PatternProviderLabel tag) {
        getItemStack().set(ComponentRegisters.PATTERN_PROVIDER_LABEL, tag);
    }
}
