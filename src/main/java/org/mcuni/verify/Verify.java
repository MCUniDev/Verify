package org.mcuni.verify;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

@Plugin(
        id = "verify",
        name = "Verify",
        version = "1.0-SNAPSHOT"
)
public class Verify {

    public String ServerNickname = "UOPMC";
    public String UniversityName = "the University of Plymouth";
    public String NetworkID = "PLYMOUTH";

    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
    }

    public Component kickMessage() {
        return Component.text("Welcome to "+ServerNickname +
                "\n" +
                "This server is only for students at "+UniversityName+".\n" +
                "You need to verify that you're a student before you can join.\n" +
                "\n" +
                "To verify your account please visit mcuni.org/verify");
    }

    @Subscribe
    public void ServerPostConnectEvent(Player player, @Nullable RegisteredServer previousServer) {
        logger.debug(player.getUsername()+" connected.");
        if (!getUserInfo(player.getUsername(), String.valueOf(player.getUniqueId()))) {
            logger.debug("[Kit][Whitelist] Kicked player '"+player.getUsername()+"'. Player is not registered.");
            player.disconnect(kickMessage());
        } else {
            logger.debug("[Kit][Whitelist] Allowed player '"+player.getUsername()+"'. Player is registered.");
        }
    }

    private boolean getUserInfo(String username, String uuid) {
        try {
            logger.debug("[Kit][Whitelist] Fetching player data for user '"+username+"' with UUID '"+uuid+"'.");
            URL url = new URL("https://kit.mcuni.org/api/v1/user.php?username="+username+"&uuid="+uuid+"&network="+NetworkID);
            logger.debug("[DEBUG] https://kit.mcuni.org/api/v1/user.php?username="+username+"&uuid="+uuid+"&network="+NetworkID);
            Scanner s = new Scanner(url.openStream());
            if (s.hasNextLine()) {
                String response = s.nextLine();
                if (response.equals("")) {
                    logger.debug("[Kit][Whitelist] There was no response from the server.");
                    return false;
                } else {
                    logger.debug("[Kit][Whitelist] Fetched: " + response);
                    return response.equals("true");
                }
            }
        }
        catch(IOException ex) {
            logger.debug("[Kit][Broadcast] Fatal error.");
            logger.debug(Arrays.toString(ex.getStackTrace()));
        }
        return false;
    }
}
