/*
 * File name:        SQLQueries.java (package mainServlet)
 * Author(s):        Lukas König
 * Java version:     8.0
 * Generation date:  28.07.2015 (07:58)
 * Part of the EAS => VFP => XWizard webapp implementation.
 *
 * (c) This file and the EAS (Easy Agent Simulation) framework containing it
 * is protected by Creative Commons by-nc-sa license. Any altered or
 * further developed versions of this file have to meet the agreements
 * stated by the license conditions. 
 * 
 * In a nutshell
 * -------------
 * You are free:
 * - to Share -- to copy, distribute and transmit the work
 * - to Remix -- to adapt the work
 * 
 * Under the following conditions:
 * - Attribution -- You must attribute the work in the manner specified by the 
 *   author or licensor (but not in any way that suggests that they endorse 
 *   you or your use of the work).
 * - Noncommercial -- You may not use this work for commercial purposes.
 * - Share Alike -- If you alter, transform, or build upon this work, you may 
 *   distribute the resulting work only under the same or a similar license to 
 *   this one. 
 * 
 * + Detailed license conditions (Germany):
 *   http://creativecommons.org/licenses/by-nc-sa/3.0/de/
 * + Detailed license conditions (unported):
 *   http://creativecommons.org/licenses/by-nc-sa/3.0/deed.en
 * 
 * This header must be placed in the beginning of any version of this file.
 */

package mainServlet;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import eas.GlobalVariables;
import veryFastPDF.VFPVariables;
import veryFastPDF.pdfProcessors.LaTeXPDF;
import veryFastPDF.script.DummyRepresentable;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableFactory;
import veryFastPDF.web.ConvenienceMethods;
import veryFastPDF.web.SessionMetaInf;
import veryFastPDF.web.Watchdog;

/**
 * Collection of SQL queries to access the database. Only this class should be
 * used to access the database. Within the class there are only two non-private
 * methods to actually communicate with the database.<BR/> 
 * <BR/>
 * Namely:<BR/>
 * - accessDatabase and<BR/>
 * - updateOrInsertScriptInMainTable<BR/>
 * <BR/>
 * Keeping this interface
 * slim intends to avoid bad SQL requests. The two non-private methods have to
 * sanity-check all inputs from "outside" that might be harmful.
 * 
 * @author Lukas König
 */
public final class SQLQueries {

    private static final String COLUMN_NAME_SESSION_ID = "sessionID";
    private static final String COLUMN_NAME_SOURCE_SCRIPT_ID = "sourceScriptID";
    private static final String COLUMN_NAME_TARGET_SCRIPT_ID = "targetScriptID";
    private static final String COLUMN_NAME_CONVERSION_METHODS_PK = "ID";
    private static final String COLUMN_NAME_SCRIPT = "script";
    private static final String COLUMN_NAME_WEB_FREE = "webFree";
    private static final String COLUMN_NAME_ERROR_CODE = "errorCode";
    private static final String COLUMN_NAME_XWIZARD_VERSION = "xwizardVersion";
    private static final String COLUMN_NAME_SVG_CACHED = "svgCached";
    private static final String COLUMN_NAME_PDF_CACHED = "pdfCached";

    private static final String TABLE_NAME_SESSION_DATA = "SESSION_DATA";
    private static final String TABLE_NAME_CONVERSION_METHODS = "CONVERSION_METHODS";
    private static final String TABLE_NAME_SCRIPTS = "SCRIPTS";
    
    private static final String PK_NAME_SCRIPT_ID = "scriptID";

	private static boolean tableExists(String tableName, Connection connect) {
		boolean exists = false;
		
		try {
			DatabaseMetaData meta = connect.getMetaData();
			ResultSet res = meta.getTables(null, null, tableName, new String[] {"TABLE"});
			res.last();
			exists = res.getRow() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
        return exists;
	}

	private static boolean columnExists(String tableName, String colName, Connection connect) {
		ResultSet meta;
		boolean colExists = false;
		
		try {
			meta = connect.getMetaData().getColumns(null, null, tableName, colName);
			meta.last();
			colExists = meta.getRow() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return colExists;
	}
	
	/**
	 * Creates an empty table, i.e., only one column which is of type int
	 * and is considered primary key, not null and automatically incrementing.
	 * If the table already exists, no action is performed.
	 * 
	 * @param tableName   The name of the new table.
	 * @param primaryKey  The primary key column.
	 * @param connect     The connection.
	 */
	private static void createEmptyTable(
			String tableName, 
			String primaryKey,
			Connection connect) {
    	if (!tableExists(tableName, connect)) {
	    	try (PreparedStatement statement = connect.prepareStatement("CREATE TABLE " + tableName + "(\r\n" + 
	        		primaryKey + " int(11) NOT NULL AUTO_INCREMENT,"
	        				+ "PRIMARY KEY (" + primaryKey + ")"
	        						+ ") DEFAULT CHARSET=utf8;")) {
	            logDebug(statement.toString());
	    	    statement.executeUpdate();
	    	    log("Table added (" + tableName + " - PK: " + primaryKey + ")");
	    	} catch (SQLException e) {
	    	    log("Table NOT added (" + tableName + " - PK: " + primaryKey + ")");
                e.printStackTrace();
            }
    	}
	}
	
	private static void addColumn(
			String toTable, 
			String colName, 
			String type, 
			int length, 
			Connection connect) {
		addColumn(toTable, colName, type, length, connect, "");
	}

	/**
	 * Adds a new column to a table. If the column already exists, no actions
	 * are performed. No check of existence of the table is performed.
	 * 
	 * @param toTable  The table to alter.
	 * @param colName  The name of the column.
	 * @param type     The data type of the column.
	 * @param length   The max. length of the column content.
	 * @param connect  The connection.
	 * @param addProps Additional properties such as "NOT NULL", "DEFAULT CURRENT_TIMESTAMP" etc.
	 */
	private static void addColumn(
			String toTable, 
			String colName, 
			String type, 
			int length, 
			Connection connect,
			String addProps) {
		boolean colExists = columnExists(toTable, colName, connect);
		if (colExists) {
			return; // Column exists.
		}
		
		String lengthStr = " ";
		if (length >= 0) {
			lengthStr = "(" + length + ") ";
		}
		
    	try (PreparedStatement statement = connect.prepareStatement(
    	        "ALTER TABLE " + toTable + " ADD " + colName + " " + type + lengthStr + addProps)) {
    	    logDebug(statement.toString());
    	    statement.executeUpdate();
    	    log("Column added (" + toTable + " => " + colName + ")");
    	} catch (SQLException e) {
            log("Column NOT added (" + toTable + " => " + colName + ")");
            e.printStackTrace();
        }
	}

	private static boolean scriptIDExistsInTable(String tableName, int value, Connection connect) {
		try (PreparedStatement statement = connect.prepareStatement("SELECT count(*) from " 
		        + tableName + " WHERE " + PK_NAME_SCRIPT_ID + "=?")) {
		    statement.setInt(1, value);
		    
		    logDebug(statement.toString());
		    
		    ResultSet resultSet = statement.executeQuery();
		    resultSet.next();
		    return resultSet.getInt(1) > 0;
		} catch (SQLException e) {
            e.printStackTrace();
        }
		
		return false;
	}
	
//	private static int scriptExists(String script, Connection connect) {
//	    getScriptIdIfAny(script)
//	    
//        try (PreparedStatement statement = connect.prepareStatement("SELECT count(*) from " 
//                + SCRIPTS_TABLE_NAME + " WHERE " + SCRIPT_COLUMN_NAME + "=?")) {
//            statement.setString(1, script);
//            
//            logDebug(statement.toString());
//            
//            ResultSet resultSet = statement.executeQuery();
//            resultSet.next();
//            return resultSet.getInt(1);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        
//        return -1;
//	}
	
	/**
	 * Update a row with a specified primary key (name and value) to a set
	 * of column-value pairs. Note that the primary key must exist in the
	 * table.
	 *
	 * @param pkName     The name of the primary key.
	 * @param pkValue    The value of the primary key (has to exist).
	 * @param tableName  The name of the table.
	 * @param data       The (non-pk) data to insert.
	 * @param connect    The connection.
	 */
	private static void updateRowInTable(
			String pkName,
			int pkValue,
			String tableName, 
			HashMap<String, String> data, 
			Connection connectRaw) {
        Connection connect = connectRaw;
        if (connectRaw == null) {
            connect = establishConnection();
        }

        ArrayList<String> colNames = new ArrayList<>(data.keySet());

		String query = "UPDATE " + tableName + "\n" + 
                "SET " + pkName + "=?";

        for (String colName : colNames) {
            query += "," + colName + "=?";
        }
        
        query += "\nWHERE " + pkName + "=?";

		try (PreparedStatement statement = connect.prepareStatement(query)) {
		    statement.setInt(1, pkValue);
		    int i = 2;
		    for (String colName : colNames) {
		        statement.setString(i, data.get(colName));
		        i++;
		    }
		    statement.setInt(i, pkValue);
		    
		    logDebug(statement.toString());
		    
	        statement.executeUpdate();
		} catch (SQLException e) {
            e.printStackTrace();
        }
	}

    /**
     * Insert a row with a specified primary key (name and value), and a set
     * of column-value pairs. Note that the primary key must not exist in the
     * table, otherwise an exception is thrown.
     *
     * @param tableName  The name of the table.
     * @param data       The (non-pk) data to insert.
     * @return  The auto-increased value.
     */
	private static int insertRowIntoTable(
            String tableName, 
            HashMap<String, String> data) {
	    return insertRowIntoTable(null, null, tableName, data, null);
	}
	
	/**
	 * Insert a row with a specified primary key (name and value), and a set
	 * of column-value pairs. Note that the primary key must not exist in the
	 * table, otherwise an exception is thrown.
	 *
	 * @param pkName     The name of the primary key.
	 * @param pkValue    The value of the primary key (has to be unique; 
	 *                   will be auto-increased if {@code null}).
	 * @param tableName  The name of the table.
	 * @param data       The (non-pk) data to insert.
	 * @param connect    The connection.
	 * @return  The value of the given pk (if non-null, the value itself is
	 *          returned, otherwise the auto-increased value).
	 */
	private static int insertRowIntoTable(
			String pkName,
			Integer pkValue,
			String tableName, 
			HashMap<String, String> data, 
			Connection connectRaw) {
	    Connection connect = connectRaw;
	    if (connectRaw == null) {
	        connect = establishConnection();
	    }
	    
		ArrayList<String> colNames = new ArrayList<>(data.keySet());
		String pkName2 = pkName + ", ";
		
		if (pkValue == null) {
		    pkName2 = "";
		}
		
		String query = "INSERT INTO " + tableName + " (" + pkName2;

		boolean first = true;
		for (String colName : colNames) {
		    String comma = ", ";
            if (first) {
                comma = "";
            }
			query += comma + colName;
			first = false;
		}
		
		query += ")\n";
		query += "VALUES (" + (pkValue != null ? "?" : "");
		
		first = pkValue == null ? true : false;
		for (int i = 0; i < colNames.size(); i++) {
            String comma = ", ";
            if (first) {
                comma = "";
            }
			query += comma + "?";
			first = false;
		}
		
		query += ");";
		
		try (PreparedStatement statement = connect.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
		    int i = 1;
		    if (pkValue != null) {
		        statement.setInt(1, pkValue);
		        i++;
		    }
		    
		    for (String colName : colNames) {
		        statement.setString(i, data.get(colName));
		        i++;
		    }
		    
		    logDebug(statement.toString());
		    
		    statement.executeUpdate();
		    
		    if (pkValue != null) {
		        return pkValue;
		    }
		    
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newID = generatedKeys.getInt(1);
                    
                    if (newID >= 0) {
                        return newID;
                    }
                }
                else {
                    throw new SQLException("Creating record and retrieving generated key failed.");
                }
            }
		} catch (SQLException e) {
            e.printStackTrace();
        }
		
		throw new RuntimeException("Row not inserted.");
	}
	
	/**
	 * Checks if the script has already been stored, and retrieves its id in
	 * that case.
	 * 
	 * @param script  The script to check.
	 * @return  The script's ID if it has already been stored, -1 otherwise.
	 */
	private static int getScriptIdIfAny(String script) {
	    Connection connect = establishConnection();
        if (connect == null) {
            return -1;
        }
	    
	    try (PreparedStatement statement = connect.prepareStatement(
                "select " + PK_NAME_SCRIPT_ID + " "
                + "from " + TABLE_NAME_SCRIPTS + " "
                + "where " + COLUMN_NAME_SCRIPT + "=?")) {
	        statement.setString(1, script);
	        
	        logDebug(statement.toString());
	        
	        ResultSet result = statement.executeQuery();

	        if (result == null || !result.next()) {
	            return -1;
	        }
            
	        return result.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
	    
	    return -1;
	}
	
	/**
	 * Inserts a single complete row into one of the TYPE tables, i.e.,
	 * the tables containing type-specific meta information for the stored
	 * scripts.
	 * 
	 * @param tableName  The table name of the specified script type.
	 * @param data       The data to store - mere key-value pairs of type
	 *                   String where the key must denote a registered
	 *                   column (this is a column given in the meta information
	 *                   method of the according script type). If the
	 *                   column does not exist in the table, it is inserted.
	 *                   If there is no data (anymore, due to changes in the
	 *                   meta information method) for certain columns, they
	 *                   are left empty.
	 * @param scriptID  The ID pointing to the respective script. The ID is
	 *                  a primary key of the type tables. If the ID is already
	 *                  stored in the table, the respective row is update
	 *                  instead of inserting a new row.
	 *                  
	 * @throws UnsupportedDatabaseAccessException  This exception is thrown if a 
	 * 					non-present, non-registered column name is requested
	 *                  to be filled.
	 */
	private static void updateOrInsertScriptInfInTypeTable(
			String tableName, 
			HashMap<String, String> data,
			int scriptID) throws UnsupportedDatabaseAccessException {
	    if (tableName.equals(tableName(new DummyRepresentable(null)))) {
	        return; // No meta information for DummyRepresentable.
	    }
	    
		Connection connect = establishConnection();
		
		// Check if database structure up-to-date and refresh otherwise.
		if (!tableExists(tableName, connect)) {
			createAndUpdateAllTables();
			if (!tableExists(tableName, connect)) {
				throw new UnsupportedDatabaseAccessException("Table " + tableName + " not existing and/or not registered.");
			}
		}

		for (String colName : data.keySet()) {
			if (!columnExists(tableName, colName, connect)) {
				createAndUpdateAllTables();
				if (!columnExists(tableName, colName, connect)) {
					throw new UnsupportedDatabaseAccessException("Column " + colName + " in table " + tableName + " not existing and/or not registered.");
				}
			}
		}
		// EO Check.
		
		if (scriptIDExistsInTable(tableName, scriptID, connect)) {
		    updateRowInTable(PK_NAME_SCRIPT_ID, scriptID, tableName, data, connect);
		} else {
		    insertRowIntoTable(PK_NAME_SCRIPT_ID, scriptID, tableName, data, connect);
		}
	}
	
	/**
	 * There is only one singleton connection per request thread, which is
	 * closed finally after one of the two non-private database methods has
	 * been called.
	 */
	private static void closeConnection() {
		Connection connection = singletonConnection.get();
		if (connection != null) {
			try {
			    connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			singletonConnection.remove();
		}
	}

    // ThreadLocal, not a plain static field: Tomcat serves each request on its
    // own thread, and a single shared Connection across concurrent requests
    // caused "Connection is null"/"statement closed" races under load.
    private static final ThreadLocal<Connection> singletonConnection = new ThreadLocal<>();
    
	private static Connection establishConnection() {
	    Connection connection = singletonConnection.get();
	    try {
            if (connection == null || connection.isClosed()) {
            	Context context = null;
                DataSource datasource = null;
   
                try {
            		context = new InitialContext();
            		datasource = (DataSource) context.lookup("java:/comp/env/jdbc/xwizard");
            		connection = datasource.getConnection();
            		singletonConnection.set(connection);
            	} catch (NamingException | SQLException e1) {
            		e1.printStackTrace();
            	}
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
	    
		return connection;
	}

    private static void updateTablesInDebugMode() {
        if (WebLink.isDebugMode()) { // In debug mode, update tables.
            logDebug("Updating all tables every time in DEBUG mode.");
            createAndUpdateAllTables();
        }
    }

	/**
	 * BE VERY CAUTIOUS WITH THIS METHOD! IT WILL DELETE 
	 * (and recreate in empty state) ALL TABLES.
	 */
	@SuppressWarnings("unused")
    private static void resetDatabase() {
		dropAllTables();
		createAndUpdateAllTables();
	}
	
	/**
	 * BE VERY CAUTIOUS WITH THIS METHOD! IT WILL DELETE ALL TABLES.
	 * TODO: Actually not sure if this will work since connected
	 *       database is deleted temporarily.
	 */
	private static void dropAllTables() {
        Connection connect = establishConnection();

        try (PreparedStatement statement = connect.prepareStatement("DROP DATABASE IF EXISTS xwizard")) {
            logDebug(statement.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (PreparedStatement statement = connect.prepareStatement("CREATE DATABASE IF NOT EXISTS xwizard\n" + 
                "    DEFAULT CHARACTER SET = utf8")) {
            logDebug(statement.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	private static void log(String logString) {
    	GlobalVariables.getParameters().logWeb(
    			"<" + SQLQueries.class.getSimpleName() + "> " 
    		        + logString);
	}
	
    private static void logDebug(String logString) {
        GlobalVariables.getParameters().logDebug(
                "<" + SQLQueries.class.getSimpleName() + "> " 
                    + logString);
    }

    private static void createAndUpdateAllTables() {
        Connection connect = establishConnection();
        String table;
        
        table = TABLE_NAME_SCRIPTS;
        createEmptyTable(table, PK_NAME_SCRIPT_ID, connect);
        addColumn(table, COLUMN_NAME_SCRIPT, "varchar", 20000, connect);
        addColumn(table, COLUMN_NAME_WEB_FREE, "boolean", -1, connect, "DEFAULT false");
        addColumn(table, COLUMN_NAME_SVG_CACHED, "mediumtext", -1, connect);
        addColumn(table, COLUMN_NAME_PDF_CACHED, "mediumtext", -1, connect);

        table = TABLE_NAME_SESSION_DATA;
        createEmptyTable(table, COLUMN_NAME_SESSION_ID, connect);
        addColumn(table, PK_NAME_SCRIPT_ID, "int", 11, connect);
        addColumn(table, COLUMN_NAME_SOURCE_SCRIPT_ID, "int", 11, connect);
        addColumn(table, COLUMN_NAME_TARGET_SCRIPT_ID, "int", 11, connect);
        addColumn(table, SessionMetaInf.COLUMN_NAME_SOURCE_TYPE, "varchar", 50, connect);
        addColumn(table, SessionMetaInf.COLUMN_NAME_SOURCE_METHOD, "varchar", 50, connect);
        addColumn(table, SessionMetaInf.COLUMN_NAME_SOURCE_COMPLETE_USER, "varchar", 500, connect);
        addColumn(table, SessionMetaInf.COLUMN_NAME_SOURCE_BROWSER_NAME, "varchar", 20, connect);
        addColumn(table, SessionMetaInf.COLUMN_NAME_SOURCE_MOBILE_ACCESS, "boolean", -1, connect);
        addColumn(table, SessionMetaInf.COLUMN_NAME_TIMESTAMP, "TIMESTAMP", -1, connect, "DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");
        addColumn(table, SessionMetaInf.COLUMN_NAME_DURATION, "int", 11, connect);
        addColumn(table, SessionMetaInf.COLUMN_NAME_DEBUG_MODE, "boolean", -1, connect);
        addColumn(table, SessionMetaInf.COLUMN_NAME_COOKIE_USER_NAME, "bigint", -1, connect, "DEFAULT 0");
        addColumn(table, SessionMetaInf.COLUMN_NAME_LANGUAGE, "varchar", 3, connect, "DEFAULT null");
        addColumn(table, SessionMetaInf.COLUMN_NAME_SCRIPT_TYPE, "varchar", 40, connect, "DEFAULT null");
        addColumn(table, SessionMetaInf.COLUMN_NAME_EXERCISE, "boolean", -1, connect, "DEFAULT null");
        addColumn(table, SessionMetaInf.COLUMN_NAME_EXERCISE_SOLVED, "boolean", -1, connect, "DEFAULT null");
        addColumn(table, SessionMetaInf.COLUMN_NAME_SOLUTION, "varchar", 50, connect, "DEFAULT null");
        addColumn(table, SessionMetaInf.COLUMN_NAME_ENCRYPTED, "boolean", -1, connect, "DEFAULT null");
        addColumn(table, SessionMetaInf.COLUMN_NAME_FROM_ID, "varchar", 20, connect, "DEFAULT null");
        addColumn(table, COLUMN_NAME_ERROR_CODE, "int", 2, connect, "DEFAULT 0");
        addColumn(table, COLUMN_NAME_XWIZARD_VERSION, "varchar", 40, connect, "DEFAULT null");

        createEmptyTable(TABLE_NAME_CONVERSION_METHODS, COLUMN_NAME_CONVERSION_METHODS_PK, connect);
        
        for (Class<? extends RepresentableAsPDF> repClass : WebLink.availablePDFTypes) {
            RepresentableAsPDF r = RepresentableFactory.getRepByClass(repClass);
            
            table = tableName(r);
            createEmptyTable(table, PK_NAME_SCRIPT_ID, connect);
            
            addColumn(TABLE_NAME_CONVERSION_METHODS, ConvenienceMethods.repNameSQL(r), "varchar", 500, connect);
            
            try {
                for (String propName : r.getMetaProperties().keySet()) {
                    addColumn(table, propName, "varchar", 256, connect);
                }
            } catch (Exception e) {
                GlobalVariables.getParameters().logWeb("Meta properties not available for rep. '" + repClass.getSimpleName() + "'");
                GlobalVariables.getParameters().logWeb("Columns not created in table '" + tableName(r) + "'");
                e.printStackTrace();
            }
        }
    }

    private static boolean isIDWebFree(int id) {
        Connection connect = establishConnection();
        
        String query1 = "SELECT " + COLUMN_NAME_WEB_FREE + " FROM " + TABLE_NAME_SCRIPTS + "\n" + 
                "WHERE " + PK_NAME_SCRIPT_ID + "=" + id + ";";

        try (PreparedStatement statement1 = connect.prepareStatement(query1)) {
            logDebug(statement1.toString());
            ResultSet res = statement1.executeQuery();
            boolean hasNext = res.next();
            
            if (!hasNext) {
                return false;
            }
            
            return res.getBoolean(1); // Script found, and it is web-free.
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    /* ******** The following methods are non-private. ******** */
    protected static void addConversionMethodClicked(RepresentableAsPDF r, String methodName) {
        Connection connect = establishConnection();
        String query1 = "INSERT INTO " + TABLE_NAME_CONVERSION_METHODS + " (" + ConvenienceMethods.repNameSQL(r) + ")\r\n" + 
                "VALUES (?);";
        
        try (PreparedStatement statement1 = connect.prepareStatement(query1)) {
            statement1.setString(1, methodName);
            
            logDebug(statement1.toString());
            statement1.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    protected static HashMap<String, Integer> getConversionMethodClickCounts(RepresentableAsPDF r) {
        Connection connect = establishConnection();
        String query = "SELECT " 
                + ConvenienceMethods.repNameSQL(r) 
                + ", COUNT("
                + ConvenienceMethods.repNameSQL(r)
                + ") AS num_methods FROM xwizard."
                + TABLE_NAME_CONVERSION_METHODS
                + " GROUP BY "
                + ConvenienceMethods.repNameSQL(r)
                + ";";

        try (PreparedStatement statement = connect.prepareStatement(query)) {
            HashMap<String, Integer> clickCounts = new HashMap<>();
            logDebug(statement.toString());
            
            ResultSet res = statement.executeQuery();

            while (res.next()) {
                if (null != res.getString(1)) {
                    clickCounts.put(res.getString(1), (res.getInt(2)));
                }
            }
            
            return clickCounts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected static int countWebFreeScripts() {
        Connection connect = establishConnection();
        String query1 = "SELECT COUNT(*) AS num FROM SCRIPTS WHERE webFree=?;";
        
        try (PreparedStatement statement1 = connect.prepareStatement(query1)) {
            statement1.setInt(1, 1);
            logDebug(statement1.toString());
            ResultSet res = statement1.executeQuery();
            res.next();
            return res.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return -1;
    }
    
    protected static Integer[] getAllWebFreeIDs() {
        Connection connect = establishConnection();
        Integer[] ids = new Integer[countWebFreeScripts()];
        
        String query1 = "SELECT " + PK_NAME_SCRIPT_ID + " FROM " + TABLE_NAME_SCRIPTS + "\n" + 
                "WHERE " + COLUMN_NAME_WEB_FREE + "=?;";

        try (PreparedStatement statement1 = connect.prepareStatement(query1)) {
            statement1.setInt(1, 1);
            logDebug(statement1.toString());
            ResultSet res = statement1.executeQuery();
            int i = 0;
            
            while (res.next()) {
                ids[i] = res.getInt(1);
                i++;
            }
            
            return ids;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected static int webFreeIDIfAny(String script) {
        int id = getScriptIdIfAny(script);
        return (id > 0 && isIDWebFree(id)) ? id : -1;
    }
    
    protected static int makeWebFree(String script) {
        Connection connect = establishConnection();
        
        int id = getScriptIdIfAny(script);
        
        if (id >= 0) {
            String query = "UPDATE " + TABLE_NAME_SCRIPTS + "\n" + 
                    "SET " + COLUMN_NAME_WEB_FREE + "=1 WHERE " + PK_NAME_SCRIPT_ID + "=" + id + ";";
            
            try (PreparedStatement statement = connect.prepareStatement(query)) {
                logDebug(statement.toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return id;
    }

    public static String getWebFreeScript(int id) {
        Connection connect = establishConnection();

        String query1 = "SELECT " + COLUMN_NAME_WEB_FREE + " FROM " + TABLE_NAME_SCRIPTS + "\n" + 
                "WHERE " + PK_NAME_SCRIPT_ID + "=" + id + ";";

        try (PreparedStatement statement1 = connect.prepareStatement(query1)) {
            logDebug(statement1.toString());
            ResultSet res = statement1.executeQuery();
            boolean hasNext = res.next();
            
            if (!hasNext) {
                return LaTeXPDF.message("No script with id " + id + " in database.");
            }
            
            if (res.getBoolean(1)) { // Script found, and it is web-free.
                String query2 = "SELECT " + COLUMN_NAME_SCRIPT + " FROM " + TABLE_NAME_SCRIPTS + "\n" + 
                        "WHERE " + PK_NAME_SCRIPT_ID + "=" + id + ";";
                try (PreparedStatement statement2 = connect.prepareStatement(query2)) {
                    res = statement2.executeQuery();
                    hasNext = res.next();
                    
                    if (!hasNext) { // This should never happen!
                        return LaTeXPDF.message("No script with id " + id + " in database.");
                    }
                    
                    return res.getString(1);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                return LaTeXPDF.message("There is a script with id " 
                        + id + " in the database, but it has restricted access and cannot be shown.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return LaTeXPDF.message("Something went wrong during script retrieval.");
    }
    
    /**
     * Creates a string which denotes the name of the type table corresponding
     * to the given rep type. Note that no database access is performed by this
     * method.
     * 
     * @param r  The rep type.
     * @return  The type's according table name.
     */
    protected static String tableName(RepresentableAsPDF r) {
        if (r == null) {
            return null;
        }
        
        return "TYPE_" + ConvenienceMethods.repNameSQL(r);
    }
    
    /**
     * Inserts a script into the database. If it was already there, the script
     * data is only updated. In any case, the scriptID is returned.
     * 
     * @param   script  A script (new or already existing).
     * @return  The scriptID given to the script inserted into the scripts table.
     */
    protected static int updateOrInsertScriptInMainTable(String script) {
        String sanitizedScript = script;
        Connection connect = establishConnection();
        
        // This can only happen at first start. Only two static columns here.
        if (!tableExists(TABLE_NAME_SCRIPTS, connect)) {
            createAndUpdateAllTables();
        }
        
        int id = getScriptIdIfAny(sanitizedScript);
        
        HashMap<String, String> data = new HashMap<>();
        data.put(COLUMN_NAME_SCRIPT, sanitizedScript);
        
        if (id < 0) { // No valid ID means script not yet stored
            id = insertRowIntoTable(PK_NAME_SCRIPT_ID, null, TABLE_NAME_SCRIPTS, data, connect);
        } else {
            updateRowInTable(PK_NAME_SCRIPT_ID, id, TABLE_NAME_SCRIPTS, data, connect);
        }
        
        closeConnection();
        
        return id;
    }

    /**
     * Perform a database access storing all available information in new rows
     * (creating new tables and columns if necessary).This is the main interface
     * for the outside world to access the database.
     * 
     * @param script             The script to be stored.
     * @param sourceScriptID     The id of the script that led to this script (-1 if none).
     * @param targetScriptID     The id of the script this script will lead to (-1 if none).
     * @param calcDuration       Duration of the calculation of this script.
     * @param metaInf            All the meta information of the session.
     * @param repTableName       The table name of the representable (null if none).
     * @param repMetaProperties  All the meta information of this representable (null if none).
     * @param errorCode          0 or, if an error occurred (particularly if rep... is null), a
     *                           none-0 error code.
     * @return
     */
    protected static int accessDatabase(
            String script, 
            int sourceScriptID,
            int targetScriptID,
            long calcDuration,
            SessionMetaInf metaInf,
            String repTableName,
            HashMap<String, String> repMetaProperties,
            int errorCode,
            Watchdog w) {
        if (w != null) {
            w.pauseWatching(); // EO WATCHDOG.
        }
        Watchdog.addSession(metaInf);
        
        int scriptID = -2; // Help or impressum button case (or else some error occurred?).
        
        try {
            updateTablesInDebugMode();
            
            // First database call.
            if (script != null) {
                scriptID = SQLQueries.updateOrInsertScriptInMainTable(script);
            }
            
            if (repTableName != null && repMetaProperties != null) { // Name may be null, if script type has not been detected.
                // Second database call.
                SQLQueries.updateOrInsertScriptInfInTypeTable(repTableName, repMetaProperties, scriptID);
            }

            // Session meta inf.
            HashMap<String, String> data = new HashMap<>();
            data.put(SQLQueries.PK_NAME_SCRIPT_ID, scriptID + "");
            data.put(SQLQueries.COLUMN_NAME_SOURCE_SCRIPT_ID, sourceScriptID + "");
            data.put(SQLQueries.COLUMN_NAME_TARGET_SCRIPT_ID, targetScriptID + "");
            data.put(SessionMetaInf.COLUMN_NAME_DURATION, calcDuration + "");
            data.put(SessionMetaInf.COLUMN_NAME_SOURCE_BROWSER_NAME, metaInf.getBrowserName());
            data.put(SessionMetaInf.COLUMN_NAME_SOURCE_COMPLETE_USER, metaInf.getCompleteUserInformation());
            data.put(SessionMetaInf.COLUMN_NAME_SOURCE_METHOD, metaInf.getSourceMethod());
            data.put(SessionMetaInf.COLUMN_NAME_SOURCE_MOBILE_ACCESS, metaInf.isMobileAccess() + "");
            data.put(SessionMetaInf.COLUMN_NAME_SOURCE_TYPE, metaInf.getSourceType());
            data.put(SessionMetaInf.COLUMN_NAME_TIMESTAMP, new Timestamp(System.currentTimeMillis()).toString());
            data.put(SessionMetaInf.COLUMN_NAME_DEBUG_MODE, (WebLink.isDebugMode() ? 1 : 0) + "");
            data.put(SessionMetaInf.COLUMN_NAME_COOKIE_USER_NAME, metaInf.getCookieUserName());
            data.put(SessionMetaInf.COLUMN_NAME_LANGUAGE, metaInf.getLanguage());
            data.put(SessionMetaInf.COLUMN_NAME_SCRIPT_TYPE, metaInf.getScriptType());
            data.put(SessionMetaInf.COLUMN_NAME_EXERCISE, (metaInf.isExercise() ? 1 : 0) + "");
            if (metaInf.getExerciseSolved() != null) {
                data.put(SessionMetaInf.COLUMN_NAME_EXERCISE_SOLVED, (metaInf.getExerciseSolved() ? 1 : 0) + "");
            }
            data.put(SessionMetaInf.COLUMN_NAME_SOLUTION, metaInf.getSolution());
            data.put(SessionMetaInf.COLUMN_NAME_ENCRYPTED, (metaInf.isEncrypted() ? 1 : 0) + "");
            data.put(SessionMetaInf.COLUMN_NAME_FROM_ID, metaInf.getScriptID());
            data.put(SQLQueries.COLUMN_NAME_ERROR_CODE, errorCode + "");
            data.put(SQLQueries.COLUMN_NAME_XWIZARD_VERSION, VFPVariables.PROG_VERSION_XWIZZ + "");
            
            // Third database call.
            SQLQueries.insertRowIntoTable(SQLQueries.TABLE_NAME_SESSION_DATA, data);
        } catch (Exception e) {
            GlobalVariables.getParameters().logWeb("Database access failed.");
            e.printStackTrace();
        }

        closeConnection();
        
        if (w != null) {
            w.resumeWatching(); // WATCHDOG.
        }
        
        return scriptID;
    }

    public static void storeCache(String script, String svgString, String pdfString) {
        if (establishConnection() == null) {
            return;
        }

        Integer scriptID = Wizz.scriptIsID(script);
        
        int id = scriptID == null ? getScriptIdIfAny(script) : scriptID;
        
        if (id < 0) {
            throw new RuntimeException("Script not in database althouh it should be:\n" + script);
        }
        
        HashMap<String, String> data = new HashMap<>();
        data.put(COLUMN_NAME_SVG_CACHED, svgString);
        data.put(COLUMN_NAME_PDF_CACHED, pdfString);
        
        updateRowInTable(PK_NAME_SCRIPT_ID, id, TABLE_NAME_SCRIPTS, data, null);
    }
    
    /**
     * Retrieves the cached SVG for a given script, if existing, 
     * <code>null</code> otherwise.
     * 
     * @param script  The script to retrieve.
     * @return  The cached SVG or <code>null</code>.
     */
    public static String retrieveCachedSVG(String script) {
        updateTablesInDebugMode();
        Connection connect = establishConnection();
        if (connect == null) {
            return null;
        }
        
        Integer scriptID = Wizz.scriptIsID(script);
        
        try (PreparedStatement statement = connect.prepareStatement(
                "select " + COLUMN_NAME_SVG_CACHED + " "
                + "from " + TABLE_NAME_SCRIPTS + " "
                + "where " + (scriptID == null ? COLUMN_NAME_SCRIPT : PK_NAME_SCRIPT_ID) + "=?")) {
            statement.setString(1, scriptID == null ? script : scriptID + "");
            
            logDebug(statement.toString());
            
            ResultSet result = statement.executeQuery();

            if (result == null || !result.next()) {
                return null;
            }
            
            return result.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        
//        return null;
    }
}
