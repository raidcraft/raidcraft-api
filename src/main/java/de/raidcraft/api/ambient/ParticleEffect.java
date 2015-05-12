package de.raidcraft.api.ambient;

import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ParticleEffect {


    public static void sendToPlayer(EnumWrappers.Particle effect, Player player, Location location, float offsetX, float offsetY, float offsetZ, int count) {

        WrapperPlayServerWorldParticles packet = createPacket(effect, location, offsetX, offsetY, offsetZ, count);
        packet.sendPacket(player);
    }

    public static void sendToLocation(EnumWrappers.Particle effect, Location location, float offsetX, float offsetY, float offsetZ, int count) {

        WrapperPlayServerWorldParticles packet = createPacket(effect, location, offsetX, offsetY, offsetZ, count);
        Bukkit.getOnlinePlayers().forEach(packet::sendPacket);
    }

    public static void sendCrackToPlayer(boolean icon, int id, byte data, Player player, Location location, float offsetX, float offsetY, float offsetZ, int count) {

        WrapperPlayServerWorldParticles packet = createCrackPacket(icon, id, data, location, offsetX, offsetY, offsetZ, count);
        packet.sendPacket(player);
    }

    public static void sendCrackToLocation(boolean icon, int id, byte data, Location location, float offsetX, float offsetY, float offsetZ, int count) {

        WrapperPlayServerWorldParticles packet = createCrackPacket(icon, id, data, location, offsetX, offsetY, offsetZ, count);
        Bukkit.getOnlinePlayers().forEach(packet::sendPacket);
    }

    public static WrapperPlayServerWorldParticles createPacket(EnumWrappers.Particle effect, Location location, float offsetX, float offsetY, float offsetZ, int count) {

        if (count <= 0) {
            count = 1;
        }
        WrapperPlayServerWorldParticles wrapper = new WrapperPlayServerWorldParticles();
        wrapper.setParticleType(effect);
        wrapper.setX((float) location.getX());
        wrapper.setY((float) location.getY());
        wrapper.setZ((float) location.getZ());
        wrapper.setOffsetX(offsetX);
        wrapper.setOffsetY(offsetY);
        wrapper.setOffsetZ(offsetZ);
        wrapper.setNumberOfParticles(count);
        return wrapper;
    }

    public static WrapperPlayServerWorldParticles createCrackPacket(boolean icon, int id, float data, Location location, float offsetX, float offsetY, float offsetZ, int count) {

        if (count <= 0) {
            count = 1;
        }
        WrapperPlayServerWorldParticles wrapper = createPacket(icon ? EnumWrappers.Particle.ITEM_CRACK : EnumWrappers.Particle.BLOCK_CRACK, location, offsetX, offsetY, offsetZ, count);
        int[] ids = {id};
        wrapper.setData(ids);
        wrapper.setParticleData(data);
        return wrapper;
    }
}