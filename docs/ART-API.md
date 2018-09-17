# **A**ctions **R**equirements **T**rigger API

Die ART API bildet das Herzstück für die meisten Raid-Craft Plugins und ermöglicht es durch einheitliche Configs komplexe Aktionen auszuführen.
Jedes Plugin hat die Möglichkeit eigene [Actions](#actions), [Requirements](#requirements) und [Trigger](#trigger) zu definieren. Diese ART Objekte stehen dann allen anderen Plugins zur Verfügung und können in Configs genutzt werden.

> Eine Liste mit allen `ART` Configs lässt sich im Spiel über `/actionapi` generieren.

- [Actions](#actions)
    - [Entwicklung von Actions](#entwicklung-von-actions)
    - [Verwendung von Actions](#verwendung-von-actions)
- [Requirements](#requirements)
    - [Entwicklung von Requirements](#entwicklung-von-requirements)
    - [Verwendung von Requirements](#verwendung-von-requirements)

## Actions

Actions sind, wie der Name sagt, Aktionen die durch die ART API ausgeführt werden können. Plugins können hierführ beliebige Aktionen durch kleine Code Schnippsel bereitstellen, z.B. das Teleportieren von einem Spieler oder die Vergabe von EXP im [eigenen Skill System](https://git.faldoria.de/raidcraft/rcskills).

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

Die Action muss anschließend noch in der [RaidCraft API](https://git.faldoria.de/raidcraft/raidcraft-api) registriert werden. Dabei wird der Action ID der Name des Plugins vorrausgesetzt.

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
    - '!teleport.location 0,64,0,world'
```

Alternativ könnte man auch die Parameter ausschreiben, damit man später noch den Überblick hat welcher Parameter was bedeutet. In dem Beispiel wurde der `world` Parameter weggelassen um den Spieler immer an den Spawn in der aktuellen Welt zu teleportieren.

Durch die Verwendung der deklarativen Parameter ist es außerdem möglich bestimmte Parameter zu überspringen und nur die wichtigen anzugeben. In diesem Fall wurde `yaw` und `world` nicht angegeben.

```yml
complete-actions:
    - '!teleport.location x:0 y:64 z:0 pitch:90'
```

## Requirements

Requirements sind Vorrausetzungen für [Actions](#actions) und [Trigger](#trigger) um deren Ausführung zu steuern. Mehrere Requirements hintereinander werden dabei automatisch in einer `UND` Verknüpfung zusammengefügt. Zum Beispiel kann man mit Requirements prüfen ob der Spieler bereits eine bestimmte [Quest](https://git.faldoria.de/plugin-configs/quests) abgeschlossen oder das gewünschte Level erreicht hat.

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

Das Requirement muss anschließend noch in der [RaidCraft API](https://git.faldoria.de/raidcraft/raidcraft-api) registriert werden. Dabei wird der Requirement ID der Name des Plugins vorrausgesetzt.

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
    # Prüft ob der Spieler mindestens Level 10 erreicht hat.
    - '?hero.level 10'
    # Die Teleport Action wird nur ausgeführt wenn das Requirement zutrifft.
    - '!teleport.location 0,64,0,world'
```

Mehrere Requirements hintereinander werden automatisch mit `UND` verknüpft und müssen alle erfüllt sein, damit die nachfolgende Action ausgeführt wird.

```yml
complete-actions:
    # Prüft ob der Spieler mindestens Level 10 erreicht hat.
    - '?hero.level 10'
    # UND ob der Spieler die aktive Quest "foobar" hat
    - '?quest.active foobar'
    # Die Teleport Action wird nur ausgeführt wenn die beiden Requirements zutreffen.
    - '!teleport.location 0,64,0,world'
```