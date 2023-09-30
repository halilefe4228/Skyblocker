package me.xmrvizzy.skyblocker.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.xmrvizzy.skyblocker.SkyblockerMod;
import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.skyblock.FancyStatusBars;
import me.xmrvizzy.skyblocker.skyblock.HotbarSlotLock;
import me.xmrvizzy.skyblocker.skyblock.dungeon.DungeonMap;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Unique
    private static final Identifier SLOT_LOCK = new Identifier(SkyblockerMod.NAMESPACE, "textures/gui/slot_lock.png");
    @Unique
    private final FancyStatusBars statusBars = new FancyStatusBars();

    @Shadow
    private int scaledHeight;
    @Shadow
    private int scaledWidth;

    @Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbarItem(Lnet/minecraft/client/gui/DrawContext;IIFLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V", ordinal = 0))
    public void skyblocker$renderHotbarItemLock(float tickDelta, DrawContext context, CallbackInfo ci, @Local(ordinal = 4, name = "m") int index, @Local(ordinal = 5, name = "n") int x, @Local(ordinal = 6, name = "o") int y) {
        if(Utils.isOnSkyblock() && SkyblockerConfig.get().general.itemRarityBackground){
            if(MinecraftClient.getInstance().player.getInventory().getStack(index).hasNbt()){
                String text = MinecraftClient.getInstance().player.getInventory().getStack(index).getNbt().toString();
                String RarityMatchRegex = "\\b(COMMON|UNCOMMON|RARE|EPIC|MYTHIC|LEGENDARY|SPECIAL|VERY SPECIAL)\\b";
                Pattern pattern = Pattern.compile(RarityMatchRegex);
                Matcher matcher = pattern.matcher(text);
                if(matcher.find()){
                    if(!Objects.equals(matcher.group(), "VERY SPECIAL")){ context.drawTexture(new Identifier(SkyblockerMod.NAMESPACE,"textures/gui/"+matcher.group().toLowerCase()+".png"), x, y, 0, 0, 16, 16);}
                    else { context.drawTexture(new Identifier(SkyblockerMod.NAMESPACE,"textures/gui/veryspecial.png"), x, y, 0, 0, 16, 16);}
                }
            }
        }
        if (Utils.isOnSkyblock() && HotbarSlotLock.isLocked(index)) {
            context.drawTexture(SLOT_LOCK, x, y, 0, 0, 16, 16);
        }
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void skyblocker$renderExperienceBar(CallbackInfo ci) {
        if (Utils.isOnSkyblock() && SkyblockerConfig.get().general.bars.enableBars && !Utils.isInTheRift())
            ci.cancel();
    }

    @Inject(method = "renderStatusBars", at = @At("HEAD"), cancellable = true)
    private void skyblocker$renderStatusBars(DrawContext context, CallbackInfo ci) {
        if (!Utils.isOnSkyblock())
            return;
        if (statusBars.render(context, scaledWidth, scaledHeight))
            ci.cancel();

        if (Utils.isInDungeons() && SkyblockerConfig.get().locations.dungeons.enableMap)
            DungeonMap.render(context.getMatrices());
    }

    @Inject(method = "renderMountHealth", at = @At("HEAD"), cancellable = true)
    private void skyblocker$renderMountHealth(CallbackInfo ci) {
        if (Utils.isOnSkyblock() && SkyblockerConfig.get().general.bars.enableBars && !Utils.isInTheRift())
            ci.cancel();
    }
    
    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    private void skyblocker$dontRenderStatusEffects(CallbackInfo ci) {
        if (Utils.isOnSkyblock() && SkyblockerConfig.get().general.hideStatusEffectOverlay) ci.cancel();
    }
}