package de.raidcraft;

import de.raidcraft.api.random.RDSRandom;
import de.raidcraft.util.ConfigUtil;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@Data
@RunWith(Parameterized.class)
public class RDSRandomTests {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { 5, 10 },
                { -5, 5 },
                { -7, -2 },
                { 0, 1 },
                { -1, 0 }
        });
    }

    private final int min;
    private final int max;

    @Test
    public void testReplacePathReferences() {

        for (int i = 0; i < 1000; i++) {
            int result = RDSRandom.getIntNegativePositiveValue(min, max);
            Assert.assertTrue("Expected " + result + " to be >= " + min + " and <= " + max, result >= min && result <= max);
        }

    }
}
