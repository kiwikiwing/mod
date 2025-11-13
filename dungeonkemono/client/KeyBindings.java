package com.kiwi.dungeonkemono.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final String KEY_CATEGORY = "key.categories.dungeonkemono";

    public static final KeyMapping OPEN_JOB_GUI = new KeyMapping(
            "key.dungeonkemono.open_job_gui",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            KEY_CATEGORY
    );

    public static final KeyMapping OPEN_STATS_GUI = new KeyMapping(
            "key.dungeonkemono.open_stats_gui",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            KEY_CATEGORY
    );
}