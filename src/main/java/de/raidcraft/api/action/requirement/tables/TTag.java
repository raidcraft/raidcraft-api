package de.raidcraft.api.action.requirement.tables;

import com.google.common.base.Strings;
import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import lombok.Data;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Data
@Table(name = "rc_tags")
public class TTag {

    public static Optional<TTag> findTag(String tag) {
        if (Strings.isNullOrEmpty(tag)) return Optional.empty();

        return Optional.ofNullable(RaidCraft.getDatabase(RaidCraftPlugin.class).find(TTag.class, tag));
    }

    public static TTag findOrCreateTag(String tagName, @Nullable String description) {
        return findTag(tagName).orElseGet(() -> {
            TTag newTag = new TTag();
            newTag.setId(tagName);
            newTag.setDescription(description);
            RaidCraft.getDatabase(RaidCraftPlugin.class).save(newTag);
            return newTag;
        });
    }

    public static TTag findOrCreateTag(String tagName) {
        return findOrCreateTag(tagName, null);
    }

    @Id
    private String id;
    private String description;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "tag_id")
    private List<TPlayerTag> playerTags = new ArrayList<>();
}
