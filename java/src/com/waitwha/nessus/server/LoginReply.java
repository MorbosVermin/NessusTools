package com.waitwha.nessus.server;

import java.util.Date;

import org.w3c.dom.Element;

import com.waitwha.xml.ElementNotFoundException;
import com.waitwha.xml.ElementUtils;

/**
 * <b>NessusTools</b>: LoginReply<br/>
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
public class LoginReply extends ServerReply implements ReplyContents {

	private User user;
	private String token;
	private String serverUuid;
	private String pluginSet;
	private String loadedPluginSet;
	private Date scannerBootTime;
	private boolean msp;
	private int idleTimeout;
	
	public LoginReply(Element reply) {
		super(reply);
		try  {
			Element contents = ElementUtils.getFirstElementByName(reply, "contents");
			this.token = ElementUtils.getElementValue(contents, "token");
			this.user = new User(ElementUtils.getFirstElementByName(contents, "user"));
			this.serverUuid = ElementUtils.getElementValue(contents, "server_uuid");
			this.pluginSet = ElementUtils.getElementValue(contents, "plugin_set");
			this.loadedPluginSet = ElementUtils.getElementValue(contents, "loaded_plugin_set");
			
			long scannerBootTime = Long.parseLong(ElementUtils.getElementValue(contents, "scanner_boottime"));
			scannerBootTime = scannerBootTime * 1000;
			this.scannerBootTime = new Date(scannerBootTime);
			
			this.msp = Boolean.parseBoolean(ElementUtils.getElementValue(contents, "msp"));
			this.idleTimeout = Integer.parseInt(ElementUtils.getElementValue(contents, "idle_timeout"));
			
		}catch(ElementNotFoundException e)  {
			
			
		}
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @return the serverUuid
	 */
	public String getServerUuid() {
		return serverUuid;
	}

	/**
	 * @return the pluginSet
	 */
	public String getPluginSet() {
		return pluginSet;
	}

	/**
	 * @return the loadedPluginSet
	 */
	public String getLoadedPluginSet() {
		return loadedPluginSet;
	}

	/**
	 * @return the scannerBootTime
	 */
	public Date getScannerBootTime() {
		return scannerBootTime;
	}

	/**
	 * @return the msp
	 */
	public boolean isMsp() {
		return msp;
	}

	/**
	 * @return the idleTimeout
	 */
	public int getIdleTimeout() {
		return idleTimeout;
	}

	/**
	 * @see com.waitwha.nessus.server.ServerReply#getContents()
	 */
	@Override
	public ReplyContents getContents() {
		return this;
	}

}
