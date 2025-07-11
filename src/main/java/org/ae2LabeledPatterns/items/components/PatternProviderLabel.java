package org.ae2LabeledPatterns.items.components;

import appeng.client.gui.style.Color;
import appeng.menu.guisync.PacketWritable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PatternProviderLabel(String name) implements PacketWritable {

    public PatternProviderLabel(){
        this("");
    }

    public PatternProviderLabel(RegistryFriendlyByteBuf data){
        this(STREAM_CODEC.decode(data));
    }

    public PatternProviderLabel(PatternProviderLabel other){
        this(other.name);
    }

    public boolean isEmpty() {
        return this.equals(Empty);
    }

    public static final Codec<PatternProviderLabel> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("name").forGetter(PatternProviderLabel::name)
            ).apply(instance, PatternProviderLabel::new)
    );

    public static final StreamCodec<ByteBuf, PatternProviderLabel> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, PatternProviderLabel::name,
            PatternProviderLabel::new
    );

    public void writeToNBT(CompoundTag tag){
        tag.putString("pattern_provider_label_name", name);
    }

    public static PatternProviderLabel readFromNBT(CompoundTag tag){
        String name = tag.getString("pattern_provider_label_name");
        return new PatternProviderLabel(name);
    }

    @Override
    public void writeToPacket(RegistryFriendlyByteBuf data) {
        STREAM_CODEC.encode(data, this);
    }

    public static final PatternProviderLabel Empty = new PatternProviderLabel(""); // Default empty label with black transparent color
}
