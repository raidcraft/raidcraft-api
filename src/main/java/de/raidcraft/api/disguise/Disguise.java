package de.raidcraft.api.disguise;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.base.Strings;
import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.ebean.BaseModel;
import io.ebean.EbeanServer;
import io.ebean.annotation.DbDefault;
import lombok.Data;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import me.libraryaddict.disguise.disguisetypes.TargetedDisguise;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "rc_disguises")
@Data
public class Disguise extends BaseModel {

    public static Optional<Disguise> fromAlias(String alias) {
        return RaidCraft.getComponent(DisguiseManager.class).getDisguise(alias);
    }

    public static Optional<Disguise> fromId(int id) {
        return RaidCraft.getComponent(DisguiseManager.class).getDisguise(id);
    }

    private String alias;
    @Column(length = 4196)
    private String skinTexture;
    @Column(length = 4196)
    private String skinSignature;
    private String skinOwner;
    private String skinOwnerName;
    private String skinUrl;
    private String description;
    @DbDefault("0")
    private int mineskinId = 0;

    public Disguise() {
    }

    public Disguise(String skinTexture, String skinSignature) {
        this.alias = UUID.randomUUID().toString().toLowerCase();
        this.skinTexture = skinTexture;
        this.skinSignature = skinSignature;
    }

    public Disguise(String alias, String skinTexture, String skinSignature) {
        this.alias = alias == null ? UUID.randomUUID().toString() : alias;
        this.skinTexture = skinTexture;
        this.skinSignature = skinSignature;
    }

    public TargetedDisguise getDisguise(Player player) {
        PlayerDisguise playerDisguise = new PlayerDisguise(player);
        playerDisguise.setSkin(toGameProfile(player));
        playerDisguise.setReplaceSounds(true);
        playerDisguise.setShowName(false);
        return playerDisguise;
    }

    public TargetedDisguise getDisguise(org.bukkit.entity.Entity entity) {
        PlayerDisguise playerDisguise = new PlayerDisguise(entity.getName());
        playerDisguise.setSkin(toGameProfile());
        playerDisguise.setReplaceSounds(true);
        playerDisguise.setShowName(false);
        return playerDisguise;
    }

    public TargetedDisguise getDisguise(String name) {
        PlayerDisguise playerDisguise = new PlayerDisguise(name);
        playerDisguise.setSkin(toGameProfile());
        playerDisguise.setReplaceSounds(true);
        playerDisguise.setShowName(false);
        return playerDisguise;
    }

    public TargetedDisguise applyToPlayer(Player player) {
        return applyToEntity(player);
    }

    public TargetedDisguise applyToEntity(org.bukkit.entity.Entity entity) {
        TargetedDisguise disguise = getDisguise(entity);
        disguise.setEntity(entity);
        DisguiseAPI.disguiseToAll(entity, disguise);
        return disguise;
    }

    public WrappedGameProfile toGameProfile() {
        UUID owner = UUID.randomUUID();
        if (!Strings.isNullOrEmpty(getSkinOwner())) owner = UUID.fromString(getSkinOwner());
        WrappedGameProfile gameProfile = new WrappedGameProfile(owner, getAlias());
        gameProfile.getProperties().clear();
        gameProfile.getProperties().put(DisguiseManager.GAME_PROFILE_TEXTURE_PROPERTY, getTextureProperty());
        return gameProfile;
    }

    public WrappedGameProfile toGameProfile(Player player) {
        WrappedGameProfile gameProfile = new WrappedGameProfile(player.getUniqueId(), player.getDisplayName());
        gameProfile.getProperties().clear();
        gameProfile.getProperties().put(DisguiseManager.GAME_PROFILE_TEXTURE_PROPERTY, getTextureProperty());
        return gameProfile;
    }

    public WrappedSignedProperty getTextureProperty() {
        return new WrappedSignedProperty(DisguiseManager.GAME_PROFILE_TEXTURE_PROPERTY, getSkinTexture(), getSkinSignature());
    }

    @Override
    protected EbeanServer database() {
        return RaidCraft.getDatabase(RaidCraftPlugin.class);
    }
}
