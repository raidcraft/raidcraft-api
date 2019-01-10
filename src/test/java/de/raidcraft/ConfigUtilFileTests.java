package de.raidcraft;

import de.raidcraft.util.ConfigUtil;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;

public class ConfigUtilFileTests {

    @Test
    public void testConfig() throws IOException, InvalidConfigurationException {

        YamlConfiguration config = new YamlConfiguration();
        config.load(new InputStreamReader(this.getClass().getResourceAsStream("/config/tests/example-config.yml")));
        ConfigUtil.replacePathReferences(config, "config.tests");

        YamlConfiguration expectedConfig = new YamlConfiguration();
        expectedConfig.load(new InputStreamReader(this.getClass().getResourceAsStream("/config/tests/example-config-replaced.yml")));

        Assert.assertEquals(expectedConfig.saveToString(), config.saveToString());
    }
}
