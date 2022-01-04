package me.argentochest.northlights.other;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@Data
public class Loc {
    private Location loc;

    public Loc(Location loc) {
        this.loc = loc;
    }

    public Loc(String strLoc) {
        String[] arr = strLoc.split(",");
        loc = new Location(Bukkit.getWorld(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]), Integer.parseInt(arr[3]),
                Float.parseFloat(arr[4]), Float.parseFloat(arr[5]));
    }

    public String toString() {
        return loc.getWorld().getName()+","+loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ()+","+loc.getYaw()+","+loc.getPitch();
    }
}
