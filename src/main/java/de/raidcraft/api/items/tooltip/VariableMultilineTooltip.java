package de.raidcraft.api.items.tooltip;

import de.raidcraft.util.Font;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public class VariableMultilineTooltip extends Tooltip {

    private String original;
    private boolean quote;
    private boolean italic;
    private ChatColor color;
    private String[] lines;

    public VariableMultilineTooltip(TooltipSlot slot, String original, boolean quote, boolean italic, ChatColor color) {

        super(slot);
        this.original = original;
        this.quote = quote;
        this.italic = italic;
        this.color = color;
        updateLineWidth();
    }

    public void updateLineWidth() {

        List<String> output = new ArrayList<>();
        StringBuilder out = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        out.append(color);
        if (italic) out.append(ChatColor.ITALIC);
        int cWidth = 0;
        int tWidth = 0;
        String currentColour = color.toString();
        String dMsg = quote ? "\"" + original + "\"" : original;
        for (int i = 0; i < dMsg.length(); i++) {
            char c = dMsg.charAt(i);
            temp.append(c);
            if (c == ChatColor.COLOR_CHAR || c == '&') {
                i += 1;
                temp.append(dMsg.charAt(i));
                currentColour = ChatColor.COLOR_CHAR + "" + dMsg.charAt(i);
                continue;
            }
            if (c == ' ') {
                tWidth += 4;
            } else {
                tWidth += Font.WIDTHS[c] + 1;
            }
            if (c == ' ' || i == dMsg.length() - 1) {
                if (cWidth + tWidth > getWidth()) {
                    cWidth = 0;
                    cWidth += tWidth;
                    tWidth = 0;
                    output.add(out.toString());
                    out = new StringBuilder();
                    out.append(currentColour);
                    if (italic) out.append(ChatColor.ITALIC);
                    out.append(temp);
                    temp = new StringBuilder();
                } else {
                    out.append(temp);
                    temp = new StringBuilder();
                    cWidth += tWidth;
                    tWidth = 0;
                }
            }
        }
        out.append(temp);
        output.add(out.toString());

        lines = output.toArray(new String[output.size()]);
    }

    @Override
    public String[] getTooltip() {

        return lines;
    }
}
