package org.ae2LabeledPatterns.menus;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ae2LabeledPatterns.AE2LabeledPatterns;
import org.ae2LabeledPatterns.integration.ae2wtlib.WirelessLabeledTerminalMenu;

import java.util.function.Supplier;

public class MenuRegisters {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(BuiltInRegistries.MENU, AE2LabeledPatterns.MODID);

    public static final Supplier<MenuType<LabelerMenu>> LABELER_MENU = MENUS.register("labeler", () -> LabelerMenu.TYPE);
    public static final Supplier<MenuType<LabeledPatternAccessTerminalMenu>> LABELED_PATTERN_ACCESS_TERMINAL_MENU =
            MENUS.register("labeled_pattern_access_terminal", () -> LabeledPatternAccessTerminalMenu.TYPE);
    public static final Supplier<MenuType<WirelessLabeledTerminalMenu>> WIRELESS_LABELED_PATTERN_ACCESS_TERMINAL_MENU =
            MENUS.register("wireless_labeled_pattern_access_terminal", () -> WirelessLabeledTerminalMenu.TYPE);
}
