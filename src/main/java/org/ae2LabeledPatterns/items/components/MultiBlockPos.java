package org.ae2LabeledPatterns.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record MultiBlockPos(
        List<BlockPos> blockPos
) {
    public MultiBlockPos() {this(new ArrayList<>());}

    public static final Codec<MultiBlockPos> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.list(BlockPos.CODEC).fieldOf("blockPos").forGetter(MultiBlockPos::blockPos)
            ).apply(instance, MultiBlockPos::new)
    );

    public static final StreamCodec<ByteBuf, MultiBlockPos> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()),
            MultiBlockPos::blockPos,
            MultiBlockPos::new
    );

    public boolean isEmpty() {
        return blockPos.isEmpty();
    }

    public boolean isFull() {
        return blockPos.size() >= 2;
    }

    public void addPos(BlockPos pos) {
        blockPos.add(pos);
    }

    @Nullable
    public BlockPos p1() {
        return isEmpty() ? null : blockPos.getFirst();
    }

    @Nullable
    public BlockPos p2() {
        return isFull() ? blockPos.get(1) : null ;
    }

    public MultiBlockPos clear(){
        blockPos.clear();
        return this;
    }
}
