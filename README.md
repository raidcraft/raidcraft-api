[![pipeline status](https://git.faldoria.de/tof/plugins/raidcraft/raidcraft-api/badges/master/pipeline.svg)](https://git.faldoria.de/tof/plugins/raidcraft/raidcraft-api/commits/master)

# RaidCraft API

Die RaidCraft API ist das Kernstück für alle RaidCraft Plugins. In ihr befinden sich grundlegende Methoden und APIs für den Zugriff auf Minecraft Schnittstellen.

** CURRENT MIGRATION: 1.27 on RCLoot **

## Getting Started

Die `RaidCraft API` einfach in den [Maven](https://maven.apache.org/) dependencies referenzieren und loslegen.

```xml
<dependency>
    <groupId>de.faldoria</groupId>
    <artifactId>raidcraft-api</artifactId>
    <version>1.12-0.1-SNAPSHOT</version>
</dependency>
```

### Prerequisites

Um alle Funktionen der API optimal nutzen zu können sollte in IntelliJ das [Lombok Plugin](https://plugins.jetbrains.com/plugin/6317-lombok-plugin) installiert werden.

## Module

Die meisten APIs können direkt über die statische `RaidCraft` Klasse referenziert werden.
Folgende APIs stehen dabei zentral im `RaidCraft API` Plugin zur Verfügung.

* [**A**ctions **R**equirements **T**rigger API](docs/ART-API.md)
* [Custom Items](https://git.faldoria.de/tof/plugins/raidcraft/rcitems)

