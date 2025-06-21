package org.ae2PatternTagger.ae2patterntagger.network;

import appeng.core.network.ClientboundPacket;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.ae2PatternTagger.ae2patterntagger.Ae2patterntagger;
import org.ae2PatternTagger.ae2patterntagger.blocks.attachments.AttachmentRegisters;
import org.ae2PatternTagger.ae2patterntagger.items.components.PatternProviderTag;

public record SaveTagAttachmentPacket(
        PatternProviderTag tag,
        BlockPos blockPos
) implements ClientboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, SaveTagAttachmentPacket> STREAM_CODEC = StreamCodec.ofMember(
            SaveTagAttachmentPacket::write,
            SaveTagAttachmentPacket::decode
    );

    public static final Type<SaveTagAttachmentPacket> TYPE =
            new CustomPacketPayload.Type<>(Ae2patterntagger.makeId("save_tag_attachment"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static SaveTagAttachmentPacket decode(RegistryFriendlyByteBuf stream){
        var tag = PatternProviderTag.STREAM_CODEC.decode(stream);
        var blockPos = stream.readBlockPos();
        return new SaveTagAttachmentPacket(tag, blockPos);
    }

    private void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        PatternProviderTag.STREAM_CODEC.encode(registryFriendlyByteBuf, tag);
        registryFriendlyByteBuf.writeBlockPos(blockPos);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleOnClient(Player player){
        var level = player.level();
        var blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof PatternProviderLogicHost){
            blockEntity.setData(AttachmentRegisters.PATTERN_PROVIDER_TAG, tag);
        }
    }
}
