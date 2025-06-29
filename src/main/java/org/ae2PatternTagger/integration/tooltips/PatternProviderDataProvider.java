package org.ae2PatternTagger.integration.tooltips;

import appeng.api.integrations.igtooltip.TooltipBuilder;
import appeng.api.integrations.igtooltip.TooltipContext;
import appeng.api.integrations.igtooltip.providers.BodyProvider;
import appeng.api.integrations.igtooltip.providers.ServerDataProvider;
import appeng.blockentity.AEBaseBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.ae2PatternTagger.blocks.attachments.AttachmentRegisters;
import org.ae2PatternTagger.items.components.PatternProviderTag;

@SuppressWarnings({"UnstableApiUsage", "NonExtendableApiUsage"})
public final class PatternProviderDataProvider
        implements BodyProvider<AEBaseBlockEntity>, ServerDataProvider<AEBaseBlockEntity> {

    private static final String NBT_HAS_PATTERN_PROVIDER_TAG = "hasPatternProviderTag";
    private static final String NBT_PATTERN_PROVIDER_TAG = "patternProviderTag";

    @Override
    public void buildTooltip(AEBaseBlockEntity host, TooltipContext context, TooltipBuilder tooltip) {
        var serverData = context.serverData();
        var hasTag = serverData.getBoolean(NBT_HAS_PATTERN_PROVIDER_TAG);
        if (hasTag){
            var tagData = serverData.getCompound(NBT_PATTERN_PROVIDER_TAG);
            if (!tagData.isEmpty()){
                var tag = PatternProviderTag.readFromNBT(tagData);
                if (!tag.isEmpty()) {
                    tooltip.addLine(InGameTooltip.PatternProviderTag.text(tag.name(), tag.color().toString()));
                }
            }
        }
    }

    @Override
    public void provideServerData(Player player, AEBaseBlockEntity host, CompoundTag serverData) {
        var entity = host.getBlockEntity();
        if (entity.hasData(AttachmentRegisters.PATTERN_PROVIDER_TAG.get())){
            var tag = entity.getData(AttachmentRegisters.PATTERN_PROVIDER_TAG.get());
            if (!tag.isEmpty()) {
                var tagData = new CompoundTag();
                tag.writeToNBT(tagData);
                serverData.put(NBT_PATTERN_PROVIDER_TAG, tagData);
            }
            serverData.putBoolean(NBT_HAS_PATTERN_PROVIDER_TAG, true);
        }else{
            serverData.putBoolean(NBT_HAS_PATTERN_PROVIDER_TAG, false);
        }
    }
}
