package de.raidcraft.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Silthus
 */
public class SimpleScoreboard {

    public interface UpdateScore {

        String update();
    }

    private Scoreboard scoreboard;

    private String title;
    private Map<String, UpdateScore> scores;
    private Map<UpdateScore, Map.Entry<Team, Score>> updaters;
    private List<Team> teams;

    public SimpleScoreboard(String title) {

        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.title = title;
        this.scores = Maps.newLinkedHashMap();
        this.teams = Lists.newArrayList();
        this.updaters = Maps.newHashMap();
    }

    public SimpleScoreboard blankLine() {

        add(" ");
        return this;
    }

    public SimpleScoreboard add(String text) {

        return add(text, null);
    }

    public SimpleScoreboard add(String text, UpdateScore updateScore) {

        Preconditions.checkArgument(text.length() < 48, "text cannot be over 48 characters in length");
        text = fixDuplicates(text);
        scores.put(text, updateScore);
        return this;
    }

    private String fixDuplicates(String text) {

        while (scores.containsKey(text))
            text += "§r";
        if (text.length() > 48)
            text = text.substring(0, 47);
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
        teams.add(team);
        return new AbstractMap.SimpleEntry<>(team, result);
    }

    public void update() {

        updaters.forEach((updateScore, teamStringEntry) -> {
            String text = updateScore.update();
            Iterator<String> iterator = Splitter.fixedLength(16).split(text).iterator();
            Team team = teamStringEntry.getKey();
            team.setPrefix(iterator.next());
            String result = iterator.next();
            if (text.length() > 32)
                team.setSuffix(iterator.next());
            if (!teamStringEntry.getValue().getEntry().equals(result)) {
                teamStringEntry.getValue().
            }
        });
    }

    public void build() {

        Objective obj = scoreboard.registerNewObjective((title.length() > 16 ? title.substring(0, 15) : title), "dummy");
        obj.setDisplayName(title);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        int index = scores.size();

        for (Map.Entry<String, UpdateScore> entry : scores.entrySet()) {
            Map.Entry<Team, String> team = createTeam(entry.getKey());
            OfflinePlayer player = Bukkit.getOfflinePlayer(team.getValue());
            if (team.getKey() != null) {
                team.getKey().addPlayer(player);
            }
            Score score = obj.getScore(player.getName());
            score.setScore(index);
            if (entry.getValue() != null) {
                updaters.put(entry.getValue(),
            }
            index -= 1;
        }

        Objective objective = scoreboard.registerNewObjective("health", "health");
        objective.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "❤");
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
    }

    public SimpleScoreboard reset() {

        title = null;
        scores.clear();
        teams.forEach(org.bukkit.scoreboard.Team::unregister);
        teams.clear();
        return this;
    }

    public Scoreboard getScoreboard() {

        return scoreboard;
    }

    public void send(Player... players) {

        for (Player player : players)
            player.setScoreboard(scoreboard);
    }

}