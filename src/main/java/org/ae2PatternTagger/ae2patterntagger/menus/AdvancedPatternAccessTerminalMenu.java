package org.ae2PatternTagger.ae2patterntagger.menus;

import appeng.api.config.Settings;
import appeng.api.config.ShowPatternProviders;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.storage.ILinkStatus;
import appeng.core.AELog;
import appeng.core.network.clientbound.SetLinkStatusPacket;
import appeng.helpers.InventoryAction;
import appeng.helpers.patternprovider.PatternContainer;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.guisync.GuiSync;
import appeng.menu.guisync.LinkStatusAwareMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.FilteredInternalInventory;
import appeng.util.inv.filter.IAEItemFilter;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.ae2PatternTagger.ae2patterntagger.Ae2patterntagger;
import org.ae2PatternTagger.ae2patterntagger.blocks.attachments.AttachmentRegisters;
import org.ae2PatternTagger.ae2patterntagger.items.components.PatternProviderTag;
import org.ae2PatternTagger.ae2patterntagger.network.AdvancedPatternAccessTerminalPacket;
import org.ae2PatternTagger.ae2patterntagger.network.ClearAdvancedPatternAccessTerminalPacket;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AdvancedPatternAccessTerminalMenu extends AEBaseMenu implements LinkStatusAwareMenu, ITagsProvider {
    private final IAdvancedPatternAccessTermMenuHost host;
    @GuiSync(1)
    public ShowPatternProviders showPatternProviders;
    private ILinkStatus linkStatus;
    public static final MenuType<AdvancedPatternAccessTerminalMenu> TYPE = MenuTypeBuilder
            .create(AdvancedPatternAccessTerminalMenu::new, IAdvancedPatternAccessTermMenuHost.class)
            .buildUnregistered(Ae2patterntagger.makeId("advancedpatternaccessterminal"));
    private static long inventorySerial = Long.MIN_VALUE;
    private final Map<PatternContainer, ContainerTracker> diList;
    private final Long2ObjectOpenHashMap<ContainerTracker> byId;
    private final Set<PatternContainer> pinnedHosts;

    private static final String ACTION_CYCLE_TAG = "cycleTag";
    @GuiSync(2)
    public PatternProviderTag currentTag = PatternProviderTag.Empty;

    public HashSet<PatternProviderTag> allTagTypes = new HashSet<>();

    public ShowPatternProviders getShownProviders() {
        return this.showPatternProviders;
    }

    public AdvancedPatternAccessTerminalMenu(int id, Inventory ip, IAdvancedPatternAccessTermMenuHost anchor) {
        this(TYPE, id, ip, anchor, true);
    }

    public AdvancedPatternAccessTerminalMenu(MenuType<?> menuType, int id, Inventory ip, IAdvancedPatternAccessTermMenuHost host, boolean bindInventory) {
        super(menuType, id, ip, host);
        this.showPatternProviders = ShowPatternProviders.VISIBLE;
        this.linkStatus = ILinkStatus.ofDisconnected();
        this.diList = new IdentityHashMap();
        this.byId = new Long2ObjectOpenHashMap();
        this.pinnedHosts = Collections.newSetFromMap(new IdentityHashMap());
        this.host = host;
        if (bindInventory) {
            this.createPlayerInventorySlots(ip);
        }

        registerClientAction(ACTION_CYCLE_TAG, Boolean.class, this::cycleTag);

    }

    public void broadcastChanges() {
        if (!this.isClientSide()) {
            this.showPatternProviders = (ShowPatternProviders)this.host.getConfigManager().getSetting(Settings.TERMINAL_SHOW_PATTERN_PROVIDERS);
            this.currentTag = this.host.getCurrentTag();
            super.broadcastChanges();
            this.updateLinkStatus();
            if (this.showPatternProviders != ShowPatternProviders.NOT_FULL) {
                this.pinnedHosts.clear();
            }

            IGrid grid = this.getGrid();
            VisitorState state = new VisitorState();
            if (grid != null) {
                for(Class<?> machineClass : grid.getMachineClasses()) {
                    if (PatternContainer.class.isAssignableFrom(machineClass)) {
                        this.visitPatternProviderHosts(grid, (Class<PatternContainer>)machineClass, state);
                    }
                }

                this.pinnedHosts.removeIf((host) -> host.getGrid() != grid);
            } else {
                this.pinnedHosts.clear();
            }

            if (state.total == this.diList.size() && !state.forceFullUpdate) {
                this.sendIncrementalUpdate();
            } else {
                this.sendFullUpdate(grid);
            }

        }
    }

    private @Nullable IGrid getGrid() {
        IGridNode agn = this.host.getGridNode();
        return agn != null && agn.isActive() ? agn.getGrid() : null;
    }

    public ILinkStatus getLinkStatus() {
        return this.linkStatus;
    }

    private boolean isFull(PatternContainer logic) {
        for(int i = 0; i < logic.getTerminalPatternInventory().size(); ++i) {
            if (logic.getTerminalPatternInventory().getStackInSlot(i).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    private boolean isVisible(PatternContainer container) {
        boolean isVisible = container.isVisibleInTerminal();
        boolean var10000;
        switch (this.getShownProviders()) {
            case VISIBLE -> var10000 = isVisible;
            case NOT_FULL -> var10000 = isVisible && (this.pinnedHosts.contains(container) || !this.isFull(container));
            case ALL -> var10000 = true;
            default -> throw new MatchException((String)null, (Throwable)null);
        }

        return var10000;
    }

    private <T extends PatternContainer> void visitPatternProviderHosts(IGrid grid, Class<T> machineClass, VisitorState state) {
        for(T container : grid.getActiveMachines(machineClass)) {
            if (this.isVisible(container)) {
                if (this.getShownProviders() == ShowPatternProviders.NOT_FULL) {
                    this.pinnedHosts.add(container);
                }
                if (container instanceof PatternProviderLogicHost) {
                    var entity = ((PatternProviderLogicHost) container).getBlockEntity();
                    var tagData = entity != null ? entity.getData(AttachmentRegisters.PATTERN_PROVIDER_TAG) : null;
                    if (tagData != null && !tagData.isEmpty()) {
                        if (currentTag != null && !currentTag.isEmpty()){
                            if (tagData.equals(currentTag)) {
                                ContainerTracker t = (ContainerTracker) this.diList.get(container);
                                if (t == null || !t.group.equals(container.getTerminalGroup())) {
                                    state.forceFullUpdate = true;
                                }
                                ++state.total;
                            }
                        }else{
                            ContainerTracker t = (ContainerTracker) this.diList.get(container);
                            if (t == null || !t.group.equals(container.getTerminalGroup())) {
                                state.forceFullUpdate = true;
                            }
                            ++state.total;
                        }
                    } else{
                        if (currentTag == null || currentTag.isEmpty()){
                            ContainerTracker t = (ContainerTracker) this.diList.get(container);
                            if (t == null || !t.group.equals(container.getTerminalGroup())) {
                                state.forceFullUpdate = true;
                            }
                            ++state.total;
                        }
                    }
                }
            }
        }

    }

    public void doAction(ServerPlayer player, InventoryAction action, int slot, long id) {
        ContainerTracker inv = (ContainerTracker)this.byId.get(id);
        if (inv != null) {
            if (slot >= 0 && slot < inv.server.size()) {
                ItemStack is = inv.server.getStackInSlot(slot);
                FilteredInternalInventory patternSlot = new FilteredInternalInventory(inv.server.getSlotInv(slot), new PatternSlotFilter());
                ItemStack carried = this.getCarried();
                switch (action) {
                    case PICKUP_OR_SET_DOWN:
                        if (!carried.isEmpty()) {
                            ItemStack inSlot = patternSlot.getStackInSlot(0);
                            if (inSlot.isEmpty()) {
                                this.setCarried(patternSlot.addItems(carried));
                            } else {
                                inSlot = inSlot.copy();
                                ItemStack inHand = carried.copy();
                                patternSlot.setItemDirect(0, ItemStack.EMPTY);
                                this.setCarried(ItemStack.EMPTY);
                                this.setCarried(patternSlot.addItems(inHand.copy()));
                                if (this.getCarried().isEmpty()) {
                                    this.setCarried(inSlot);
                                } else {
                                    this.setCarried(inHand);
                                    patternSlot.setItemDirect(0, inSlot);
                                }
                            }
                        } else {
                            this.setCarried(patternSlot.getStackInSlot(0));
                            patternSlot.setItemDirect(0, ItemStack.EMPTY);
                        }
                        break;
                    case SPLIT_OR_PLACE_SINGLE:
                        if (!carried.isEmpty()) {
                            ItemStack extra = carried.split(1);
                            if (!extra.isEmpty()) {
                                extra = patternSlot.addItems(extra);
                            }

                            if (!extra.isEmpty()) {
                                carried.grow(extra.getCount());
                            }
                        } else if (!is.isEmpty()) {
                            this.setCarried(patternSlot.extractItem(0, (is.getCount() + 1) / 2, false));
                        }
                        break;
                    case SHIFT_CLICK:
                        ItemStack stack = patternSlot.getStackInSlot(0).copy();
                        if (!player.getInventory().add(stack)) {
                            patternSlot.setItemDirect(0, stack);
                        } else {
                            patternSlot.setItemDirect(0, ItemStack.EMPTY);
                        }
                        break;
                    case MOVE_REGION:
                        for(int x = 0; x < inv.server.size(); ++x) {
                            ItemStack stack2 = inv.server.getStackInSlot(x);
                            if (!player.getInventory().add(stack2)) {
                                patternSlot.setItemDirect(0, stack2);
                            } else {
                                patternSlot.setItemDirect(0, ItemStack.EMPTY);
                            }
                        }
                        break;
                    case CREATIVE_DUPLICATE:
                        if (player.getAbilities().instabuild && carried.isEmpty()) {
                            this.setCarried(is.isEmpty() ? ItemStack.EMPTY : is.copy());
                        }
                }

            } else {
                AELog.warn("Client refers to invalid slot %d of inventory %s", new Object[]{slot, inv.container});
            }
        }
    }

    private void sendFullUpdate(@Nullable IGrid grid) {
        this.byId.clear();
        this.diList.clear();
        this.allTagTypes.clear();
        this.sendPacketToClient(new ClearAdvancedPatternAccessTerminalPacket());
        if (grid != null) {
            for(Class<?> machineClass : grid.getMachineClasses()) {
                Class<? extends PatternContainer> containerClass = tryCastMachineToContainer(machineClass);
                if (containerClass != null) {
                    for(PatternContainer container : grid.getActiveMachines(containerClass)) {
                        if (this.isVisible(container)) {
//                            this.diList.put(container, new ContainerTracker(container, container.getTerminalPatternInventory(), container.getTerminalGroup()));
                            if (container instanceof PatternProviderLogicHost) {
                                var entity = ((PatternProviderLogicHost) container).getBlockEntity();
                                var tagData = entity != null ? entity.getData(AttachmentRegisters.PATTERN_PROVIDER_TAG) : null;
                                if (tagData != null && !tagData.isEmpty()) {
                                    allTagTypes.add(tagData);
                                    if (currentTag != null && !currentTag.isEmpty()){
                                        if (tagData.equals(currentTag)) {
                                            this.diList.put(container, new ContainerTracker(container, container.getTerminalPatternInventory(), container.getTerminalGroup()));
                                        }
                                    }else{
                                        this.diList.put(container, new ContainerTracker(container, container.getTerminalPatternInventory(), container.getTerminalGroup()));
                                    }
                                }else{
                                    if (currentTag == null || currentTag.isEmpty()){
                                        this.diList.put(container, new ContainerTracker(container, container.getTerminalPatternInventory(), container.getTerminalGroup()));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            for(ContainerTracker inv : this.diList.values()) {
                this.byId.put(inv.serverId, inv);
                this.sendPacketToClient(inv.createFullPacket());
            }
        }
    }

    private void sendIncrementalUpdate() {
        for(ContainerTracker inv : this.diList.values()) {
            AdvancedPatternAccessTerminalPacket packet = inv.createUpdatePacket();
            if (packet != null) {
                this.sendPacketToClient(packet);
            }
        }

    }

    private static Class<? extends PatternContainer> tryCastMachineToContainer(Class<?> machineClass) {
        return PatternContainer.class.isAssignableFrom(machineClass) ? machineClass.asSubclass(PatternContainer.class) : null;
    }

    protected void updateLinkStatus() {
        ILinkStatus linkStatus = this.host.getLinkStatus();
        if (!Objects.equals(this.linkStatus, linkStatus)) {
            this.linkStatus = linkStatus;
            this.sendPacketToClient(new SetLinkStatusPacket(linkStatus));
        }

    }

    public void setLinkStatus(ILinkStatus linkStatus) {
        this.linkStatus = linkStatus;
    }

    @Override
    public List<PatternProviderTag> getTags() {
        return new ArrayList<>(allTagTypes);
    }

    @Override
    public PatternProviderTag currentTag() {
        return currentTag;
    }

    @Override
    public boolean hasTag(PatternProviderTag tag) {
        return allTagTypes.contains(tag);
    }

    @Override
    public void setCurrentTag(PatternProviderTag tag) {
        host.setCurrentTag(tag);
    }

    public void cycleTag(boolean isRightClick){
        LogUtils.getLogger().debug("cycleTag:\ncurrent: {}\nallTagTypes: {}\nisRightClick: {}", currentTag, allTagTypes, isRightClick);
        LogUtils.getLogger().debug("idList: {}", diList.keySet());
        if (isClientSide()){
            sendClientAction(ACTION_CYCLE_TAG, isRightClick);
            return;
        }
        if (currentTag != null && !currentTag.isEmpty()){
            List<PatternProviderTag> tags = new ArrayList<>(allTagTypes);
            if (tags.isEmpty()) {
                return;
            }
            int currentIndex = tags.indexOf(currentTag);
            if (currentIndex == -1 && isRightClick) {
                currentIndex = tags.size(); // if current tag is not found, we set it to the last or first tag based on right-click
            }
            // when right-clicking, we want to go to the previous tag, otherwise we go to the next tag, but when index is out of bounds, I want to set it to empty
//            int nextIndex = !isRightClick ? (currentIndex + 1) % tags.size() : (currentIndex - 1 + tags.size()) % tags.size();
            int nextIndex = isRightClick ? currentIndex - 1 : currentIndex + 1 ;
            if (nextIndex < 0 || nextIndex >= tags.size()) {
                setCurrentTag(PatternProviderTag.Empty);
                return;
            }

            PatternProviderTag nextTag = tags.get(nextIndex);
            setCurrentTag(nextTag);
        } else {
            if (!allTagTypes.isEmpty()) {
                setCurrentTag(allTagTypes.iterator().next());
            }
        }
    }

    private static class VisitorState {
        int total;
        boolean forceFullUpdate;

        private VisitorState() {
        }
    }

    private static class ContainerTracker {
        private final PatternContainer container;
        private final long sortBy;
        private final long serverId;
        private final PatternContainerGroup group;
        private final InternalInventory client;
        private final InternalInventory server;

        public ContainerTracker(PatternContainer container, InternalInventory patterns, PatternContainerGroup group) {
            this.serverId = (long)(inventorySerial++);
            this.container = container;
            this.server = patterns;
            this.client = new AppEngInternalInventory(this.server.size());
            this.group = group;
            this.sortBy = container.getTerminalSortOrder();
        }

        public AdvancedPatternAccessTerminalPacket createFullPacket() {
            Int2ObjectArrayMap<ItemStack> slots = new Int2ObjectArrayMap(this.server.size());

            for(int i = 0; i < this.server.size(); ++i) {
                ItemStack stack = this.server.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    slots.put(i, stack);
                }
            }

            return AdvancedPatternAccessTerminalPacket.fullUpdate(this.serverId, this.server.size(), this.sortBy, this.group, slots);
        }

        public @Nullable AdvancedPatternAccessTerminalPacket createUpdatePacket() {
            IntList changedSlots = this.detectChangedSlots();
            if (changedSlots == null) {
                return null;
            } else {
                Int2ObjectArrayMap<ItemStack> slots = new Int2ObjectArrayMap(changedSlots.size());

                for(int i = 0; i < changedSlots.size(); ++i) {
                    int slot = changedSlots.getInt(i);
                    ItemStack stack = this.server.getStackInSlot(slot);
                    this.client.setItemDirect(slot, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
                    slots.put(slot, stack);
                }

                return AdvancedPatternAccessTerminalPacket.incrementalUpdate(this.serverId, slots);
            }
        }

        private @Nullable IntList detectChangedSlots() {
            IntList changedSlots = null;

            for(int x = 0; x < this.server.size(); ++x) {
                if (isDifferent(this.server.getStackInSlot(x), this.client.getStackInSlot(x))) {
                    if (changedSlots == null) {
                        changedSlots = new IntArrayList();
                    }

                    changedSlots.add(x);
                }
            }

            return changedSlots;
        }

        private static boolean isDifferent(ItemStack a, ItemStack b) {
            if (a.isEmpty() && b.isEmpty()) {
                return false;
            } else if (!a.isEmpty() && !b.isEmpty()) {
                return !ItemStack.matches(a, b);
            } else {
                return true;
            }
        }
    }

    private static class PatternSlotFilter implements IAEItemFilter {
        private PatternSlotFilter() {
        }

        public boolean allowExtract(InternalInventory inv, int slot, int amount) {
            return true;
        }

        public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
            return !stack.isEmpty() && PatternDetailsHelper.isEncodedPattern(stack);
        }
    }
}
