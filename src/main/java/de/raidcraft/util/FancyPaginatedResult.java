/*
 * CommandBook
 * Copyright (C) 2011 sk89q <http://www.sk89q.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.raidcraft.util;

import com.sk89q.minecraft.util.commands.CommandException;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Commands that wish to display a paginated list of results can use this class to do
 * the actual pagination, giving a list of items, a page number, and basic formatting information.
 */
public abstract class FancyPaginatedResult<T> {

    private final FancyMessage header;

    protected static final int PER_PAGE = 7;
    protected int count = 1;

    public FancyPaginatedResult(FancyMessage header) {

        this.header = header;
    }

    public FancyPaginatedResult(String header) {

        this.header = new FancyMessage(header).color(ChatColor.YELLOW);
    }

    public void display(CommandSender sender, Collection<? extends T> results, int page) throws CommandException {

        display(sender, new ArrayList<>(results), page);
    }

    public void display(CommandSender sender, List<? extends T> results, int page) throws CommandException {

        if (results.size() == 0) throw new CommandException("Keine Einträge gefunden!");
        --page;

        int maxPages = (int) ((results.size() / (double) PER_PAGE) + 0.99);
        if (page < 0 || page > maxPages) {
            throw new CommandException(
                    "Unbekannte Seite selektiert! " + (maxPages) + " Seiten existieren.");
        }

        header.then(" (Seite ").color(ChatColor.YELLOW).then((page + 1) + "").color(ChatColor.AQUA)
                .then("/").color(ChatColor.YELLOW)
                .then(maxPages + "").color(ChatColor.AQUA)
                .then(")").color(ChatColor.YELLOW).send(sender);
        for (int i = PER_PAGE * page; i < PER_PAGE * page + PER_PAGE && i < results.size(); i++) {
            format(results.get(i)).send(sender);
            count++;
        }
    }

    public abstract FancyMessage format(T entry);

    public final int getCount() {

        return count;
    }

}
