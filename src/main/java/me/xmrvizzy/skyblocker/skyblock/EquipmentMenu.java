package me.xmrvizzy.skyblocker.skyblock;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class EquipmentMenu {
    public static KeyBinding openEqMenu;
    public static void init(){
        openEqMenu = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.skyblocker.openEqMenu",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_UNKNOWN,
                        "key.categories.skyblocker"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(openEqMenu.wasPressed()){
                MinecraftClient.getInstance().player.networkHandler.sendCommand("eq");
            }
        });
    }
}
