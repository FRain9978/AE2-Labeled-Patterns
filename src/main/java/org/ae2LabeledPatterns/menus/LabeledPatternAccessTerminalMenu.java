package org.ae2LabeledPatterns.menus;

import appeng.api.config.Settings;
import appeng.api.config.ShowPatternProviders;
import appeng.api.config.YesNo;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.storage.ILinkStatus;
import appeng.core.AELog;
import appeng.core.definitions.AEItems;
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
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.ae2LabeledPatterns.Ae2LabeledPatterns;
import org.ae2LabeledPatterns.config.MSettings;
import org.ae2LabeledPatterns.attachments.AttachmentRegisters;
import org.ae2LabeledPatterns.items.components.PatternProviderLabel;
import org.ae2LabeledPatterns.network.LabeledPatternAccessTerminalPacket;
import org.ae2LabeledPatterns.network.ClearLabeledPatternAccessTerminalPacket;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LabeledPatternAccessTerminalMenu extends AEBaseMenu implements LinkStatusAwareMenu, ILabelsProvider {
    private final ILabeledPatternAccessTermMenuHost host;
    @GuiSync(1)
    public ShowPatternProviders showPatternProviders;
    @GuiSync(2)
    public MoveConvenience moveConvenience;
    @GuiSync(3)
    public YesNo showGroupSelectRatio;
    private ILinkStatus linkStatus;
    public static final MenuType<LabeledPatternAccessTerminalMenu> TYPE = MenuTypeBuilder
            .create(LabeledPatternAccessTerminalMenu::new, ILabeledPatternAccessTermMenuHost.class)
            .buildUnregistered(Ae2LabeledPatterns.makeId("labeledpatternaccessterminal"));
    private static long inventorySerial = Long.MIN_VALUE;
    private final Map<PatternContainer, ContainerTracker> diList;
    private final Long2ObjectOpenHashMap<ContainerTracker> byId;
    private final Set<PatternContainer> pinnedHosts;

    private static final String ACTION_CYCLE_TAG = "cycleTag";
    @GuiSync(4)
    public PatternProviderLabel currentTag = PatternProviderLabel.Empty;

    public HashSet<PatternProviderLabel> allTagTypes = new HashSet<>();

    public ShowPatternProviders getShownProviders() {
        return this.showPatternProviders;
    }
    public MoveConvenience getMoveConvenience() {
        return this.moveConvenience;
    }
    public YesNo getShowGroupSelectRatio() {
        return this.showGroupSelectRatio;
    }

    public LabeledPatternAccessTerminalMenu(int id, Inventory ip, ILabeledPatternAccessTermMenuHost anchor) {
        this(TYPE, id, ip, anchor, true);
    }

    public LabeledPatternAccessTerminalMenu(MenuType<?> menuType, int id, Inventory ip, ILabeledPatternAccessTermMenuHost host, boolean bindInventory) {
        super(menuType, id, ip, host);
        this.showPatternProviders = ShowPatternProviders.VISIBLE;
        this.moveConvenience = MoveConvenience.NONE;
        this.showGroupSelectRatio = YesNo.NO;
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
            this.moveConvenience = (MoveConvenience) this.host.getConfigManager().getSetting(MSettings.TERMINAL_MOVE_CONVENIENCE);
            this.showGroupSelectRatio = (YesNo) this.host.getConfigManager().getSetting(MSettings.TERMINAL_SHOW_GROUP_SELECT_RATIO);
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
                    var tagData = entity != null ? entity.getData(AttachmentRegisters.PATTERN_PROVIDER_LABEL) : null;
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
                AELog.warn("Client refers to invalid playerInventorySlot %d of inventory %s", new Object[]{slot, inv.container});
            }
        }
    }

    public void quickMoveToPatternContainer(ServerPlayer player, int playerInventorySlot, int mouseButton, List<Long> containerIds, PatternContainerGroup group) {
        if (playerInventorySlot >= 0 && playerInventorySlot <= player.getInventory().getContainerSize()){
            ItemStack carried = player.getInventory().getItem(playerInventorySlot);
            if (carried.isEmpty()) {
                return;
            }
            var nothingGroup = PatternContainerGroup.nothing();
            switch (moveConvenience){
                case NONE: {
                    Slot patternSlot = getSlot(playerInventorySlot);
                    var ids = new ArrayList<>(mouseButton == 1 ? containerIds.reversed(): containerIds);
                    for (Long id : ids) {
                        ContainerTracker inv = this.byId.get(id);
                        if (!group.equals(nothingGroup) && !inv.group.equals(group)) {
                            continue;
                        } else {
                            if (insertPatternToContainer(patternSlot.getItem().copy(), inv.server, false)) {
                                patternSlot.set(ItemStack.EMPTY);
                                return;
                            }
                        }
                    }

                    return;
                }
                case ONCE_FOR_ALL: {
                    if (group.equals(nothingGroup)) return;
                    // first check how many blank pattern in player's inventory
                    int blankPatternCount = 0;
                    Map<Integer, Integer> patternCount = new HashMap<>();
                    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                        ItemStack stack = player.getInventory().getItem(i);
                        if (stack.is(AEItems.BLANK_PATTERN.get())) {
                            blankPatternCount += stack.getCount();
                            patternCount.put(i, stack.getCount());
                        }
                    }
                    int insertedCount = -1;
                    Slot patternSlot = getSlot(playerInventorySlot);
                    ItemStack itemStack = patternSlot.getItem();
                    // if there are blank patterns, insert the carried item to all container in byId map as many as possible
                    var ids = new ArrayList<>(mouseButton == 1 ? containerIds.reversed(): containerIds);
                    Set<Long> visitedContainerIds = new HashSet<>();
                    for (Long id : ids){
                        ContainerTracker inv = this.byId.get(id);
                        visitedContainerIds.add(id);
                        if (inv.group.equals(group) && insertPatternToContainer(itemStack.copy(), inv.server, false)) {
                            insertedCount++;
                            if (insertedCount >= blankPatternCount) {
                                break; // stop if we have inserted as many as we can
                            }
                        }
                    }
                    if (insertedCount < blankPatternCount){
                        for (ContainerTracker container : this.byId.values()) {
                            if (visitedContainerIds.contains(container.serverId)) continue;
                            if (container.group.equals(group) && insertPatternToContainer(itemStack.copy(), container.server, false)) {
                                insertedCount++;
                                if (insertedCount >= blankPatternCount) {
                                    break; // stop if we have inserted as many as we can
                                }
                            }
                        }
                    }

                    if (insertedCount >= 0) {
                        // if inserted, remove the carried item from player's inventory
//                        player.getInventory().setItem(playerInventorySlot, ItemStack.EMPTY);
                        patternSlot.set(ItemStack.EMPTY);
                        // then update the blank pattern count in player's inventory
                        for (Map.Entry<Integer, Integer> entry : patternCount.entrySet()) {
                            int slot = entry.getKey();
                            int count = entry.getValue();
                            int newCount = count - insertedCount;
                            if (newCount <= 0) {
                                getSlot(slot).set(ItemStack.EMPTY);
                                insertedCount -= count;
                            } else {
                                getSlot(slot).getItem().setCount(newCount);
                                insertedCount = 0;
                            }
                            if (insertedCount <= 0) {
                                break;
                            }
                        }
                    }
                    return;
                }
                case ONCE_FOR_ALL_STRICT: {
                    if (group.equals(nothingGroup)) return;
//                    if (currentTag.isEmpty()) return;
                    // first check how many blank pattern in player's inventory
                    int blankPatternCount = 0;
                    Map<Integer, Integer> patternCount = new HashMap<>();
                    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                        ItemStack stack = player.getInventory().getItem(i);
                        if (stack.is(AEItems.BLANK_PATTERN.get())) {
                            blankPatternCount += stack.getCount();
                            patternCount.put(i, stack.getCount());
                        }
                    }
                    int insertedCount = -1;
                    Slot patternSlot = getSlot(playerInventorySlot);
                    ItemStack itemStack = patternSlot.getItem();
                    // if there are blank patterns, insert the carried item to all container in byId map as many as possible
                    Set<ContainerTracker> containerTrackers = new HashSet<>();
                    for (ContainerTracker container : this.byId.values()) {
                        if (container.group.equals(group) && insertPatternToContainer(itemStack.copy(), container.server, true)) {
                            containerTrackers.add(container);
                            insertedCount++;
                            if (insertedCount > blankPatternCount) {
                                // strict mode return directly
                                return;
                            }
                        }
                    }
                    for (ContainerTracker container : containerTrackers) {
                        insertPatternToContainer(itemStack.copy(), container.server, false);
                    }
                    if (insertedCount >= 0) {
                        // if inserted, remove the carried item from player's inventory
                        patternSlot.set(ItemStack.EMPTY);
//                        player.getInventory().setItem(playerInventorySlot, ItemStack.EMPTY);
                        // then update the blank pattern count in player's inventory
                        for (Map.Entry<Integer, Integer> entry : patternCount.entrySet()) {
                            int slot = entry.getKey();
                            int count = entry.getValue();
                            int newCount = count - insertedCount;
                            if (newCount <= 0) {
                                getSlot(slot).set(ItemStack.EMPTY);
                                insertedCount -= count;
                            } else {
                                getSlot(slot).getItem().setCount(newCount);
                                insertedCount = 0;
                            }
                            if (insertedCount <= 0) {
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            AELog.warn("Client refers to invalid playerInventorySlot %d of inventory %s", new Object[]{playerInventorySlot, player.getInventory()});
            return;
        }
    }

    private boolean insertPatternToContainer(ItemStack itemStack, InternalInventory inventory, boolean simulate) {
        FilteredInternalInventory patternSlot = new FilteredInternalInventory(inventory, new PatternSlotFilter());
        if (patternSlot.addItems(itemStack, simulate).isEmpty()){
//            if (!simulate) sourceSlot.set(ItemStack.EMPTY);
            return true;
        }else{
            return false;
        }
    }

    private void sendFullUpdate(@Nullable IGrid grid) {
        this.byId.clear();
        this.diList.clear();
        this.allTagTypes.clear();
        this.sendPacketToClient(new ClearLabeledPatternAccessTerminalPacket());
        if (grid != null) {
            for(Class<?> machineClass : grid.getMachineClasses()) {
                Class<? extends PatternContainer> containerClass = tryCastMachineToContainer(machineClass);
                if (containerClass != null) {
                    for(PatternContainer container : grid.getActiveMachines(containerClass)) {
                        if (this.isVisible(container)) {
//                            this.diList.put(container, new ContainerTracker(container, container.getTerminalPatternInventory(), container.getTerminalGroup()));
                            if (container instanceof PatternProviderLogicHost) {
                                var entity = ((PatternProviderLogicHost) container).getBlockEntity();
                                var tagData = entity != null ? entity.getData(AttachmentRegisters.PATTERN_PROVIDER_LABEL) : null;
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
            LabeledPatternAccessTerminalPacket packet = inv.createUpdatePacket();
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
    public List<PatternProviderLabel> getLabels() {
        return new ArrayList<>(allTagTypes);
    }

    @Override
    public PatternProviderLabel currentLabel() {
        return currentTag;
    }

    @Override
    public boolean hasLabel(PatternProviderLabel label) {
        return allTagTypes.contains(label);
    }

    @Override
    public void setCurrentLabel(PatternProviderLabel tag) {
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
            List<PatternProviderLabel> tags = new ArrayList<>(allTagTypes);
            if (tags.isEmpty()) {
                return;
            }
            int currentIndex = tags.indexOf(currentTag);
            if (currentIndex == -1 && isRightClick) {
                currentIndex = tags.size(); // if current label is not found, we set it to the last or first label based on right-click
            }
            // when right-clicking, we want to go to the previous label, otherwise we go to the next label, but when index is out of bounds, I want to set it to empty
//            int nextIndex = !isRightClick ? (currentIndex + 1) % tags.size() : (currentIndex - 1 + tags.size()) % tags.size();
            int nextIndex = isRightClick ? currentIndex - 1 : currentIndex + 1 ;
            if (nextIndex < 0 || nextIndex >= tags.size()) {
                setCurrentLabel(PatternProviderLabel.Empty);
                return;
            }

            PatternProviderLabel nextTag = tags.get(nextIndex);
            setCurrentLabel(nextTag);
        } else {
            if (!allTagTypes.isEmpty()) {
                setCurrentLabel(allTagTypes.iterator().next());
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

        public LabeledPatternAccessTerminalPacket createFullPacket() {
            Int2ObjectArrayMap<ItemStack> slots = new Int2ObjectArrayMap(this.server.size());

            for(int i = 0; i < this.server.size(); ++i) {
                ItemStack stack = this.server.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    slots.put(i, stack);
                }
            }

            return LabeledPatternAccessTerminalPacket.fullUpdate(this.serverId, this.server.size(), this.sortBy, this.group, slots);
        }

        public @Nullable LabeledPatternAccessTerminalPacket createUpdatePacket() {
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

                return LabeledPatternAccessTerminalPacket.incrementalUpdate(this.serverId, slots);
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
