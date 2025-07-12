package org.ae2LabeledPatterns.network;

import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.core.network.ServerboundPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.ae2LabeledPatterns.Ae2LabeledPatterns;
import org.ae2LabeledPatterns.menus.LabeledPatternAccessTerminalMenu;

import java.util.List;

public record InventoryQuickMovePacket(
        int playerInventorySlot,
        int mouseButton,
        List<Long> containerIds,
        PatternContainerGroup group
)implements ServerboundPacket
{
//    public static final StreamCodec<RegistryFriendlyByteBuf, InventoryQuickMovePacket> STREAM_CODEC =
//            StreamCodec.ofMember(
//                    InventoryQuickMovePacket::write,
//                    InventoryQuickMovePacket::decode);
    public static final StreamCodec<RegistryFriendlyByteBuf, InventoryQuickMovePacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    InventoryQuickMovePacket::playerInventorySlot,
                    ByteBufCodecs.VAR_INT,
                    InventoryQuickMovePacket::mouseButton,
                    ByteBufCodecs.VAR_LONG.apply(ByteBufCodecs.list()),
                    InventoryQuickMovePacket::containerIds,
                    StreamCodec.ofMember(PatternContainerGroup::writeToPacket, PatternContainerGroup::readFromPacket),
                    InventoryQuickMovePacket::group,
                    InventoryQuickMovePacket::new
            );
    public static final CustomPacketPayload.Type<InventoryQuickMovePacket> TYPE =
            new CustomPacketPayload.Type<>(Ae2LabeledPatterns.makeId("inventory_quick_move"));

    public CustomPacketPayload.Type<InventoryQuickMovePacket> type() {
        return TYPE;
    }


    public void handleOnServer(ServerPlayer player) {
        AbstractContainerMenu var3 = player.containerMenu;
        if (var3 instanceof LabeledPatternAccessTerminalMenu menu) {
            menu.quickMoveToPatternContainer(player, this.playerInventorySlot, this.mouseButton, this.containerIds, this.group);
        }
    }

}
