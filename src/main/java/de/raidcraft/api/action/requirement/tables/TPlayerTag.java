package de.raidcraft.api.action.requirement.tables;

import com.google.common.base.Strings;
import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.ebean.BaseModel;
import io.ebean.EbeanServer;
import io.ebean.annotation.NotNull;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Entity
@Data
@Table(name = "rc_player_tags")
public class TPlayerTag extends BaseModel {

    public static Optional<TPlayerTag> findTag(UUID playerId, String tag) {
        if (playerId == null || Strings.isNullOrEmpty(tag)) return Optional.empty();

        return RaidCraft.getDatabase(RaidCraftPlugin.class).find(TPlayerTag.class)
                .where().eq("player_id", playerId)
                .and().eq("tag", tag)
                .findOneOrEmpty();
    }

    @NotNull
    private UUID playerId;
    private String player;
    @NotNull
    private String tag;
    private String duration = null;

    @Override
    protected EbeanServer database() {
        return RaidCraft.getDatabase(RaidCraftPlugin.class);
    }
}
