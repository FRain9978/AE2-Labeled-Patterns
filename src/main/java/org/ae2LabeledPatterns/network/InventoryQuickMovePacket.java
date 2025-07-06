package org.ae2LabeledPatterns.network;

import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.core.network.ServerboundPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.ae2LabeledPatterns.Ae2LabeledPatterns;
import org.ae2LabeledPatterns.menus.LabeledPatternAccessTerminalMenu;

public record InventoryQuickMovePacket(
        int playerInventorySlot,
        long containerId,
        PatternContainerGroup group
)implements ServerboundPacket
{
    public static final StreamCodec<RegistryFriendlyByteBuf, InventoryQuickMovePacket> STREAM_CODEC =
            StreamCodec.ofMember(
                    InventoryQuickMovePacket::write,
                    InventoryQuickMovePacket::decode);
    public static final CustomPacketPayload.Type<InventoryQuickMovePacket> TYPE =
            new CustomPacketPayload.Type<>(Ae2LabeledPatterns.makeId("inventory_quick_move"));

    public InventoryQuickMovePacket(int slot, PatternContainerGroup group) {
        this(slot, 0L, group);
    }

    public CustomPacketPayload.Type<InventoryQuickMovePacket> type() {
        return TYPE;
    }

    public static InventoryQuickMovePacket decode(RegistryFriendlyByteBuf stream) {
        int slot = stream.readInt();
        long extraId = stream.readLong();
        PatternContainerGroup group = PatternContainerGroup.readFromPacket(stream);
        return new InventoryQuickMovePacket(slot, extraId, group);
    }

    public void write(RegistryFriendlyByteBuf data) {
        data.writeInt(this.playerInventorySlot);
        data.writeLong(this.containerId);
        this.group.writeToPacket(data);
    }

    public void handleOnServer(ServerPlayer player) {
        AbstractContainerMenu var3 = player.containerMenu;
        if (var3 instanceof LabeledPatternAccessTerminalMenu menu) {
            menu.quickMoveToPatternContainer(player, this.playerInventorySlot, this.containerId, this.group);
        }

    }

}
