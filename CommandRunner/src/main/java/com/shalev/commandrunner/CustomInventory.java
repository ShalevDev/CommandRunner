package com.shalev.commandrunner;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CustomInventory implements InventoryHolder {
    private String loc;

    public CustomInventory(String loc) {
        this.loc = loc;
    }

    public String getLoc(){
        return loc;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
