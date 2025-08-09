package org.ae2LabeledPatterns.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.ae2LabeledPatterns.AE2LabeledPatterns;

public class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue AUTO_FOCUS = BUILDER
            .comment("Whether to automatically focus the search field when opening labeled terminal.")
            .define("auto_focus", false);

    private static final ModConfigSpec.BooleanValue REMEMBER_POSITION = BUILDER
            .comment("Whether to remember the last position of scrollbar in the labeled terminal.")
            .define("remember_position", false);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean GetAutoFocus() {
        return AUTO_FOCUS.get();
    }

    public static boolean GetRememberPosition() {
        return REMEMBER_POSITION.get();
    }

    public static void SetAutoFocus(boolean value) {
        AUTO_FOCUS.set(value);
    }
    public static void SetRememberPosition(boolean value) {
        REMEMBER_POSITION.set(value);
    }

    public static void Save(){
        SPEC.save();
    }
}
