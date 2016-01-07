package de.raidcraft.api.hero;

import java.util.UUID;

/**
 * Created by Philip on 07.01.2016.
 */
public class DefaultHeroProvider implements HeroProvider {

    @Override
    public int getLevel(UUID uuid) {
        return 0;
    }

    @Override
    public String getClass(UUID uuid) {
        return "None";
    }

    @Override
    public int getSkillPoints(UUID uuid) {
        return 0;
    }

    @Override
    public int getEXP(UUID uuid) {
        return 0;
    }
}
