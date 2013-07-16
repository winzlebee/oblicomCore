package com.oblicom.plugins.oblicomcore.database;

import com.oblicom.plugins.oblicomcore.OblicomConfig;
import com.oblicom.plugins.oblicomcore.exceptions.OblicomDatabaseException;
import java.sql.SQLException;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author nagib.kanaan
 */
public class OblicomDatabase {

    private static OblicomDatabase instance = null;
    private Database database = null;
    private Plugin plugin;
    
    private OblicomDatabase(Plugin plugin) throws OblicomDatabaseException {
        database = new SQLite(plugin.getLogger(), "[OblicomCore] ", 
                     plugin.getDataFolder().getAbsolutePath(), 
                     OblicomConfig.getConfiguration().getString("database.filename"),
                     OblicomConfig.getConfiguration().getString("database.extension"));
        
        database.open();
        createTables();
    }
    
    private void createTables() throws OblicomDatabaseException {
        try {
            database.query("CREATE TABLE IF NOT EXISTS wanted(id INTEGER PRIMARY KEY AUTOINCREMENT, player TEXT, reason TEXT, status INTEGER, date INTEGER);");
            database.query("CREATE TABLE IF NOT EXISTS jail(id INTEGER PRIMARY KEY AUTOINCREMENT, player TEXT, reason TEXT, status INTEGER, date INTEGER);");
            //database.query("CREATE TABLE IF NOT EXISTS player(id INTEGER PRIMARY KEY AUTOINCREMENT, player TEXT, score INTEGER, date INTEGER);");
        } catch (SQLException error) {
            throw new OblicomDatabaseException(error.getMessage() + " - SQLState: " + error.getSQLState());
        }
    }
    
    public static Database getDatabase() {
        return OblicomDatabase.instance.database;
    }

    public static Database getDatabase(Plugin plugin) throws OblicomDatabaseException {
        if (OblicomDatabase.instance == null) {
            OblicomDatabase.instance = new OblicomDatabase(plugin);
        }
        
        return OblicomDatabase.instance.database;
    }    
}