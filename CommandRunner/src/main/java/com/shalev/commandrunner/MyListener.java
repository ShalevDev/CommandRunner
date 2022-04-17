package com.shalev.commandrunner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class MyListener implements Listener {
    private Plugin plugin;
    private boolean command = false;
    private Player commandP;
    private String location;

    public MyListener(Plugin plugin) {
        this.plugin = plugin;

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.getHand() == EquipmentSlot.HAND && e.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            Location loc = e.getClickedBlock().getLocation();
            String sloc = loc.getBlockX()+";"+loc.getBlockY()+";"+loc.getBlockZ();

            // plugin.getConfig().isSet(sloc)
            ItemStack item = e.getPlayer().getInventory().getItemInMainHand();

            if(item.getType() == Material.GOLDEN_SWORD && item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Command Tool"))
            {
                mapCommand(sloc,e.getPlayer());

            }
            else if(plugin.getConfig().isSet(sloc)) {
                String cmd = plugin.getConfig().getString(sloc);
                if(!e.getPlayer().isOp()){
                    e.getPlayer().setOp(true);
                    e.getPlayer().performCommand(cmd);
                    e.getPlayer().setOp(false);
                }
                else
                    e.getPlayer().performCommand(cmd);


            }
        }
    }

    public void mapCommand(String sloc,Player p){
        if(plugin.getConfig().isSet(sloc)) {
            CustomInventory holder = new CustomInventory(sloc);
            createInventory(holder,p);
        }
        else {
            if(p.hasPermission("cr.setCommand")) {
                p.sendMessage(ChatColor.GREEN + "Please type a command:");
                command=true;
                commandP = p;
                location = sloc;
            }
            else{
                p.sendMessage(ChatColor.RED+"You do not have the permission to perform this command!");
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(e.getInventory().getHolder() instanceof CustomInventory && e.getCurrentItem()!=null){
            ItemStack item = e.getCurrentItem();
            e.setCancelled(true);
            if(item.getType() == Material.PAPER){
                if(e.getWhoClicked().hasPermission("cr.setCommand")) {
                    CustomInventory holder = (CustomInventory) e.getInventory().getHolder();
                    e.getWhoClicked().closeInventory();
                    e.getWhoClicked().sendMessage(ChatColor.GREEN + "Please type a command:");
                    command=true;
                    commandP = (Player) e.getWhoClicked();
                    location = holder.getLoc();

                }
                else{
                    e.getWhoClicked().closeInventory();
                    e.getWhoClicked().sendMessage(ChatColor.RED+"You do not have the permission to perform this command!");
                }
            }
            else if(item.getType() == Material.BARRIER){
                if(e.getWhoClicked().hasPermission("cr.removeCommand")) {
                    CustomInventory holder = (CustomInventory) e.getInventory().getHolder();
                    plugin.getConfig().set(holder.getLoc(), null);
                    plugin.saveConfig();
                    e.getWhoClicked().closeInventory();
                    e.getWhoClicked().sendMessage(ChatColor.GREEN + "Succesfully removed block action");
                }
                else{
                    e.getWhoClicked().closeInventory();
                    e.getWhoClicked().sendMessage(ChatColor.RED+"You do not have the permission to perform this command!");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerSpeak(AsyncPlayerChatEvent e){
        if(command && e.getPlayer() == commandP)
        {
            e.setCancelled(true);

            plugin.getConfig().set(location, e.getMessage());
            plugin.saveConfig();
            e.getPlayer().sendMessage(ChatColor.GREEN + "Successfully set action!");

            command = false;

        }
    }

    public void createInventory(CustomInventory holder,Player p){
        Inventory inv = Bukkit.createInventory(holder,27,"Choose an action");
        setFrame(inv,new ItemStack(Material.RED_STAINED_GLASS_PANE));

        inv.setItem(12,setItemName(Material.PAPER,ChatColor.GREEN+"Remap Action"));
        inv.setItem(14,setItemName(Material.BARRIER,ChatColor.RED+"Remove Action"));

        p.openInventory(inv);
    }

    private void setFrame(Inventory inv,ItemStack item){
        for(int i=0;i<9;i++) {
            inv.setItem(i, item);
            inv.setItem(inv.getSize()-i-1, item);
        }
        for(int i=9;i<inv.getSize()-9;i+=9){
            inv.setItem(i,item);
            inv.setItem(i+8,item);
        }

    }

    private ItemStack setItemName(Material m, String name){
        ItemStack item = new ItemStack(m);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

}


