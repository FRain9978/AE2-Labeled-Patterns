package org.ae2LabeledPatterns.integration.tooltips;

import appeng.api.integrations.igtooltip.TooltipBuilder;
import appeng.api.integrations.igtooltip.TooltipContext;
import appeng.api.integrations.igtooltip.providers.BodyProvider;
import appeng.api.integrations.igtooltip.providers.ServerDataProvider;
import appeng.blockentity.AEBaseBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.ae2LabeledPatterns.blocks.attachments.AttachmentRegisters;
import org.ae2LabeledPatterns.items.components.PatternProviderLabel;

@SuppressWarnings({"UnstableApiUsage", "NonExtendableApiUsage"})
public final class PatternProviderDataProvider
        implements BodyProvider<AEBaseBlockEntity>, ServerDataProvider<AEBaseBlockEntity> {

    private static final String NBT_HAS_PATTERN_PROVIDER_LABEL = "hasPatternProviderLabel";
    private static final String NBT_PATTERN_PROVIDER_LABEL = "patternProviderLabel";

    @Override
    public void buildTooltip(AEBaseBlockEntity host, TooltipContext context, TooltipBuilder tooltip) {
        var serverData = context.serverData();
        var hasLabel = serverData.getBoolean(NBT_HAS_PATTERN_PROVIDER_LABEL);
        if (hasLabel){
            var labelData = serverData.getCompound(NBT_PATTERN_PROVIDER_LABEL);
            if (!labelData.isEmpty()){
                var label = PatternProviderLabel.readFromNBT(labelData);
                if (!label.isEmpty()) {
                    tooltip.addLine(InGameTooltip.PatternProviderLabel.text(label.name(), label.color().toString()));
                }
            }
        }
    }

    @Override
    public void provideServerData(Player player, AEBaseBlockEntity host, CompoundTag serverData) {
        var entity = host.getBlockEntity();
        if (entity.hasData(AttachmentRegisters.PATTERN_PROVIDER_LABEL.get())){
            var label = entity.getData(AttachmentRegisters.PATTERN_PROVIDER_LABEL.get());
            if (!label.isEmpty()) {
                var labelData = new CompoundTag();
                label.writeToNBT(labelData);
                serverData.put(NBT_PATTERN_PROVIDER_LABEL, labelData);
            }
            serverData.putBoolean(NBT_HAS_PATTERN_PROVIDER_LABEL, true);
        }else{
            serverData.putBoolean(NBT_HAS_PATTERN_PROVIDER_LABEL, false);
        }
    }
}
