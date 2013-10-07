package com.waitwha.util;

import java.util.Hashtable;

/**
 * <b>NessusTools</b>: Scoreboard<br/>
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
 * @package com.waitwha.util
 */
public class Scoreboard extends Hashtable<String, Integer> {

	private static final long serialVersionUID = 8936694719984288267L;

	/**
	 * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public synchronized Integer put(String key, Integer value) {
		if(this.containsKey(key))  {
			value = this.get(key);
			value++;
		}
		
		if(value == null)
			value = new Integer(1);
		
		return super.put(key, value);
	}
	
	public Integer score(String key)  {
		return this.put(key, null);
	}

}
