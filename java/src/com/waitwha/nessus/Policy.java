package com.waitwha.nessus;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.waitwha.logging.LogManager;
import com.waitwha.xml.ElementNotFoundException;
import com.waitwha.xml.ElementUtils;

/**
 * <b>NessusTools</b>: Policy<br/>
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
 * Policy element of the NessusClientData (v2) object/scan file. This contains information
 * regarding the various options used on the server when the scan was performed. 
 *
 * @author Mike Duncan <mike.duncan@waitwha.com>
 * @version $Id$
 * @package com.waitwha.nessus
 */
public class Policy {
	
	private static Logger log = LogManager.getLogger(Policy.class.getName());
	
	public class Preference  {
		
		private String name;
		private String value;
		
		public Preference(String name, String value)  {
			this.name = name;
			this.value = value;
		}
		
		public Preference(Element e) throws ElementNotFoundException  {
			this(ElementUtils.getElementValue(e, "name"), ElementUtils.getElementValue(e, "value"));
		}
		
		public String getName()  {
			return this.name;
		}
		
		public String getValue()  {
			return this.value;
		}
		
		@Override
		public String toString()  {
			return this.getName();
		}
		
	}
	
	/**
	 * ServerPreferences contain Preference objects which show server options
	 * which were used during this scan.
	 * 
	 */
	public class ServerPreferences extends ArrayList<Preference>  {

		private static final long serialVersionUID = 1L;
		
		public ServerPreferences(Element e)  {
			super();
			try  {
				NodeList prefs = e.getElementsByTagName("preference");
				for(int i = 0; i < prefs.getLength(); i++)  {
					Element pref = (Element)prefs.item(i);
					this.add(new Preference(pref));
				}
				
			}catch(ElementNotFoundException ex)  {
				log.warning(ex.getMessage());
				
			}
		}
		
	}
	
	public class Item {
		
		private String pluginName;
		private int pluginId;
		private String fullName;
		private String preferenceName;
		private String preferenceType;
		private String preferenceValues;
		private String selectedValue;
		
		public Item(Element item)  {
			try  {
				this.pluginName = ElementUtils.getElementValue(item, "pluginName");
				this.fullName = ElementUtils.getElementValue(item, "fullName");
				this.preferenceName = ElementUtils.getElementValue(item, "preferenceName");
				this.preferenceType = ElementUtils.getElementValue(item, "preferenceType");
				this.preferenceValues = ElementUtils.getElementValue(item, "preferenceValues");
				this.selectedValue = ElementUtils.getElementValue(item, "selectedValue");
				this.pluginId = Integer.parseInt(ElementUtils.getElementValue(item, "pluginId"));
				
			}catch(NumberFormatException nfe)  {
				log.warning(String.format("Could not parse 'pluginId' from item: %s", nfe.getMessage()));
				
			}catch(ElementNotFoundException e)  {
				log.warning(e.getMessage());
				
			}
		}

		/**
		 * @return the pluginName
		 */
		public String getPluginName() {
			return pluginName;
		}

		/**
		 * @return the pluginId
		 */
		public int getPluginId() {
			return pluginId;
		}

		/**
		 * @return the fullName
		 */
		public String getFullName() {
			return fullName;
		}

		/**
		 * @return the preferenceName
		 */
		public String getPreferenceName() {
			return preferenceName;
		}

		/**
		 * @return the preferenceType
		 */
		public String getPreferenceType() {
			return preferenceType;
		}

		/**
		 * @return the preferenceValues
		 */
		public String getPreferenceValues() {
			return preferenceValues;
		}

		/**
		 * @return the selectedValue
		 */
		public String getSelectedValue() {
			return selectedValue;
		}
		
		@Override
		public String toString()  {
			return this.getPreferenceName();
		}
		
	}
	
	/**
	 * PluginPreferences is a container of various plugin preferences which were
	 * used for this scan.
	 * 
	 */
	public class PluginPreferences extends ArrayList<Item>  {

		private static final long serialVersionUID = 1L;
		
		public PluginPreferences(Element e)  {
			super();
			NodeList items = e.getElementsByTagName("item");
			for(int i = 0; i < items.getLength(); i++)  {
				Element item = (Element)items.item(i);
				this.add(new Item(item));
			}
		}
		
	}
	
	public class FamilyItem  {
		
		private String familyName;
		private String status;
		
		public FamilyItem(Element e)  {
			this.status = "";
			
			try  {
				this.familyName = ElementUtils.getElementValue(e, "FamilyName");
				this.status = ElementUtils.getElementValue(e, "status");
			
			}catch(ElementNotFoundException ex)  {
				log.fine(String.format("Could not find FamilyName or (more likely) status within FamilyItem. This can likely be ignored: %s", ex.getMessage()));
				
			}
		}

		/**
		 * @return the familyName
		 */
		public String getFamilyName() {
			return familyName;
		}

		/**
		 * @return the status
		 */
		public String getStatus() {
			return status;
		}
		
	}
	
	/**
	 * NessusClientData_v2 -> Policy -> FamilySelection
	 * 
	 * TODO Document this class.
	 * 
	 */
	public class FamilySelection extends ArrayList<FamilyItem>  {

		private static final long serialVersionUID = 1L;
		
		public FamilySelection(Element e)  {
			super();
			NodeList items = e.getElementsByTagName("FamilyItem");
			for(int i = 0; i < items.getLength(); i++)  {
				Element item = (Element)items.item(i);
				this.add(new FamilyItem(item));
			}
		}
		
	}

	private String name;
	private String comments;
	private ServerPreferences serverPrefs;
	private PluginPreferences pluginPrefs;
	private FamilySelection familySelection;
	
	public Policy(Element policy)  {
		try  {
			this.name = ElementUtils.getElementValue(policy, "policyName");
			log.fine(String.format("Parsing policy '%s'", this.name));
			
			Element preferences = ElementUtils.getFirstElementByName(policy, "Preferences");
			this.serverPrefs = new ServerPreferences(ElementUtils.getFirstElementByName(preferences, "ServerPreferences"));
			log.fine(String.format("Parsed %d ServerPreferences from policy '%s'", this.serverPrefs.size(), this.name));
			
			this.pluginPrefs = new PluginPreferences(ElementUtils.getFirstElementByName(preferences, "PluginsPreferences"));
			log.fine(String.format("Parsed %d PluginPreferences from policy '%s'", this.pluginPrefs.size(), this.name));
			
			Element familySelection = ElementUtils.getFirstElementByName(policy, "FamilySelection");
			this.familySelection = new FamilySelection(familySelection);
			log.fine(String.format("Parsed %d FamilySelection from policy '%s'", this.familySelection.size(), this.name));
			
		}catch(ElementNotFoundException e)  {
			log.warning(e.getMessage());
			
		}
	}

	/**
	 * @return the name of the policy.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the comments for this policy.
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @return the serverPrefs
	 */
	public ServerPreferences getServerPrefs() {
		return serverPrefs;
	}

	/**
	 * @return the pluginPrefs
	 */
	public PluginPreferences getPluginPrefs() {
		return pluginPrefs;
	}

	/**
	 * @return the familySelection
	 */
	public FamilySelection getFamilySelection() {
		return familySelection;
	}
	
}
