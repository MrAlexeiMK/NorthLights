package me.argentochest.northlights;

import me.argentochest.northlights.other.Pair;
import me.argentochest.northlights.other.Triple;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DB {
    private Connection con = null;
    private String url, user, password;
    private String wars_table, lights_table;

    public DB() {
        url = "";
        user = "";
        password = "";
        wars_table = "";
        lights_table = "";
    }

    public DB(String url, String user, String password, String wars_table, String lights_table) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.wars_table = wars_table;
        this.lights_table = lights_table;

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
        } catch (SQLException e) {
            try {
                con = DriverManager.getConnection(url, user, password);
            } catch (SQLException e1) {
                e1.printStackTrace();
                return false;
            };
        };
        return true;
    }

    public boolean isPlayerInDB(String p_name) {
        String query = "SELECT * FROM "+wars_table+" WHERE player = '"+p_name+"'";
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)){
            return rs.next();
        } catch (SQLException e) {}
        return false;
    }

    public void addToDB(String p_name, double wars, double lights) {
        if(!isPlayerInDB(p_name)) {
            String query1 = "INSERT INTO "+wars_table+" (player, wars) VALUES ('" + p_name + "', '" + wars + "')";
            String query2 = "INSERT INTO "+lights_table+" (player, lights) VALUES ('" + p_name + "', '" + lights + "')";
            try (Statement stmt = con.createStatement();) {
                stmt.executeUpdate(query1);
                stmt.executeUpdate(query2);
            } catch (SQLException e) {}
        }
    }

    public void logLights(String p_name, double lights, String action) {
        String query = "INSERT INTO logs (player, lights, action) VALUES ('"+p_name+"', '"+lights+"', '"+action+"')";
        try (Statement stmt = con.createStatement();) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {}
    }

    public double getWars(String p_name) {
        String query = "SELECT wars FROM "+wars_table+" WHERE player = '"+p_name + "'";
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            rs.next();
            return rs.getDouble(1);
        } catch (SQLException e) {}
        return 0;
    }

    public void setWars(String p_name, double wars) {
        if(!isPlayerInDB(p_name)) {
            addToDB(p_name, wars, 0.0);
        }
        else {
            String query = "UPDATE " + wars_table + " SET wars = '" + wars + "' WHERE player = '" + p_name + "'";
            try (Statement stmt = con.createStatement()) {
                stmt.executeUpdate(query);
            } catch (SQLException e) {
            }
        }
    }

    public void setLights(String p_name, double lights) {
        if(!isPlayerInDB(p_name)) {
            addToDB(p_name, 0.0, lights);
        }
        else {
            String query = "UPDATE " + lights_table + " SET lights = '" + lights + "' WHERE player = '" + p_name + "'";
            try (Statement stmt = con.createStatement()) {
                stmt.executeUpdate(query);
            } catch (SQLException e) {
            }
        }
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
        String query = "SELECT lights FROM "+lights_table+" WHERE player = '"+p_name + "'";
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            rs.next();
            return rs.getDouble(1);
        } catch (SQLException e) {}
        return 0;
    }

    public void createTables() {
        String query1 = "CREATE TABLE "+wars_table+" (id INTEGER NOT NULL AUTO_INCREMENT, player VARCHAR(255) NOT NULL UNIQUE," +
                " wars DOUBLE, PRIMARY KEY (id))";
        String query2 = "CREATE TABLE "+lights_table+" (id INTEGER NOT NULL AUTO_INCREMENT, player VARCHAR(255) NOT NULL UNIQUE," +
                " lights DOUBLE, PRIMARY KEY (id))";
        String query3 = "CREATE TABLE logs (id INTEGER NOT NULL AUTO_INCREMENT, player VARCHAR(255) NOT NULL," +
                " lights DOUBLE, action TEXT, PRIMARY KEY (id))";
        try (Statement stmt = con.createStatement()){
            stmt.execute(query1);
            stmt.execute(query2);
            stmt.execute(query3);
        }
        catch(SQLException e) {};
    }

    public List<Pair<String, Double>> getTopWars(int max_rows, boolean reverse) {
        String rev = "";
        if(reverse) rev = "DESC";
        List<Pair<String, Double>> top = new ArrayList<>();
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM "+wars_table+" ORDER BY wars "+rev+" LIMIT "+max_rows)) {
            int i = 0;
            while (rs.next() && i < max_rows) {
                String nick = rs.getString("player");
                double val = rs.getDouble("wars");
                Pair<String, Double> pair = new Pair<>(nick, val);
                top.add(pair);
                ++i;
            }
            return top;
        } catch(Exception ignored) {}
        return null;
    }

    public List<Triple<String, Double, String>> getLastLogs(int last_rows) {
        List<Triple<String, Double, String>> last = new ArrayList<>();
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM logs ORDER BY id DESC LIMIT "+last_rows)) {
            int i = 0;
            while(rs.next() && i < last_rows) {
                String nick = rs.getString("player");
                double val = rs.getDouble("lights");
                String action = rs.getString("action");
                Triple<String, Double, String> triple = new Triple<>(nick, val, action);
                last.add(triple);
                ++i;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return last;
    }

    public List<Pair<String, Double>> getTopLights(int max_rows, boolean reverse) {
        String rev = "";
        if(reverse) rev = "DESC";
        List<Pair<String, Double>> top = new ArrayList<>();
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM "+lights_table+" ORDER BY lights "+rev+" LIMIT "+max_rows)) {
            int i = 0;
            while(rs.next() && i < max_rows) {
                String nick = rs.getString("player");
                double val = rs.getDouble("lights");
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
