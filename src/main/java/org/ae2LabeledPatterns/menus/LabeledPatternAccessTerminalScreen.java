package org.ae2LabeledPatterns.menus;

import appeng.api.config.Settings;
import appeng.api.config.ShowPatternProviders;
import appeng.api.config.TerminalStyle;
import appeng.api.config.YesNo;
import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.ILinkStatus;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.me.patternaccess.PatternContainerRecord;
import appeng.client.gui.me.patternaccess.PatternSlot;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.*;
import appeng.core.AEConfig;
import appeng.core.AppEng;
import appeng.core.localization.GuiText;
import appeng.core.network.serverbound.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import appeng.util.inv.AppEngInternalInventory;
import com.google.common.collect.HashMultimap;
import guideme.color.ConstantColor;
import guideme.document.LytRect;
import guideme.render.SimpleRenderContext;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.ae2LabeledPatterns.config.MSettings;
import org.ae2LabeledPatterns.menus.widgets.MActionButton;
import org.ae2LabeledPatterns.menus.widgets.MServerSettingToggleButton;
import org.ae2LabeledPatterns.network.InventoryQuickMovePacket;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class LabeledPatternAccessTerminalScreen<W extends LabeledPatternAccessTerminalMenu> extends AEBaseScreen<W> {
    private static final Logger LOG = LoggerFactory.getLogger(LabeledPatternAccessTerminalScreen.class);
    private static final int GUI_WIDTH = 195;
    private static final int GUI_TOP_AND_BOTTOM_PADDING = 54;
    private static final int GUI_PADDING_X = 8;
    private static final int GUI_PADDING_Y = 6;
    private static final int GUI_HEADER_HEIGHT = 17;
    private static final int GUI_FOOTER_HEIGHT = 99;
    private static final int COLUMNS = 9;
    private static final int PATTERN_PROVIDER_NAME_MARGIN_X = 2;
    private static final int TEXT_MAX_WIDTH = 155;
    private static final int ROW_HEIGHT = 18;
    private static final int SLOT_SIZE = ROW_HEIGHT;
    private static final Rect2i HEADER_BBOX = new Rect2i(0, 0, GUI_WIDTH, GUI_HEADER_HEIGHT);
    private static final Rect2i ROW_TEXT_TOP_BBOX = new Rect2i(0, 17, GUI_WIDTH, ROW_HEIGHT);
    private static final Rect2i ROW_TEXT_MIDDLE_BBOX = new Rect2i(0, 53, GUI_WIDTH, ROW_HEIGHT);
    private static final Rect2i ROW_TEXT_BOTTOM_BBOX = new Rect2i(0, 89, GUI_WIDTH, ROW_HEIGHT);
    private static final Rect2i ROW_INVENTORY_TOP_BBOX = new Rect2i(0, 35, GUI_WIDTH, ROW_HEIGHT);
    private static final Rect2i ROW_INVENTORY_MIDDLE_BBOX = new Rect2i(0, 71, GUI_WIDTH, ROW_HEIGHT);
    private static final Rect2i ROW_INVENTORY_BOTTOM_BBOX = new Rect2i(0, 107, GUI_WIDTH, ROW_HEIGHT);
    private static final Rect2i FOOTER_BBOX = new Rect2i(0, 125, GUI_WIDTH, GUI_FOOTER_HEIGHT);
    private static final Comparator<PatternContainerGroup> GROUP_COMPARATOR = Comparator.comparing((group) -> group.name().getString().toLowerCase(Locale.ROOT));
    private final HashMap<Long, PatternContainerRecord> byId = new HashMap();
    private final HashMultimap<PatternContainerGroup, PatternContainerRecord> byGroup = HashMultimap.create();
    private final ArrayList<PatternContainerGroup> groups = new ArrayList();
    private final ArrayList<Row> rows = new ArrayList();
    private final Map<String, Set<Object>> cachedSearches = new WeakHashMap();
    private final Scrollbar scrollbar;
    private final AETextField searchField;
    private final Map<ItemStack, String> patternSearchText = new WeakHashMap();
    private int visibleRows = 0;
    private final ServerSettingToggleButton<ShowPatternProviders> showPatternProviders;
    private final MActionButton cycleTagButton;
    private final ServerSettingToggleButton<MoveConvenience> moveConvenienceButton;
    private final ServerSettingToggleButton<YesNo> showGroupSelectRatioButton;

    private final HashMap<PatternContainerGroup, AECheckbox> checkboxMap = new HashMap<>();
    private PatternContainerGroup currentGroup = null;

    private boolean isShowGroupSelectRatio;

    public LabeledPatternAccessTerminalScreen(W menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.scrollbar = this.widgets.addScrollBar("scrollbar", Scrollbar.BIG);
        this.imageWidth = GUI_WIDTH;
        TerminalStyle terminalStyle = AEConfig.instance().getTerminalStyle();
        this.addToLeftToolbar(new SettingToggleButton<>(Settings.TERMINAL_STYLE, terminalStyle, this::toggleTerminalStyle));
        this.showPatternProviders = new ServerSettingToggleButton<>(Settings.TERMINAL_SHOW_PATTERN_PROVIDERS, ShowPatternProviders.VISIBLE);
        this.addToLeftToolbar(this.showPatternProviders);
        this.searchField = this.widgets.addTextField("search");
        this.searchField.setResponder((str) -> this.refreshList());
        this.searchField.setPlaceholder(GuiText.SearchPlaceholder.text());

        cycleTagButton = new MActionButton(Icon.SORT_BY_MOD,
                (button) -> {
                   this.menu.cycleTag(isHandlingRightClick());
                },
                GUIText.LabeledTerminalCycleTagButtonMessage.text());
        addToLeftToolbar(cycleTagButton);
        this.showGroupSelectRatioButton = new MServerSettingToggleButton<>(MSettings.TERMINAL_SHOW_GROUP_SELECT_RATIO, YesNo.NO);
        this.addToLeftToolbar(this.showGroupSelectRatioButton);
        this.moveConvenienceButton = new MServerSettingToggleButton<>(MSettings.TERMINAL_MOVE_CONVENIENCE, MoveConvenience.NONE);
        this.addToLeftToolbar(this.moveConvenienceButton);
//        this.widgets.add("cycleTagButton", cycleTagButton);
    }

    public void init() {
        this.visibleRows = Math.max(2, this.config.getTerminalStyle().getRows(
                (this.height - GUI_HEADER_HEIGHT - GUI_FOOTER_HEIGHT - GUI_TOP_AND_BOTTOM_PADDING) / ROW_HEIGHT));
        this.imageHeight = GUI_HEADER_HEIGHT + GUI_FOOTER_HEIGHT + this.visibleRows * ROW_HEIGHT;
        super.init();
        this.setInitialFocus(this.searchField);
        this.resetScrollbar();
        this.clearCheckboxes();
    }

    private void clearCheckboxes() {
        for (var checkBox : checkboxMap.values()) {
            removeWidget(checkBox);
        }
        checkboxMap.clear();
        currentGroup = null;
    }

    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        (this.menu).slots.removeIf((slot) -> slot instanceof PatternSlot);
        int textColor = this.style.getColor(PaletteColor.DEFAULT_TEXT_COLOR).toARGB();
        ClientLevel level = Minecraft.getInstance().level;
        int scrollLevel = this.scrollbar.getCurrentScroll();
        Set<PatternContainerGroup> visibleGroups = new HashSet<>();

        for(int i = 0; i < this.visibleRows; ++i) {
            if (scrollLevel + i < this.rows.size()) {
                Row row = (Row)this.rows.get(scrollLevel + i);
                if (row instanceof SlotsRow) {
                    SlotsRow slotsRow = (SlotsRow)row;
                    PatternContainerRecord container = slotsRow.container;

                    for(int col = 0; col < slotsRow.slots; ++col) {
                        var slot = new PatternSlot(
                                container,
                                slotsRow.offset + col,
                                col * SLOT_SIZE + GUI_PADDING_X,
                                (i + 1) * SLOT_SIZE);
                        (this.menu).slots.add(slot);
                        ItemStack pattern = container.getInventory().getStackInSlot(slotsRow.offset + col);
                        if (!pattern.isEmpty() && PatternDetailsHelper.decodePattern(pattern, level) == null) {
                            guiGraphics.fill(
                                    slot.x,
                                    slot.y,
                                    slot.x + 16,
                                    slot.y + 16,
                                    0x7fff0000);
                        }
                    }
                } else if (row instanceof GroupHeaderRow) {
                    GroupHeaderRow headerRow = (GroupHeaderRow)row;
                    PatternContainerGroup group = headerRow.group;
                    int offsetForCheckBox = isShowGroupSelectRatio? 14 : 0;
                    if (isShowGroupSelectRatio){
                        visibleGroups.add(group);
                        renderCheckBox(
                                group,
                                offsetX + GUI_PADDING_X + PATTERN_PROVIDER_NAME_MARGIN_X - 2,
                                offsetY + GUI_PADDING_Y + GUI_HEADER_HEIGHT + i * ROW_HEIGHT - 3);
                    }

                    if (group.icon() != null) {
                        SimpleRenderContext renderContext = new SimpleRenderContext(LytRect.empty(), guiGraphics);
                        renderContext.renderItem(
                                group.icon().getReadOnlyStack(),
                                GUI_PADDING_X + PATTERN_PROVIDER_NAME_MARGIN_X + offsetForCheckBox,
                                GUI_PADDING_Y + GUI_HEADER_HEIGHT + i * ROW_HEIGHT,
                                8.0F,
                                8.0F);
                    }

                    int rows = this.byGroup.get(group).size();
                    FormattedText displayName;
                    if (rows > 1) {
                        displayName = Component.empty().append(group.name()).append(Component.literal(" (" + rows + ")"));
                    } else {
                        displayName = group.name();
                    }

                    FormattedCharSequence text = Language.getInstance().getVisualOrder(this.font.substrByWidth(displayName, TEXT_MAX_WIDTH - 10));
                    guiGraphics.drawString(font, text, GUI_PADDING_X + PATTERN_PROVIDER_NAME_MARGIN_X + offsetForCheckBox + 10,
                            GUI_PADDING_Y + GUI_HEADER_HEIGHT + i * ROW_HEIGHT, textColor, false);
                }
            }
        }
        if (isShowGroupSelectRatio){
            // set check box visible depend on whether the group is visible
            checkboxMap.entrySet().stream().filter( entry -> !visibleGroups.contains(entry.getKey()))
                    .forEach(
                            entry -> {
                                var checkBox = entry.getValue();
                                checkBox.visible = false;
                            }
                    );
        }

        this.renderLinkStatus(guiGraphics, (this.getMenu()).getLinkStatus());
    }

    private void renderCheckBox(PatternContainerGroup group, int x, int y){
        if (checkboxMap.containsKey(group)){
            var checkBox = checkboxMap.get(group);
            checkBox.setPosition(x, y);
            checkBox.setSelected(currentGroup == group);
            checkBox.visible = true;
        } else {
            var checkBox = getCheckBox(group, x, y);
            checkboxMap.put(group, checkBox);
//            this.checkboxes.push(checkBox);
        }
    }

    private @NotNull AECheckbox getCheckBox(PatternContainerGroup group, int x, int y) {
        var checkBox = new AECheckbox(
                x,
                y,
                Minecraft.getInstance().font.width(group.name()) + 14,
                AECheckbox.SIZE,
                this.style,
                Component.empty()
                );
        checkBox.setSelected(currentGroup == group);
        checkBox.setChangeListener(
                () -> {
                    if (checkBox.isSelected()) {
                        currentGroup = group;
                    } else {
                        currentGroup = null;
                    }
                }
        );
        checkBox.setRadio(true);
        this.addRenderableWidget(checkBox);
        return checkBox;
    }

    private void renderLinkStatus(GuiGraphics guiGraphics, ILinkStatus linkStatus) {
        if (!linkStatus.connected()) {
            SimpleRenderContext renderContext = new SimpleRenderContext(LytRect.empty(), guiGraphics);
            var rect = new LytRect(
                    GUI_PADDING_X - 1,
                    GUI_HEADER_HEIGHT,
                    COLUMNS * 18,
                    visibleRows * ROW_HEIGHT);
            renderContext.fillRect(rect, new ConstantColor(0x3f000000));
            Component statusDescription = linkStatus.statusDescription();
            if (statusDescription != null) {
                renderContext.renderTextCenteredIn(statusDescription.getString(), ERROR_TEXT_STYLE, rect);
            }
        }

    }

    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        if (this.hoveredSlot == null) {
            int hoveredLineIndex = this.getHoveredLineIndex(x, y);
            if (hoveredLineIndex != -1) {
                Row row = (Row)this.rows.get(hoveredLineIndex);
                if (row instanceof GroupHeaderRow) {
                    GroupHeaderRow headerRow = (GroupHeaderRow)row;
                    if (!headerRow.group.tooltip().isEmpty()) {
                        guiGraphics.renderTooltip(this.font, headerRow.group.tooltip(), Optional.empty(), x, y);
                        return;
                    }
                }
            }
        }

        super.renderTooltip(guiGraphics, x, y);
    }

    private int getHoveredLineIndex(int x, int y) {
        x = x - this.leftPos - GUI_PADDING_X;
        y = y - this.topPos - SLOT_SIZE;
        if (x >= 0 && y >= 0) {
            if (x < SLOT_SIZE * COLUMNS && y < visibleRows * ROW_HEIGHT) {
                int rowIndex = this.scrollbar.getCurrentScroll() + y / ROW_HEIGHT;
                return rowIndex >= 0 && rowIndex < this.rows.size() ? rowIndex : -1;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    public boolean mouseClicked(double xCoord, double yCoord, int btn) {
        if (btn == 1 && this.searchField.isMouseOver(xCoord, yCoord)) {
            this.searchField.setValue("");
        }

        return super.mouseClicked(xCoord, yCoord, btn);
    }

    protected void slotClicked(Slot slot, int slotIdx, int mouseButton, ClickType clickType) {
        if (slot instanceof PatternSlot) {
            InventoryAction action = null;
            switch (clickType) {
                case PICKUP:
                    action = mouseButton == 1 ? InventoryAction.SPLIT_OR_PLACE_SINGLE : InventoryAction.PICKUP_OR_SET_DOWN;
                    break;
                case QUICK_MOVE:
                    action = mouseButton == 1 ? InventoryAction.PICKUP_SINGLE : InventoryAction.SHIFT_CLICK;
                    break;
                case CLONE:
                    if (this.getPlayer().getAbilities().instabuild) {
                        action = InventoryAction.CREATIVE_DUPLICATE;
                    }
                case THROW:
            }

            if (action != null) {
                PatternSlot machineSlot = (PatternSlot)slot;
                InventoryActionPacket p = new InventoryActionPacket(action, machineSlot.getSlotIndex(), machineSlot.getMachineInv().getServerId());
                PacketDistributor.sendToServer(p, new CustomPacketPayload[0]);
            }

        } else {
            // check if the playerInventorySlot is a normal inventory playerInventorySlot
            if (clickType == ClickType.QUICK_MOVE && slot != null && slot.container == this.getMenu().getPlayerInventory()) {
                // check if the playerInventorySlot is a pattern item
                ItemStack stack = slot.getItem();
                if (!stack.isEmpty() && PatternDetailsHelper.isEncodedPattern(stack)) {
                    // check first slots row index and if it is valid
                    // then send the slots container info to server
                    var rowSet = getVisibleSlotRowRecordSet();
                    if (rowSet.isEmpty()) {
                        return;
                    }
                    InventoryQuickMovePacket p = new InventoryQuickMovePacket(
                            slot.getSlotIndex(),
                            mouseButton,
                            List.copyOf(rowSet.stream().map(PatternContainerRecord::getServerId).toList()),
                            currentGroup != null ? currentGroup : PatternContainerGroup.nothing());
                    PacketDistributor.sendToServer(p);
                }
            }
            super.slotClicked(slot, slotIdx, mouseButton, clickType);
        }
    }

    private Set<PatternContainerRecord> getVisibleSlotRowRecordSet() {
        Set<PatternContainerRecord> rowSet = new LinkedHashSet<>();
        int scrollLevel = this.scrollbar.getCurrentScroll();
        for (int i = 0; i < this.visibleRows; ++i) {
            if (scrollLevel + i < this.rows.size()) {
                Row row = this.rows.get(scrollLevel + i);
                if (row instanceof SlotsRow slotsRow) {
                    rowSet.add(slotsRow.container);
                }
            }
        }
        return rowSet;
    }

    public void drawBG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
        this.blit(guiGraphics, offsetX, offsetY, HEADER_BBOX);
        int scrollLevel = this.scrollbar.getCurrentScroll();
        int currentY = offsetY + GUI_HEADER_HEIGHT;
        this.blit(guiGraphics, offsetX, currentY + this.visibleRows * ROW_HEIGHT, FOOTER_BBOX);

        for(int i = 0; i < this.visibleRows; ++i) {
            boolean firstLine = i == 0;
            boolean lastLine = i == this.visibleRows - 1;
            Rect2i bbox = this.selectRowBackgroundBox(false, firstLine, lastLine);
            this.blit(guiGraphics, offsetX, currentY, bbox);
            if (scrollLevel + i < this.rows.size()) {
                Row row = (Row)this.rows.get(scrollLevel + i);
                if (row instanceof SlotsRow) {
                    SlotsRow slotsRow = (SlotsRow)row;
                    bbox = this.selectRowBackgroundBox(true, firstLine, lastLine);
                    bbox.setWidth(GUI_PADDING_X + SLOT_SIZE * slotsRow.slots - 1);
                    this.blit(guiGraphics, offsetX, currentY, bbox);
                }
            }

            currentY += ROW_HEIGHT;
        }

    }

    private Rect2i selectRowBackgroundBox(boolean isInvLine, boolean firstLine, boolean lastLine) {
        if (isInvLine) {
            if (firstLine) {
                return ROW_INVENTORY_TOP_BBOX;
            } else {
                return lastLine ? ROW_INVENTORY_BOTTOM_BBOX : ROW_INVENTORY_MIDDLE_BBOX;
            }
        } else if (firstLine) {
            return ROW_TEXT_TOP_BBOX;
        } else {
            return lastLine ? ROW_TEXT_BOTTOM_BBOX : ROW_TEXT_MIDDLE_BBOX;
        }
    }

    public boolean charTyped(char character, int key) {
        return character == ' ' && this.searchField.getValue().isEmpty() ? true : super.charTyped(character, key);
    }

    public void clear() {
        this.byId.clear();
        this.cachedSearches.clear();
        this.refreshList();
    }

    public void postFullUpdate(long inventoryId, long sortBy, PatternContainerGroup group, int inventorySize, Int2ObjectMap<ItemStack> slots) {
        PatternContainerRecord record = new PatternContainerRecord(inventoryId, inventorySize, sortBy, group);
        this.byId.put(inventoryId, record);
        AppEngInternalInventory inventory = record.getInventory();
        ObjectIterator var10 = slots.int2ObjectEntrySet().iterator();

        while(var10.hasNext()) {
            Int2ObjectMap.Entry<ItemStack> entry = (Int2ObjectMap.Entry)var10.next();
            inventory.setItemDirect(entry.getIntKey(), (ItemStack)entry.getValue());
        }

        this.cachedSearches.clear();
        this.refreshList();
    }

    public void postIncrementalUpdate(long inventoryId, Int2ObjectMap<ItemStack> slots) {
        PatternContainerRecord record = (PatternContainerRecord)this.byId.get(inventoryId);
        if (record == null) {
            LOG.warn("Ignoring incremental update for unknown inventory id {}", inventoryId);
        } else {
            AppEngInternalInventory inventory = record.getInventory();
            ObjectIterator var6 = slots.int2ObjectEntrySet().iterator();

            while(var6.hasNext()) {
                Int2ObjectMap.Entry<ItemStack> entry = (Int2ObjectMap.Entry)var6.next();
                inventory.setItemDirect(entry.getIntKey(), (ItemStack)entry.getValue());
            }

        }
    }

    public void updateBeforeRender() {
        this.showPatternProviders.set((this.menu).getShownProviders());
        this.moveConvenienceButton.set((this.menu).getMoveConvenience());
        this.showGroupSelectRatioButton.set((this.menu).getShowGroupSelectRatio());
        var lastGroupSelectRatio = isShowGroupSelectRatio;
        isShowGroupSelectRatio = this.menu.getShowGroupSelectRatio() == YesNo.YES;
        if (lastGroupSelectRatio && !isShowGroupSelectRatio) {
            this.clearCheckboxes();
        }

        var currentTag = (this.menu).currentTag;
        this.cycleTagButton.setExtraTooltip(List.of(currentTag.isEmpty() ?
                GUIText.LabeledTerminalCycleTagButtonEmptyFocus.text() :
                GUIText.LabeledTerminalCycleTagButtonFocusing.text(currentTag.name())));
    }

    private void refreshList() {
        this.byGroup.clear();
        String searchFilterLowerCase = this.searchField.getValue().toLowerCase();
        Set<Object> cachedSearch = this.getCacheForSearchTerm(searchFilterLowerCase);
        boolean rebuild = cachedSearch.isEmpty();

        for(PatternContainerRecord entry : this.byId.values()) {
            if (rebuild || cachedSearch.contains(entry)) {
                boolean found = searchFilterLowerCase.isEmpty();
                if (!found) {
                    for(ItemStack itemStack : entry.getInventory()) {
                        found = this.itemStackMatchesSearchTerm(itemStack, searchFilterLowerCase);
                        if (found) {
                            break;
                        }
                    }
                }

                if (!found && !entry.getSearchName().contains(searchFilterLowerCase)) {
                    cachedSearch.remove(entry);
                } else {
                    this.byGroup.put(entry.getGroup(), entry);
                    cachedSearch.add(entry);
                }
            }
        }

        this.groups.clear();
        this.groups.addAll(this.byGroup.keySet());
        this.groups.sort(GROUP_COMPARATOR);
        this.rows.clear();
        this.rows.ensureCapacity(this.getMaxRows());

        for(PatternContainerGroup group : this.groups) {
            this.rows.add(new GroupHeaderRow(group));
            ArrayList<PatternContainerRecord> containers = new ArrayList(this.byGroup.get(group));
            Collections.sort(containers);

            for(PatternContainerRecord container : containers) {
                AppEngInternalInventory inventory = container.getInventory();

                for(int offset = 0; offset < inventory.size(); offset += COLUMNS) {
                    int slots = Math.min(inventory.size() - offset, COLUMNS);
                    SlotsRow containerRow = new SlotsRow(container, offset, slots);
                    this.rows.add(containerRow);
                }
            }
        }

        this.resetScrollbar();
        this.clearCheckboxes();
    }

    private void resetScrollbar() {
        this.scrollbar.setHeight(this.visibleRows * ROW_HEIGHT - 2);
        this.scrollbar.setRange(0, this.rows.size() - this.visibleRows, 2);
    }

    private boolean itemStackMatchesSearchTerm(ItemStack itemStack, String searchTerm) {
        return itemStack.isEmpty() ? false : ((String)this.patternSearchText.computeIfAbsent(itemStack, this::getPatternSearchText)).contains(searchTerm);
    }

    private String getPatternSearchText(ItemStack stack) {
        Level level = (this.menu).getPlayer().level();
        StringBuilder text = new StringBuilder();
        IPatternDetails pattern = PatternDetailsHelper.decodePattern(stack, level);
        if (pattern != null) {
            for(GenericStack output : pattern.getOutputs()) {
                output.what().getDisplayName().visit((content) -> {
                    text.append(content.toLowerCase());
                    return Optional.empty();
                });
                text.append('\n');
            }
        }

        return text.toString();
    }

    private Set<Object> getCacheForSearchTerm(String searchTerm) {
        if (!this.cachedSearches.containsKey(searchTerm)) {
            this.cachedSearches.put(searchTerm, new HashSet());
        }

        Set<Object> cache = this.cachedSearches.get(searchTerm);
        if (cache.isEmpty() && searchTerm.length() > 1) {
            cache.addAll(this.getCacheForSearchTerm(searchTerm.substring(0, searchTerm.length() - 1)));
        }

        return cache;
    }

    private void reinitialize() {
        this.children().removeAll(this.renderables);
        this.renderables.clear();
        this.init();
    }

    private void toggleTerminalStyle(SettingToggleButton<TerminalStyle> btn, boolean backwards) {
        TerminalStyle next = (TerminalStyle)btn.getNextValue(backwards);
        AEConfig.instance().setTerminalStyle(next);
        btn.set(next);
        this.reinitialize();
    }

    private int getMaxRows() {
        return this.groups.size() + this.byId.size();
    }

    private void blit(GuiGraphics guiGraphics, int offsetX, int offsetY, Rect2i srcRect) {
        ResourceLocation texture = AppEng.makeId("textures/guis/patternaccessterminal.png");
        guiGraphics.blit(texture, offsetX, offsetY, srcRect.getX(), srcRect.getY(), srcRect.getWidth(), srcRect.getHeight());
    }

    protected int getVisibleRows() {
        return this.visibleRows;
    }

    static record GroupHeaderRow(PatternContainerGroup group) implements Row {
        GroupHeaderRow(PatternContainerGroup group) {
            this.group = group;
        }

        public PatternContainerGroup group() {
            return this.group;
        }
    }

    static record SlotsRow(PatternContainerRecord container, int offset, int slots) implements Row {
        SlotsRow(PatternContainerRecord container, int offset, int slots) {
            this.container = container;
            this.offset = offset;
            this.slots = slots;
        }

        public PatternContainerRecord container() {
            return this.container;
        }

        public int offset() {
            return this.offset;
        }

        public int slots() {
            return this.slots;
        }
    }

    sealed interface Row permits GroupHeaderRow, SlotsRow {
    }
}