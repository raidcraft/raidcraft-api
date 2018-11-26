package de.raidcraft.api.action.requirement.tables;

import com.google.common.base.Strings;
import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.ebean.BaseModel;
import io.ebean.EbeanServer;
import io.ebean.annotation.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Entity
@Data()
@EqualsAndHashCode(callSuper = true)
@Table(name = "rc_player_tags")
public class TPlayerTag extends BaseModel {

    public static Optional<TPlayerTag> findTag(UUID playerId, String tag) {
        if (playerId == null || Strings.isNullOrEmpty(tag)) return Optional.empty();

        return RaidCraft.getDatabase(RaidCraftPlugin.class).find(TPlayerTag.class)
                .where().eq("player_id", playerId)
                .and().eq("tag_id", tag)
                .findOneOrEmpty();
    }

    public static TPlayerTag createTag(Player player, String tagName, String duration) {

        TPlayerTag tag = TPlayerTag.findTag(player.getUniqueId(), tagName)
                .orElse(new TPlayerTag());

        tag.setPlayerId(player.getUniqueId());
        tag.setPlayer(player.getName());
        tag.setTag(TTag.findOrCreateTag(tagName));
        tag.setDuration(duration);

        tag.save();

        return tag;
    }

    @NotNull
    private UUID playerId;
    private String player;
    @ManyToOne
    @Column(name = "tag_id")
    private TTag tag;
    private String duration = null;

    @Override
    protected EbeanServer database() {
        return RaidCraft.getDatabase(RaidCraftPlugin.class);
    }
}
