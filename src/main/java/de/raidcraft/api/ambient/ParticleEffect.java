package de.raidcraft.api.ambient;

import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class ParticleEffect {


    public static void sendToPlayer(Particle particle, Player player, Location location, float offsetX, float offsetY, float offsetZ, int count) {

        WrapperPlayServerWorldParticles packet = createPacket(WrappedParticle.create(particle, particle), location, offsetX, offsetY, offsetZ, count);
        packet.sendPacket(player);
    }

    public static void sendToLocation(Particle particle, Location location, float offsetX, float offsetY, float offsetZ, int count) {

        WrapperPlayServerWorldParticles packet = createPacket(WrappedParticle.create(particle, particle), location, offsetX, offsetY, offsetZ, count);
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

    public static WrapperPlayServerWorldParticles createPacket(WrappedParticle effect, Location location, float offsetX, float offsetY, float offsetZ, int count) {

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
        WrappedParticle<Float> particle = WrappedParticle.create(icon ? Particle.ITEM_CRACK : Particle.BLOCK_CRACK, data);
        WrapperPlayServerWorldParticles wrapper = createPacket(particle, location, offsetX, offsetY, offsetZ, count);
        wrapper.setParticleData(data);
        return wrapper;
    }
}