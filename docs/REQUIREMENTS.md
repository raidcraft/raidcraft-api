# **R**equirements

Eine Liste aller Requirements lässt sich im Spiel mit dem Befehl `/actionapi` generieren.

## Tags

Als Gegenstück zu der [`!player.tag` Action](ACTIONS.md#tags) gibt es das Tag Requirement welches das Vorhandensein eines Tags prüft.
Default wird immer auch geprüft ob der Tag bereits abgelaufen ist, d.h. die `duration` erreicht wurde. Dieses Verhalten kann man mit dem Parameter `ignore-duration:true` umgehen.

### player.tag

```yml
requirements:
  flow:
    # Prüft ob der Spieler einen aktiven Tag foobar hat
    - '?player.tag foobar'
    # Prüft ob der Spieler den Tag foo hat, egal ob er bereits abgelaufen ist.
    - '?player.tag tag:foo ignore-duration:true'
```

Neu seit Version **2.0.0** ist, dass alle Tags automatisch gecounted werden. Den Count eines Tags kann man mit dem `count` Parameter abfragen.

Folgende Operatoren sind zum Vergleich eines Tags verfügbar:

- `>` größer als
- `>=` größer oder gleich als
- `<` kleiner als
- `<=` kleiner oder gleich als
- `=` gleich als

``````yml
requirements:
  flow:
    # Prüft ob der Spieler einen aktiven Tag foobar hat und ob der Tag mindestens 6 mal geschrieben wurde.
    - '?player.tag foobar >5'
    # Prüft ob der Spieler den Tag foo hat der weniger als oder gleich 5 mal getaggt wurde.
    - '?player.tag tag:foo count:<=5'
```