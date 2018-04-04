/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guy.sinfulgaming.webvault;


import java.io.*;
import java.util.List;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Guy
 */
public class Config {

    public static String url = null;
    public static String table = null;
    public static String userName = null;
    public static String password = null;
    public static List<String> worlds = null;

    @SuppressWarnings("unchecked")
    public Config(WebVault instance) throws IOException, InvalidConfigurationException {
        File file = new File(instance.getDataFolder(), "config.yml");
        if (!instance.getDataFolder().exists()) {
            instance.getDataFolder().mkdir();
        }
        if (!file.exists()) {
            copy(instance.getResource("config.yml"), file);
        }

        instance.getConfig().load(file);

        YamlConfiguration YCon = new YamlConfiguration();
        YCon.load(file);
        instance.getConfig().addDefaults(YCon);
        instance.getConfig().options().copyDefaults(true);

        Config.url = instance.getConfig().getString("url");
        Config.table = instance.getConfig().getString("table");
        Config.password = instance.getConfig().getString("password");
        Config.userName = instance.getConfig().getString("userName");
        Config.worlds = instance.getConfig().getStringList("worlds");
        
        instance.getConfig().save(file);
    }

    private void copy(InputStream src, File dst) throws IOException {
        OutputStream out = new FileOutputStream(dst);

// Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = src.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        src.close();
        out.close();
    }
}
