package org.ae2LabeledPatterns.items;

import appeng.api.config.Setting;
import appeng.api.config.YesNo;
import appeng.api.implementations.menuobjects.IMenuItem;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.storage.ISubMenuHost;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.menu.locator.MenuLocators;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import org.ae2LabeledPatterns.MSettings;
import org.ae2LabeledPatterns.blocks.attachments.AttachmentRegisters;
import org.ae2LabeledPatterns.items.components.ComponentRegisters;
import org.ae2LabeledPatterns.items.components.PatternProviderLabel;
import org.ae2LabeledPatterns.menus.LabelerMenu;
import org.ae2LabeledPatterns.network.SaveLabelAttachmentPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LabelerItem extends Item implements IMenuItem, IConfigurableObject {
    private final IConfigManager configManager;

    public LabelerItem(Properties properties) {
        super(properties);
        this.configManager = IConfigManager.builder(this::onConfigChanged)
                .registerSetting(MSettings.LABELER_INPUT_LOCKED, YesNo.NO)
                .build();
    }

    private void onConfigChanged(IConfigManager manager, Setting<?> setting) {

    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        var selfData = stack.getComponents().getOrDefault(ComponentRegisters.PATTERN_PROVIDER_LABEL.get(), new PatternProviderLabel("sample"));
        tooltipComponents.add(Component.literal(String.format("name: %s color: %s", selfData.name(), selfData.color())).withColor(TextColor.parseColor(selfData.color().toString().substring(0, 7)).getOrThrow().getValue()));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
//        player.sendSystemMessage(Component.literal("use item"));
        if (player.isCrouching()) return InteractionResultHolder.pass(player.getItemInHand(usedHand));
        if (!level.isClientSide()) {
            MenuOpener.open(LabelerMenu.TYPE, player, MenuLocators.forHand(player, usedHand));
        }
        player.swing(usedHand);
        return new InteractionResultHolder<>(InteractionResult.sidedSuccess(level.isClientSide()),
                player.getItemInHand(usedHand));
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        BlockEntity blockEntity = level.getBlockEntity(context.getClickedPos());
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;
        if (blockEntity instanceof PatternProviderLogicHost){
            if (blockEntity.hasData(AttachmentRegisters.PATTERN_PROVIDER_LABEL)){
                player.sendSystemMessage(Component.literal("-----------------"));
                player.sendSystemMessage(Component.literal("Client: " + level.isClientSide));
                player.sendSystemMessage(Component.literal("Content: "+ blockEntity.getData(AttachmentRegisters.PATTERN_PROVIDER_LABEL).name()));
                player.sendSystemMessage(Component.literal("Color: "+ blockEntity.getData(AttachmentRegisters.PATTERN_PROVIDER_LABEL).color().toString()));
                player.sendSystemMessage(Component.literal("-----------------"));
            }
            if (level.isClientSide){
                return InteractionResult.sidedSuccess(true);
            }
            ServerPlayer serverPlayer = (ServerPlayer) player;
            if (player.isCrouching()){
                ItemStack itemStack = context.getItemInHand();
                if (itemStack.getItem() instanceof LabelerItem){
                    var hasLabel = itemStack.has(ComponentRegisters.PATTERN_PROVIDER_LABEL.get());
                    if (hasLabel){
                        var label = itemStack.getComponents().get(ComponentRegisters.PATTERN_PROVIDER_LABEL.get());
                        var data = blockEntity.getData(AttachmentRegisters.PATTERN_PROVIDER_LABEL);
                        if (label != null && !label.equals(data)) {
                            blockEntity.setData(AttachmentRegisters.PATTERN_PROVIDER_LABEL, label);
                            serverPlayer.connection.send(
                                    new SaveLabelAttachmentPacket(label, blockEntity.getBlockPos()));
                        }
                    }
                    return InteractionResult.sidedSuccess(false);
                }
            }
        }
        return super.useOn(context);
    }

    @Override
    public @Nullable ItemMenuHost<?> getMenuHost(Player player, ItemMenuHostLocator locator, @Nullable BlockHitResult hitResult) {
        return new TaggerItemMenuHost(this, player, locator);
    }

    @Override
    public IConfigManager getConfigManager() {
        return configManager;
    }

    public class TaggerItemMenuHost extends ItemMenuHost<LabelerItem> implements ISubMenuHost{

        public TaggerItemMenuHost(LabelerItem item, Player player, ItemMenuHostLocator locator) {
            super(item, player, locator);
        }

        @Override
        public void returnToMainMenu(Player player, ISubMenu subMenu) {
            MenuOpener.open(LabelerMenu.TYPE, player, subMenu.getLocator(), true);
        }

        @Override
        public ItemStack getMainMenuIcon() {
            return LabelerItem.this.getDefaultInstance();
        }
    }
}
