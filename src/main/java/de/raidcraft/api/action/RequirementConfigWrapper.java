package de.raidcraft.api.action;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.ContextualRequirement;
import de.raidcraft.api.action.requirement.Reasonable;
import de.raidcraft.api.action.requirement.ReasonableRequirement;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.requirement.tables.TPersistantRequirement;
import de.raidcraft.api.action.requirement.tables.TPersistantRequirementMapping;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.util.ConfigUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
@EqualsAndHashCode(of = { "requirement" })
@Data
public class RequirementConfigWrapper<T> implements ReasonableRequirement<T>, Comparable<Requirement<T>> {

    public static <T> RequirementConfigWrapper<T> of(String id, Requirement<T> requirement, Class<T> tClass) {

        return new RequirementConfigWrapper<>(id, requirement, new MemoryConfiguration(), tClass);
    }

    private static final String CHECKED_KEY = "checked";
    private static final String COUNT_KEY = "count";

    private final String id;
    private final Class<T> type;
    private final Requirement<T> requirement;
    private final Reasonable<T> reasonable;
    private final boolean persistant;
    private final boolean negate;
    private final int order;
    private final int requiredCount;
    private final String countText;
    private final String description;
    private final boolean optional;
    private final ConfigurationSection config;
    private final List<Action<T>> successActions;
    private final List<Action<T>> failureActions;
    private final Map<UUID, Map<String, String>> mappings = new HashMap<>();

    @SuppressWarnings("unchecked")
    protected RequirementConfigWrapper(String id, Requirement<T> requirement, ConfigurationSection config,
            Class<T> type) {

        this.id = config.isSet("id") ? config.getString("id") : id;
        this.requirement = requirement;
        this.type = type;
        if (requirement instanceof Reasonable) {
            this.reasonable = (Reasonable<T>) requirement;
        } else {
            this.reasonable = null;
        }
        this.persistant = config.getBoolean("persistent", false);
        this.negate = config.getBoolean("negate", false);
        this.order = config.getInt("order", 0);
        this.requiredCount = config.getInt("count", 0);
        this.countText = config.getString("count-text");
        this.description = config.getString("description");
        this.optional = config.getBoolean("optional", false);
        this.successActions = ActionAPI.createActions(config.getConfigurationSection("success")).stream()
                .filter(action -> ActionAPI.matchesType(action, getType())).map(action -> (Action<T>) action)
                .collect(Collectors.toList());
        this.failureActions = ActionAPI.createActions(config.getConfigurationSection("failure")).stream()
                .filter(action -> ActionAPI.matchesType(action, getType())).map(action -> (Action<T>) action)
                .collect(Collectors.toList());
        this.config = config;
    }

    public ConfigurationSection getConfig() {

        ConfigurationSection args = config.getConfigurationSection("args");
        if (args == null)
            args = config.createSection("args");
        return args;
    }

    @Override
    public Requirement<T> with(String key, Object value) {

        config.set(key, value);
        return this;
    }

    @Override
    public Requirement<T> withArgs(String key, Object value) {

        getConfig().set(key, value);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addAction(Action<?> action) {

        if (ActionAPI.matchesType(action, getType())) {
            successActions.add((Action<T>) action);
        }
    }

    public int getIntMapping(T entity, String key) {

        String mapping = getMapping(entity, key);
        return mapping == null ? 0 : Integer.parseInt(mapping);
    }

    public boolean getBoolMapping(T entity, String key) {

        String mapping = getMapping(entity, key);
        return mapping != null && Boolean.parseBoolean(mapping);
    }

    public double getDoubleMapping(T entity, String key) {

        String mapping = getMapping(entity, key);
        return mapping == null ? 0.0 : Double.parseDouble(mapping);
    }

    public String getMapping(T entity, String key) {

        if (entity instanceof Player) {
            return mappings.getOrDefault(((Player) entity).getUniqueId(), new HashMap<>()).getOrDefault(key, null);
        }
        return null;
    }

    public void setMapping(T entity, String key, String value) {

        if (entity instanceof Player) {
            UUID uniqueId = ((Player) entity).getUniqueId();
            setMapping(uniqueId, key, value);
        }
    }

    public void setMapping(UUID uuid, String key, String value) {

        if (!mappings.containsKey(uuid)) {
            mappings.put(uuid, new HashMap<>());
        }
        mappings.get(uuid).put(key, value);
    }

    public boolean isChecked(T entity) {

        return getBoolMapping(entity, CHECKED_KEY);
    }

    public void setChecked(T entity, boolean checked) {

        setMapping(entity, CHECKED_KEY, checked);
    }

    public void setMapping(T entity, String key, int value) {

        setMapping(entity, key, Integer.toString(value));
    }

    public void setMapping(T entity, String key, boolean value) {

        setMapping(entity, key, Boolean.toString(value));
    }

    public boolean isMapped(T entity, String key) {

        return getMapping(entity, key) != null;
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

        return test(type, getConfig());
    }

    @Override
    public boolean test(T entity, ConfigurationSection config) {

        RaidCraftPlugin plugin = RaidCraft.getComponent(RaidCraftPlugin.class);
        boolean successfullyChecked = isChecked(entity);

        if (isPersistant() && isMapped(entity, CHECKED_KEY)) {
            if (plugin.getConfig().debugRequirements) {
                plugin.getLogger().info("PERSISTANT REQUIREMENT: " + ActionAPI.getIdentifier(getRequirement()) + " -> "
                        + successfullyChecked);
            }
            return successfullyChecked;
        }

        if (negate) {
            if (requirement instanceof ContextualRequirement) {
                successfullyChecked = !((ContextualRequirement<T>) requirement).test(entity, this, config);
            } else {
                successfullyChecked = !requirement.test(entity, config);
            }
        } else {
            if (requirement instanceof ContextualRequirement) {
                successfullyChecked = ((ContextualRequirement<T>) requirement).test(entity, this, config);
            } else {
                successfullyChecked = requirement.test(entity, config);
            }
        }

        if (isCounting()) {
            if (successfullyChecked)
                setMapping(entity, COUNT_KEY, getCount(entity) + 1);
            if (hasCountText() && entity instanceof Player && successfullyChecked) {
                ((Player) entity).sendMessage(getCountText(entity));
            }
            if (plugin.getConfig().debugRequirements) {
                plugin.getLogger().info("COUNTING REQUIREMENT: " + ActionAPI.getIdentifier(getRequirement()) + " -> "
                        + getCount(entity) + "/" + requiredCount);
            }
            successfullyChecked = getCount(entity) >= getRequiredCount();
        }
        if (isPersistant()) {
            if (isCounting()) {
                if (successfullyChecked)
                    setChecked(entity, true);
            } else {
                setChecked(entity, successfullyChecked);
            }
        }
        save();
        if (successfullyChecked) {
            if (plugin.getConfig().debugRequirements) {
                plugin.getLogger()
                        .info("EXECUTING REQUIREMENT SUCCESS ACTIONS: " + ActionAPI.getIdentifier(getRequirement()));
            }
            successActions.forEach(tAction -> tAction.accept(entity));
        } else {
            if (plugin.getConfig().debugRequirements) {
                plugin.getLogger()
                        .info("EXECUTING REQUIREMENT FAILURE ACTIONS: " + ActionAPI.getIdentifier(getRequirement()));
            }
            failureActions.forEach(tAction -> tAction.accept(entity));
        }
        return successfullyChecked;
    }

    @Override
    public String getReason(T entity) {

        return getReason(entity, getConfig());
    }

    @Override
    public String getReason(T entity, ConfigurationSection config) {

        if (reasonable != null) {
            return reasonable.getReason(entity, config);
        }
        return "Requirement has no defined reasons! " + ConfigUtil.getFileName(config);
    }

    @Override
    public Optional<String> getDescription(T entity) {

        return getDescription(entity, getConfig());
    }

    @Override
    public Optional<String> getDescription(T entity, ConfigurationSection config) {

        Optional<String> description = requirement.getDescription(entity, config);
        if (description.isPresent()) {
            return description;
        }
        return Optional.ofNullable(this.description);
    }

    @Override
    public int compareTo(Requirement<T> other) {

        return Integer.compare(getOrder(),
                (other instanceof RequirementConfigWrapper ? ((RequirementConfigWrapper) other).getOrder() : 0));
    }

    @Override
    public void save() {

        if (!isPersistant())
            return;
        if (getConfig().getRoot() instanceof ConfigurationBase) {
            BasePlugin plugin = ((ConfigurationBase) getConfig().getRoot()).getPlugin();
            EbeanServer database = RaidCraft.getDatabase(RaidCraftPlugin.class);
            for (Map.Entry<UUID, Map<String, String>> entry : mappings.entrySet()) {
                TPersistantRequirement dbEntry = database.find(TPersistantRequirement.class).where()
                        .eq("uuid", entry.getKey()).eq("plugin", plugin.getName()).eq("requirement", getId())
                        .findUnique();
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

        if (!isPersistant())
            return;
        if (getConfig().getRoot() instanceof ConfigurationBase) {
            BasePlugin plugin = ((ConfigurationBase) getConfig().getRoot()).getPlugin();
            EbeanServer database = RaidCraft.getDatabase(RaidCraftPlugin.class);
            List<TPersistantRequirement> list = database.find(TPersistantRequirement.class).where()
                    .eq("plugin", plugin.getName()).eq("requirement", getId()).findList();
            list.forEach(entry -> {
                for (TPersistantRequirementMapping mapping : entry.getMappings()) {
                    setMapping(entry.getUuid(), mapping.getMappedKey(), mapping.getMappedValue());
                }
            });
        }
    }

    @Override
    public void delete(T entity) {

        if (!isPersistant())
            return;
        if (entity instanceof Player && getConfig().getRoot() instanceof ConfigurationBase) {
            BasePlugin plugin = ((ConfigurationBase) getConfig().getRoot()).getPlugin();
            EbeanServer database = RaidCraft.getDatabase(RaidCraftPlugin.class);
            List<TPersistantRequirement> list = database.find(TPersistantRequirement.class).where()
                    .eq("uuid", ((Player) entity).getUniqueId()).eq("plugin", plugin.getName())
                    .eq("requirement", getId()).findList();
            database.delete(list);
        }
    }
}
