package com.waitwha.nessus.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import com.waitwha.nessus.NessusClientData;
import com.waitwha.nessus.Report.ReportHost;
import com.waitwha.nessus.Report.ReportItem;

/**
 * <b>Nessus Trend Analyzer</b>: SqliteDatabase<br/>
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
 * Database abstraction layer for Sqlite databases. This uses the Sqlite JDBC driver 
 * available from https://bitbucket.org/xerial/sqlite-jdbc. 
 *
 * @author Mike Duncan <mike.duncan@waitwha.com>
 * @version $Id$
 * @package com.waitwha.nessus.trendanalyzer.db
 */
public class SqliteDatabase extends Database {
	
	private String path;
	
	/**
	 * Constructor for using a database file on the filesystem at the 
	 * given path.
	 *
	 * @param path	String path to the database file.
	 * @throws DatabaseInitializationException
	 */
	public SqliteDatabase(String path) throws DatabaseInitializationException  {
		super("org.sqlite.JDBC", "jdbc:sqlite:"+ path, "", "", true);
		this.path = path;
	}
	
	/**
	 * Constructor for an in-memory database.
	 *
	 * @throws DatabaseInitializationException
	 */
	public SqliteDatabase() throws DatabaseInitializationException  {
		super("org.sqlite.JDBC", "jdbc:sqlite::memory:", "", "", true);
		this.path = "";
	}
	
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	@Override
	public void connect()  {
		super.connect();
		if(this.isCreate())  {
			/*
			Statement stmt = null;
			try {
				stmt = this.connection.createStatement();
				stmt.executeUpdate("DROP TABLE IF EXISTS scans");
				stmt.executeUpdate("DROP TABLE IF EXISTS hosts");
				stmt.executeUpdate("DROP TABLE IF EXISTS vulns");
				stmt.executeUpdate("DROP TABLE IF EXISTS admins");
				//TODO Create the schema.
				
			}catch(SQLException e) {
				log.warning(String.format("Could not prep/setup database schema: %s", e.getMessage()));
			
			}finally{
				try  {
					stmt.close();
				}catch(SQLException e) {}
				
			}
			*/
			
		}
	}

	/**
	 * @see com.waitwha.nessus.db.Database#importScan(com.waitwha.nessus.NessusClientData)
	 */
	@Override
	public void importScan(NessusClientData scan) {
		PreparedStatement pstmt = null;
		try  {
			String scanUuid = UUID.randomUUID().toString();
			pstmt = connection.prepareStatement("INSERT INTO scans (UUID, NAME) VALUES(?,?)");
			pstmt.setString(1, scanUuid);
			pstmt.setString(2, scan.getPolicy().getName());
			pstmt.executeUpdate();
			pstmt.close();
			log.finest(String.format("Successfully added scan record: %s", scanUuid));
			
			for(ReportHost host : scan.getReport().getReportHosts())  {
				String hostUuid = UUID.randomUUID().toString();
				pstmt = connection.prepareStatement("INSERT INTO hosts (UUID, SCAN_UUID, HOSTNAME, IP) VALUES(?,?,?,?)");
				pstmt.setString(1, hostUuid);
				pstmt.setString(2, scanUuid);
				pstmt.setString(3, host.getName());
				pstmt.setString(4, host.getAddress());
				pstmt.executeUpdate();
				pstmt.close();
				log.finest(String.format("Successfully added host '%s' to scan '%s'.", host.getName(), scanUuid));
				
				for(ReportItem item : host.getReportItems())  {
					String vulnUuid = UUID.randomUUID().toString();
					pstmt = connection.prepareStatement("INSERT INTO vulns (UUID, SCAN_UUID, HOST_UUID, DESCRIPTION, CRITICALITY, SEVERITY, SOLUTION, SYNOPSIS, PLUGIN_NAME, PLUGIN_FAMILY) VALUES(?,?,?,?,?,?,?,?,?,?)");
					pstmt.setString(1, vulnUuid);
					pstmt.setString(2, scanUuid);
					pstmt.setString(3, hostUuid);
					pstmt.setString(4, item.getDescription());
					pstmt.setString(5, item.getRiskFactor());
					pstmt.setInt(6, item.getSeverity());
					pstmt.setString(7, item.getPluginName());
					pstmt.setString(8, item.getPluginFamily());
					pstmt.executeUpdate();
					pstmt.close();
					log.finest(String.format("Successfully added vulnerability record '%s' for host '%s' and scan '%s'.", vulnUuid, host.getName(), scanUuid));
					
				}
			}
			
		}catch(SQLException e)  {
			log.warning(String.format("Could not import scan: %s", e.getMessage()));
			
		}finally{
			if(pstmt != null)  {
				try  {
					pstmt.close();
				}catch(SQLException e) {}
			}
			
		}
	}

}
