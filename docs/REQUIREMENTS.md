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