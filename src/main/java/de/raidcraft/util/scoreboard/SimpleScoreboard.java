package de.raidcraft.util.scoreboard;

import com.google.common.base.Splitter;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleScoreboard {

    private Player player;
    private Scoreboard scoreboard;
    private Map<Integer, String> scores = new LinkedHashMap<>();
    private String displayName;
    private Objective objective;
    private int highestIndex = 100;
    private int lowestIndex = highestIndex;
    @Getter
    private int currentIndex;

    public SimpleScoreboard(Player player, String displayName) {

        this.player = player;
        this.displayName = displayName;

        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("scoreboard", "dummy");

        objective.setDisplayName(this.displayName);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public SimpleScoreboard append(String text) {

        return line(lowestIndex - 1, text);
    }

    public SimpleScoreboard appendBlankLine() {

        return blankLine(lowestIndex - 1);
    }

    public SimpleScoreboard prepend(String text) {

        return line(highestIndex + 1, text);
    }

    public SimpleScoreboard prependBlankLine() {

        return blankLine(highestIndex + 1);
    }

    public SimpleScoreboard line(int line, String text) {

        if (line > highestIndex) highestIndex = line;
        if (line < lowestIndex) lowestIndex = line;
        currentIndex = line;
        scores.put(line, fixText(text));
        return this;
    }

    public SimpleScoreboard blankLine(int line) {

        return line(line, " ");
    }

    private String fixText(String text) {

        for (int i : scores.keySet()) {
            String s = scores.get(i);
            if (s.equalsIgnoreCase(text)) {
                text += "§r";
            }
        }
        if (text.length() > 48) {
            text = text.substring(0, 47);
        }
        return text;
    }

    private Map.Entry<Team, String> createTeam(String text) {

        String result;
        if (text.length() <= 16)
            return new AbstractMap.SimpleEntry<>(null, text);
        Team team = scoreboard.registerNewTeam("text-" + scoreboard.getTeams().size());
        Iterator<String> iterator = Splitter.fixedLength(16).split(text).iterator();
        team.setPrefix(iterator.next());
        result = iterator.next();
        if (text.length() > 32)
            team.setSuffix(iterator.next());
        return new AbstractMap.SimpleEntry<>(team, result);
    }

    public SimpleScoreboard build() {

        for (int i : scores.keySet()) {
            String text = scores.get(i);
            Map.Entry<Team, String> team = createTeam(text);
            String value = team.getValue();
            if (team.getKey() != null) {
                team.getKey().addEntry(value);
            }
            objective.getScore(value).setScore(i);
        }
        return this;
    }

    public SimpleScoreboard send() {

        player.setScoreboard(scoreboard);
        return this;
    }

    public SimpleScoreboard update() {

        if (player.getScoreboard() != null) {
            for (String s : player.getScoreboard().getEntries()) {
                player.getScoreboard().resetScores(s);
            }

            for (int i : scores.keySet()) {
                String text = scores.get(i);
                Map.Entry<Team, String> team = createTeam(text);
                String value = team.getValue();
                if (team.getKey() != null) {
                    team.getKey().addEntry(value);
                }
                objective.getScore(value).setScore(i);
                player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(value).setScore(i);
            }
        }
        return this;
    }

    public SimpleScoreboard resetScores() {

        if (player.getScoreboard() != null) {
            for (String s : player.getScoreboard().getEntries()) {
                player.getScoreboard().resetScores(s);
            }
        }
        scoreboard.getEntries().forEach(scoreboard::resetScores);
        return this;
    }

    public SimpleScoreboard clearScores() {

        scores.clear();
        return this;
    }

    public String getDisplayName() {

        return displayName;
    }

    public SimpleScoreboard setDisplayName(String displayName) {

        this.displayName = displayName;
        return this;
    }

    public Scoreboard getScoreboard() {

        return scoreboard;
    }
}