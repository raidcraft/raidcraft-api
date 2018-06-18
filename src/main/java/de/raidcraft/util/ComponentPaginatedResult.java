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
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Commands that wish to display a paginated list of results can use this class to do
 * the actual pagination, giving a list of items, a page number, and basic formatting information.
 */
public abstract class ComponentPaginatedResult<T> {

    private final ComponentBuilder header;

    protected static final int PER_PAGE = 7;
    protected int count = 1;

    public ComponentPaginatedResult(ComponentBuilder header) {

        this.header = header;
    }

    public ComponentPaginatedResult(String header) {

        this.header = new ComponentBuilder(header).color(ChatColor.YELLOW);
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

        BaseComponent[] headerComponents = header.append(" (Seite ").color(ChatColor.YELLOW).append((page + 1) + "").color(ChatColor.AQUA)
                .append("/").color(ChatColor.YELLOW)
                .append(maxPages + "").color(ChatColor.AQUA)
                .append(")").color(ChatColor.YELLOW).create();
        sender.spigot().sendMessage(headerComponents);

        for (int i = PER_PAGE * page; i < PER_PAGE * page + PER_PAGE && i < results.size(); i++) {
            sender.spigot().sendMessage(format(results.get(i)));
            count++;
        }
    }

    public abstract BaseComponent[] format(T entry);

    public final int getCount() {

        return count;
    }

}
