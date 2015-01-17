package de.raidcraft.api.chestui;

/**
 * @author Dragonfire
 */

import lombok.Getter;
import lombok.Setter;

public abstract class MenuListener {

    @Setter
    @Getter
    public boolean accepted;

    public abstract void cancel();

    public void accept() {
        accepted = true;
    }
}
