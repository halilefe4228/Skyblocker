package me.xmrvizzy.skyblocker.skyblock.tabhud.widget;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.xmrvizzy.skyblocker.skyblock.tabhud.util.Ico;
import me.xmrvizzy.skyblocker.skyblock.tabhud.util.PlayerListMgr;
import me.xmrvizzy.skyblocker.skyblock.tabhud.widget.component.ProgressComponent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

// this widget shows broad info about the current dungeon
// opened/completed rooms, % of secrets found and time taken

public class DungeonServerWidget extends Widget {

    private static final MutableText TITLE = Text.literal("Dungeon Info").formatted(Formatting.DARK_PURPLE,
            Formatting.BOLD);

    // match the secrets text
    // group 1: % of secrets found (without "%")
    private static final Pattern SECRET_PATTERN = Pattern.compile("Secrets Found: (?<secnum>.*)%");

    public DungeonServerWidget() {
        super(TITLE, Formatting.DARK_PURPLE.getColorValue());
    }

    @Override
    public void updateContent() {
        this.addSimpleIcoText(Ico.NTAG, "Name:", Formatting.AQUA, 41);
        this.addSimpleIcoText(Ico.SIGN, "Rooms Visited:", Formatting.DARK_PURPLE, 42);
        this.addSimpleIcoText(Ico.SIGN, "Rooms Completed:", Formatting.LIGHT_PURPLE, 43);

        Matcher m = PlayerListMgr.regexAt(44, SECRET_PATTERN);
        if (m == null) {
            this.addComponent(new ProgressComponent());
        } else {
            ProgressComponent scp = new ProgressComponent(Ico.CHEST, Text.of("Secrets found:"),
                    Float.parseFloat(m.group("secnum")),
                    Formatting.DARK_PURPLE.getColorValue());
            this.addComponent(scp);
        }

        this.addSimpleIcoText(Ico.CLOCK, "Time:", Formatting.GOLD, 45);
    }

}
