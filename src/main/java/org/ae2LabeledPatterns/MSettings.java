package org.ae2LabeledPatterns;

import appeng.api.config.Setting;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import org.ae2LabeledPatterns.items.components.LabelerMode;
import org.ae2LabeledPatterns.menus.MoveConvenience;

import java.lang.reflect.Method;

public class MSettings {
    private static final Method registerMethod1;
    private static final Method registerMethod2;

    static {
        try {
            registerMethod1 = Settings.class.getDeclaredMethod("register", String.class, Class.class);
            registerMethod1.setAccessible(true);
            registerMethod2 = Settings.class.getDeclaredMethod("register", String.class, Enum.class, Enum[].class);
            registerMethod2.setAccessible(true);
        }catch (Exception e){
            throw new RuntimeException("AE2 Settings reflection failed", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> Setting<T> RegiterSetting(String name, Class<T> enumClass) {
        try {
            return (Setting<T>) registerMethod1.invoke(null, name, enumClass);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register AE2 Setting: " + name, e);
        }

    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> Setting<T> RegiterSetting(String name, T defalut, T... others) {
        try {
            return (Setting<T>) registerMethod2.invoke(null, name, defalut, others);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register AE2 Setting: " + name, e);
        }
    }

    public static Setting<YesNo> LABELER_INPUT_LOCKED = RegiterSetting("labeler_input_locked", YesNo.YES, YesNo.NO);
    public static Setting<MoveConvenience> TERMINAL_MOVE_CONVENIENCE = RegiterSetting("terminal_move_convenience",
            MoveConvenience.class);
}
