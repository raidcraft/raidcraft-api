# **A**ctions **R**equirements **T**rigger API

Die ART API bildet das Herzstück für die meisten Raid-Craft Plugins und ermöglicht es durch einheitliche Configs komplexe Aktionen auszuführen.
Jedes Plugin hat die Möglichkeit eigene [Actions](#actions), [Requirements](#requirements) und [Trigger](#trigger) zu definieren. Diese ART Objekte stehen dann allen anderen Plugins zur Verfügung und können in Configs genutzt werden.

> Eine Liste mit allen `ART` Configs lässt sich im Spiel über `/actionapi` generieren.

- [Actions](#actions)
    - [Entwicklung von Actions](#entwicklung-von-actions)
    - [Verwendung von Actions](#verwendung-von-actions)
- [Requirements](#requirements)

## Actions

Actions sind, wie der Name sagt, Aktionen die durch die ART API ausgeführt werden können. Plugins können hierführ beliebige Aktionen durch kleine Code Schnippsel bereitstellen, z.B. das Teleportieren von einem Spieler oder die Vergabe von EXP im [eigenen Skill System](https://git.faldoria.de/raidcraft/rcskills).

Einige Beispiele und Erklärung zu [Actions findet man hier](ACTIONS.md).

### Entwicklung von Actions

Eine Implementierung des `Action<T>` Interfaces und die Dekoration der `accept(Player, ConfigurationSection)` Methode mit dem `@Information` Tag reicht aus um eine Action zu erstellen.

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

