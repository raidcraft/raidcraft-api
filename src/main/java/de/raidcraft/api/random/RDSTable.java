package de.raidcraft.api.random;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;

/**
 * This interface describes a table of IRDSObjects. One (or more) of them is/are picked as the result set.
 */
public interface RDSTable extends RDSObject {

    /**
     * Sets the id of the loot-table.
     *
     * @param id as given by the config loader.
     */
    void setId(String id);

    /**
     * Unique id of the loot-table. The id is based on the location of the loot-table.
     * Can be empty if the table was not created by loading a config.
     *
     * @return unique id of the table or empty if not loaded from a config.
     */
    Optional<String> getId();

    /**
     * The maximum number of entries expected in the Result. The final count of items in the result may be lower
     * if some of the entries may return a null result (no drop).
     *
     * @return maximum number of entries expected in the result
     */
    int getCount();

    /**
     * Sets the maximum number of entries expected in the Result. The final count of items in the result may be lower
     * if some of the entries may return a null result (no drop).
     *
     * @param count of the maximum entries to expected in the result
     */
    void setCount(int count);

    /**
     * Gets the content of the table that will be evaluated when querying for the result.
     * THe content can also be other table objects and they may contain more tables.
     * Can recurse indefinetly.
     *
     * @return contents of the table
     */
    Collection<RDSObject> getContents();

    Optional<Player> getLootingPlayer();

    /**
     * Clears all contents of the {@link RDSTable}.
     */
    void clearContents();

    RDSTable addEntry(RDSObject object);

    RDSTable addEntry(RDSObject object, double probability);

    RDSTable addEntry(RDSObject object, double probability, boolean enabled, boolean always, boolean unique);

    /**
     * Gets the result. Calling this method will start the random pick process and generate the result.
     * This result remains constant for the lifetime of this table object.
     * Use the {@link #loot()} method to clear the result and create a new one.
     *
     * @return calculated random result of this table
     */
    Collection<RDSObject> getResult();

    /**
     * Will reset the last cached result and
     * generate a new result by calling {@link #getResult()}.
     *
     * @return fresh random result
     */
    Collection<RDSObject> loot();

    /**
     * Will reset the cache and loot the object in a player context.
     * This means that requirements get evaluated. Otherwise requirements will all be true.
     *
     * @param player that is looting
     * @return random loot with evaluated requirements
     */
    Collection<RDSObject> loot(Player player);
}
