/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guy.sinfulgaming.webvault;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Guy
 */
public class sqlHandler {
    
    WebVault plugin;
    Connection conn;                                                            
    Statement st;                                                               
    ResultSet rs;
    int id;
    
    public sqlHandler(WebVault instance){
        plugin = instance;
    }
    
    private void connect(){
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(Config.url + Config.table, Config.userName, Config.password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    public Inventory getInventory(Player player){
        ItemStack temp[] = new ItemStack[9];
        Inventory inv = plugin.getServer().createInventory(player, 9, "WebVault");
        connect();
        try{
            st = conn.createStatement();
            rs = st.executeQuery("SELECT PlayerId FROM playertable WHERE PlayerName='" + player.getName() + "'");
            while(rs.next()){
                id = rs.getInt(1);
            }
            int open = st.executeUpdate("UPDATE webvault SET Open='1' WHERE PlayerId='" + id + "' AND World='" + player.getWorld().getName() + "'");
            for(int i = 0; i<temp.length;i++){
                rs = st.executeQuery("SELECT item" + i + ",amount" + i + ",duri"+i+ " FROM webvault WHERE PlayerId='" + id + "' AND World='" + player.getWorld().getName() + "'");
                while(rs.next()){
                    temp[i] = new ItemStack(rs.getInt(1), rs.getInt(2));
                    temp[i].setDurability((short)rs.getInt(3));
                }
                rs = st.executeQuery("SELECT Enchant,EnchLv FROM itemstats WHERE PlayerId='" + id + "' AND World='" + player.getWorld().getName() + "' AND ItemId='" + i + "'");
                while(rs.next()){
                    temp[i].addUnsafeEnchantment(Enchantment.getById(rs.getInt(1)), rs.getInt(2));
                }
                inv.setItem(i, temp[i]);
                
            }
            st.close();
            conn.close();
            
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return inv;
        
    }
    
    public void saveInventory(Inventory inv, Player player){
        Map<Enchantment,Integer> test = new HashMap<Enchantment, Integer>();
        ItemStack temp[] = new ItemStack[9];
        int open = 0;
        
        connect();
        try {
            st = conn.createStatement();
            rs = st.executeQuery("SELECT PlayerId FROM playertable WHERE PlayerName='" + player.getName() + "'");
            while(rs.next()){
                id = rs.getInt(1);
            }
            rs = st.executeQuery("SELECT Open FROM webvault WHERE PlayerId='" + id + "' AND World='" + player.getWorld().getName() + "'");
            
            while(rs.next()){
                open = rs.getInt(1);
            }    
            if(open == 1){
                for(int i = 0; i<temp.length;i++){
                    temp[i] = inv.getItem(i);
                    if(temp[i] == null){
                        int item = st.executeUpdate("UPDATE webvault SET item" + i + "='0' WHERE PlayerId='" + id + "' AND World='" + player.getWorld().getName() + "'");
                        int amount = st.executeUpdate("UPDATE webvault SET amount" + i + "='0' WHERE PlayerId='" + id + "' AND World='" + player.getWorld().getName() + "'");
                        int duribility = st.executeUpdate("UPDATE webvault SET duri" + i + "='0' WHERE PlayerId='" + id + "' AND World='" + player.getWorld().getName() + "'");
                        int enchant = st.executeUpdate("DELETE FROM itemstats WHERE PlayerID='" + id + "' AND World='" + player.getWorld().getName() + "' AND ItemId='" + i + "'");
                    }else{
                        int item = st.executeUpdate("UPDATE webvault SET item" + i + "='" + temp[i].getTypeId() + "' WHERE PlayerId='" + id + "' AND World='" + player.getWorld().getName() + "'");
                        int amount = st.executeUpdate("UPDATE webvault SET amount" + i + "='" + temp[i].getAmount() + "' WHERE PlayerId='" + id + "' AND World='" + player.getWorld().getName() + "'");
                        int duribility = st.executeUpdate("UPDATE webvault SET duri" + i + "='" + temp[i].getDurability() + "' WHERE PlayerId='" + id + "' AND World='" + player.getWorld().getName() + "'");
                        test = temp[i].getEnchantments();
                        if(test.isEmpty()){
                            int enchant = st.executeUpdate("DELETE FROM itemstats WHERE PlayerID='" + id + "' AND World='" + player.getWorld().getName() + "' AND ItemId='" + i + "'");
                        }else if(!test.isEmpty()){
                            int enchant = st.executeUpdate("DELETE FROM itemstats WHERE PlayerID='" + id + "' AND World='" + player.getWorld().getName() + "' AND ItemId='" + i + "'");
                            for(int j = 0; j<=51;j++){
                                if(test.containsKey(Enchantment.getById(j))){
                                    int enchantset = st.executeUpdate("INSERT INTO itemstats(PlayerId,World,ItemID,Enchant,EnchLv) VALUES('" + id + "','" + player.getWorld().getName() + "','" + i + "','" + j + "','" + temp[i].getEnchantmentLevel(Enchantment.getById(j)) + "')");
                                }
                            } 
                        }
                    }
                }
                int close = st.executeUpdate("UPDATE webvault SET Open='0' WHERE PlayerId='" + id + "' AND World='" + player.getWorld().getName() + "'");
            }
            st.close();
            conn.close();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    public void onLogin(PlayerLoginEvent event) {  // Might change this to check local table before the forum table to have less calls to outside connections
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://192.168.1.135:3388/website","userName","password");
            st = conn.createStatement();
            rs = st.executeQuery("SELECT * FROM mybb_userfields WHERE fid4='" + event.getPlayer().getName() + "'");
            if (rs.next()) {
                int ufId = rs.getInt("ufid");
                connect();
                try{
                    st = conn.createStatement();
                    rs = st.executeQuery("SELECT * FROM playertable WHERE PlayerName='" + event.getPlayer().getName() + "'");
                    if(!rs.next()){
                        int player = st.executeUpdate("INSERT INTO playertable(ufId) VALUES('" + ufId + "')");
                        int playerName = st.executeUpdate("UPDATE playertable SET PlayerName='" + event.getPlayer().getName() + "' WHERE ufId='" + ufId + "'");
                        System.out.println("Player was put in table");
                    }
                    st.close();
                    conn.close();
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }
            }
            st.close();
            conn.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void tableTest(){
        connect();
        try{
            st = conn.createStatement();
            rs = st.executeQuery("SHOW TABLES LIKE 'playertable'");
            if(rs.next()){
                System.out.println("Tables Exist");
            } else{
                int ptable = st.executeUpdate("CREATE TABLE playertable (PlayerId INT UNSIGNED NOT NULL AUTO_INCREMENT KEY, ufId INT(11), PlayerName VARCHAR(30)) ENGINE MyISAM");
                int wvtable = st.executeUpdate("CREATE TABLE webvault (PlayerId INT(11), World VARCHAR(30), item0 INT(11) NOT NULL DEFAULT '0', amount0 INT(11) NOT NULL DEFAULT '0', duri0 INT(11) NOT NULL DEFAULT '0', item1 INT(11) NOT NULL DEFAULT '0', amount1 INT(11) NOT NULL DEFAULT '0', duri1 INT(11) NOT NULL DEFAULT '0', item2 INT(11) NOT NULL DEFAULT '0', amount2 INT(11) NOT NULL DEFAULT '0', duri2 INT(11) NOT NULL DEFAULT '0', item3 INT(11) NOT NULL DEFAULT '0', amount3 INT(11) NOT NULL DEFAULT '0', duri3 INT(11) NOT NULL DEFAULT '0', item4 INT(11) NOT NULL DEFAULT '0', amount4 INT(11) NOT NULL DEFAULT '0', duri4 INT(11) NOT NULL DEFAULT '0', item5 INT(11) NOT NULL DEFAULT '0', amount5 INT(11) NOT NULL DEFAULT '0', duri5 INT(11) NOT NULL DEFAULT '0', item6 INT(11) NOT NULL DEFAULT '0', amount6 INT(11) NOT NULL DEFAULT '0', duri6 INT(11) NOT NULL DEFAULT '0', item7 INT(11) NOT NULL DEFAULT '0', amount7 INT(11) NOT NULL DEFAULT '0', duri7 INT(11) NOT NULL DEFAULT '0', item8 INT(11) NOT NULL DEFAULT '0', amount8 INT(11) NOT NULL DEFAULT '0', duri8 INT(11) NOT NULL DEFAULT '0', Open TINYINT(1) NOT NULL DEFAULT '0') ENGINE MyISAM");
                int istable = st.executeUpdate("CREATE TABLE itemstats (PlayerId INT(11), World VARCHAR(30), ItemId INT(11), Enchant INT(11), EnchLv INT(11)) ENGINE MyISAM");
                System.out.println("Tables created.");
            }
            st.close();
            conn.close();
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
            
    }
    
    public void worldTest(Player player){
        connect();
        try{
            st = conn.createStatement();
            rs = st.executeQuery("SELECT PlayerId FROM playertable WHERE PlayerName='" + player.getName() + "'");
            if(rs.next()){
                id = rs.getInt(1);
            }
            rs = st.executeQuery("SELECT World FROM webvault WHERE PlayerId='" + id + "' AND World='" + player.getWorld().getName() + "'");
            if(!rs.next()){
                int wadd = st.executeUpdate("INSERT INTO webvault(PlayerId,World) VALUES('" + id + "','" + player.getWorld().getName() + "')");
            }
            st.close();
            conn.close();
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public boolean inTable(Player player){
        connect();
        try{
            st = conn.createStatement();
            rs = st.executeQuery("SELECT PlayerName FROM playertable WHERE PlayerName='" + player.getName() + "'");
            if(rs.next()){
                return true;
            }
            
            st.close();
            conn.close();
            
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    
}
