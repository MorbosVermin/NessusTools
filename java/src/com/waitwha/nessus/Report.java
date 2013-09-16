package com.waitwha.nessus;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.waitwha.logging.LogManager;
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
 * Report is the container of the findings of this scan. It will contain one or more
 * ReportHost objects which represent the hosts scanned and contain properties and 
 * items within to display the vulnerability and plugin information.
 *
 * @author Mike Duncan <mike.duncan@waitwha.com>
 * @version $Id$
 * @package com.waitwha.nessus
 */
public class Report {
	
	private static final Logger log = LogManager.getLogger(Report.class.getName());
	
	public class Tag  {
		
		private String name;
		private String value;
		
		public Tag(Element tag)  {
			this.name = tag.getAttribute("name");
			this.value = tag.getTextContent();
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}
		
	}
	
	/**
	 * HostProperties are properties for the ReportHost such as 
	 * OS and timestamps of scans.
	 * 
	 */
	public class HostProperties extends ArrayList<Tag>  {

		private static final long serialVersionUID = 1L;
		
		public HostProperties(Element e)  {
			NodeList tags = e.getElementsByTagName("tag");
			for(int i = 0; i < tags.getLength(); i++)  {
				Element tag = (Element)tags.item(i);
				this.add(new Tag(tag));
			}
		}
		
	}
	
	/**
	 * Represents a ReportItem element, which contains the port, protocol, and 
	 * information about the service and vulnerability for that service. 
	 *  
	 */
	public class ReportItem  {
		
		private int port;
		private String protocol;
		private String serviceName;
		private int severity;
		private int pluginId;
		private String pluginName;
		private String pluginFamily;
		private String description;
		private String fileName;
		private String pluginModificationDate;
		private String pluginType;
		private String pluginPublicationDate;
		private String riskFactor;
		private String solution;
		private String synopsis;
		private String pluginOutput;
		private String scriptVersion;
		
		public ReportItem(Element reportItem)  {
			//There have been instances where these do not exist.
			this.solution = getValue(reportItem, "solution");
			this.pluginOutput = getValue(reportItem, "plugin_output");
			
			try  {
				this.port = Integer.parseInt(reportItem.getAttribute("port"));
				this.protocol = reportItem.getAttribute("protocol");
				this.serviceName = reportItem.getAttribute("svc_name");
				this.severity = Integer.parseInt(reportItem.getAttribute("severity"));
				this.pluginName = reportItem.getAttribute("pluginName");
				this.pluginId = Integer.parseInt(reportItem.getAttribute("pluginID"));
				this.pluginFamily = reportItem.getAttribute("pluginFamily");
				
				this.description = ElementUtils.getElementValue(reportItem, "description");
				this.fileName = ElementUtils.getElementValue(reportItem, "fname");
				this.scriptVersion = ElementUtils.getElementValue(reportItem, "script_version");
				this.pluginModificationDate = ElementUtils.getElementValue(reportItem, "plugin_modification_date");
				this.pluginPublicationDate = ElementUtils.getElementValue(reportItem, "plugin_publication_date");
				this.riskFactor = ElementUtils.getElementValue(reportItem, "risk_factor");
				
			}catch(NumberFormatException nfe)  {
				log.warning("Could not parse some numerics from report item: "+ reportItem.getAttribute("protocol") +"/"+ reportItem.getAttribute("port"));
				
			}catch(ElementNotFoundException ex)  {
				log.warning(ex.getMessage());
				
			}
		}
		
		/**
		 * Returns the text content of the element by the given name, which is a child 
		 * element of the element (e) given. This function uses ElementUtils.getElementValue(),
		 * however if an Exception is thrown, we default the value returned to blank. 
		 * 
		 * @param e		Parent element.
		 * @param name	Name of the child element to grab the text content of.
		 * @see ElementUtils#getElementValue(Element, String)
		 * @return
		 */
		private String getValue(Element e, String name)  {
			String value = "";
			
			try  {
				value = ElementUtils.getElementValue(e, name);
				
			}catch(ElementNotFoundException ex)  {
				log.fine(String.format("Defaulting value of %s/%s to blank; %s", e.getTagName(), name, ex.getMessage()));
				
			}
			
			return value;
		}

		/**
		 * @return the port
		 */
		public int getPort() {
			return port;
		}

		/**
		 * @return the protocol
		 */
		public String getProtocol() {
			return protocol;
		}

		/**
		 * @return the serviceName
		 */
		public String getServiceName() {
			return serviceName;
		}

		/**
		 * @return the severity
		 */
		public int getSeverity() {
			return severity;
		}

		/**
		 * @return the pluginId
		 */
		public int getPluginId() {
			return pluginId;
		}

		/**
		 * @return the pluginName
		 */
		public String getPluginName() {
			return pluginName;
		}

		/**
		 * @return the pluginFamily
		 */
		public String getPluginFamily() {
			return pluginFamily;
		}

		/**
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * @return the fileName
		 */
		public String getFileName() {
			return fileName;
		}

		/**
		 * @return the pluginModificationDate
		 */
		public String getPluginModificationDate() {
			return pluginModificationDate;
		}

		/**
		 * @return the pluginType
		 */
		public String getPluginType() {
			return pluginType;
		}

		/**
		 * @return the pluginPublicationDate
		 */
		public String getPluginPublicationDate() {
			return pluginPublicationDate;
		}

		/**
		 * @return the riskFactor
		 */
		public String getRiskFactor() {
			return riskFactor;
		}

		/**
		 * @return the solution
		 */
		public String getSolution() {
			return solution;
		}

		/**
		 * @return the synopsis
		 */
		public String getSynopsis() {
			return synopsis;
		}

		/**
		 * @return the pluginOutput
		 */
		public String getPluginOutput() {
			return pluginOutput;
		}
		
		/**
		 * @return the scriptVersion
		 */
		public String getScriptVersion() {
			return scriptVersion;
		}

		@Override
		public String toString()  {
			return String.format("[%s] '%s' on port %s/%d; severity = %d: %s", 
					this.riskFactor, this.pluginName, this.protocol, this.port, this.severity, this.description.substring(0,50));
		}
		
	}
	
	public class ReportItems extends ArrayList<ReportItem>  {

		private static final long serialVersionUID = 1L;
		
		public ReportItems(Element reportHost)  {
			super();
			NodeList items = reportHost.getElementsByTagName("ReportItem");
			for(int i = 0; i < items.getLength(); i++)  {
				Element reportItem = (Element)items.item(i);
				this.add(new ReportItem(reportItem));
			}
		}
	}
	
	/**
	 * ReportHost is the actual host object of this scan. This is 
	 * the container object for the HostProperties and ReportItems
	 * which show the actual vulnerabilities (as reported by 
	 * Nessus) for this host.
	 * 
	 */
	public class ReportHost {
		
		private String name;
		private HostProperties hostProperties;
		private ReportItems reportItems;
		
		public ReportHost(Element e)  {
			this.name = e.getAttribute("name");
			log.fine(String.format("Parsing report host '%s'.", this.name));
			
			try  {
				this.hostProperties = new HostProperties(ElementUtils.getFirstElementByName(e, "HostProperties"));
				log.fine(String.format("Parsed %d host properties for host '%s'.", this.hostProperties.size(), this.name));
			
			}catch(ElementNotFoundException ex)  {
				log.warning(ex.getMessage());
			}
			
			this.reportItems = new ReportItems(e);
			log.fine(String.format("Parsed %d report items for host '%s'.", this.reportItems.size(), this.name));
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the hostProperties
		 */
		public HostProperties getHostProperties() {
			return hostProperties;
		}

		/**
		 * @return the reportItems
		 */
		public ReportItems getReportItems() {
			return reportItems;
		}
		
		public String getOS()  {
			for(Tag tag : this.hostProperties)
				if(tag.getName().equals("operating-system"))
					return tag.getValue();
			
			return "(unknown)";
		}
		
		/**
		 * An attempt at assigning an overall severity to this host, this will go through
		 * and add up the total severity from the ReportItems and then minus this value
		 * from the total number of ReportItems. This should hopefully better represent 
		 * the severity of this host by (1) producing a sum based on all of the ReportItems 
		 * and (2) by only really paying attention to the higher severities (None = 0). 
		 * 
		 * @return	int	Overall severity of this host based on the ReportItems.
		 */
		public int getOverallSeverity()  {
			int total = 0;
			for(ReportItem reportItem : this.reportItems)
				total += reportItem.getSeverity();
			
			log.fine(String.format("Severity of host %s is %d (of %d).", this.name, total, reportItems.size())); 
			return (reportItems.size() - total);
		}
		
		@Override
		public String toString()  {
			return String.format("%s (%d ports, '%s', %d overall severity)", 
					this.name,
					this.reportItems.size(),
					this.getOS().trim().replaceAll("\\n", " "),
					this.getOverallSeverity());
		}
		
	}
	
	private ArrayList<ReportHost> reportHosts;
	
	/**
	 * Constructor
	 *
	 * @param report	Report element.
	 */
	public Report(Element report)  {
		this.reportHosts = new ArrayList<ReportHost>();
		NodeList reportHosts = report.getElementsByTagName("ReportHost");
		for(int i = 0; i < reportHosts.getLength(); i++)  {
			Element reportHost = (Element)reportHosts.item(i);
			this.reportHosts.add(new ReportHost(reportHost));
		}
		
		log.fine(String.format("Parsed %d hosts.", this.reportHosts.size()));
	}

	/**
	 * @return the reportHosts
	 */
	public ArrayList<ReportHost> getReportHosts() {
		return reportHosts;
	}
	
}
