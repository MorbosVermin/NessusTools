package com.waitwha.nessus.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.waitwha.logging.LogManager;
import com.waitwha.nessus.NessusClientData;

/**
 * <b>Nessus Trend Analyzer</b>: Database<br/>
 * <small>Copyright (c)2013 Mike Duncan &lt;<a href="mailto:mike.duncan@waitwha.com">mike.duncan@waitwha.com</a>&gt;</small><p />
 *
 * <pre>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * </pre>
 *
 * TODO Document this class/interface.
 *
 * @author Mike Duncan <mike.duncan@waitwha.com>
 * @version $Id$
 * @package com.waitwha.nessus.trendanalyzer.db
 */
public abstract class Database {
	
	/**
	 * Reusable Logger instance.
	 */
	protected final Logger log = LogManager.getLogger(Database.class.getName());
	
	private String uri;
	private String username;
	private String password;
	private boolean create;
	
	/**
	 * SQL Connection
	 */
	protected Connection connection;
	
	public Database(String driverName, String uri, String username, String password, boolean create) throws DatabaseInitializationException  {
		this.uri = uri;
		this.username = username;
		this.password = password;
		this.create = create;
		this.connection = null;
		
		try  {
			Class.forName(driverName);
			
		}catch(Exception e)  {
			log.warning(String.format("Could not load JDBC driver %s: %s %s", driverName, e.getClass().getName(), e.getMessage()));
			throw new DatabaseInitializationException(driverName);
		
		}
	}
	
	/**
	 * @return Connection
	 */
	protected Connection getConnection()  {
		return this.connection;
	}

	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the create
	 */
	public boolean isCreate() {
		return create;
	}
	
	/**
	 * Returns whether or not the database connection has been made.
	 * 
	 * @return	boolean
	 */
	public boolean isConnected()  {
		return (this.connection != null);
	}
	
	/**
	 * Connects to the underlying database layer.
	 * 
	 */
	public void connect()  {
		try {
			this.connection = DriverManager.getConnection(this.uri, this.username, this.password);
			log.finest(String.format("Connected to database: %s", this.uri));
			
		}catch(SQLException e) {
			log.warning(String.format("Could not connect to database '%s': %s %s", this.uri, e.getClass().getName(), e.getMessage()));

		}
	}
	
	/**
	 * Disconnects from the database.
	 * 
	 */
	public void close()  {
		if(this.connection != null)  {
			try  {
				this.connection.close();
			}catch(SQLException e) {}
		}
	}
	
	/**
	 * Imports a NessusClientData (scan) into the database.
	 * 
	 * @param scan	NessusClientData to import.
	 */
	public abstract void importScan(NessusClientData scan);
	
}
