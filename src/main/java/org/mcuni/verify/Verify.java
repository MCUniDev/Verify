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
        logger.info("[Verify] Started MCUni Verify version 1.0");
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
        logger.info(player.getUsername()+" connected.");
        if (!getUserInfo(player.getUsername(), String.valueOf(player.getUniqueId()))) {
            logger.info("[Verify] Kicked player '"+player.getUsername()+"'. Player is not registered.");
            player.disconnect(kickMessage());
        } else {
            logger.info("[Verify] Allowed player '"+player.getUsername()+"'. Player is registered.");
        }
    }

    private boolean getUserInfo(String username, String uuid) {
        try {
            logger.info("[Verify] Fetching player data for user '"+username+"' with UUID '"+uuid+"'.");
            URL url = new URL("https://kit.mcuni.org/api/v1/user.php?username="+username+"&uuid="+uuid+"&network="+NetworkID);
            logger.info("[DEBUG] https://kit.mcuni.org/api/v1/user.php?username="+username+"&uuid="+uuid+"&network="+NetworkID);
            Scanner s = new Scanner(url.openStream());
            if (s.hasNextLine()) {
                String response = s.nextLine();
                if (response.equals("")) {
                    logger.info("[Verify] There was no response from the server.");
                    return false;
                } else {
                    logger.info("[Verify] Fetched: " + response);
                    return response.equals("true");
                }
            }
        }
        catch(IOException ex) {
            logger.error("[Verify][Broadcast] Fatal error.");
            logger.error(Arrays.toString(ex.getStackTrace()));
        }
        return false;
    }
}
