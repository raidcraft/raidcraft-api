package de.raidcraft.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import org.bukkit.entity.Player;

import java.io.*;

/**
 * @author Philip
 */
public class BungeeCordUtil {

    public static void sendPluginMessage(Player player, String channel, String message) {

        sendPluginMessage(player, "ALL", channel, message);
    }

    public static void sendPluginMessage(Player player, String targetServer, String channel, String message) {

        try {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            DataOutputStream msgData = new DataOutputStream(bao);
            msgData.writeUTF("Forward");
            msgData.writeUTF(targetServer);	// Server
            msgData.writeUTF(channel);			// Channel
            msgData.writeShort(message.length()); 	// Data Length
            msgData.writeBytes(message); 			// Data
            player.sendPluginMessage(RaidCraft.getComponent(RaidCraftPlugin.class), "BungeeCord", bao.toByteArray());
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
    }

    public static void changeServer(Player player, String targetServer) {

        try {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            DataOutputStream msgData = new DataOutputStream(bao);
            msgData.writeUTF("Connect");
            msgData.writeUTF(targetServer);
            player.sendPluginMessage(RaidCraft.getComponent(RaidCraftPlugin.class), "BungeeCord", bao.toByteArray());
            bao.reset();
        } catch(IOException ex) {
            ex.printStackTrace();
            return;
        }
    }

    public static String decodeMessage(byte[] encoded, String channel) {

        String isChannel;
        byte[] data;
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(encoded));
            isChannel = in.readUTF();
            short len = in.readShort();
            data = new byte[len];
            in.readFully(data);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        if(!isChannel.equalsIgnoreCase(channel)) {
            return null;
        }

        String message = new String(data);
        return message;
    }
}
