package com.waitwha.nessus.server;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.waitwha.xml.ElementNotFoundException;
import com.waitwha.xml.ElementUtils;

/**
 * <b>NessusTools</b>: ReportListReply<br/>
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
 * @package com.waitwha.nessus.server
 */
public class ReportListReply extends ServerReply implements ReplyContents {

	private ArrayList<Report> reports;
	
	public ReportListReply(Element reply) {
		super(reply);
		
		this.reports = new ArrayList<Report>();
		try  {
			Element contents = ElementUtils.getFirstElementByName(reply, "contents");
			Element reports = ElementUtils.getFirstElementByName(contents, "reports");
			NodeList r = reports.getElementsByTagName("report");
			for(int i = 0; i < r.getLength(); i++)
				this.reports.add(new Report((Element)r.item(i)));
			
		}catch(ElementNotFoundException e)  {
			
			
		}
	}
	
	public ArrayList<Report> getReports()  {
		return this.reports;
	}

	/**
	 * @see com.waitwha.nessus.server.ServerReply#getContents()
	 */
	@Override
	public ReplyContents getContents() {
		return this;
	}

}
