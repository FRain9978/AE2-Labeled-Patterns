package org.ae2PatternTagger.ae2patterntagger.network;

import appeng.core.network.ServerboundPacket;
import appeng.menu.AEBaseMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.ae2PatternTagger.ae2patterntagger.Ae2patterntagger;
import org.ae2PatternTagger.ae2patterntagger.config.QuickMoveAction;
import org.ae2PatternTagger.ae2patterntagger.menus.AdvancedPatternAccessTerminalMenu;

public record InventoryQuickMovePacket(
        QuickMoveAction action,
        int playerInventorySlot,
        long containerId
)implements ServerboundPacket
{
    public static final StreamCodec<RegistryFriendlyByteBuf, InventoryQuickMovePacket> STREAM_CODEC =
            StreamCodec.ofMember(
                    InventoryQuickMovePacket::write,
                    InventoryQuickMovePacket::decode);
    public static final CustomPacketPayload.Type<InventoryQuickMovePacket> TYPE =
            new CustomPacketPayload.Type<>(Ae2patterntagger.makeId("inventory_quick_move"));

    public InventoryQuickMovePacket(QuickMoveAction action, int slot) {
        this(action, slot, 0L);
    }

    public CustomPacketPayload.Type<InventoryQuickMovePacket> type() {
        return TYPE;
    }

    public static InventoryQuickMovePacket decode(RegistryFriendlyByteBuf stream) {
        QuickMoveAction action = (QuickMoveAction)stream.readEnum(QuickMoveAction.class);
        int slot = stream.readInt();
        long extraId = stream.readLong();
        return new InventoryQuickMovePacket(action, slot, extraId);
    }

    public void write(RegistryFriendlyByteBuf data) {
        data.writeEnum(this.action);
        data.writeInt(this.playerInventorySlot);
        data.writeLong(this.containerId);
    }

    public void handleOnServer(ServerPlayer player) {
        AbstractContainerMenu var3 = player.containerMenu;
        if (var3 instanceof AdvancedPatternAccessTerminalMenu menu) {
            menu.quickMoveToPatternContainer(player, this.action, this.playerInventorySlot, this.containerId);
        }

    }

}
