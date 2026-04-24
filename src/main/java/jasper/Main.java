package jasper;

import jasper.ListenerEvent.ButtonInteraction;
import jasper.ListenerEvent.ListenerMessage;
import jasper.ListenerEvent.onEnable;
import jasper.ListenerEvent.onSlashCommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class Main {
    public static void main(String[] args) throws LoginException {
        JDA jda = JDABuilder.createDefault(System.getenv("DISCORD_TOKEN"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT,GatewayIntent.GUILD_MEMBERS)
                .setActivity(Activity.customStatus("/jhelp ♦️ ⛏"))
                .build();
        jda.addEventListener(new ListenerMessage(), new onSlashCommandListener(),
                new ButtonInteraction(), new onEnable());
    }
}