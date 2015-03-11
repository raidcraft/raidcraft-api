package de.raidcraft.api.action.requirement;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.action.requirement.tables.TPersistantRequirement;
import de.raidcraft.api.action.requirement.tables.TPersistantRequirementMapping;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.util.ConfigUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Silthus
 */
@EqualsAndHashCode(of = {"requirement"})
@Data
class RequirementConfigWrapper<T> implements Requirement<T>, Comparable<Requirement<T>> {

    private static final String CHECKED_KEY = "checked";
    private static final String COUNT_KEY = "count";

    private final String id;
    private final Requirement<T> requirement;
    private final boolean persistant;
    private final int order;
    private final int requiredCount;
    private final String countText;
    private final boolean optional;
    private final ConfigurationSection config;
    private final Map<UUID, Map<String, String>> mappings = new HashMap<>();

    protected RequirementConfigWrapper(String id, Requirement<T> requirement, ConfigurationSection config) {

        this.id = config.isSet("id") ? config.getString("id") : id;
        this.requirement = requirement;
        this.persistant = config.getBoolean("persistant", false);
        this.order = config.getInt("order", 0);
        this.requiredCount = config.getInt("count", 0);
        this.countText = config.getString("count-text");
        this.optional = config.getBoolean("optional", false);
        this.config = config;
    }

    private int getIntMapping(T entity, String key) {

        String mapping = getMapping(entity, key);
        return mapping == null ? 0 : Integer.parseInt(mapping);
    }

    private boolean getBoolMapping(T entity, String key) {

        String mapping = getMapping(entity, key);
        return mapping != null && Boolean.parseBoolean(mapping);
    }

    private String getMapping(T entity, String key) {

        if (entity instanceof Player) {
            return mappings.getOrDefault(((Player) entity).getUniqueId(), new HashMap<>()).getOrDefault(key, null);
        }
        return null;
    }

    private void setMapping(T entity, String key, String value) {

        if (entity instanceof Player) {
            UUID uniqueId = ((Player) entity).getUniqueId();
            if (!mappings.containsKey(uniqueId)) {
                mappings.put(uniqueId, new HashMap<>());
            }
            mappings.get(uniqueId).put(key, value);
        }
    }

    private void setMapping(T entity, String key, int value) {

        setMapping(entity, key, Integer.toString(value));
    }

    private void setMapping(T entity, String key, boolean value) {

        setMapping(entity, key, Boolean.toString(value));
    }

    public boolean isChecked(T entity) {

        return getBoolMapping(entity, CHECKED_KEY);
    }

    public boolean isOrdered() {

        return getOrder() > 0;
    }

    public boolean isCounting() {

        return getRequiredCount() > 1;
    }

    public int getCount(T entity) {

        return getIntMapping(entity, COUNT_KEY);
    }

    public boolean hasCountText() {

        return countText != null && !countText.equals("");
    }

    public String getCountText(T entity) {

        return ConfigUtil.replaceCount(getPath(), countText, getCount(entity), getRequiredCount());
    }

    @Override
    public boolean test(T type) {

        ConfigurationSection args = getConfig().getConfigurationSection("args");
        if (args == null) args = getConfig().createSection("args");
        return test(type, args);
    }

    @Override
    public boolean test(T entity, ConfigurationSection config) {

        boolean successfullyChecked = isChecked(entity);

        if (isPersistant() && successfullyChecked) return true;

        successfullyChecked = requirement.test(entity, config);

        if (isCounting()) {
            if (successfullyChecked) setMapping(entity, COUNT_KEY, getCount(entity) + 1);
            if (hasCountText() && entity instanceof Player && successfullyChecked) {
                ((Player) entity).sendMessage(getCountText(entity));
            }
            successfullyChecked = getRequiredCount() <= getCount(entity);
        }
        if (isPersistant()) setMapping(entity, CHECKED_KEY, successfullyChecked);
        save();
        return successfullyChecked;
    }

    @Override
    public int compareTo(Requirement<T> other) {

        return Integer.compare(getOrder(), (other instanceof RequirementConfigWrapper ? ((RequirementConfigWrapper) other).getOrder() : 0));
    }

    @Override
    public void save() {

        if (!isPersistant()) return;
        if (getConfig().getRoot() instanceof ConfigurationBase) {
            BasePlugin plugin = ((ConfigurationBase) getConfig().getRoot()).getPlugin();
            EbeanServer database = RaidCraft.getDatabase(RaidCraftPlugin.class);
            for (Map.Entry<UUID, Map<String, String>> entry : mappings.entrySet()) {
                TPersistantRequirement dbEntry = database.find(TPersistantRequirement.class)
                        .where()
                        .eq("uuid", entry.getKey())
                        .eq("plugin", plugin.getName())
                        .eq("requirement", getId()).findUnique();
                if (dbEntry == null) {
                    dbEntry = new TPersistantRequirement();
                    dbEntry.setPlugin(plugin.getName());
                    dbEntry.setRequirement(getId());
                    dbEntry.setUuid(entry.getKey());
                    database.save(dbEntry);
                }
                Map<String, String> values = new HashMap<>(entry.getValue());
                List<TPersistantRequirementMapping> dbEntryMappings = dbEntry.getMappings();
                // update the existing values
                dbEntryMappings.forEach(mapping -> {
                    if (values.containsKey(mapping.getMappedKey())) {
                        mapping.setMappedValue(values.remove(mapping.getMappedKey()));
                    } else {
                        database.delete(mapping);
                    }
                });
                // add the remaining values
                for (Map.Entry<String, String> valueEntry : values.entrySet()) {
                    TPersistantRequirementMapping mapping = new TPersistantRequirementMapping();
                    mapping.setRequirement(dbEntry);
                    mapping.setMappedKey(valueEntry.getKey());
                    mapping.setMappedValue(valueEntry.getValue());
                    dbEntryMappings.add(mapping);
                }
                database.save(dbEntryMappings);
                dbEntry.setMappings(dbEntryMappings);
                database.update(dbEntry);
            }
        }
    }

    @Override
    public void load() {

        if (!isPersistant()) return;
        if (getConfig().getRoot() instanceof ConfigurationBase) {
            BasePlugin plugin = ((ConfigurationBase) getConfig().getRoot()).getPlugin();
            EbeanServer database = RaidCraft.getDatabase(RaidCraftPlugin.class);
            List<TPersistantRequirement> list = database.find(TPersistantRequirement.class)
                    .where()
                    .eq("plugin", plugin.getName())
                    .eq("requirement", getId()).findList();
            list.forEach(entry -> {
                if (!mappings.containsKey(entry.getUuid())) {
                    mappings.put(entry.getUuid(), new HashMap<>());
                }
                Map<String, String> map = mappings.get(entry.getUuid());
                entry.getMappings().forEach(mapEntry -> map.put(mapEntry.getMappedKey(), mapEntry.getMappedValue()));
            });
        }
    }

    @Override
    public void delete(T entity) {

        if (!isPersistant()) return;
        if (entity instanceof Player && getConfig().getRoot() instanceof ConfigurationBase) {
            BasePlugin plugin = ((ConfigurationBase) getConfig().getRoot()).getPlugin();
            EbeanServer database = RaidCraft.getDatabase(RaidCraftPlugin.class);
            List<TPersistantRequirement> list = database.find(TPersistantRequirement.class)
                    .where()
                    .eq("uuid", ((Player) entity).getUniqueId())
                    .eq("plugin", plugin.getName())
                    .eq("requirement", getId()).findList();
            database.delete(list);
        }
    }
}
