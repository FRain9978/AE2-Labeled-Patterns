
package org.ae2PatternTagger.ae2patterntagger.network;

import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.core.network.ClientboundPacket;
import appeng.core.network.CustomAppEngPayload;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.ae2PatternTagger.ae2patterntagger.menus.AdvancedPatternAccessTerminalScreen;
import org.slf4j.LoggerFactory;

/**
 * Sends the content for a single {@link appeng.helpers.patternprovider.PatternContainer} shown in the pattern access
 * terminal to the client.
 */
public record AdvancedPatternAccessTerminalPacket(
        boolean fullUpdate,
        long inventoryId,
        int inventorySize, // Only valid if fullUpdate
        long sortBy, // Only valid if fullUpdate
        PatternContainerGroup group, // Only valid if fullUpdate
        Int2ObjectMap<ItemStack> slots) implements ClientboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, AdvancedPatternAccessTerminalPacket> STREAM_CODEC = StreamCodec
            .ofMember(
                    AdvancedPatternAccessTerminalPacket::write,
                    AdvancedPatternAccessTerminalPacket::decode);

    private static final StreamCodec<RegistryFriendlyByteBuf, Int2ObjectMap<ItemStack>> SLOTS_STREAM_CODEC = ByteBufCodecs
            .map(
                    Int2ObjectOpenHashMap::new, ByteBufCodecs.SHORT.map(Short::intValue, Integer::shortValue),
                    ItemStack.OPTIONAL_STREAM_CODEC, 128);

    public static final Type<AdvancedPatternAccessTerminalPacket> TYPE = CustomAppEngPayload
            .createType("advanced_pattern_access_terminal");

    @Override
    public Type<AdvancedPatternAccessTerminalPacket> type() {
        return TYPE;
    }

    public static AdvancedPatternAccessTerminalPacket decode(RegistryFriendlyByteBuf stream) {
        var inventoryId = stream.readVarLong();
        var fullUpdate = stream.readBoolean();
        int inventorySize = 0;
        long sortBy = 0;
        PatternContainerGroup group = null;
        if (fullUpdate) {
            inventorySize = stream.readVarInt();
            sortBy = stream.readVarLong();
            group = PatternContainerGroup.readFromPacket(stream);
        }

        var slots = SLOTS_STREAM_CODEC.decode(stream);
        return new AdvancedPatternAccessTerminalPacket(fullUpdate, inventoryId, inventorySize, sortBy, group, slots);
    }

    public void write(RegistryFriendlyByteBuf data) {
        data.writeVarLong(inventoryId);
        data.writeBoolean(fullUpdate);
        if (fullUpdate) {
            data.writeVarInt(inventorySize);
            data.writeVarLong(sortBy);
            group.writeToPacket(data);
        }
        SLOTS_STREAM_CODEC.encode(data, slots);
    }

    public static AdvancedPatternAccessTerminalPacket fullUpdate(long inventoryId,
                                                                 int inventorySize,
                                                                 long sortBy,
                                                                 PatternContainerGroup group,
                                                                 Int2ObjectMap<ItemStack> slots) {
        return new AdvancedPatternAccessTerminalPacket(
                true,
                inventoryId,
                inventorySize,
                sortBy,
                group,
                slots);
    }

    public static AdvancedPatternAccessTerminalPacket incrementalUpdate(long inventoryId,
                                                                        Int2ObjectMap<ItemStack> slots) {
        return new AdvancedPatternAccessTerminalPacket(
                false,
                inventoryId,
                0,
                0,
                null,
                slots);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleOnClient(Player player) {
        if (Minecraft.getInstance().screen instanceof AdvancedPatternAccessTerminalScreen patternAccessTerminal) {
            LoggerFactory.getLogger(this.getClass()).debug("Handling advanced pattern access terminal packet for player: {}", player.getName().getString());
            if (fullUpdate) {
                patternAccessTerminal.postFullUpdate(this.inventoryId, sortBy, group, inventorySize, slots);
            } else {
                patternAccessTerminal.postIncrementalUpdate(this.inventoryId, slots);
            }
        }
    }
}
