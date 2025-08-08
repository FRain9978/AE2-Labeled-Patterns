package org.ae2LabeledPatterns.items;

import appeng.api.config.Setting;
import appeng.api.config.YesNo;
import appeng.api.implementations.menuobjects.IMenuItem;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.storage.ISubMenuHost;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.menu.locator.MenuLocators;
import appeng.util.EnumCycler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
import org.ae2LabeledPatterns.AE2LabeledPatternsClient;
import org.ae2LabeledPatterns.config.Config;
import org.ae2LabeledPatterns.config.MSettings;
import org.ae2LabeledPatterns.attachments.AttachmentRegisters;
import org.ae2LabeledPatterns.integration.CheckProvider;
import org.ae2LabeledPatterns.menus.GUIText;
import org.ae2LabeledPatterns.menus.InGameTooltip;
import org.ae2LabeledPatterns.items.components.*;
import org.ae2LabeledPatterns.menus.LabelerMenu;
import org.ae2LabeledPatterns.network.SaveLabelAttachmentPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.ae2LabeledPatterns.items.components.ComponentRegisters.*;

public class LabelerItem extends Item implements IMenuItem, IConfigurableObject, IMMouseWheelItem {
//    private final int maxEntityCountInMultiSetMode = 64; // Maximum number of entities that can be set in multi-set mode

    private final IConfigManager configManager;

    public LabelerItem(Properties properties) {
        super(properties.stacksTo(1)
                .component(ComponentRegisters.LABELER_SETTING.value(), new LabelerSetting())
                .component(ComponentRegisters.MULTI_BLOCK_TARGET.value(), new MultiBlockTarget()
                )
        );
        this.configManager = IConfigManager.builder(this::onConfigChanged)
                .registerSetting(MSettings.LABELER_INPUT_LOCKED, YesNo.NO)
                .build();
    }

    @Override
    public boolean hasCraftingRemainingItem(@NotNull ItemStack itemStack) {
        return itemStack.is(ItemRegisters.LABELER);
    }

    @Override
    public @NotNull ItemStack getCraftingRemainingItem(@NotNull ItemStack itemStack) {
        return itemStack.is(ItemRegisters.LABELER) ? itemStack.copy() : ItemStack.EMPTY;
    }

    private void onConfigChanged(IConfigManager manager, Setting<?> setting) {

    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        var selfData = stack.getComponents().getOrDefault(ComponentRegisters.PATTERN_PROVIDER_LABEL.get(), new PatternProviderLabel("Sample"));
        var currentSetting = stack.get(LABELER_SETTING.get());
        tooltipComponents.add(GUIText.LabelerHoverTooltipLabel.text(selfData.name()).withColor(Color.GREEN.getRGB()));
        if (currentSetting != null) {
            tooltipComponents.add(GUIText.LabelerHoverTooltipMode.text(currentSetting.mode().text()).withColor(Color.CYAN.getRGB()));
            tooltipComponents.add(currentSetting.isRename() ?
                    GUIText.LabelerHoverTooltipRenameOn.text().withColor(Color.WHITE.getRGB()) :
                    GUIText.LabelerHoverTooltipRenameOff.text().withColor(Color.LIGHT_GRAY.getRGB()));
        }
        if (tooltipFlag.hasShiftDown()){
            tooltipComponents.add(GUIText.LabelerHoverTooltipChangeMode.text(AE2LabeledPatternsClient.MOUSE_WHEEL_ITEM_MODIFIER_1.getKey().getDisplayName()).withColor(Color.ORANGE.getRGB()));
            tooltipComponents.add(GUIText.LabelerHoverTooltipChangeLabel.text(AE2LabeledPatternsClient.MOUSE_WHEEL_ITEM_MODIFIER_2.getKey().getDisplayName()).withColor(Color.ORANGE.getRGB()));
        }else{
            tooltipComponents.add(GUIText.LabelerHoverTooltipShiftTip.text().withColor(Color.GRAY.getRGB()));
        }
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
        ItemStack itemStack = context.getItemInHand();
        if (itemStack.getItem() instanceof LabelerItem){
            var setting = itemStack.get(LABELER_SETTING.get());
            if (setting == null) return InteractionResult.PASS;
            switch (setting.mode()){
                case LabelerMode.SINGLE_SET:{
                    if (blockEntity == null) return InteractionResult.PASS;
                    if (CheckProvider.isEntityProvider(blockEntity)){
                        if (level.isClientSide){
                            return InteractionResult.sidedSuccess(true);
                        }
                        ServerPlayer serverPlayer = (ServerPlayer) player;
                        if (player.isCrouching()){
                            var hasLabel = itemStack.has(ComponentRegisters.PATTERN_PROVIDER_LABEL.get());
                            if (hasLabel){
                                var label = itemStack.getComponents().get(ComponentRegisters.PATTERN_PROVIDER_LABEL.get());
                                if (label != null){
                                    if (setting.isRename()){
                                        setProviderName(serverPlayer, blockEntity, label, true);
                                    }else{
                                        setProviderLabel(serverPlayer, blockEntity, label, true);
                                    }
                                }
                            }
                            return InteractionResult.sidedSuccess(false);
                        }
                    }
                }
                case LabelerMode.SINGLE_CLEAR:{
                    if (blockEntity == null) return InteractionResult.PASS;
                    if (CheckProvider.isEntityProvider(blockEntity)) {
                        if (level.isClientSide) {
                            return InteractionResult.sidedSuccess(true);
                        }
                        ServerPlayer serverPlayer = (ServerPlayer) player;
                        if (player.isCrouching()) {
                            if (setting.isRename()){
                                clearProviderName(serverPlayer, blockEntity, true);
                            }else {
                                clearProviderLabel(serverPlayer, blockEntity, true);
                            }
                            return InteractionResult.sidedSuccess(false);
                        }
                    }
                    break;
                }
                case LabelerMode.AREA_SET:{
                    if (level.isClientSide) {
                        return InteractionResult.sidedSuccess(true);
                    }
                    var currentPosTarget = itemStack.get(ComponentRegisters.MULTI_BLOCK_TARGET.get());
                    if (currentPosTarget == null) {return InteractionResult.sidedSuccess(false);}
                    currentPosTarget.addTarget(level, context.getClickedPos());
                    if (currentPosTarget.isFull()){
                        // check if entity size between points are valid
                        var t1 = currentPosTarget.t1();
                        var t2 = currentPosTarget.t2();
                        if (t1 == null || t2 == null) {return InteractionResult.sidedSuccess(false);}
                        if (!checkTargetDimValid(level, t1, t2)){
                            player.sendSystemMessage(InGameTooltip.LabelerSelectDiffDimension.text());
                            return InteractionResult.sidedSuccess(false);
                        }
                        var p1 = t1.pos();
                        var p2 = t2.pos();
                        if (getAreaEntityCount(p1, p2) > Config.maxAllowBlockSpace){
                            itemStack.set(ComponentRegisters.MULTI_BLOCK_TARGET.get(), currentPosTarget.clear());
                            player.sendSystemMessage(InGameTooltip.LabelerSelectedAreaTooBig.text(Config.maxAllowBlockSpace));
                            return InteractionResult.sidedSuccess(false);
                        }
                        ServerPlayer serverPlayer = (ServerPlayer) player;
                        var label = itemStack.get(ComponentRegisters.PATTERN_PROVIDER_LABEL.get());
                        // get all the blockEntity between the points
                        if (setting.isRename()){
                            areaSetProviderName(p1, p2, level, serverPlayer, label, false);
                        }else{
                            areaSetProviderLabel(p1, p2, level, serverPlayer, label, false);
                        }
                        // clear the currentPosTarget
                        itemStack.set(ComponentRegisters.MULTI_BLOCK_TARGET.get(), currentPosTarget.clear());
                        if (label != null) {
                            if (setting.isRename()){
                                player.sendSystemMessage(InGameTooltip.LabelerAreaSetProviderName.text(label.name()));
                            }else {
                                player.sendSystemMessage(InGameTooltip.LabelerAreaSetProviderLabel.text(label.name()));
                            }
                        }
                    }else{
                        itemStack.set(ComponentRegisters.MULTI_BLOCK_TARGET.get(), currentPosTarget);
                        player.sendSystemMessage(InGameTooltip.LabelerSelectFirstPoint.text());
                    }
                    return InteractionResult.sidedSuccess(false);
                }
                case LabelerMode.AREA_CLEAR:{
                    if (level.isClientSide) {
                        return InteractionResult.sidedSuccess(true);
                    }
                    var currentPosTarget = itemStack.get(ComponentRegisters.MULTI_BLOCK_TARGET.get());
                    if (currentPosTarget == null) {return InteractionResult.sidedSuccess(false);}
                    currentPosTarget.addTarget(level, context.getClickedPos());
                    if (currentPosTarget.isFull()) {
                        // clear all the blockEntity between the points
                        var t1 = currentPosTarget.t1();
                        var t2 = currentPosTarget.t2();
                        if (t1 == null || t2 == null) {return InteractionResult.sidedSuccess(false);}
                        if (!checkTargetDimValid(level, t1, t2)){
                            player.sendSystemMessage(InGameTooltip.LabelerSelectDiffDimension.text());
                            return InteractionResult.sidedSuccess(false);
                        }
                        var p1 = t1.pos();
                        var p2 = t2.pos();
                        if (getAreaEntityCount(p1, p2) > Config.maxAllowBlockSpace){
                            player.sendSystemMessage(InGameTooltip.LabelerSelectedAreaTooBig.text(Config.maxAllowBlockSpace));
                            return InteractionResult.sidedSuccess(false);
                        }
                        ServerPlayer serverPlayer = (ServerPlayer) player;
                        // get all the blockEntity between the points
                        if (setting.isRename()){
                            areaClearProviderName(p1, p2, level, serverPlayer, false);
                        }else{
                            areaClearProviderLabel(p1, p2, level, serverPlayer, false);
                        }
                        // clear the currentPosTarget
                        itemStack.set(ComponentRegisters.MULTI_BLOCK_TARGET.get(), currentPosTarget.clear());
                        if (setting.isRename()){
                            player.sendSystemMessage(InGameTooltip.LabelerAreaClearProviderName.text());
                        }else {
                            player.sendSystemMessage(InGameTooltip.LabelerAreaClearProviderLabel.text());
                        }
                    }else{
                        itemStack.set(ComponentRegisters.MULTI_BLOCK_TARGET.get(), currentPosTarget);
                        player.sendSystemMessage(InGameTooltip.LabelerSelectFirstPoint.text());
                    }
                    return InteractionResult.sidedSuccess(false);
                }
                case LabelerMode.COPY:{
                    if (level.isClientSide) {
                        return InteractionResult.sidedSuccess(true);
                    }
                    if (player.isCrouching()){
                        PatternProviderLabel data = PatternProviderLabel.Empty;
                        if (setting.isRename()){
                            if (blockEntity instanceof AEBaseBlockEntity aeBaseBlockEntity){
                                var customName = aeBaseBlockEntity.getCustomName();
                                if (customName != null && !customName.getString().isBlank()){
                                    data = new PatternProviderLabel(customName.getString());
                                }
                            }
                        }else{
                            data = CheckProvider.getEntityProviderLabel(blockEntity);
                        }
                        if (!data.isEmpty()) {
                            var savedLabels = itemStack.get(SAVED_LABELS.get());
                            if (savedLabels != null) {
                                if (!savedLabels.contains(data)) {
                                    var newSavedLabels = new ArrayList<>(savedLabels);
                                    newSavedLabels.add(data);
                                    itemStack.set(SAVED_LABELS.get(), newSavedLabels);
                                }
                            } else {
                                itemStack.set(SAVED_LABELS.get(), List.of(data));
                            }
                            itemStack.set(PATTERN_PROVIDER_LABEL.get(), data);
                            if (setting.isRename()){
                                player.sendSystemMessage(InGameTooltip.LabelerCopiedName.text(data.name()));
                            }else {
                                player.sendSystemMessage(InGameTooltip.LabelerCopiedLabel.text(data.name()));
                            }
                        }

                        return InteractionResult.sidedSuccess(false);
                    }
                }
            }

        }

        return super.useOn(context);
    }

    protected void setProviderLabel(ServerPlayer serverPlayer, BlockEntity blockEntity, PatternProviderLabel label, boolean showMessage) {
        var data = blockEntity.getData(AttachmentRegisters.PATTERN_PROVIDER_LABEL);
        if (label != null && !label.equals(data)) {
            blockEntity.setData(AttachmentRegisters.PATTERN_PROVIDER_LABEL, label);
            if (showMessage){
                serverPlayer.sendSystemMessage(InGameTooltip.LabelerSetProviderLabel.text(label.name()));
            }
            serverPlayer.connection.send(
                    new SaveLabelAttachmentPacket(label, blockEntity.getBlockPos()));
        }
    }

    protected void setProviderName(ServerPlayer serverPlayer, BlockEntity blockEntity, PatternProviderLabel label, boolean showMessage){
        if (blockEntity instanceof AEBaseBlockEntity aeBaseBlockEntity) {
            aeBaseBlockEntity.setName(label.name());
            aeBaseBlockEntity.saveChanges();
            if (showMessage){
                serverPlayer.sendSystemMessage(InGameTooltip.LabelerSetProviderName.text(label.name()));
            }
        }
    }

    private void areaSetProviderLabel(BlockPos p1, BlockPos p2, Level level, ServerPlayer serverPlayer, PatternProviderLabel label, boolean showMessage){
        for (int x = Math.min(p1.getX(), p2.getX()); x <= Math.max(p1.getX(), p2.getX()); x++) {
            for (int y = Math.min(p1.getY(), p2.getY()); y <= Math.max(p1.getY(), p2.getY()); y++) {
                for (int z = Math.min(p1.getZ(), p2.getZ()); z <= Math.max(p1.getZ(), p2.getZ()); z++) {
                    BlockEntity be = level.getBlockEntity(new BlockPos(x, y, z));
                    if (CheckProvider.isEntityProvider(be)) {
                        setProviderLabel(serverPlayer, be, label, showMessage);
                    }
                }
            }
        }
    }

    private void areaSetProviderName(BlockPos p1, BlockPos p2, Level level, ServerPlayer serverPlayer, PatternProviderLabel label, boolean showMessage){
        for (int x = Math.min(p1.getX(), p2.getX()); x <= Math.max(p1.getX(), p2.getX()); x++) {
            for (int y = Math.min(p1.getY(), p2.getY()); y <= Math.max(p1.getY(), p2.getY()); y++) {
                for (int z = Math.min(p1.getZ(), p2.getZ()); z <= Math.max(p1.getZ(), p2.getZ()); z++) {
                    BlockEntity be = level.getBlockEntity(new BlockPos(x, y, z));
                    if (CheckProvider.isEntityProvider(be)) {
                        setProviderName(serverPlayer, be, label, showMessage);
                    }
                }
            }
        }
    }

    protected void clearProviderLabel(ServerPlayer serverPlayer, BlockEntity blockEntity, boolean showMessage) {
        blockEntity.setData(AttachmentRegisters.PATTERN_PROVIDER_LABEL, PatternProviderLabel.Empty);
        if (showMessage){
            serverPlayer.sendSystemMessage(InGameTooltip.LabelerClearProviderLabel.text());
        }
        serverPlayer.connection.send(
                new SaveLabelAttachmentPacket(new PatternProviderLabel(), blockEntity.getBlockPos()));
    }

    private static final Field customName;

    static {
        try {
            customName = AEBaseBlockEntity.class.getDeclaredField("customName");
            customName.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    protected void clearProviderName(ServerPlayer serverPlayer, BlockEntity blockEntity, boolean showMessage) {
        try {
            if (blockEntity instanceof AEBaseBlockEntity aeBaseBlockEntity) {
                customName.set(aeBaseBlockEntity, null);
                aeBaseBlockEntity.saveChanges();
                if (showMessage){
                    serverPlayer.sendSystemMessage(InGameTooltip.LabelerClearProviderName.text());
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to clear provider name", e);
        }

    }

    private void areaClearProviderLabel(BlockPos p1, BlockPos p2, Level level, ServerPlayer serverPlayer, boolean showMessage) {
        for (int x = Math.min(p1.getX(), p2.getX()); x <= Math.max(p1.getX(), p2.getX()); x++) {
            for (int y = Math.min(p1.getY(), p2.getY()); y <= Math.max(p1.getY(), p2.getY()); y++) {
                for (int z = Math.min(p1.getZ(), p2.getZ()); z <= Math.max(p1.getZ(), p2.getZ()); z++) {
                    BlockEntity be = level.getBlockEntity(new BlockPos(x, y, z));
                    if (CheckProvider.isEntityProvider(be)) {
                        clearProviderLabel(serverPlayer, be, showMessage);
                    }
                }
            }
        }
    }

    private void areaClearProviderName(BlockPos p1, BlockPos p2, Level level, ServerPlayer serverPlayer, boolean showMessage) {
        for (int x = Math.min(p1.getX(), p2.getX()); x <= Math.max(p1.getX(), p2.getX()); x++) {
            for (int y = Math.min(p1.getY(), p2.getY()); y <= Math.max(p1.getY(), p2.getY()); y++) {
                for (int z = Math.min(p1.getZ(), p2.getZ()); z <= Math.max(p1.getZ(), p2.getZ()); z++) {
                    BlockEntity be = level.getBlockEntity(new BlockPos(x, y, z));
                    if (CheckProvider.isEntityProvider(be)) {
                        clearProviderName(serverPlayer, be, showMessage);
                    }
                }
            }
        }
    }

    private int getAreaEntityCount(BlockPos p1, BlockPos p2) {
        int distanceX = Math.abs(p1.getX() - p2.getX()) + 1;
        int distanceY = Math.abs(p1.getY() - p2.getY()) + 1;
        int distanceZ = Math.abs(p1.getZ() - p2.getZ()) + 1;
        return distanceX * distanceY * distanceZ;
    }

    private boolean checkTargetDimValid(Level level, BlockTarget t1, BlockTarget t2) {
        if (t1 == null || t2 == null) return false;
        return t1.dimension().equals(t2.dimension()) && level.dimension().location().equals(t1.dimension());
    }

    @Override
    public @Nullable ItemMenuHost<?> getMenuHost(Player player, ItemMenuHostLocator locator, @Nullable BlockHitResult hitResult) {
        return new LabelerItemMenuHost(this, player, locator);
    }

    @Override
    public IConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public void onWheel(ServerPlayer serverPlayer, ItemStack is, boolean up, int index) {
        if (index == 1 && is.has(LABELER_SETTING.get())){
            var setting = is.get(LABELER_SETTING.get());
            if (setting != null) {
                var currentValue = setting.mode();
                var newValue = EnumCycler.rotateEnum(currentValue, up, Set.of(LabelerMode.values()));
                is.set(LABELER_SETTING.get(), new LabelerSetting(setting.isLockEdit(), newValue, setting.isRename()));
                is.update(MULTI_BLOCK_TARGET.get(), new MultiBlockTarget(), MultiBlockTarget::clear);
                serverPlayer.displayClientMessage(InGameTooltip.CycleLabelerMode.text(newValue.text()).withColor(Color.CYAN.getRGB()), true);
            }
        } else if (index == 2 && is.has(SAVED_LABELS)) {
            var savedLabels = is.get(SAVED_LABELS.get());
            var currentLabel = is.get(PATTERN_PROVIDER_LABEL.get());
            if (savedLabels != null && !savedLabels.isEmpty() && currentLabel != null &&!currentLabel.isEmpty()) {
                var currentLabelIndex = savedLabels.indexOf(currentLabel);
                if (currentLabelIndex == -1) return;
                var nextIndex = up ? (currentLabelIndex + 1) % savedLabels.size() : (currentLabelIndex - 1 + savedLabels.size()) % savedLabels.size();
                var nextLabel = savedLabels.get(nextIndex);
                is.set(PATTERN_PROVIDER_LABEL.get(), nextLabel);
                serverPlayer.displayClientMessage(InGameTooltip.CycleLabelerLabel.text(nextLabel.name()).withColor(Color.GREEN.getRGB()), true);
            }
        }

    }

    public class LabelerItemMenuHost extends ItemMenuHost<LabelerItem> implements ISubMenuHost{

        public LabelerItemMenuHost(LabelerItem item, Player player, ItemMenuHostLocator locator) {
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
