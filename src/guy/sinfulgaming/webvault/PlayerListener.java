/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guy.sinfulgaming.webvault;

import com.mini.Arguments;
import org.bukkit.GameMode;
import org.bukkit.Location;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author Guy
 */
public class PlayerListener implements Listener{
    //Member variables
    WebVault plugin;
    
    //Constructor
    public PlayerListener(WebVault instance){
        plugin = instance;
    }
    
    //The PlayerInteractEvent Method
    @EventHandler
    public void RightClick(PlayerInteractEvent event){
        if(event.getClickedBlock()!=null&&event.getClickedBlock().getType().equals(Material.CHEST)){
            Player player = (Player)event.getPlayer();
            Location current = event.getClickedBlock().getLocation();
            if(plugin.isAdding(player)){
                Arguments entry = new Arguments(event.getClickedBlock().getWorld().getName());
                entry.setValue("x", String.valueOf(current.getX()));
                entry.setValue("y", String.valueOf(current.getY()));
                entry.setValue("z", String.valueOf(current.getZ()));
                plugin.database.addIndex(entry.getKey(), entry);
                plugin.database.update();
                plugin.removeAdder(player);
            }
            if(plugin.sqlh.inTable(event.getPlayer())){    
                Arguments chestentry = plugin.database.getArguments(event.getClickedBlock().getWorld().getName());
                Location chestLocation = new Location(event.getClickedBlock().getWorld(), chestentry.getDouble("x"), chestentry.getDouble("y"), chestentry.getDouble("z"));
                if(current.getX() == chestLocation.getX() && current.getY() == chestLocation.getY() && current.getZ() == chestLocation.getZ()){
                    if(event.getPlayer().getGameMode() == GameMode.SURVIVAL || event.getPlayer().getGameMode() == GameMode.ADVENTURE){
                        event.setCancelled(true);
                        Inventory inv = plugin.sqlh.getInventory(player);
                        player.openInventory(inv);
                    }
                }
            } 
            
        }
            
        
    }
    @EventHandler
    public void worldChange(PlayerJoinEvent event){
        if(Config.worlds.contains(event.getPlayer().getWorld().getName())){
            Player player = (Player)event.getPlayer();
            plugin.sqlh.worldTest(player);
        }
        
    }
    @EventHandler
    public void worldChange(PlayerChangedWorldEvent event){
        if(Config.worlds.contains(event.getPlayer().getWorld().getName())){
            Player player = (Player)event.getPlayer();
            plugin.sqlh.worldTest(player);
        }
        
    }
}
