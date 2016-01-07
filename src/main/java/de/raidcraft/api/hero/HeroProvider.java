package de.raidcraft.api.hero;

import java.util.UUID;

/**
 * Created by Philip on 07.01.2016.
 */
public interface HeroProvider {

    int getLevel(UUID uuid);

    String getClass(UUID uuid);

    int getSkillPoints(UUID uuid);

    int getEXP(UUID uuid);
}
