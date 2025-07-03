package org.ae2LabeledPatterns.menus;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ae2LabeledPatterns.Ae2LabeledPatterns;

import java.util.function.Supplier;

public class MenuRegisters {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(BuiltInRegistries.MENU, Ae2LabeledPatterns.MODID);

//    public static final Supplier<MenuType<LabelerMenu>> TAGGER_MENU = registerMenuType("tagger", LabelerMenu.TYPE);
    public static final Supplier<MenuType<LabelerMenu>> TAGGER_MENU = MENUS.register("tagger", () -> LabelerMenu.TYPE);
    public static final Supplier<MenuType<LabeledPatternAccessTerminalMenu>> ADVANCED_PATTERN_ACCESS_TERMINAL_MENU =
            MENUS.register("advanced_pattern_access_terminal", () -> LabeledPatternAccessTerminalMenu.TYPE);

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name,
                                                                                                               IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }
}
