package org.ae2LabeledPatterns.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record MultiBlockTarget(
        List<BlockTarget> blockTargets
) {
    public MultiBlockTarget() {this(new ArrayList<>());}

    public static final Codec<MultiBlockTarget> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.list(BlockTarget.CODEC).fieldOf("blockTargets").forGetter(MultiBlockTarget::blockTargets)
            ).apply(instance, MultiBlockTarget::new)
    );

    public static final StreamCodec<ByteBuf, MultiBlockTarget> STREAM_CODEC = StreamCodec.composite(
            BlockTarget.STREAM_CODEC.apply(ByteBufCodecs.list()),
            MultiBlockTarget::blockTargets,
            MultiBlockTarget::new
    );

    public boolean isEmpty() {
        return blockTargets.isEmpty();
    }

    public boolean isFull() {
        return blockTargets.size() >= 2;
    }

    public void addTarget(Level level, BlockPos pos) {
        blockTargets.add(new BlockTarget(level.dimension().location(), pos));
    }

    @Nullable
    public BlockTarget t1() {
        return isEmpty() ? null : blockTargets.getFirst();
    }

    @Nullable
    public BlockTarget t2() {
        return isFull() ? blockTargets.get(1) : null ;
    }

    public MultiBlockTarget clear(){
        blockTargets.clear();
        return this;
    }
}
