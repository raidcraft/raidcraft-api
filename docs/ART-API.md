# **A**ctions **R**equirements **T**rigger API

Die ART API bildet das Herzstück für die meisten Raid-Craft Plugins und ermöglicht es durch einheitliche Configs komplexe Aktionen auszuführen.
Jedes Plugin hat die Möglichkeit eigene [Actions](#actions), [Requirements](#requirements) und [Trigger](#trigger) zu definieren. Diese ART Objekte stehen dann allen anderen Plugins zur Verfügung und können in Configs genutzt werden.

> Eine Liste mit allen `ART` Configs lässt sich im Spiel über `/actionapi` generieren.

- [Actions](#actions)
    - [Entwicklung von Actions](#entwicklung-von-actions)
    - [Verwendung von Actions](#verwendung-von-actions)
        - [Action Parameter](#action-parameter)
- [Requirements](#requirements)
    - [Entwicklung von Requirements](#entwicklung-von-requirements)
    - [Verwendung von Requirements](#verwendung-von-requirements)
        - [Requirement Parameter](#requirement-parameter)
- [Trigger](#trigger)
    - [Entwicklung von Triggern](#entwicklung-von-triggern)
    - [Verwendung von Triggern](#verwendung-von-triggern)
        - [Trigger Parameter](#trigger-parameter)
- [Answers](#answers)
- [Alias Groups](#alias-groups)
- [Flow Syntax](#flow-syntax)
- [Referenzen](#referenzen)

## Actions

Actions sind, wie der Name sagt, Aktionen die durch die ART API ausgeführt werden können. Plugins können hierführ beliebige Aktionen durch kleine Code Schnippsel bereitstellen, z.B. das Teleportieren von einem Spieler oder die Vergabe von EXP im [eigenen Skill System](https://git.faldoria.de/tof/plugins/raidcraft/rcskills).

Einige Beispiele und Erklärung zu [Actions findet man hier](ACTIONS.md).

### Entwicklung von Actions

Eine Implementierung des `Action<T>` Interfaces und die Dekoration der `accept(T, ConfigurationSection)` Methode mit dem `@Information` Tag reicht aus um eine Action zu erstellen.

```java
public class TeleportPlayerAction implements Action<Player> {

    @Override
    @Information(
        value = "teleport.location",
        // Alle aliase werden zusätzlich registriert.
        aliases = {"player.teleport", "teleport"}
        desc = "Teleports the player to the given location.",
        // Die Reihenfolge der Config Parameter spielt eine wichtige Rolle für Quest Entwickler,
        // da diese in der Flow Syntax mit einer Komma Separierung angegeben werden können.
        conf = {
                "x",
                "y",
                "z",
                "world: [current]",
                "yaw",
                "pitch"
        }
    )
    public void accept(Player player, ConfigurationSection config) {

        // ConfigUtil.getLocationFromConfig parsed automatisch ein gültiges Location Objekt
        // aus der angegebenen Config Section.
        player.teleport(ConfigUtil.getLocationFromConfig(config, player));
    }
}

```

Die Action muss anschließend noch in der [RaidCraft API](https://git.faldoria.de/tof/plugins/raidcraft/raidcraft-api) registriert werden. Dabei wird der Action ID der Name des Plugins vorrausgesetzt.

Die Action `teleport.location` wird also z.B. zu `meinplugin.teleport.location`.

```java
ActionAPI.action(new TeleportPlayerAction());
```

Um eine Action ohne das Plugin Prefix zu registrieren muss nur die `global()` Methode vor der Registrierung aufgerufen werden.

```java
ActionAPI.global().action(new TeleportPlayerAction());
```

### Verwendung von Actions

Die `teleport.location` Action kann nun in Plugins die Configs mit Action Blöcken laden verwendet werden. Ein Beispiel dafür wäre das [Quest Plugin](https://git.faldoria.de/raidcraft/quests).

Im folgenden Beispiel wird der Spieler nach Abschluss der Quest durch Verwendung der Teleport Action zum Spawn teleportiert. In diesem Beispiel wird die empfohlene [Flow Syntax](#flow-syntax) verwendet. Das <kbd>!</kbd> sagt dem Parser, dass es sich um eine `Action` handelt.

```yml
complete-actions:
  flow:
    - '!teleport.location 0,64,0,world'
```

Alternativ könnte man auch die Parameter ausschreiben, damit man später noch den Überblick hat welcher Parameter was bedeutet. In dem Beispiel wurde der `world` Parameter weggelassen um den Spieler immer an den Spawn in der aktuellen Welt zu teleportieren.

Durch die Verwendung der deklarativen Parameter ist es außerdem möglich bestimmte Parameter zu überspringen und nur die wichtigen anzugeben. In diesem Fall wurde `yaw` und `world` nicht angegeben.

```yml
complete-actions:
  flow:
    - '!teleport.location x:0 y:64 z:0 pitch:90'
```

#### Action Parameter

Neben den Parametern für die Action an sich, können der Action auch Parameter mitgeteilt werden, die den Ablauf der Action beeinflussen. Dabei werden die Parameter direkt nach dem Name der Action in `( )` Klammern geschrieben.

| Parameter    | Beschreibung                                              | Beispiel                        |
| ------------ | --------------------------------------------------------- | ------------------------------- |
| delay        | Führt die Action nach der angegebenen Zeit aus.           | `!teleport.location(delay:10s)` |
| cooldown     | Die Action wird erst wieder nach dem Cooldown ausgeführt. | `!player.kill(cooldown:1d)`     |
| execute-once | Die Action wird nur einmal ausgeführt.                    | `!text(execute-once:true)`      |

> Mehrere "Globale Parameter" können Komma seperiert angegeben werden.

```yml
complete-actions:
  flow:
    - '!teleport.location(execute-once:true,delay:3s) x:0 y:64 z:0 pitch:90'
```

## Requirements

Requirements sind Vorrausetzungen für [Actions](#actions) und [Trigger](#trigger) um deren Ausführung zu steuern. Mehrere Requirements hintereinander werden dabei automatisch in einer `UND` Verknüpfung zusammengefügt. Zum Beispiel kann man mit Requirements prüfen ob der Spieler bereits eine bestimmte [Quest](https://git.faldoria.de/tof/plugin-configs/quests) abgeschlossen oder das gewünschte Level erreicht hat.

Einige Beispiele inklusive Erklärungen zu wichtigen [Requirements gibt es hier](REQUIREMENTS.md).

### Entwicklung von Requirements

Eine Implementierung des `Requirement<T>` Interfaces und die Dekoration der `test(T, ConfigurationSection)` Methode mit dem `@Information` Tag reicht aus um ein Requirement zu erstellen.

```java
public class ObjectiveCompletedRequirement implements Requirement<Player> {

    @Override
    @Information(
            value = "objective.completed",
            desc = "Tests if the player has completed the given objective of the given quest.",
            conf = {
                    "quest: <quest id>",
                    "objective: <objective id>"
            }
    )
    public boolean test(Player player, ConfigurationSection config) {

        QuestHolder questHolder = RaidCraft.getComponent(QuestManager.class).getQuestHolder(player);
        if (questHolder == null) return false;
        Optional<Quest> quest = questHolder.getQuest(config.getString("quest"));
        if (!quest.isPresent()) return false;
        return quest.get().isObjectiveCompleted(config.getInt("objective"));
    }
}
```

Zusätzlich zu den normalen Requirements gibt es auch die Möglichkeit Requirements mit einem Ablehnungsgrund zu erstellen. Der Grund wird jedesmal wenn das Requirements geprüft wird angezeigt. Dazu einfach das `ReasonableRequirement<T>` Interface implementieren und zusätzlich zur `test(T, ConfigurationSection)` die `getReason(T, ConfigurationSection)` Methode implementieren.

```java
public class LevelRequirement implements ReasonableRequirement<Player> {

    @Override
    @Information(
            value = "hero.level",
            desc = "Checks if the level of the hero/profession/skill is greater,equals or lower than the given level.",
            conf = {
                    "level: 1",
                    "modifier: [gt/<ge>/eq/le/lt]",
                    "type: <hero/skill/profession>",
                    "[skill]",
                    "[profession]"
            }
    )
    public boolean test(Player player, ConfigurationSection config) {

        // omited for simplicity
    }

    @Override
    public String getReason(Player player, ConfigurationSection config) {

        Hero hero = RaidCraft.getComponent(CharacterManager.class).getHero(player);
        String type = config.getString("type", "hero");
        int requiredLevel = config.getInt("level", 1);
        switch (type) {
            case "skill":
                try {
                    Skill skill;
                    if (config.isSet("profession")) {
                        Profession profession = hero.getProfession(config.getString("profession"));
                        skill = profession.getSkill(config.getString("skill"));
                    } else {
                        skill = hero.getSkill(config.getString("skill"));
                    }
                    if (skill instanceof Levelable) {
                        return skill.getFriendlyName() + " auf Level " + requiredLevel + " benötigt.";
                    } else {
                        return "Der Skill " + skill.getFriendlyName() + " ist nicht Levelbar.";
                    }
                } catch (UnknownSkillException | UnknownProfessionException e) {
                    return "Der Skill " + config.getString("skill") + " exisistiert nicht!";
                }
            case "profession":
                try {
                    return hero.getProfession(config.getString("profession")).getFriendlyName() + " Spezialisierung auf Level " + requiredLevel + " benötigt.";
                } catch (UnknownSkillException | UnknownProfessionException e) {
                    return "Die Spezialisierung " + config.getString("profession") + " existiert nicht.";
                }
            case "hero":
            default:
                return "Level " + requiredLevel + " benötigt.";
        }
    }
}
```

Das Requirement muss anschließend noch in der [RaidCraft API](https://git.faldoria.de/tof/plugins/raidcraft/raidcraft-api) registriert werden. Dabei wird der Requirement ID der Name des Plugins vorrausgesetzt.

Das Requirement `objective.completed` wird also z.B. zu `rcquests.objective.completed`.

```java
ActionAPI.requirement(new ObjectiveCompletedRequirement());
```

Um ein Requirement ohne das Plugin Prefix zu registrieren muss nur die `global()` Methode vor der Registrierung aufgerufen werden.

```java
ActionAPI.global().requirement(new LevelRequirement());
```

### Verwendung von Requirements

Um zum Beispiel das Ausführen des [obrigen Beispiels](#verwendung-von-actions) auf ein bestimmtes Level zu beschränken, kann ein Requirement verwendet werden.

Jedes Requirement wird in der [Flow Syntax](#flow-syntax) mit einem <kbd>?</kbd> Präfix definiert.

```yml
complete-actions:
  flow:
    # Prüft ob der Spieler mindestens Level 10 erreicht hat.
    - '?hero.level 10'
    # Die Teleport Action wird nur ausgeführt wenn das Requirement zutrifft.
    - '!teleport.location 0,64,0,world'
```

Mehrere Requirements hintereinander werden automatisch mit `UND` verknüpft und müssen alle erfüllt sein, damit die nachfolgende Action ausgeführt wird.

```yml
complete-actions:
  flow:
    # Prüft ob der Spieler mindestens Level 10 erreicht hat.
    - '?hero.level 10'
    # UND ob der Spieler die aktive Quest "foobar" hat
    - '?quest.active foobar'
    # Die Teleport Action wird nur ausgeführt wenn die beiden Requirements zutreffen.
    - '!teleport.location 0,64,0,world'
```

#### Requirement Parameter

Es gibt die Möglichkeit in Requirements direkt nach dem Name des Requirements globale Parameter anzuhängen, diese Parameter steuern wie das Requirement geprüft wird.

| Parameter  | Beschreibung                                                                                                                                      | Beispiel                                                                   |
| ---------- | ------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------- |
| persistant | Speichert die erste Evaluierung des Requirements in der Datenbank. Alle folgenden Evaluierung nutzen das Ergebnis aus der Datenbank.              | `?player.location(persistant:true)`                                        |
| negate     | Invertiert das Ergebnis des Requirements                                                                                                          | `?hero.level(negate:true)`                                                 |
| count      | Zählt die positiven Evaluierungen des Requirements. Das Requirement ist erst `true` wenn `count` erreicht wird.                                   | `?rcmobs.mob.kill(count:5)`                                                |
| count-text | Der Text wird jedesmal angezeigt wenn das Requirement erfolgreich evaluiert wurde. Es werden die Variablen `%current%` und `%count%` unterstützt. | `?rcmobs.mob.kill(count:5,count-text:"%current%/%count% Schafe getötet.")` |
| optional   | Nur eins der Requirements muss erfüllt sein.                                                                                                      | `?player.ghost(optional:true)`                                             |

> Wie bei den [Action Parametern](#action-parameter) können mehrere Parameter mit Komma separiert angegeben werden.

## Trigger

Trigger sind die Auslöser von [Actions](#actions). Durch sie werden die meisten Logik Abfragen initialisiert. Trigger filtern außerdem bereits im eigenen Rahmen einige Dinge vor. 

Einige Beispiele zu wichtigen Triggern inkl. Erklärungen [gibt es hier](TRIGGER.md).

### Entwicklung von Triggern

Trigger unterscheiden sich in der Entwicklung von [Requirements](#entwicklung-von-requirements) und [Actions](#entwicklung-von-actions). Trigger müssen kein Interface implementieren sondern der abstrakten Klasse `Trigger` extenden und dort beim Auslösen des Triggers `informListeners(String action, T triggeringEntity, Predicate<ConfigurationSection> predicate)` aufrufen.

> Im super Constructor des Triggers muss erst die Trigger Base und dann alle in der Klasse enthaltenen Sub-Stichwörter mitgegeben werden.

```java
    public GlobalPlayerTrigger() {
        // Durch den super call registriert die Klasse folgende Trigger:
        // * player.interact
        // * player.block.break
        // ...
        super("player", "interact", "block.break", "block.place", "move", "craft", "death", "join");
    }
```

Die Trigger Klasse kann z.B. dazu verwendet werden um auf Bukkit Events zu hören und diese dann als Trigger weiterzuleiten. Auch hier muss jede Methode mit einem `@Information` dekoriert werden.

```java
public class GlobalPlayerTrigger extends Trigger implements Listener {

    public GlobalPlayerTrigger() {

        super("player", "interact", "block.break", "block.place", "move", "craft", "death", "join");
    }

    @Information(
            value = "player.interact",
            desc = "Listens for player interaction (with certain blocks at the defined location).",
            conf = {
                    "x",
                    "y",
                    "z",
                    "world",
                    "type: DIRT"
            }
    )
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {

        informListeners("interact", event.getPlayer(), config -> {

            Block block = event.getClickedBlock();
            if (config.isSet("x") && config.isSet("y") && config.isSet("z")) {
                Location blockLocation = block.getLocation();
                World world = config.isSet("world") ? Bukkit.getWorld(config.getString("world")) : event.getPlayer().getWorld();
                if (blockLocation.getBlockX() != config.getInt("x")
                        || blockLocation.getBlockY() != config.getInt("y")
                        || blockLocation.getBlockZ() != config.getInt("z")
                        || !blockLocation.getWorld().equals(world)) {
                    return false;
                }
            }
            Set<Material> blocks = new HashSet<>();
            if (config.isList("blocks")) {
                config.getStringList("blocks").forEach(b -> {
                    Material material = Material.matchMaterial(b);
                    if (material != null) {
                        blocks.add(material);
                    } else {
                        RaidCraft.LOGGER.warning("Wrong block defined in player.interact trigger! " + ConfigUtil.getFileName(config));
                    }
                });
                return blocks.contains(block.getType());
            }
            return !config.isSet("block") || Material.matchMaterial(config.getString("block", "AIR")) == block.getType();
        });
    }

    @Information(
            value = "player.craft",
            desc = "Listens for crafting of items (can be custom).",
            conf = {
                    "item: <rc1337/so43034/world.quest.named-item/WOOD:5>"
            }
    )
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCraft(CraftItemEvent event) {

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        informListeners("craft", event.getWhoClicked(), config -> {
            try {
                return !config.isSet("item") || RaidCraft.getSafeItem(config.getString("item")).isSimilar(event.getRecipe().getResult());
            } catch (CustomItemException e) {
                return false;
            }
        });
    }

    @Information(
            value = "player.death",
            desc = "Triggered when the player died."
    )
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {

        informListeners("death", event.getEntity());
    }
}
```

### Verwendung von Triggern

Trigger ermöglichen die Reaktion auf Aktionen der Spieler oder Umwelt. Zum Beispiel kann darauf reagiert werden wenn ein Spieler einen Mob tötet oder sich zu einer bestimmten Stelle bewegt. Dabei können Trigger je nach Programmierung selber bereits einige Plausi Checks durchführen.

> Ein `@player.move` Trigger muss z.B. nicht jedesmal ein `?player.location` [Requirement](#requirements) besitzen, sondern der kann selber bereits mit Parametern die Location filtern: `@player.move x:5 y:10 z:20 radius:10`.

Jeder Trigger wird in der [Flow Syntax](#flow-syntax) mit einem <kbd>@</kbd> Präfix definiert.

```yml
start-trigger:
  flow:
    # Dieser Trigger wird ausgelöst wenn der Spieler den NPC Bob anklickt.
    - '@host.interact this.bob'
    # Anschließend kann man beliebige Actions ausführen.
    # In diesem Fall wird nach einem Klick auf Bob die Unterhaltung foobar gestartet.
    - '!conversation.start conv:this.foobar host:this.bob'
```

Trigger können auch Requirements besitzen die dann jedesmal wenn der Trigger ausgelöst wird abgefragt werden. Das ist z.B. sehr nützlich um Sammel und Töten Quests umzusetzen.

```yml
trigger:
  flow:
    # Dieser Trigger wird jedesmal ausgelöst wenn der Spieler am Töten eines Goblin beteiligt ist.
    '@rcmobs.mob.kill this.goblin'
    # Das Requirement wird jedesmal abgefragt wenn der Trigger ausgelöst wurde und zählt den Count hoch.
    # Dabei wird der angegbene Text ausgegeben.
    '?rcmobs.mob.kill(count:5,count-text:"%current% von %count% Goblins getötet.")'
    # Wenn fünf Goblins getötet wurden wird das objective 2 in der Quest goblin-jaeger abgeschlossen
    '!rcquests.objective.complete this.goblin-jaeger 2'
```

#### Trigger Parameter

Wie auch [Actions](#action-parameter) und [Requirements](#requirement-parameter) haben Trigger spezielle globale Parameter die das Ausführen des Triggers beinflussen.

| Parameter    | Beschreibung                                                        | Beispiel                                       |
| ------------ | ------------------------------------------------------------------- | ---------------------------------------------- |
| execute-once | Der Trigger wird nur einmal ausgeführt.                             | `@player.location(execute-once:true)`          |
| cooldown     | Der Trigger wird erst nach Ablauf des `cooldown` wieder ausgeführt. | `@player.death(cooldown:2d)`                   |
| delay        | Die Prüfung des Triggers wird erst nach dem `delay` durchgeführt.   | `@item.pickup(delay:2s)`                       |
| action-delay | Actions werden erst nach dem `action-delay` ausgeführt.             | `@objective.started(action-delay:10s)`         |
| worlds       | Welten in denen der Trigger aktiv ist.                              | `@player.join(worlds:["world","world_nether"])` |
| count        | Erst wenn der `count` des Triggers erreicht wurde löst der Trigger aus. Das stellt eine Kurzform des dummy(count:XX) requirements dar. | `@rcmobs.mob.kill(count:5)` |
| count-text   | Das Gegenstück zu `count` ermöglicht es bei jeder Erhöhung des `count` den Text auszugeben. Siehe das `dummy` Requirement für Details. | `@rcmobs.mob.kill(count:5,count-text:"%current% von %count% Mobs getötet.")`|

## Answers

Eine spezielle Action in der `ART` API sind Antworten, welche in [Conversations](https://git.faldoria.de/tof/plugins/raidcraft/conversations) verwendet werden. Diese Antworten sind für den Spieler anklickbar und bieten die Möglichkeit in Gesprächen zu interagieren.

Jede Antwort wird in der [Flow Syntax](#flow-syntax) mit einem <kbd>:</kbd> Präfix definiert.

```yml
stages:
    start:
        actions:
            flow:
                # Text den der NPC vor der Ausgabe der Antworten sagt.
                - '!text "Hallo mein Freund. Was kann ich für dich tun?"'
                # Antwortmöglichkeit 1
                # Wenn nach der Antwort keine Actions stehen wird die Unterhaltung automatisch beendet.
                - ':"Nichts, danke dir!"'
                # Antwortmöglichkeit 2
                - ':"Ich werde dich töten!."'
                # Alle Actions unterhalb einer Antwort werden nacheinander ausgeführt.
                - '!text "Das denke ich eher nicht..."'
                # Die player.kill Action wird erst nach 2s ausgeführt.
                - '~2s'
                - '!player.kill'
                # Erst wenn die nächste Antwort beginnt werden keine weitere Actions ausgeführt.
                # Antwortmöglichkeit 3
                - ':"Hast Du eine Quest für mich?"'
                # Action die den Spieler in eine neue Conversation Stage bringt.
                - '!stage quest-blubbi'
                # Antworten können auch abhängig von Requirements eingeblendet werden.
                - '?hero.level 5'
                # Antwortmöglichkeit 4
                - ':"Töte den Drachen!"'
                - '!quest.start this.der-drache'
```

> Antworten besitzen keine globalen Parameter.

## Alias Groups

Zusätzlich zu der normalen Auflistung von [Actions](#actions), [Requirements](#requirements) und [Triggern](#triggers) unterhalb des `flow` Blocks, gibt es die Möglichkeit Alias Gruppen für ein oder mehere Statements zu erstellen. Diese Gruppen können (aktuell) nur innerhalb des jeweiligen Blocks benutzt werden. Dies ist z.B. nützlich um verschiedene Actions zu gruppieren und dann auf einmal unterhalb eines Requirements auszuführen.

> Ein Alias hat immer Vorrang und wird, wenn der Name gleich ist, anstatt der registrierten ID verwendet.

```yml
# Die unten definierten Aliase gelten nur innerhalb dieses Blocks
actions:
    # Alle aliase müssen unterhalb der groups Sektion definiert werden.
    groups:
        # Der Key gibt immer den Alias an und muss innerhalb des Blocks einzigartig sein
        'held_und_geld':
            # Unterhalb eines Alias können mehrere Actions oder Requirements gruppiert werden.
            # Dabei zählen die gleichen Regeln wie in den normalen Flow Statements
            - '?player.money.has 1g2s'
            - '?rcskills.hero.level 5'
        # Auch einzelne Aliase sind möglich, z.B. nützlich für Coordinaten
        'held_tot': '!player.kill'
        # Soll ein Action Alias mit vorrausgestelltem Requirement verwendet werden,
        # MUSS vor dem Alias der Typ angegeben werden, also ein ! oder ? je nachdem.
        '!held_tot_text':
            - '?player.location 1,2,3'
            # Innrhalb der Gruppen können auch Delays verwendet werden
            - '~2s'
            - '!text "Du bist tot..."'
    flow:
        # Die Alias Actions und Requirements können wie normale ART Statements verwendet werden.
        - '?held_und_geld'
        - '!held_tot_text'
        - '?held_und_geld(negate:true)'
        - '!held_tot'
```

## Flow Syntax

Die Flow Syntax ist eine spezielle Art und Weise ART Konfigurationen zu schreiben und ermöglicht einen übersichtlichen Ablauf. Jedes Modul hat in der Flow Syntax ein eigenes Präfix Symbol und die Möglichkeit durch Klammern nach dem Name globale Parameter entgegen zu nehmen.

> Alle oben genannten Beispiele sind in der Flow Syntax verfasst.

| Präfix       | Typ                          |
| :----------: | ---------------------------- |
| <kbd>!</kbd> | [Action](#actions)           |
| <kbd>?</kbd> | [Requirement](#requirements) |
| <kbd>@</kbd> | [Trigger](#trigger)          |
| <kbd>:</kbd> | [Answer](#answers)           |
| <kbd>~</kbd> | Delay, z.B. `~2s`            |

## Referenzen

Die Action API wird in den folgenden Plugins verwendet.

* [Conversations](https://git.faldoria.de/tof/plugins/raidcraft/conversations)
* [Achievements](https://git.faldoria.de/raidcraft/achievements)
* [Quests](https://git.faldoria.de/tof/plugins/raidcraft/rcquests)
* [Tips](https://git.faldoria.de/raidcraft/rctips)
* [Skills](https://git.faldoria.de/tof/plugins/raidcraft/rcskills)