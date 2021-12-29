package me.argentochest.northlights;

import me.clip.placeholderapi.PlaceholderHook;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;

public class PlaceHolders extends EZPlaceholderHook {

    private Main main;

    public PlaceHolders(Main main) {
        super(main, "northlights");
        this.main = main;
    }

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        if (p == null) {
            return "";
        }
        //%northlights_wars%
        if (identifier.equals("wars")) {
            return String.valueOf(Main.getDB().getWars(p.getName()));
        }
        //%northlights_lights%
        if (identifier.equals("lights")) {
            return String.valueOf(Main.getDB().getLights(p.getName()));
        }
        return null;
    }
}