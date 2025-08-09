package org.ae2LabeledPatterns.menus.widgets;

import appeng.api.config.Setting;
import appeng.api.config.YesNo;
import appeng.client.gui.Icon;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import org.ae2LabeledPatterns.config.MSettings;
import org.ae2LabeledPatterns.menus.GUIText;
import org.ae2LabeledPatterns.menus.MoveConvenience;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class MServerSettingToggleButton<T extends Enum<T>> extends ServerSettingToggleButton<T> {
    // 缓存反射结果
    private static Class<?> enumPairClass;
    private static Class<?> buttonAppearanceClass;
    private static Constructor<?> enumPairConstructor;
    private static Constructor<?> buttonAppearanceConstructor;
    private static Field appearancesField;

    private static boolean isInitialized = false;

    // 初始化所有反射元素
    static {
        try {
            var targetClass = SettingToggleButton.class;
            // 获取内部类
            for (Class<?> clazz : targetClass.getDeclaredClasses()) {
                if (clazz.getSimpleName().equals("EnumPair")) {
                    enumPairClass = clazz;
                } else if (clazz.getSimpleName().equals("ButtonAppearance")) {
                    buttonAppearanceClass = clazz;
                }
            }

            if (enumPairClass == null || buttonAppearanceClass == null) {
                throw new RuntimeException("Required inner classes not found");
            }

            // 获取构造函数和字段
            enumPairConstructor = enumPairClass.getDeclaredConstructors()[0];
            enumPairConstructor.setAccessible(true);

            buttonAppearanceConstructor = buttonAppearanceClass.getDeclaredConstructors()[0];
            buttonAppearanceConstructor.setAccessible(true);

            appearancesField = targetClass.getDeclaredField("appearances");
            appearancesField.setAccessible(true);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Initialization failed", e);
        }
    }

    // 添加元素到map的方法
    public static <T extends Enum<T>> void addAppearance(Icon icon, Setting<T> setting, T val, Component title, Component... tooltipLines) {
        try {
            // 创建EnumPair实例
            Object enumPair = enumPairConstructor.newInstance(setting, val);

            var lines = new ArrayList<Component>();
            lines.add(title);
            Collections.addAll(lines, tooltipLines);

            // 创建ButtonAppearance实例
            Object buttonAppearance = buttonAppearanceConstructor.newInstance(icon, null, lines);

            // 获取map并添加元素
            @SuppressWarnings("unchecked")
            Map<Object, Object> map = (Map<Object, Object>) appearancesField.get(null);
            map.put(enumPair, buttonAppearance);
//            LogUtils.getLogger().debug("Added appearance: {} -> {}", enumPair, buttonAppearance);

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Operation failed", e);
        }
    }

    public MServerSettingToggleButton(Setting<T> setting, T val) {
        super(setting, val);
//        LogUtils.getLogger().debug("MServerSettingToggleButton:{}, val:{}", setting.getName(), val.name());

        if (!isInitialized) {
            initializeAppearances();
            isInitialized = true;
        }
    }
    private static void initializeAppearances() {
        addAppearance(
                Icon.ARROW_RIGHT,
                MSettings.TERMINAL_MOVE_CONVENIENCE,
                MoveConvenience.NONE,
                GUIText.MoveConvenienceButtonTitle.text(),
                GUIText.MoveConvenienceButtonNone.text()
        );
        addAppearance(
                Icon.BLOCKING_MODE_NO,
                MSettings.TERMINAL_MOVE_CONVENIENCE,
                MoveConvenience.ONCE_FOR_ALL,
                GUIText.MoveConvenienceButtonTitle.text(),
                GUIText.MoveConvenienceButtonForAll.text()
        );

        addAppearance(
                Icon.FULLNESS_FULL,
                MSettings.TERMINAL_SHOW_GROUP_SELECT_RATIO,
                YesNo.YES,
                GUIText.ShowGroupSelectRatioTitle.text(),
                GUIText.ShowGroupSelectRatioYes.text()
        );
        addAppearance(
                Icon.FULLNESS_EMPTY,
                MSettings.TERMINAL_SHOW_GROUP_SELECT_RATIO,
                YesNo.NO,
                GUIText.ShowGroupSelectRatioTitle.text(),
                GUIText.ShowGroupSelectRatioNo.text()
        );
    }
}
