package org.ae2LabeledPatterns.items;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public interface IMMouseWheelItem {
    void onWheel(ServerPlayer serverPlayer, ItemStack is, boolean up, int index);
}

