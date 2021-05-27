/**
 * SQLite
 * Inherited subclass for making a connection to a SQLite server.
 * Remaking!
 * 
 * @author Kasper Franz
 */
package lib.genplew.SQLibrary;

/*
 * SQLite
 */
import java.io.File;
/*
 * Both
 */
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class SQLite extends Database {
	public String location;
	public String name;
	private File sqlFile;

	public SQLite(Logger log, String prefix, String name, File file) {
		super(log, prefix, "[SQLite] ");
		this.name = name;

		if (name.contains("/") || name.contains("\\") || name.endsWith(".db")) {
			writeError("The database name cannot contain: /, \\, or .db", true);
		}
		if (!file.exists()) {
			file.mkdir();
		}

		sqlFile = new File(file.getAbsolutePath() + File.separator + name + ".db");
	}

	public SQLite(Logger logger, String prefix, String name2, String absolutePath) {
		// TODO Auto-generated constructor stub
	}

	protected boolean initialize() {
		try {
			Class.forName("org.sqlite.JDBC");

			return true;
		} catch (ClassNotFoundException e) {
			writeError("Class not found in initialize(): " + e, true);
			return false;
		}
	}

	@Override
	public Connection open() {
		if (initialize()) {
			try {
				this.connection = DriverManager.getConnection("jdbc:sqlite:" + sqlFile.getAbsolutePath());
				return this.connection;
			} catch (SQLException e) {
				this.writeError("SQL exception in open(): " + e, true);
			}
		}
		return null;
	}

	@Override
	public boolean close() {
		boolean rtnstr = false;
		if (connection != null)
			try {
				connection.close();
				rtnstr = true;
			} catch (SQLException ex) {
				this.writeError("SQL exception in close(): " + ex, true);
				rtnstr = false;
			}

		return rtnstr;
	}

	@Override
	public Connection getConnection() {
		if (this.connection == null)
			return open();
		return this.connection;
	}

	@Override
	public boolean checkConnection() {
		if (connection != null)
			return true;
		return false;
	}

	@Override
	public ResultSet query(String query) {
		Statement statement = null;
		ResultSet result = null;

		try {
			connection = this.open();
			statement = connection.createStatement();
			result = statement.executeQuery("SELECT date('now')");

			switch (this.getStatement(query)) {
			case SELECT:
				result = statement.executeQuery(query);
				break;

			case INSERT:
				statement.executeUpdate(query);
				result = null;
				break;
			case UPDATE:
				statement.executeUpdate(query);
				result = null;
				break;
			case DELETE:
				statement.executeUpdate(query);
				result = null;
				break;
			case CREATE:
			case ALTER:
			case DROP:
			case TRUNCATE:
			case RENAME:
			case DO:
			case REPLACE:
			case LOAD:
			case HANDLER:
			case CALL:
				this.lastUpdate = statement.executeUpdate(query);
				break;

			default:
				result = statement.executeQuery(query);

			}
			return result;
		} catch (SQLException e) {
			if (e.getMessage().toLowerCase().contains("locking") || e.getMessage().toLowerCase().contains("locked")) {
				return retry(query);
			} else {
				this.writeError("SQL exception in query(): " + e.getMessage() + e.getClass(), false);
			}

		}
		return null;
	}

	@Override
	public PreparedStatement prepare(String query) {
		try {
			connection = open();
			PreparedStatement ps = connection.prepareStatement(query);
			return ps;
		} catch (SQLException e) {
			if (!e.toString().contains("not return ResultSet"))
				this.writeError("SQL exception in prepare(): " + e.getMessage(), false);
		}
		return null;
	}

	@Override
	public boolean createTable(String query) {
		Statement statement = null;
		try {
			if (query.equals("") || query == null) {
				this.writeError("Parameter 'query' empty or null in createTable().", true);
				return false;
			}

			statement = connection.createStatement();
			statement.execute(query);
			statement.close();
			return true;
		} catch (SQLException e) {
			this.writeError(e.getMessage(), true);

			return false;
		}
	}

	@Override
	public boolean checkTable(String table) {
		DatabaseMetaData dbm = null;
		boolean returnstr;
		try {
			dbm = this.open().getMetaData();
			ResultSet tables = dbm.getTables(null, null, table, null);
			if (tables.next()) {
				returnstr = true;
			} else {
				returnstr = false;

			}
			tables.close();
		} catch (SQLException e) {
			this.writeError("Failed to check if table \"" + table + "\" exists: " + e.getMessage(), true);
			return false;
		}
		return returnstr;
	}

	@Override
	public boolean wipeTable(String table) {
		Statement statement = null;
		String query = null;
		try {
			if (!this.checkTable(table)) {
				this.writeError("Table \"" + table + "\" in wipeTable() does not exist.", true);
				return false;
			}
			statement = connection.createStatement();
			query = "DELETE FROM " + table + ";";
			statement.executeQuery(query);
			return true;
		} catch (SQLException ex) {
			if (!(ex.getMessage().toLowerCase().contains("locking") || ex.getMessage().toLowerCase().contains("locked"))
					&& !ex.toString().contains("not return ResultSet"))
				this.writeError("Error at SQL Wipe Table Query: " + ex, false);
			return false;
		}
	}

	/*
	 * <b>retry</b><br> <br> Retries a statement and returns a ResultSet. <br> <br>
	 * 
	 * @param query The SQL query to retry.
	 * 
	 * @return The SQL query result.
	 */
	public ResultSet retry(String query) {
		Statement statement = null;
		ResultSet result = null;

		try {
			statement = connection.createStatement();
			result = statement.executeQuery(query);
			return result;
		} catch (SQLException ex) {
			if (ex.getMessage().toLowerCase().contains("locking") || ex.getMessage().toLowerCase().contains("locked")) {
				this.writeError("Please close your previous ResultSet to run the query: \n\t" + query, false);
			} else {
				this.writeError("SQL exception in retry(): " + ex.getMessage(), false);
			}
		}

		return null;
	}
}