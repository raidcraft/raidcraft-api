# **A**ctions **R**equirements **T**rigger API

Die ART API bildet das Herzstück für die meisten Raid-Craft Plugins und ermöglicht es durch einheitliche Configs komplexe Aktionen auszuführen.
Jedes Plugin hat die Möglichkeit eigene [Actions](#actions), [Requirements](#requirements) und [Trigger](#trigger) zu definieren. Diese ART Objekte stehen dann allen anderen Plugins zur Verfügung und können in Configs genutzt werden.

- [Actions](#actions)
    - [Entwicklung von Actions](#entwicklung-von-actions)
    - [Verwendung von Actions](#verwendung-von-actions)
        - [Action Beispiele](#action-beispiele)
            - [Vergabe von Items](#vergabe-von-items)
            - [Conversation Actions](#conversation-actions)
            - [Timer](#timer)

## Actions

Actions sind, wie der Name sagt, Aktionen die durch die ART API ausgeführt werden können. Plugins können hierführ beliebige Aktionen durch kleine Code Schnippsel bereitstellen, z.B. das Teleportieren von einem Spieler oder die Vergabe von EXP im [eigenen Skill System](https://git.faldoria.de/raidcraft/rcskills).

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

#### Action Beispiele

##### Vergabe von Items

Items die nicht ins Inventar passen werden vor dem Spieler gedroppt.

```yml
complete-actions:
    # Gibt dem Spieler das Custom Item 3x mit der ID 1337
    - '!player.give.item item:rc1337 amount:3'
    # Gibt dem Spieler das konfigurierte Item 1x im selben Order
    # mit dem Dateinamen custom-quest-item.item.yml
    - '!player.give.item this.custom-quest-item'
    # Gibt dem Spieler einen Stack Birkenholz
    - '!player.give.item WOOD:5,64'
```

##### Conversation Actions

Einer der am häufigsten verwendeten Actions in [Quests](https://git.faldoria.de/plugin-configs/quests) sind [Conversation](https://git.faldoria.de/raidcraft/conversations) Actions.

###### conversation.start Action

Startet die angegebene Unterhaltung, optional mit einem Host und einer Stage.

```yml
actions:
    # Startet die Unterhaltung mit der Config im selben Ordner und dem Dateinamen
    # conv-template.conv.yml. Default wird die Stage 'start' gestartet.
    - '!conversation.start this.conv-template'
    # Startet die Unterhaltung aus einem Überordner mit dem Namen conv-template.conv.yml
    # Als Host wird der NPC mit der Config im gleichen Ordner
    # und dem Dateinamen host.host.yml verwendet.
    - '!conversation.start ../conv-template this.host'
    # Analog der anderen, mit dem Unterschied dass direkt die Stage 'stage1' gestartet wird.
    - '!conversation.start conv:this.conv-template host:this.host stage:stage1'
```

##### Timer

Um komplexe Abläufe, z.B. für [Achievements](https://git.faldoria.de/raidcraft/achievements), zu realisieren wurden Timer eingeführt. Jeder Timer ist immer an den Spieler gebunden und kann im `default` (läuft ab) oder `interval` (läuft ab und tickt) Mode ausgeführt werden.

###### timer.start Action

Als erstes muss der Timer mit der `timer.start` Action gestartet werden. Dabei vergibt man dem Timer eine eindeutige ID. Diese ID wird später referenziert um den Timer zu stoppen oder zu verlängern.

```yml
actions:
    # Startet einen Countdown von 1 Minute 30 Sekunden 10 Ticks
    - '!timer.start id:mein-achievement duration:1m30s10
```

Wenn der Timer abläuft wird der Trigger [`timer.end`](#timer-end-trigger) ausgeführt.

###### timer.add Action

Mit der `timer.add` Action hat man die Möglichkeit Zeit zu einem Laufenden Timer hinzuzufügen. Dabei wird die ursprüngliche Endzeit nach hinten verschoben. Ein Timer von `1m30s` hätte nach einem Aufruf von `!timer.add id:123 time:30s` eine Gesamtzeit von `2m`.

Wenn man `timer.add` mit dem Parameter `temporary:true` aufruft wird die Zeit nur der aktuellen Zeit hinzugefügt. Ein Timer von `1m30s` ist aktuell bei `51s`. Ein Aufruf von `!timer.add id:123 time:30s temporary:true` würde den Timer nach `1m21s` verschieben und die gleiche Endzeit von `1m30s` beibehalten.

```yml
actions:
    # Verlängert den Timer um 30s
    - '!timer.add id:mein-achievement time:30s'
    # "Verkürzt" den Timer um 30s, da die fortgeschrittene Zeit erhöht wird,
    # ohne die Endzeit zu verändern.
    - '!timer.add id:mein-achievement time:30s temporary:true'
```

###### timer.end Action

Um einen Timer frühzeitig zu beenden kann man die `timer.end` Action aufrufen. Dabei wird der Timer normal beendet und auch der [`timer.end`](#timer-end-trigger) ausgeführt.

```yml
actions:
    # Bricht den Countdown mit der ID mein-achievement ab.
    - '!timer.end id:mein-achievement
```

Wenn ein Timer beendet wird, wird der Trigger [`timer.end`](#timer-end-trigger) aufgerufen.

###### timer.cancel Action

Es gibt die Möglichkeit laufende Timer durch die `timer.cancel` Action abzubrechen. Wenn ein Timer gecancelled wird, wird kein [`timer.end`](#timer-end-trigger) Trigger aufgerufen.

```yml
actions:
    # Bricht den Countdown mit der ID mein-achievement ab.
    - '!timer.cancel id:mein-achievement
```

Wenn ein Timer abgebrochen wird, wird der Trigger [`timer.cancel`](#timer-cancel-trigger) aufgerufen.

###### timer.reset Action

Die `timer.reset` Action startet den ausgewählten Timer neu.

```yml
actions:
    # Bricht den Countdown mit der ID mein-achievement ab und startet ihn neu.
    - '!timer.reset id:mein-achievement
```

Bei einem Reset wird der [`timer.cancel`](#timer-cancel-trigger) Trigger aufgerufen.