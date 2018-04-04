package guy.sinfulgaming.webvault;


import com.mini.Mini;
import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


/**
 *
 * @author Guy
 */
public class WebVault extends JavaPlugin {

    loginListener logListener = new loginListener(this);                        //Registers LogListener
    BlockListener blockListener = new BlockListener(this);                      //REgisters BlockListener
    PlayerListener playerListener = new PlayerListener(this);
    sqlHandler sqlh = new sqlHandler(this);
    public static final Logger log = Logger.getLogger("Minecraft");             //Registers the Logger
    public HashMap<Player, Boolean> vPlayer = new HashMap<Player,Boolean>();                                                              
    Mini database;
    
    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
        try{
            new Config(this);
        }catch (Exception e){
            e.printStackTrace();
        }
        database = new Mini(getDataFolder().getPath(), "Vault.mini");
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(playerListener, this);
        pm.registerEvents(this.logListener, this);
        pm.registerEvents(this.blockListener, this);
        sqlh.tableTest();
        
        
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]){
        if((commandLabel.equalsIgnoreCase("webvault")) || (commandLabel.equalsIgnoreCase("wv"))){
            if(args[0].equalsIgnoreCase("add")){
                if(sender.getName().equalsIgnoreCase("the01guy") || sender.getName().equalsIgnoreCase("deadadm1n")){
                    this.setAdder((Player)sender, true);
                    return true;
                }
            }
            
                
        }
        return false;
    }
    
    public boolean isAdding(final Player player){
            if(vPlayer.containsKey(player)){
                return vPlayer.get(player);
            }else{
                return false;
            }
    }
        
    public void setAdder(final Player player, final boolean value){
            vPlayer.put(player, value);
    }
        
    public void removeAdder(Player player){
            vPlayer.remove(player);
    }
}