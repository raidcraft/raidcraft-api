package de.raidcraft.util;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.server.v1_6_R3.ContainerMerchant;
import net.minecraft.server.v1_6_R3.EntityPlayer;
import net.minecraft.server.v1_6_R3.IMerchant;
import net.minecraft.server.v1_6_R3.MerchantRecipe;
import net.minecraft.server.v1_6_R3.MerchantRecipeList;
import net.minecraft.server.v1_6_R3.NBTTagCompound;
import net.minecraft.server.v1_6_R3.NBTTagList;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AttributeHider {

    private Field cC;

    public AttributeHider(JavaPlugin plugin) {

        try {
            cC = EntityPlayer.class.getDeclaredField("containerCounter");
            cC.setAccessible(true);
        } catch (NoSuchFieldException e) {
            return;
        }
        Set<Integer> packets = new HashSet<Integer>();
        packets.add(0x67);
        packets.add(0x68);
        packets.add(0x6B);
        packets.add(0xFA);
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ConnectionSide.SERVER_SIDE, ListenerPriority.NORMAL, packets) {
            @Override
            public void onPacketSending(PacketEvent event) {

                PacketContainer packet = event.getPacket();
                switch (packet.getID()) {
                    case 0x68:
                        try {
                            ItemStack[] read = packet.getItemArrayModifier().read(0);
                            for (int i = 0; i < read.length; i++) {
                                read[i] = removeAttributes(read[i]);
                            }
                            packet.getItemArrayModifier().write(0, read);
                        } catch (Exception e) {
                            Logger.getLogger(AttributeHider.class.getName()).log(Level.SEVERE, null, e);
                        }
                        break;
                    case 0xFA:
                        if (!packet.getStrings().read(0).equalsIgnoreCase("MC|TrList")) {
                            break;
                        }
                        try {
                            EntityPlayer p = ((CraftPlayer) event.getPlayer()).getHandle();
                            ContainerMerchant cM = ((ContainerMerchant) p.activeContainer);
                            Field fieldMerchant = cM.getClass().getDeclaredField("merchant");
                            fieldMerchant.setAccessible(true);
                            IMerchant imerchant = (IMerchant) fieldMerchant.get(cM);

                            MerchantRecipeList merchantrecipelist = imerchant.getOffers(p);
                            MerchantRecipeList nlist = new MerchantRecipeList();
                            for (Object orecipe : merchantrecipelist) {
                                MerchantRecipe recipe = (MerchantRecipe) orecipe;
                                int uses = recipe.i().getInt("uses");
                                int maxUses = recipe.i().getInt("maxUses");
                                MerchantRecipe nrecipe = new MerchantRecipe(removeAttributes(recipe.getBuyItem1()), removeAttributes(recipe.getBuyItem2()), removeAttributes(recipe.getBuyItem3()));
                                nrecipe.a(maxUses - 7);
                                for (int i = 0; i < uses; i++) {
                                    nrecipe.f();
                                }
                                nlist.add(nrecipe);
                            }

                            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                            DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
                            dataoutputstream.writeInt(cC.getInt(p));
                            nlist.a(dataoutputstream);
                            byte[] b = bytearrayoutputstream.toByteArray();
                            packet.getByteArrays().write(0, b);
                            packet.getIntegers().write(0, b.length);
                        } catch (Exception e) {
                            Logger.getLogger(AttributeHider.class.getName()).log(Level.SEVERE, null, e);
                        }
                        break;
                    default:
                        try {
                            packet.getItemModifier().write(0, removeAttributes(packet.getItemModifier().read(0)));
                        } catch (Exception e) {
                            Logger.getLogger(AttributeHider.class.getName()).log(Level.SEVERE, null, e);
                        }

                }
            }

        });
    }

    public static ItemStack removeAttributes(ItemStack item) {

        if (item == null) {
            return item;
        }
        net.minecraft.server.v1_6_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        } else {
            tag = nmsStack.getTag();
        }
        NBTTagList am = new NBTTagList();
        tag.set("AttributeModifiers", am);
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }

    public static net.minecraft.server.v1_6_R3.ItemStack removeAttributes(net.minecraft.server.v1_6_R3.ItemStack item) {

        if (item == null) {
            return item;
        }
        NBTTagCompound tag;
        if (!item.hasTag()) {
            tag = new NBTTagCompound();
            item.setTag(tag);
        } else {
            tag = item.getTag();
        }
        NBTTagList am = new NBTTagList();
        tag.set("AttributeModifiers", am);
        item.setTag(tag);
        return item;
    }

}