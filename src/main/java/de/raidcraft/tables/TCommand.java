package de.raidcraft.tables;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author Dragonfire
 */
@Entity
@Table(name = "rc_commands")
public class TCommand {

    @Getter
    @Setter
    @Id
    private int id;
    @Getter
    @Setter
    private String host;
    @Getter
    @Setter
    private String base;
    @Getter
    @Setter
    private String aliases;
    @Getter
    @Setter
    private String description;
    @Getter
    @Setter
    private String usage_; // sql reservered word
    @Getter
    @Setter
    private int min;
    @Getter
    @Setter
    private int max;
    @Getter
    @Setter
    private String flags;
    @Getter
    @Setter
    private String help_; // sql reservered word
    @Getter
    @Setter
    private String permission;

    public static TCommand parseCommand(Method method, String host, String base) {

        TCommand cmd = new TCommand();
        if (base != null) {
            cmd.setBase(base);
        }
        if (host != null) {
            cmd.setHost(host);
        }
        Command anno_cmd = method.getAnnotation(Command.class);
        if (anno_cmd != null) {
            cmd.setAliases(printArray(anno_cmd.aliases()));
            cmd.setDescription(anno_cmd.desc());
            cmd.setUsage_(anno_cmd.usage());
            cmd.setMin(anno_cmd.min());
            cmd.setMax(anno_cmd.max());
            cmd.setHelp_(anno_cmd.help());
        }
        CommandPermissions anno_perm = method.getAnnotation(CommandPermissions.class);
        if (anno_perm != null) {
            cmd.setPermission(printArray(anno_perm.value()));
        }
        return cmd;
    }

    public static String printArray(String[] stringArray) {

        if (stringArray.length <= 0) {
            return null;
        }
        if (stringArray.length == 1) {
            return stringArray[0];
        }
        return Arrays.toString(stringArray);
    }
}
