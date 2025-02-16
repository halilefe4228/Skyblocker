package me.xmrvizzy.skyblocker.skyblock.dwarven;

import it.unimi.dsi.fastutil.ints.IntIntPair;
import me.shedaniel.autoconfig.AutoConfig;
import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.skyblock.dwarven.DwarvenHud.Commission;
import me.xmrvizzy.skyblocker.skyblock.tabhud.widget.hud.HudCommsWidget;
import me.xmrvizzy.skyblocker.utils.render.RenderHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.List;

public class DwarvenHudConfigScreen extends Screen {

    private static final List<Commission> CFG_COMMS = List.of(new DwarvenHud.Commission("Test Commission 1", "1%"), new DwarvenHud.Commission("Test Commission 2", "2%"));

    private int hudX = SkyblockerConfig.get().locations.dwarvenMines.dwarvenHud.x;
    private int hudY = SkyblockerConfig.get().locations.dwarvenMines.dwarvenHud.y;

    protected DwarvenHudConfigScreen() {
        super(Text.of("Dwarven HUD Config"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        renderBackground(context, mouseX, mouseY, delta);
        DwarvenHud.render(HudCommsWidget.INSTANCE_CFG, context, hudX, hudY, List.of(new DwarvenHud.Commission("Test Commission 1", "1%"), new DwarvenHud.Commission("Test Commission 2", "2%")));
        context.drawCenteredTextWithShadow(textRenderer, "Right Click To Reset Position", width / 2, height / 2, Color.GRAY.getRGB());
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        IntIntPair dims = DwarvenHud.getDimForConfig(CFG_COMMS);
        if (RenderHelper.pointIsInArea(mouseX, mouseY, hudX, hudY, hudX + 200, hudY + 40) && button == 0) {
            hudX = (int) Math.max(Math.min(mouseX - (double) dims.leftInt() / 2, this.width - dims.leftInt()), 0);
            hudY = (int) Math.max(Math.min(mouseY - (double) dims.rightInt() / 2, this.height - dims.rightInt()), 0);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1) {
            IntIntPair dims = DwarvenHud.getDimForConfig(CFG_COMMS);
            hudX = this.width / 2 - dims.leftInt();
            hudY = this.height / 2 - dims.rightInt();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        SkyblockerConfig.get().locations.dwarvenMines.dwarvenHud.x = hudX;
        SkyblockerConfig.get().locations.dwarvenMines.dwarvenHud.y = hudY;
        AutoConfig.getConfigHolder(SkyblockerConfig.class).save();
        super.close();
    }
}
