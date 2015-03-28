package de.raidcraft.api.items.tooltip;

import de.raidcraft.api.items.Gem;
import de.raidcraft.api.items.Socket;
import de.raidcraft.util.CustomItemUtil;

/**
 * @author mdoering
 */
public class SocketTooltip extends FixedMultilineTooltip {

    private Socket[] sockets;

    public SocketTooltip(Socket... sockets) {

        super(TooltipSlot.SOCKETS);
        this.sockets = sockets;
        String[] lines = new String[sockets.length];
        for (int i = 0; i < sockets.length; i++) {
            if (sockets[i] == null) continue;
            String line = "";
            if (sockets[i].isEmpty()) {
                line += sockets[i].getColor().getColor() + "" + Socket.EMPTY_SOCKET_SYMBOL;
            } else {
                Gem gem = sockets[i].getGem().get();
                line += CustomItemUtil.encodeItemId(gem.getId()) + sockets[i].getColor().getColor()
                        + Socket.FILLED_SOCKET_SYMBOL + " " + gem.getName();
            }
            lines[i] = line;
        }
        setTooltip(lines);
        updateLineWidth();
    }
}
