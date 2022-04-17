package com.shalev.commandrunner;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CommandRunner extends JavaPlugin {

    private MyListener listener = new MyListener(this);

    @Override
    public void onEnable() {
        saveConfig();
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(listener,this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(command.getName().equalsIgnoreCase("mapCommand"))
        {
           if(args.length == 1)
               return Collections.singletonList("tool");
           else
               return null;
        }



        return super.onTabComplete(sender, command, alias, args);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(label.equalsIgnoreCase("mapCommand")){
            if(sender.hasPermission("cr.removeCommand") || sender.hasPermission("cr.setCommand")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "This command can only be performed by a player");
                    return true;
                }

                Player p = (Player) sender;

                if (args.length == 1 && args[0].equalsIgnoreCase("tool")) {
                    ItemStack item = new ItemStack(Material.GOLDEN_SWORD);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(ChatColor.GOLD + "Command Tool");
                    item.setItemMeta(meta);
                    p.getInventory().addItem(item);
                    return true;
                }

                if (args.length == 3 && isNumeric(args[0]) && isNumeric(args[1]) && isNumeric(args[2])) {
                    String sloc = args[0] + ";" + args[1] + ";" + args[2];
                    listener.mapCommand(sloc, p);

                } else {
                    Block block = p.getTargetBlockExact(10);

                    if (block == null) {
                        sender.sendMessage(ChatColor.RED + "Couldn't find block, try standing closer to it");
                        return true;
                    }
                    Location loc = block.getLocation();
                    String sloc = loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ();


                    listener.mapCommand(sloc, p);
                }
            }
            else
                sender.sendMessage(ChatColor.RED+"You do not have the permission to perform this command!");
            return true;
        }



        return true;
    }

    private static boolean isNumeric(String strNum) {
        try {
            Integer.parseInt(strNum);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

