package org.ae2LabeledPatterns;


import appeng.api.util.AEColor;
import appeng.client.render.StaticItemColor;
import appeng.core.network.ServerboundPacket;
import appeng.helpers.IMouseWheelItem;
import appeng.items.parts.ColoredPartItem;
import appeng.items.parts.PartItem;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.ae2LabeledPatterns.items.IMMouseWheelItem;
import org.ae2LabeledPatterns.menus.InitScreens;
import org.ae2LabeledPatterns.network.MMouseWheelPacket;
import org.ae2LabeledPatterns.parts.PartRegisters;

@Mod(value = Ae2LabeledPatterns.MODID, dist = Dist.CLIENT)
public class Ae2LabeledPatternsClient {
    public static final String KEYBINDING_MOUSE_WHEEL_ITEM_MODIFIER_1_DESCRIPTION = "key." + Ae2LabeledPatterns.MODID + ".mouse_wheel_item_modifier_1.description";
    public static final String KEYBINDING_MOUSE_WHEEL_ITEM_MODIFIER_2_DESCRIPTION = "key." + Ae2LabeledPatterns.MODID + ".mouse_wheel_item_modifier_2.description";
    public static final String KEYBINDING_MOD_CATEGORY = "key." + Ae2LabeledPatterns.MODID + ".category";

    private static final KeyMapping MOUSE_WHEEL_ITEM_MODIFIER_1 = new KeyMapping(
            KEYBINDING_MOUSE_WHEEL_ITEM_MODIFIER_1_DESCRIPTION, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
            InputConstants.KEY_LSHIFT, KEYBINDING_MOD_CATEGORY);

    private static final KeyMapping MOUSE_WHEEL_ITEM_MODIFIER_2 = new KeyMapping(
            KEYBINDING_MOUSE_WHEEL_ITEM_MODIFIER_2_DESCRIPTION, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,
            InputConstants.KEY_LALT, KEYBINDING_MOD_CATEGORY);

    public Ae2LabeledPatternsClient(IEventBus modEventBus) {
        modEventBus.addListener(this::registerScreen);
        modEventBus.addListener(this::registerModels);
        modEventBus.addListener(this::registerKeyBinding);
        modEventBus.addListener(this::registerItemColors);
        NeoForge.EVENT_BUS.addListener(this::registerMouseEvent);
    }

    public void registerScreen(RegisterMenuScreensEvent event){
        InitScreens.register(event);
    }

    public void registerModels(ModelEvent.RegisterAdditional event){
//            PartRegisters.registerModels(event);
        PartRegisters.registerModels();
    }

    public void registerKeyBinding(RegisterKeyMappingsEvent event) {
        // Register the key mappings for mouse wheel item modifiers
        event.register(MOUSE_WHEEL_ITEM_MODIFIER_1);
        event.register(MOUSE_WHEEL_ITEM_MODIFIER_2);
    }

    public void registerItemColors(RegisterColorHandlersEvent.Item event){
        event.register(makeOpaque(new StaticItemColor(AEColor.TRANSPARENT)), PartRegisters.LABELED_PATTERN_ACCESS_TERMINAL);
    }

    private ItemColor makeOpaque(ItemColor itemColor) {
        return (stack, tintIndex) -> FastColor.ARGB32.opaque(itemColor.getColor(stack, tintIndex));
    }

    public void registerMouseEvent(final InputEvent.MouseScrollingEvent event) {
        if (event.getScrollDeltaY() == 0) {
            return;
        }

        final Minecraft mc = Minecraft.getInstance();
        final Player player = mc.player;

        int downIndex = 0;
        if (MOUSE_WHEEL_ITEM_MODIFIER_1.isDown()) {
            downIndex = 1;
        } else if (MOUSE_WHEEL_ITEM_MODIFIER_2.isDown()) {
            downIndex = 2;
        }
        if (downIndex != 0){
            boolean mainHand = false;
            if (player != null) {
                mainHand = player.getItemInHand(InteractionHand.MAIN_HAND)
                        .getItem() instanceof IMMouseWheelItem;
            }
            boolean offHand = false;
            if (player != null) {
                offHand = player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof IMouseWheelItem;
            }

            if (mainHand || offHand) {
                ServerboundPacket message = new MMouseWheelPacket(event.getScrollDeltaY() > 0, downIndex);
                PacketDistributor.sendToServer(message);
                event.setCanceled(true);
            }
        }
    }
}
