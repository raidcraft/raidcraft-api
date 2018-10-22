package de.raidcraft;

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
public class ConfUtilTests {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "../2-der-schmied.der-schmied", "ankanor.nebenquest.3-sandflohplage", "ankanor.nebenquest.2-der-schmied.der-schmied" },
                { "../2-der-schmied/der-schmied", "ankanor.nebenquest.3-sandflohplage", "ankanor.nebenquest.2-der-schmied.der-schmied" },
                { "../../2-der-schmied/der-schmied", "ankanor.nebenquest.3-sandflohplage.foobar", "ankanor.nebenquest.2-der-schmied.der-schmied" },
                { "../der-schmied", "ankanor.nebenquest.sandflohplage", "ankanor.nebenquest.der-schmied" },
                { "this.der-schmied", "ankanor.nebenquest", "ankanor.nebenquest.der-schmied"},
                { "this.foo/bar", "blubbi.blubb", "blubbi.blubb.foo.bar" },
                { "this.foo", "", "foo" }
        });
    }

    private final String input;
    private final String basePath;
    private final String expected;

    @Test
    public void testReplacePathReferences() {

        Assert.assertEquals(expected, ConfigUtil.replacePathReference(input, basePath));
    }
}
