# **A**ctions

Eine Liste aller Actions lässt sich im Spiel mit dem Befehl `/actionapi` generieren.

- [Vergabe von Items](#vergabe-von-items)
    - [player.give.item](#playergiveitem)
- [Timer](#timer)
    - [timer.start](#timerstart)
    - [timer.add](#timeradd)
    - [timer.end](#timerend)
    - [timer.cancel](#timercancel)
    - [timer.reset](#timerreset)

## Vergabe von Items

Items die nicht ins Inventar passen werden vor dem Spieler gedroppt.

### player.give.item

Die `player.give.item` Action ermöglicht es sowohl Custom als auch normale Items zu verteilen.

```yml
complete-actions:
  flow:
    # Gibt dem Spieler das Custom Item 3x mit der ID 1337
    - '!player.give.item item:rc1337 amount:3'
    # Gibt dem Spieler das konfigurierte Item 1x im selben Order
    # mit dem Dateinamen custom-quest-item.item.yml
    - '!player.give.item this.custom-quest-item'
    # Gibt dem Spieler einen Stack Birkenholz
    - '!player.give.item WOOD:5,64'
```

## Timer

Um komplexe Abläufe, z.B. für [Achievements](https://git.faldoria.de/raidcraft/achievements), zu realisieren wurden Timer eingeführt. Jeder Timer ist immer an den Spieler gebunden und kann im `default` (läuft ab) oder `interval` (läuft ab und tickt) Mode ausgeführt werden.

### timer.start

Als erstes muss der Timer mit der `timer.start` Action gestartet werden. Dabei vergibt man dem Timer eine eindeutige ID. Diese ID wird später referenziert um den Timer zu stoppen oder zu verlängern.

```yml
actions:
  flow:
    # Startet einen Countdown von 1 Minute 30 Sekunden 10 Ticks
    - '!timer.start id:mein-achievement duration:1m30s10
```

Wenn der Timer abläuft wird der Trigger [`timer.end`](#timer-end-trigger) ausgeführt.

### timer.add

Mit der `timer.add` Action hat man die Möglichkeit Zeit zu einem Laufenden Timer hinzuzufügen. Dabei wird die ursprüngliche Endzeit nach hinten verschoben. Ein Timer von `1m30s` hätte nach einem Aufruf von `!timer.add id:123 time:30s` eine Gesamtzeit von `2m`.

Wenn man `timer.add` mit dem Parameter `temporary:true` aufruft wird die Zeit nur der aktuellen Zeit hinzugefügt. Ein Timer von `1m30s` ist aktuell bei `51s`. Ein Aufruf von `!timer.add id:123 time:30s temporary:true` würde den Timer nach `1m21s` verschieben und die gleiche Endzeit von `1m30s` beibehalten.

```yml
actions:
  flow:
    # Verlängert den Timer um 30s
    - '!timer.add id:mein-achievement time:30s'
    # "Verkürzt" den Timer um 30s, da die fortgeschrittene Zeit erhöht wird,
    # ohne die Endzeit zu verändern.
    - '!timer.add id:mein-achievement time:30s temporary:true'
```

### timer.end

Um einen Timer frühzeitig zu beenden kann man die `timer.end` Action aufrufen. Dabei wird der Timer normal beendet und auch der [`timer.end`](#timer-end-trigger) ausgeführt.

```yml
actions:
  flow:
    # Bricht den Countdown mit der ID mein-achievement ab.
    - '!timer.end id:mein-achievement
```

Wenn ein Timer beendet wird, wird der Trigger [`timer.end`](#timer-end-trigger) aufgerufen.

### timer.cancel

Es gibt die Möglichkeit laufende Timer durch die `timer.cancel` Action abzubrechen. Wenn ein Timer gecancelled wird, wird kein [`timer.end`](#timer-end-trigger) Trigger aufgerufen.

```yml
actions:
  flow:
    # Bricht den Countdown mit der ID mein-achievement ab.
    - '!timer.cancel id:mein-achievement
```

Wenn ein Timer abgebrochen wird, wird der Trigger [`timer.cancel`](#timer-cancel-trigger) aufgerufen.

### timer.reset

Die `timer.reset` Action startet den ausgewählten Timer neu.

```yml
actions:
  flow:
    # Bricht den Countdown mit der ID mein-achievement ab und startet ihn neu.
    - '!timer.reset id:mein-achievement
```

Bei einem Reset wird der [`timer.cancel`](#timer-cancel-trigger) Trigger aufgerufen.