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
@EqualsAndHashCode(of = {"requirement", "config"})
@Data
class RequirementConfigWrapper<T> implements Requirement<T>, Comparable<Requirement<T>> {

    private static final String CHECKED_KEY = "checked";
    private static final String COUNT_KEY = "count";

    private final Requirement<T> requirement;
    private final ConfigurationSection config;
    private final Map<UUID, Map<String, Object>> mappings = new HashMap<>();

    protected RequirementConfigWrapper(Requirement<T> requirement, ConfigurationSection config) {

        this.requirement = requirement;
        this.config = config;
    }

    public ConfigurationSection getConfig() {

        return this.config;
    }

    private Object getMapping(T entity, String key) {

        if (entity instanceof Player) {
            return mappings.getOrDefault(((Player) entity).getUniqueId(), new HashMap<>()).getOrDefault(key, null);
        }
        return null;
    }

    private void setMapping(T entity, String key, Object value) {

        if (entity instanceof Player) {
            UUID uniqueId = ((Player) entity).getUniqueId();
            if (!mappings.containsKey(uniqueId)) {
                mappings.put(uniqueId, new HashMap<>());
            }
            mappings.get(uniqueId).put(key, value);
        }
    }

    public boolean isChecked(T entity) {

        Object checked = getMapping(entity, CHECKED_KEY);
        return checked != null && (boolean) checked;
    }

    public boolean isPersistant() {

        return getConfig().getBoolean("persistant", false);
    }

    public boolean isOrdered() {

        return getOrder() > 0;
    }

    public int getOrder() {

        return getConfig().getInt("order", 0);
    }

    public boolean isCounting() {

        return getRequiredCount() > 1;
    }

    public int getRequiredCount() {

        return getConfig().getInt("count", 0);
    }

    public int getCount(T entity) {

        Object count = getMapping(entity, COUNT_KEY);
        if (count == null) return 0;
        return (int) count;
    }

    public boolean hasCountText() {

        return getConfig().isSet("count-text");
    }

    public String getCountText(T entity) {

        String string = getConfig().getString("count-text", "%current%/%count%");
        string = ConfigUtil.replaceCount(getPath(), string, getCount(entity), getRequiredCount());
        return string;
    }

    public boolean isOptional() {

        return getConfig().getBoolean("optional", false);
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

        if (successfullyChecked) setMapping(entity, COUNT_KEY, getCount(entity) + 1);

        setMapping(entity, CHECKED_KEY, successfullyChecked);

        if (isCounting()) {
            if (hasCountText() && entity instanceof Player) {
                ((Player) entity).sendMessage(getCountText(entity));
            }
            return getRequiredCount() <= getCount(entity);
        }
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
            String file = ((ConfigurationBase) getConfig().getRoot()).getFile().getAbsoluteFile().getName();
            EbeanServer database = RaidCraft.getDatabase(RaidCraftPlugin.class);
            mappings.entrySet().stream().forEach(entry -> {

                TPersistantRequirement dbEntry = database.find(TPersistantRequirement.class)
                        .where()
                        .eq("uuid", entry.getKey())
                        .eq("plugin", plugin.getName())
                        .eq("requirement", file).findUnique();
                if (dbEntry == null) {
                    dbEntry = new TPersistantRequirement();
                    dbEntry.setPlugin(plugin.getName());
                    dbEntry.setRequirement(file);
                    dbEntry.setUuid(entry.getKey());
                    database.save(dbEntry);
                }
                Map<String, Object> values = new HashMap<>(entry.getValue());
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
                values.entrySet().forEach(valueEntry -> {
                    TPersistantRequirementMapping mapping = new TPersistantRequirementMapping();
                    mapping.setMappedKey(valueEntry.getKey());
                    mapping.setMappedValue(valueEntry.getValue());
                    dbEntryMappings.add(mapping);
                });
                database.update(dbEntry);
            });
        }
    }

    @Override
    public void load() {

        if (!isPersistant()) return;
        if (getConfig().getRoot() instanceof ConfigurationBase) {
            BasePlugin plugin = ((ConfigurationBase) getConfig().getRoot()).getPlugin();
            String file = ((ConfigurationBase) getConfig().getRoot()).getFile().getAbsoluteFile().getName();
            EbeanServer database = RaidCraft.getDatabase(RaidCraftPlugin.class);
            List<TPersistantRequirement> list = database.find(TPersistantRequirement.class)
                    .where()
                    .eq("plugin", plugin.getName())
                    .eq("requirement", file).findList();
            list.forEach(entry -> {
                if (!mappings.containsKey(entry.getUuid())) {
                    mappings.put(entry.getUuid(), new HashMap<>());
                }
                Map<String, Object> map = mappings.get(entry.getUuid());
                entry.getMappings().forEach(mapEntry -> map.put(mapEntry.getMappedKey(), mapEntry.getMappedValue()));
            });
        }
    }

    @Override
    public void delete(T entity) {

        if (!isPersistant()) return;
        if (entity instanceof Player && getConfig().getRoot() instanceof ConfigurationBase) {
            BasePlugin plugin = ((ConfigurationBase) getConfig().getRoot()).getPlugin();
            String file = ((ConfigurationBase) getConfig().getRoot()).getFile().getAbsoluteFile().getName();
            EbeanServer database = RaidCraft.getDatabase(RaidCraftPlugin.class);
            List<TPersistantRequirement> list = database.find(TPersistantRequirement.class)
                    .where()
                    .eq("uuid", ((Player) entity).getUniqueId())
                    .eq("plugin", plugin.getName())
                    .eq("requirement", file).findList();
            database.delete(list);
        }
    }
}
