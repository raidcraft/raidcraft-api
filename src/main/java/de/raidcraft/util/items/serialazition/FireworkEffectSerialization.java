package de.raidcraft.util.items.serialazition;

import de.raidcraft.RaidCraft;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Philip
 */
@Deprecated
public class FireworkEffectSerialization extends SimpleSerialization {

    public FireworkEffectSerialization(ItemStack item) {

        super(item);
    }

    /*
     * Firework conversation methods
     *
     * FireworkEffects structure:
     * Power=EffectName:Flicker:Trail:Color1,Color2,...:FadeColor1,FadeColor2,...|EffectName2:...
     */
    @Override
    public String serialize() {

        FireworkMeta fireworkMeta = (FireworkMeta) getItem().getItemMeta();
        if (fireworkMeta.getEffects().size() == 0) return "";

        String fireworkEffects = fireworkMeta.getPower() + "=";
        for (FireworkEffect effect : fireworkMeta.getEffects()) {
            fireworkEffects += effect.getType().name() + ":";
            fireworkEffects += ((effect.hasFlicker()) ? "1" : "0") + ":";
            fireworkEffects += ((effect.hasTrail()) ? "1" : "0") + ":";
            for (Color color : effect.getColors()) {
                fireworkEffects += color.asRGB() + ",";
            }
            fireworkEffects += ":";
            for (Color color : effect.getFadeColors()) {
                fireworkEffects += color.asRGB() + ",";
            }
            fireworkEffects += "|";
        }
        return fireworkEffects;
    }

    @Override
    public ItemStack deserialize(String serializedData) {

        try {
            FireworkMeta fireworkMeta = (FireworkMeta) getItem().getItemMeta();
            String[] powerPair = serializedData.split("=");

            fireworkMeta.setPower(Integer.valueOf(powerPair[0]));

            String[] effects = powerPair[1].split("\\|");

            for (String effect : effects) {
                String[] effectParameter = effect.split(":");
                if (effectParameter.length < 4) continue;

                boolean flicker = (effectParameter[1] == "1") ? true : false;
                boolean trail = (effectParameter[2] == "1") ? true : false;

                String[] colorString = effectParameter[3].split(",");
                List<Color> colors = new ArrayList<>();
                for (String color : colorString) {
                    if (color.length() <= 0) continue;
                    colors.add(Color.fromRGB(Integer.valueOf(color)));
                }

                List<Color> fadeColors = new ArrayList<>();
                if (effectParameter.length > 4) {
                    String[] fadeColorString = effectParameter[4].split(",");
                    for (String color : fadeColorString) {
                        if (color.length() <= 0) continue;
                        fadeColors.add(Color.fromRGB(Integer.valueOf(color)));
                    }
                }

                FireworkEffect fireworkEffect = FireworkEffect.builder()
                        .with(FireworkEffect.Type.valueOf(effectParameter[0]))
                        .flicker(flicker)
                        .trail(trail)
                        .withColor(colors)
                        .withFade(fadeColors)
                        .build();

                fireworkMeta.addEffect(fireworkEffect);
            }
            getItem().setItemMeta(fireworkMeta);
        } catch (Exception e) {
            RaidCraft.LOGGER.warning("Can't deserialize FireworkEffect ItemData");
            e.printStackTrace();
        }

        return getItem();
    }
}
