package org.mcuni.verify;

import com.google.inject.Inject;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

@Plugin(
        id = "verify",
        name = "Verify",
        version = "1.1",
        authors = {"MCUni"}
)

public class Verify {

    /** The servers "nickname" to be shown if they're kicked. */
    public String ServerNickname = "MCUni Plymouth";
    /** The name of the city or region this server is affiliated with. */
    public String CityName = "Plymouth";
    /** The MCUni Network ID number for this server. */
    public String NetworkID = "PLYMOUTH";

    @Inject
    private Logger logger;

    /**
     * Runs on the initialization (startup) of the proxy.
     * @param event Fetches detail about the event, who fired it, etc.
     */
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("Started MCUni Verify version 1.1");
        logger.info("Now listening for players...");
    }

    /**
     * Main functionality for deciding if a player should be able to connect or not.
     * Runs before a player is permitted to connect to a server.
     * @param event Fetches detail about the event, who fired it, etc.
     */
    @Subscribe
    public void onPlayerConnect(ServerPreConnectEvent event) {
        Player player = event.getPlayer();
        logger.info(player.getUsername()+" is being checked (PreConn)...");
        String reason = getUserInfo(player.getUsername(), String.valueOf(player.getUniqueId()));
        if (!reason.equals("0")) {
            logger.info("Kicked player '"+player.getUsername()+"'. Player is not registered.");
            player.disconnect(kickMessage(reason));
        } else {
            logger.info("Allowed player '"+player.getUsername()+"'. Player is registered.");
        }
    }

    /**
     * Main functionality for deciding if a player should be able to connect or not.
     * Runs before a player is permitted to connect to a server.
     * @return Component The kick message as a component.
     */
    public Component kickMessage(String reason) {
        String reasonText;
        if (reason.equals("1")) {
            reasonText = "Your account has not yet been activated, but we're not sure why.\n" +
                    "\n" +
                    "For more information please contact your server administrator.";
        } else if (reason.equals("2")) {
            reasonText = "Your account has not yet been activated.\nPlease activate your account and try again.\n" +
                    "\n" +
                    "To activate your account please visit mcuni.org/account";
        } else if (reason.equals("3")) {
            reasonText = "You have been banned from the MCUni Network.\n" +
                    "\n" +
                    "For more information please contact your server administrator.";
        } else if (reason.equals("4") || reason.equals("6")) {
            reasonText = "This server is only for students in " + CityName + ".\n" +
                    "You need to verify that you're a student before you can join.\n" +
                    "\n" +
                    "To verify your account please visit mcuni.org/verify";
        } else if (reason.equals("5")) {
            reasonText = "Whoops, looks like we couldn't find your user account.\n" +
                    "\n" +
                    "Please contact your server administrator for help.";
        } else if (reason.equals("7")) {
            reasonText = "This server sent an invalid API request so the connection was terminated.\nPlease try again.\n" +
                    "\n" +
                    "Please contact your server administrator for help.";
        } else if (reason.equals("8")) {
            reasonText = "The database is currently offline, so we can't connect you to this server.\n" +
                    "\n" +
                    "Please contact your server administrator for help.";
        } else if (reason.equals("9")) {
            reasonText = "The server's systems are having some trouble at the moment.\nPlease try again later.\n" +
                    "\n" +
                    "Please contact your server administrator for help.";
        } else {
            reasonText = "Something went wrong, but we're not sure what it was.\nPlease try again later.\n" +
                    "\n" +
                    "Please contact your server administrator for help.";
        }
        return Component.text("")
                .append(Component.text("Welcome to " + ServerNickname, NamedTextColor.GOLD).decoration(TextDecoration.BOLD, true))
                .append(Component.text("\n\n"))
                .append(Component.text(reasonText, NamedTextColor.YELLOW));
    }

    /**
     * Fetches the player's information from the MCUni Kit server.
     * @param username The player's username.
     * @param uuid The player's unique user identification number.
     * @return bool true/false - Should the player be allowed to connect or not?
     */
    private String getUserInfo(String username, String uuid) {
        try {
            logger.info("Fetching player data for user '"+username+"' with UUID '"+uuid+"'.");
            URL url = new URL("https://kit.mcuni.org/api/v3/verify.php?username="+username+"&uuid="+uuid+"&network="+NetworkID);
            logger.info("[DEBUG] https://kit.mcuni.org/api/v3/verify.php?username="+username+"&uuid="+uuid+"&network="+NetworkID);
            Scanner s = new Scanner(url.openStream());
            if (s.hasNextLine()) {
                String response = s.nextLine();
                if (response.equals("")) {
                    logger.info("There was no response from the server.");
                    return "8";
                } else {
                    logger.info("Fetched: " + response);
                    return response;
                }
            }
        }
        catch(IOException ex) {
            logger.error("Fatal error.");
            logger.error(Arrays.toString(ex.getStackTrace()));
        }
        return "9";
    }
}
