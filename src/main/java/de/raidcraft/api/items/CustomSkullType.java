package de.raidcraft.api.items;

import java.util.HashMap;

/**
 * @author Silthus
 */
public enum CustomSkullType {

    SPIDER("Kelevra_V"), // Thanks SethBling
    ENDERMAN("Violit"), // Thanks SethBling
    BLAZE("Blaze_Head"), // Thanks SethBling
    HORSE("gavertoso"), // Thanks Glompalici0us
    SQUID("squidette8"), // Thanks SethBling
    SILVERFISH("AlexVMiner"), // Thanks SethBling
    ENDER_DRAGON("KingEndermen", "KingEnderman"), // Thanks SethBling
    SLIME("HappyHappyMan", "Ex_PS3Zocker"), // Thanks SethBling
    IRON_GOLEM("zippie007"), // Thanks SethBling
    MUSHROOM_COW("MHF_MushroomCow", "Mooshroom_Stew"), // Thanks Marc Watson
    BAT("bozzobrain", "coolwhip101"), // Thanks incraftion.com
    PIG_ZOMBIE("ManBearPigZombie", "scraftbrothers5"), // Thanks cnaude of TrophyHeads
    SNOWMAN("Koebasti", "scraftbrothers2"), // Thanks MrLeikermoser
    GHAST("_QuBra_", "blaiden"), // Thanks MrLeikermoser
    PIG("XlexerX", "scrafbrothers7"), // Thanks XlexerX
    VILLAGER("Kuvase", "Villager", "scraftbrothers9"), // Thanks XlexerX
    SHEEP("SGT_KICYORASS", "Eagle_Peak"), // Thanks cowboys2317
    COW("VerifiedBernard", "CarlosTheCow"), // Thanks Jknies
    CHICKEN("scraftbrothers1"), // Thanks SuperCraftBrothers.com
    OCELOT("scraftbrothers3"), // Thanks SuperCraftBrothers.com
    WITCH("scrafbrothers4"), // Thanks SuperCraftBrothers.com
    MAGMA_CUBE("MHF_LavaSlime"), // Thanks Marc Watson
    WOLF("Budwolf"),
    CAVE_SPIDER("MHF_CaveSpider"); // Thanks Marc Watson

    private final String owner;

    private static class Holder {

        static HashMap<String, CustomSkullType> map = new HashMap<>();
    }

    CustomSkullType(String owner) {

        this.owner = owner;
        Holder.map.put(owner, this);
    }

    CustomSkullType(String owner, String... toConvert) {

        this(owner);
        for (String key : toConvert) {
            Holder.map.put(key, this);
        }
    }

    public String getOwner() {

        return owner;
    }

    public String getDisplayName() {

        return Skull.format(name());
    }

    public String getSpawnName() {

        return "HEAD_SPAWN_" + name();
    }

    public static CustomSkullType get(String owner) {

        return Holder.map.get(owner);
    }
}