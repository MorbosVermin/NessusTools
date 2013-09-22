package com.waitwha.nessus.server;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Element;

import com.waitwha.xml.ElementNotFoundException;
import com.waitwha.xml.ElementUtils;

/**
 * <b>NessusTools</b>: Report<br/>
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
 * A "report" is a scan result found 
 *
 * @author Mike Duncan <mike.duncan@waitwha.com>
 * @version $Id$
 * @package com.waitwha.nessus.server
 */
public class Report {

	/**
	 * Timestamp format.
	 */
	public static final SimpleDateFormat DATE_FORMAT = 
			new SimpleDateFormat("S");
	
	private String uuid;
	private String name;
	private String status;
	private Date timestamp;
	
	public Report(Element report)  {
		try  {
			this.uuid = ElementUtils.getElementValue(report, "name");
			this.status = ElementUtils.getElementValue(report, "status");
			this.name = ElementUtils.getElementValue(report, "readableName");
			
			long timestamp = Long.parseLong(ElementUtils.getElementValue(report, "timestamp"));
			timestamp = timestamp * 1000;
			this.timestamp = new Date(timestamp);
		
		}catch(ElementNotFoundException e)  {
			
		}
	}

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}
	
	@Override
	public String toString()  {
		return this.getName();
	}
	
}
