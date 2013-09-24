package com.waitwha.nessus.server;

import org.w3c.dom.Element;

import com.waitwha.xml.ElementNotFoundException;
import com.waitwha.xml.ElementUtils;

/**
 * <b>NessusTools</b>: ServerReply<br/>
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
 * Abstract class for reply's (responses) from the Server. All reply's have
 * sequence (i.e. seq) and status fields and then some bit of content (ReplyContent).
 *
 * @author Mike Duncan <mike.duncan@waitwha.com>
 * @version $Id$
 * @package com.waitwha.nessus.server
 */
public abstract class ServerReply {
	
	private int sequence;
	private String status;

	public ServerReply(Element reply)  {
		try {
			this.sequence = Integer.parseInt(ElementUtils.getElementValue(reply, "seq"));
			this.status = ElementUtils.getElementValue(reply, "status");
			
		}catch(ElementNotFoundException e) {
			
		}
	}
	
	/**
	 * @return the sequence
	 */
	public int getSequence() {
		return sequence;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	
	/**
	 * Returns whether or not the Reply has a status of "OK". If not, 
	 * this is likely a ErrorReply or an unhandled reply type.
	 * 
	 * @return boolean
	 */
	public boolean isOk()  {
		return this.status.equals("OK");
	}

	/**
	 * Returns the contents of the reply. This can be anything really and in 
	 * most cases for this codebase, it is the same class as a ServerReply 
	 * derived class. This is because currently the ReplyContents interface 
	 * is empty.
	 * 
	 * @return ReplyContents
	 */
	public abstract ReplyContents getContents();
	
}
