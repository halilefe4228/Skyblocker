package me.xmrvizzy.skyblocker.mixin;

import me.xmrvizzy.skyblocker.SkyblockerMod;
import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.skyblock.experiment.ChronomatronSolver;
import me.xmrvizzy.skyblocker.skyblock.experiment.ExperimentSolver;
import me.xmrvizzy.skyblocker.skyblock.experiment.SuperpairsSolver;
import me.xmrvizzy.skyblocker.skyblock.experiment.UltrasequencerSolver;
import me.xmrvizzy.skyblocker.skyblock.item.BackpackPreview;
import me.xmrvizzy.skyblocker.skyblock.item.CompactorDeletorPreview;
import me.xmrvizzy.skyblocker.skyblock.item.WikiLookup;
import me.xmrvizzy.skyblocker.skyblock.itemlist.ItemRegistry;
import me.xmrvizzy.skyblocker.utils.Utils;
import me.xmrvizzy.skyblocker.utils.render.gui.ContainerSolver;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends Screen {
    @Shadow
    @Nullable
    protected Slot focusedSlot;

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("HEAD"), method = "keyPressed")
    public void skyblocker$keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (this.client != null && this.focusedSlot != null && keyCode != 256 && !this.client.options.inventoryKey.matchesKey(keyCode, scanCode) && WikiLookup.wikiLookup.matchesKey(keyCode, scanCode)) {
            WikiLookup.openWiki(this.focusedSlot);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    // makes intellij be quiet about this.focusedSlot maybe being null. It's already null checked in mixined method.
    @Inject(method = "drawMouseoverTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V"), cancellable = true)
    public void skyblocker$drawMouseOverTooltip(DrawContext context, int x, int y, CallbackInfo ci) {
        if (!Utils.isOnSkyblock()) return;

        // Hide Empty Tooltips
        if (SkyblockerConfig.get().general.hideEmptyTooltips && focusedSlot.getStack().getName().getString().equals(" ")) {
            ci.cancel();
        }

        // Backpack Preview
        boolean shiftDown = SkyblockerConfig.get().general.backpackPreviewWithoutShift ^ Screen.hasShiftDown();
        if (shiftDown && getTitle().getString().equals("Storage") && focusedSlot.inventory != client.player.getInventory() && BackpackPreview.renderPreview(context, focusedSlot.getIndex(), x, y)) {
            ci.cancel();
        }

        // Compactor Preview
        if (SkyblockerConfig.get().general.compactorDeletorPreview) {
            ItemStack stack = focusedSlot.getStack();
            Matcher matcher = CompactorDeletorPreview.NAME.matcher(ItemRegistry.getInternalName(stack));
            if (matcher.matches() && CompactorDeletorPreview.drawPreview(context, stack, matcher.group("type"), matcher.group("size"), x, y)) {
                ci.cancel();
            }
        }
    }

    @Redirect(method = "drawMouseoverTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;getStack()Lnet/minecraft/item/ItemStack;", ordinal = 0))
    private ItemStack skyblocker$experimentSolvers$replaceTooltipDisplayStack(Slot slot) {
        return skyblocker$experimentSolvers$getStack(slot, null);
    }

    @ModifyVariable(method = "drawSlot", at = @At(value = "LOAD", ordinal = 4), ordinal = 0)
    private ItemStack skyblocker$experimentSolvers$replaceDisplayStack(ItemStack stack, DrawContext context, Slot slot) {
        if(Utils.isOnSkyblock() && SkyblockerConfig.get().general.itemRarityBackground){
            if(stack.hasNbt()){
                String text = stack.getNbt().toString();
                String RarityMatchRegex = "\\b(COMMON|UNCOMMON|RARE|EPIC|MYTHIC|LEGENDARY|SPECIAL|VERY SPECIAL)\\b";
                Pattern pattern = Pattern.compile(RarityMatchRegex);
                Matcher matcher = pattern.matcher(text);
                if(matcher.find()){
                    if(!Objects.equals(matcher.group(), "VERY SPECIAL")){ context.drawTexture(new Identifier(SkyblockerMod.NAMESPACE,"textures/gui/"+matcher.group().toLowerCase()+".png"), slot.x, slot.y, 0, 0, 16, 16);}
                    else { context.drawTexture(new Identifier(SkyblockerMod.NAMESPACE,"textures/gui/veryspecial.png"), slot.x, slot.y, 0, 0, 16, 16);}
                }
            }
        }
        return skyblocker$experimentSolvers$getStack(slot, stack);
    }


    @Unique
    private ItemStack skyblocker$experimentSolvers$getStack(Slot slot, ItemStack stack) {
        ContainerSolver currentSolver = SkyblockerMod.getInstance().containerSolverManager.getCurrentSolver();
        if ((currentSolver instanceof SuperpairsSolver || currentSolver instanceof UltrasequencerSolver) && ((ExperimentSolver) currentSolver).getState() == ExperimentSolver.State.SHOW && slot.inventory instanceof SimpleInventory) {
            ItemStack itemStack = ((ExperimentSolver) currentSolver).getSlots().get(slot.getIndex());
            return itemStack == null ? slot.getStack() : itemStack;
        }
        return (stack != null) ? stack : slot.getStack();
    }

    @Inject(method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;clickSlot(IIILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V"))
    private void skyblocker$experimentSolvers$onSlotClick(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        if (slot != null) {
            ContainerSolver currentSolver = SkyblockerMod.getInstance().containerSolverManager.getCurrentSolver();
            if (currentSolver instanceof ExperimentSolver experimentSolver && experimentSolver.getState() == ExperimentSolver.State.SHOW && slot.inventory instanceof SimpleInventory) {
                if (experimentSolver instanceof ChronomatronSolver chronomatronSolver) {
                    Item item = chronomatronSolver.getChronomatronSlots().get(chronomatronSolver.getChronomatronCurrentOrdinal());
                    if ((slot.getStack().isOf(item) || ChronomatronSolver.TERRACOTTA_TO_GLASS.get(slot.getStack().getItem()) == item) && chronomatronSolver.incrementChronomatronCurrentOrdinal() >= chronomatronSolver.getChronomatronSlots().size()) {
                        chronomatronSolver.setState(ExperimentSolver.State.END);
                    }
                } else if (experimentSolver instanceof SuperpairsSolver superpairsSolver) {
                    superpairsSolver.setSuperpairsPrevClickedSlot(slot.getIndex());
                    superpairsSolver.setSuperpairsCurrentSlot(ItemStack.EMPTY);
                } else if (experimentSolver instanceof UltrasequencerSolver ultrasequencerSolver && slot.getIndex() == ultrasequencerSolver.getUltrasequencerNextSlot()) {
                    int count = ultrasequencerSolver.getSlots().get(ultrasequencerSolver.getUltrasequencerNextSlot()).getCount() + 1;
                    ultrasequencerSolver.getSlots().entrySet().stream().filter(entry -> entry.getValue().getCount() == count).findAny().map(Map.Entry::getKey).ifPresentOrElse(ultrasequencerSolver::setUltrasequencerNextSlot, () -> ultrasequencerSolver.setState(ExperimentSolver.State.END));
                }
            }
        }
    }
}
