package de.raidcraft.api.items;

import lombok.Data;

import java.util.Optional;

/**
 * @author mdoering
 */
@Data
public class Socket {

    // black hexagon character: http://www.fileformat.info/info/unicode/char/2b22/index.htm
    public static final char FILLED_SOCKET_SYMBOL = (char) 0x2B22;
    // white hexagon character: http://www.fileformat.info/info/unicode/char/2b21/index.htm
    public static final char EMPTY_SOCKET_SYMBOL = (char) 0x2B21;

    private final GemColor color;
    private Optional<Gem> gem;

    public Socket(GemColor color) {

        this(color, null);
    }

    public Socket(GemColor color, Gem gem) {

        this.color = color;
        this.gem = Optional.ofNullable(gem);
    }

    public void setGem(Gem gem) {

        this.gem = Optional.ofNullable(gem);
    }

    public boolean isEmpty() {

        return !getGem().isPresent();
    }
}
