package org.ae2LabeledPatterns.integration.ae2wtlib;

import appeng.api.config.Settings;
import appeng.api.config.ShowPatternProviders;
import appeng.api.config.YesNo;
import appeng.api.util.IConfigManager;
import appeng.menu.locator.ItemMenuHostLocator;
import de.mari_023.ae2wtlib.api.terminal.AE2wtlibConfigManager;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.ae2LabeledPatterns.config.MSettings;
import org.ae2LabeledPatterns.menus.MoveConvenience;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class WirelessLabeledTerminalItem extends ItemWT {
    public WirelessLabeledTerminalItem(){}
    public WirelessLabeledTerminalItem(Properties properties){

    }
    @Override
    public @NotNull MenuType<?> getMenuType(@NotNull ItemMenuHostLocator itemMenuHostLocator, @NotNull Player player) {
        return WirelessLabeledTerminalMenu.TYPE;
    }

    @Override
    public @NotNull IConfigManager getConfigManager(@NotNull Supplier<ItemStack> target) {
        return AE2wtlibConfigManager.builder(target)
                .registerSetting(Settings.TERMINAL_SHOW_PATTERN_PROVIDERS, ShowPatternProviders.VISIBLE)
                .registerSetting(MSettings.TERMINAL_MOVE_CONVENIENCE, MoveConvenience.NONE)
                .registerSetting(MSettings.TERMINAL_SHOW_GROUP_SELECT_RATIO, YesNo.NO)
                .build();
    }
}
