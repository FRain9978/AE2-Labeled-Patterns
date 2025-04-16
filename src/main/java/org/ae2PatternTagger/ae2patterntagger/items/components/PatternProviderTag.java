package org.ae2PatternTagger.ae2patterntagger.items.components;

import appeng.client.gui.style.Color;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PatternProviderTag(String name, Color color) {
    public PatternProviderTag(String name, int r, int g, int b, int a){
        this(name, new Color(r,g,b,a));
    }

    public PatternProviderTag(String name, String color){
        this(name, Color.parse(color));
    }

    public PatternProviderTag(String name){
        this(name,"#ffffffff");
    }

    public PatternProviderTag(){
        this("");
    }

    public PatternProviderTag(PatternProviderTag other){
        this(other.name, new Color(other.color.getR(), other.color.getG(), other.color.getB(), other.color.getA()));
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
}
