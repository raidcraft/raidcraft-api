package de.raidcraft.util;

import com.sk89q.worldedit.blocks.BlockID;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.material.Lever;

/**
 * @author Philip Urban
 */
public class LeverUtil {

    public static boolean setState(Block block, boolean state, Block source) {

        if (block.getTypeId() != BlockID.LEVER) return false;

        // return if the lever is not attached to our IC block
        Lever lever = (Lever) block.getState().getData();

        if (!block.getRelative(lever.getAttachedFace()).equals(source))
            return false;

        // check if the lever was toggled on
        boolean wasOn = (block.getData() & 0x8) > 0;

        byte data = block.getData();
        int newData;
        // check if the state changed and set the data value
        if (!state) {
            newData = data & 0x7;
        } else {
            newData = data | 0x8;
        }

        // if the state changed lets apply physics to the source block and the lever itself
        if (wasOn != state) {

            // set the new data
            block.setData((byte) newData, true);
            // apply physics to the source block the lever is attached to
            byte sData = source.getData();
            source.setData((byte) (sData - 1), true);
            source.setData(sData, true);

            // lets call blockredstone events on the source block and the lever
            // in order to correctly update all surrounding blocks
            BlockRedstoneEvent leverEvent = new BlockRedstoneEvent(block, wasOn ? 15 : 0, state ? 15 : 0);
            BlockRedstoneEvent sourceEvent = new BlockRedstoneEvent(source, wasOn ? 15 : 0, state ? 15 : 0);
            Bukkit.getServer().getPluginManager().callEvent(leverEvent);
            Bukkit.getServer().getPluginManager().callEvent(sourceEvent);
            return true;
        }

        return false;
    }

}
