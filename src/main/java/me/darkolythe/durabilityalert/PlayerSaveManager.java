package me.darkolythe.durabilityalert;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public abstract class PlayerSaveManager
{
    private static final File playersDirectory;
    private static HashMap<Player, FileConfiguration> playersData = new HashMap<>();

    static
    {
        playersDirectory = new File(DurabilityAlert.getInstance().getDataFolder(), "players/");
        if(!playersDirectory.exists())
        {
            playersDirectory.mkdir();
        }
    }

    public static void convertOldFile()
    {
        File oldPlayerDataFile = new File(DurabilityAlert.getInstance().getDataFolder(), "PlayerData.yml");

        if(oldPlayerDataFile.exists())
        {
            FileConfiguration oldDatas = YamlConfiguration.loadConfiguration(oldPlayerDataFile);

            for(String uuid: oldDatas.getConfigurationSection("player").getKeys(false))
            {
                File playerFile = new File(playersDirectory, uuid+".yml");

                try {
                    FileConfiguration playerDatas = YamlConfiguration.loadConfiguration(playerFile);
                    playerDatas.set("data", oldDatas.getIntegerList("player."+uuid));
                    playerDatas.save(playerFile);
                } catch (IOException e) {
                    System.out.println(DurabilityAlert.getInstance().prefix + ChatColor.RED + "Could not save recipes");
                }
            }

            oldPlayerDataFile.renameTo(new File(DurabilityAlert.getInstance().getDataFolder(), "PlayerData.yml.old"));
            DurabilityAlert.getInstance().getLogger().info("Old PlayerData file converted");
        }
    }

    public static void load(Player player)
    {
        File playerFile = new File(playersDirectory, player.getUniqueId()+".yml");
        if(playerFile.exists())
        {
            FileConfiguration playerDatas = YamlConfiguration.loadConfiguration(playerFile);
            playersData.put(player, playerDatas);
            List<Integer> data = playerDatas.getIntegerList("data");
            if (data.size() < 4) {
                data.add(0);
            }
            DurabilityAlert.getInstance().setPlayerData(player, data);
        }
    }

    public static void save(Player player)
    {
        File playerFile = new File(playersDirectory, player.getUniqueId()+".yml");

        if(!playersData.containsKey(player))
        {
            if(!playersDirectory.exists())
            {
                playersDirectory.mkdir();
            }

            try {
                playerFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            playersData.put(player, YamlConfiguration.loadConfiguration(playerFile));
        }

        try {
            FileConfiguration playerDatas = playersData.get(player);
            playerDatas.set("data", DurabilityAlert.getInstance().getPlayerData(player));
            playerDatas.save(playerFile);
        } catch (IOException e) {
            System.out.println(DurabilityAlert.getInstance().prefix + ChatColor.RED + "Could not save recipes");
        }
    }

    public static void remove(Player player)
    {
        playersData.remove(player);
    }
}
