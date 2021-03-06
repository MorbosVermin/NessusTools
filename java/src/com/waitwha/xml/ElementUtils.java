package com.waitwha.xml;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * <b>NessusTools</b>: ElementUtils<br/>
 * <small>Copyright (c)2013 Mike Duncan &lt;<a href="mailto:mike.duncan@waitwha.com">mike.duncan@waitwha.com</a>&gt;</small><p />
 *
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
 *
 * TODO Document this class/interface.
 *
 * @author Mike Duncan <mike.duncan@waitwha.com>
 * @version $Id$
 * @package com.waitwha.xml
 */
public class ElementUtils {

	public static final Element getFirstElementByName(Element e, String name) throws ElementNotFoundException  {
		NodeList list = e.getElementsByTagName(name);
		if(list.getLength() == 0)
			throw new ElementNotFoundException(e, name);
		
		return (Element)list.item(0);
	}
	
	public static final String getElementValue(Element e, String childElementName) throws ElementNotFoundException  {
		Element child = ElementUtils.getFirstElementByName(e, childElementName);
		return child.getTextContent();
	}
	
}
