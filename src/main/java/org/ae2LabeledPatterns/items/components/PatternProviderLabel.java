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

public record PatternProviderLabel(String name, Color color) implements PacketWritable {
    public PatternProviderLabel(String name, int r, int g, int b, int a){
        this(name, new Color(r,g,b,a));
    }

    public PatternProviderLabel(String name, String color){
        this(name, Color.parse(color));
    }

    public PatternProviderLabel(String name){
        this(name,"#00000000");
    }

    public PatternProviderLabel(){
        this("");
    }

    public PatternProviderLabel(RegistryFriendlyByteBuf data){
        this(STREAM_CODEC.decode(data));
    }

    public PatternProviderLabel(PatternProviderLabel other){
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

    public static final Codec<PatternProviderLabel> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("name").forGetter(PatternProviderLabel::name),
                    Codec.INT.fieldOf("a").forGetter(PatternProviderLabel::getColorA),
                    Codec.INT.fieldOf("r").forGetter(PatternProviderLabel::getColorR),
                    Codec.INT.fieldOf("g").forGetter(PatternProviderLabel::getColorG),
                    Codec.INT.fieldOf("b").forGetter(PatternProviderLabel::getColorB)
            ).apply(instance, PatternProviderLabel::new)
    );

    public static final StreamCodec<ByteBuf, PatternProviderLabel> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, PatternProviderLabel::name,
            ByteBufCodecs.INT, PatternProviderLabel::getColorA,
            ByteBufCodecs.INT, PatternProviderLabel::getColorR,
            ByteBufCodecs.INT, PatternProviderLabel::getColorG,
            ByteBufCodecs.INT, PatternProviderLabel::getColorB,
            PatternProviderLabel::new
    );

    public void writeToNBT(CompoundTag tag){
        tag.putString("pattern_provider_label_name", name);
        tag.putInt("pattern_provider_label_colorR", getColorR());
        tag.putInt("pattern_provider_label_colorG", getColorG());
        tag.putInt("pattern_provider_label_colorB", getColorB());
        tag.putInt("pattern_provider_label_colorA", getColorA());
    }

    public static PatternProviderLabel readFromNBT(CompoundTag tag){
        String name = tag.getString("pattern_provider_label_name");
        int r = tag.getInt("pattern_provider_label_colorR");
        int g = tag.getInt("pattern_provider_label_colorG");
        int b = tag.getInt("pattern_provider_label_colorB");
        int a = tag.getInt("pattern_provider_label_colorA");
        return new PatternProviderLabel(name, r, g, b, a);
    }

    @Override
    public void writeToPacket(RegistryFriendlyByteBuf data) {
        STREAM_CODEC.encode(data, this);
    }

    public static final PatternProviderLabel Empty = new PatternProviderLabel("", 0, 0, 0, 0); // Default empty label with black transparent color
}
