package org.ae2PatternTagger.items.components;

import appeng.client.gui.style.Color;
import appeng.menu.guisync.PacketWritable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PatternProviderTag(String name, Color color) implements PacketWritable {
    public PatternProviderTag(String name, int r, int g, int b, int a){
        this(name, new Color(r,g,b,a));
    }

    public PatternProviderTag(String name, String color){
        this(name, Color.parse(color));
    }

    public PatternProviderTag(String name){
        this(name,"#00000000");
    }

    public PatternProviderTag(){
        this("");
    }

    public PatternProviderTag(RegistryFriendlyByteBuf data){
        this(STREAM_CODEC.decode(data));
    }

    public PatternProviderTag(PatternProviderTag other){
        this(other.name, new Color(other.color.getR(), other.color.getG(), other.color.getB(), other.color.getA()));
    }

    public boolean isEmpty() {
        return this.equals(Empty);
    }

    public int getColorR(){
        return color.getR();
    }
    public int getColorG(){
        return color.getG();
    }
    public int getColorB(){
        return color.getB();
    }
    public int getColorA(){
        return color.getA();
    }

    public static final Codec<PatternProviderTag> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("name").forGetter(PatternProviderTag::name),
                    Codec.INT.fieldOf("a").forGetter(PatternProviderTag::getColorA),
                    Codec.INT.fieldOf("r").forGetter(PatternProviderTag::getColorR),
                    Codec.INT.fieldOf("g").forGetter(PatternProviderTag::getColorG),
                    Codec.INT.fieldOf("b").forGetter(PatternProviderTag::getColorB)
            ).apply(instance, PatternProviderTag::new)
    );

    public static final StreamCodec<ByteBuf, PatternProviderTag> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, PatternProviderTag::name,
            ByteBufCodecs.INT, PatternProviderTag::getColorA,
            ByteBufCodecs.INT, PatternProviderTag::getColorR,
            ByteBufCodecs.INT, PatternProviderTag::getColorG,
            ByteBufCodecs.INT, PatternProviderTag::getColorB,
            PatternProviderTag::new
    );

    public void writeToNBT(CompoundTag tag){
        tag.putString("pattern_provider_tag_name", name);
        tag.putInt("pattern_provider_tag_colorR", getColorR());
        tag.putInt("pattern_provider_tag_colorG", getColorG());
        tag.putInt("pattern_provider_tag_colorB", getColorB());
        tag.putInt("pattern_provider_tag_colorA", getColorA());
    }

    public static PatternProviderTag readFromNBT(CompoundTag tag){
        String name = tag.getString("pattern_provider_tag_name");
        int r = tag.getInt("pattern_provider_tag_colorR");
        int g = tag.getInt("pattern_provider_tag_colorG");
        int b = tag.getInt("pattern_provider_tag_colorB");
        int a = tag.getInt("pattern_provider_tag_colorA");
        return new PatternProviderTag(name, r, g, b, a);
    }

    @Override
    public void writeToPacket(RegistryFriendlyByteBuf data) {
        STREAM_CODEC.encode(data, this);
    }

    public static final PatternProviderTag Empty = new PatternProviderTag("", 0, 0, 0, 0); // Default empty tag with black transparent color
}
