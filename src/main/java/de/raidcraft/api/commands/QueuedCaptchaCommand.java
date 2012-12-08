package de.raidcraft.api.commands;

import de.raidcraft.api.player.RCPlayer;
import org.bukkit.ChatColor;

import java.lang.reflect.Method;
import java.util.Random;

/**
 * @author Silthus
 */
public class QueuedCaptchaCommand extends QueuedCommand {

    private static final Random RANDOM = new Random();
    private final String captcha;

    public QueuedCaptchaCommand(RCPlayer player, Object object, Method method, Object... args) {

        super(player, object, method, args);
        this.captcha = generateCaptchaString();
        player.sendMessage(ChatColor.RED + "Bitte bestätige den Befehl mit: /rcconfirm " + captcha);
    }

    public String getCaptcha() {

        return captcha;
    }

    /**
     *  Generate a CAPTCHA String consisting of random lowercase & uppercase letters, and numbers.
     */
    public String generateCaptchaString() {
        int length = 5 + (Math.abs(RANDOM.nextInt()) % 3);

        StringBuilder captchaStringBuffer = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int baseCharNumber = Math.abs(RANDOM.nextInt()) % 62;
            int charNumber;
            if (baseCharNumber < 26) {
                charNumber = 65 + baseCharNumber;
            }
            else if (baseCharNumber < 52){
                charNumber = 97 + (baseCharNumber - 26);
            }
            else {
                charNumber = 48 + (baseCharNumber - 52);
            }
            captchaStringBuffer.append((char)charNumber);
        }

        return captchaStringBuffer.toString();
    }
}
