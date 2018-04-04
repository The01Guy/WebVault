/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guy.sinfulgaming.webvault;

import com.mini.Arguments;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author Guy
 */
public class BlockListener implements Listener {

    WebVault plugin;

    public BlockListener(WebVault instance) {
        plugin = instance;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if(event.getView().getTitle().equals("WebVault")){
            Inventory inv = event.getInventory();
            Player player = (Player)event.getPlayer();
            plugin.sqlh.saveInventory(inv, player);
        }
    }
    @EventHandler
    public void chestDamage(BlockDamageEvent event){
        if(event.getBlock()!=null&&event.getBlock().getType().equals(Material.CHEST)){
            Location current = event.getBlock().getLocation();
            Arguments chestentry = plugin.database.getArguments(event.getBlock().getWorld().getName());
            Location chestLocation = new Location(event.getBlock().getWorld(), chestentry.getDouble("x"), chestentry.getDouble("y"), chestentry.getDouble("z"));
            if(current.getX() == chestLocation.getX() && current.getY() == chestLocation.getY() && current.getZ() == chestLocation.getZ()){
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void chestBreak(BlockBreakEvent event){
        
        if(event.getBlock()!=null&&event.getBlock().getType().equals(Material.CHEST)){
            Location current = event.getBlock().getLocation();
            Arguments chestentry = plugin.database.getArguments(event.getBlock().getWorld().getName());
            Location chestLocation = new Location(event.getBlock().getWorld(), chestentry.getDouble("x"), chestentry.getDouble("y"), chestentry.getDouble("z"));
            if(current.getX() == chestLocation.getX() && current.getY() == chestLocation.getY() && current.getZ() == chestLocation.getZ()){
                event.setCancelled(true);
            }
        }
        
    }
}
