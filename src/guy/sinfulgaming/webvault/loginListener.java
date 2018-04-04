/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guy.sinfulgaming.webvault;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 *
 * @author Guy
 */
public class loginListener implements Listener{
    
    WebVault plugin;
    
    public loginListener(WebVault instance){
        plugin = instance;
    }
    
    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        plugin.sqlh.onLogin(event);
    }
    
}
