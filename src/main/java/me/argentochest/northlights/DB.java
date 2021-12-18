package me.argentochest.northlights;

import me.argentochest.northlights.other.Pair;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DB {
    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private String url, user, password;

    public DB() {
        url = "";
        user = "";
        password = "";
    }

    public DB(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;

        if(!connect()) {
            Main.getPlugin().getLogger().warning("Cannot connect to mysql database!");
            Bukkit.getPluginManager().disablePlugin(Main.getPlugin());
            return;
        }
        createTables();
    }

    public boolean connect() {
        Main.getPlugin().getLogger().info("Connecting...");
        try {
            con = DriverManager.getConnection(url, user, password);;
            stmt = con.createStatement();
        } catch (SQLException e) {
            try {
                con = DriverManager.getConnection(url, user, password);
                stmt = con.createStatement();
            } catch (SQLException e1) {
                e1.printStackTrace();
                return false;
            };
        };
        return true;
    }

    public boolean isPlayerInDB(String p_name) {
        String query = "SELECT * FROM players WHERE player = '"+p_name+"'";
        try {
            rs = stmt.executeQuery(query);
            return rs.next();
        } catch (SQLException e) {}
        return false;
    }

    public void addToDB(String p_name, double wars, double lights) {
        if(!isPlayerInDB(p_name)) {
            String query = "INSERT INTO players (player, wars, lights) VALUES ('" + p_name + "', '" + wars + "', '" + lights + "')";
            try {
                stmt.executeUpdate(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void logLights(String p_name, double lights, String action) {
        String query = "INSERT INTO logs (player, lights, action) VALUES ('"+p_name+"', '"+lights+"', '"+action+"')";
        try {
            stmt.executeUpdate(query);
        } catch (SQLException e) {}
    }

    public double getWars(String p_name) {
        String query = "SELECT wars FROM players WHERE player = '"+p_name + "'";
        try {
            rs = stmt.executeQuery(query);
            rs.next();
            return rs.getDouble(1);
        } catch (SQLException e) {}
        return 0;
    }

    public void setWars(String p_name, double wars) {
        String query = "UPDATE players SET wars = '"+wars+"' WHERE player = '"+p_name+"'";
        try {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
        }
    }

    public void setLights(String p_name, double lights) {
        String query = "UPDATE players SET lights = '"+lights+"' WHERE player = '"+p_name+"'";
        try {
            stmt.executeUpdate(query);
        } catch (SQLException e) {}
    }

    public EconomyResponse addWars(String p_name, double wars) {
       setWars(p_name, getWars(p_name)+wars);
       return new EconomyResponse(wars, getWars(p_name), EconomyResponse.ResponseType.SUCCESS, "");
    }

    public EconomyResponse removeWars(String p_name, double wars) {
        addWars(p_name, -wars);
        return new EconomyResponse(wars, getWars(p_name), EconomyResponse.ResponseType.SUCCESS, "");
    }

    public void addLights(String p_name, double lights) {
        setLights(p_name, getLights(p_name)+lights);
    }

    public void removeLights(String p_name, double lights) {
         addLights(p_name, -lights);
    }

    public double getLights(String p_name) {
        String query = "SELECT lights FROM players WHERE player = '"+p_name + "'";
        try {
            rs = stmt.executeQuery(query);
            rs.next();
            return rs.getDouble(1);
        } catch (SQLException e) {}
        return 0;
    }

    public void createTables() {
        String query1 = "CREATE TABLE players (id INTEGER NOT NULL AUTO_INCREMENT, player VARCHAR(255) NOT NULL UNIQUE," +
                " wars DOUBLE, lights DOUBLE, PRIMARY KEY (id))";
        String query2 = "CREATE TABLE logs (id INTEGER NOT NULL AUTO_INCREMENT, player VARCHAR(255) NOT NULL," +
                " lights DOUBLE, action TEXT, PRIMARY KEY (id))";
        try {
            stmt.execute(query1);
            stmt.execute(query2);
        }
        catch(SQLException e) {};
    }

    public List<Pair<String, Double>> getTop(String criteria, int max_rows, boolean reverse) {
        String rev = "";
        if(reverse) rev = "DESC";
        List<Pair<String, Double>> top = new ArrayList<>();
        try {
            rs = stmt.executeQuery("SELECT * FROM players ORDER BY "+criteria+" "+rev+" LIMIT "+max_rows);
            int i = 0;
            while(rs.next() && i < max_rows) {
                String nick = rs.getString("player");
                double val = rs.getDouble(criteria);
                Pair<String, Double> pair = new Pair<>(nick, val);
                top.add(pair);
                ++i;
            }
            return top;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
