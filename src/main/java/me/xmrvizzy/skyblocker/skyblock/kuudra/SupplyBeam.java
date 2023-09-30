package me.xmrvizzy.skyblocker.skyblock.kuudra;


import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.utils.render.RenderHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SupplyBeam {
    public static List<BlockPos> suppliesToPick = new ArrayList<>();
    public static List<BlockPos> supplyUnplacedPoints = new ArrayList<>();
    public static void init() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(SupplyBeam::render);
    }
    private static void render(WorldRenderContext context) {
        if (MinecraftClient.getInstance().world != null) {
            for (Entity entity : MinecraftClient.getInstance().world.getEntities()) {
                if (SkyblockerConfig.get().kuwudra.beamOnSupplyPlacePoint && entity.hasCustomName() && entity.getCustomName().getString().contains("BRING SUPPLY CHEST HERE")) {
                    RenderHelper.renderFilledThroughWallsWithBeaconBeam(context,entity.getBlockPos(),DyeColor.WHITE.getColorComponents(),0.5F);
                }
                if(SkyblockerConfig.get().kuwudra.beamOnSupplyBuild && entity.hasCustomName() && entity.getCustomName().getString().contains("PROGRESS: ") && !entity.getCustomName().getString().contains("COMPLETE")){
                    String progressStr = entity.getCustomName().getString();
                    float progressFloat = Float.parseFloat(progressStr.substring(10,progressStr.length()-1));
                    RenderHelper.renderFilledThroughWallsWithBeaconBeam(context, entity.getBlockPos(),new float[]{((100f - progressFloat) / 100),progressFloat / 100f,0.0f},0.5F);
                }
                if(SkyblockerConfig.get().kuwudra.beamOnSuppliesToPick && entity.getType()== EntityType.GIANT && entity.getBlockY()<75){
                    RenderHelper.renderFilledThroughWallsWithBeaconBeam(context, new BlockPos(entity.getBlockX()-2,entity.getBlockY()+7,entity.getBlockZ()+4), new float[]{0.0f,1.0f,0.0f}, 0.5F);
                }
            }
        }
    }
}
