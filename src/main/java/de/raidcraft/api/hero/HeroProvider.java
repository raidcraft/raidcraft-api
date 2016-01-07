package de.raidcraft.api.hero;

import java.util.UUID;

/**
 * Created by Philip on 07.01.2016.
 */
public interface HeroProvider {

    int getLevel(UUID uuid);

    String getClass(UUID uuid);

    String getSkillPoints(UUID uuid);

    String getEXP(UUID uuid);
}
