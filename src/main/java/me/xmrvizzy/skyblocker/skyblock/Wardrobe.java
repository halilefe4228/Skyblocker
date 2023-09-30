package me.xmrvizzy.skyblocker.skyblock;


import me.xmrvizzy.skyblocker.skyblock.tabhud.TabHud;
import me.xmrvizzy.skyblocker.skyblock.tabhud.screenbuilder.ScreenBuilder;
import me.xmrvizzy.skyblocker.skyblock.tabhud.util.PlayerLocator;
import me.xmrvizzy.skyblocker.utils.Utils;
import me.xmrvizzy.skyblocker.utils.scheduler.MessageScheduler;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

public class Wardrobe {
    public static KeyBinding openWardrobe;
    public static void init(){
        openWardrobe = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.skyblocker.openWardrobe",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_UNKNOWN,
                        "key.categories.skyblocker"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(openWardrobe.wasPressed()){
                MinecraftClient.getInstance().player.networkHandler.sendCommand("wd");
            }
        });
    }
    public static void tick() {
        if(Utils.isOnSkyblock()&&MinecraftClient.getInstance().player!=null && MinecraftClient.getInstance().player.getInventory().getName().getString().startsWith("Wardrobe (")){
            System.out.println("In wardrobe");
        }
    }
}
