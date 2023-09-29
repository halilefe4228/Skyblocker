package me.xmrvizzy.skyblocker.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {
    @Inject(method = "renderFireOverlay",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"))
    private static void onRenderFireOverlay(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
        if (!SkyblockerConfig.get().general.hideOnFireHUDEffect)
            return;
        matrices.translate(0.0, -1, 0.0);
    }

    @Inject(method = "renderFireOverlay",
            at = @At("HEAD"),
            cancellable = true)
    private static void onRenderFireOverlayy(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
        if (!SkyblockerConfig.get().general.hideOnFireHUDEffect)
            return;
        ci.cancel();
    }
}
