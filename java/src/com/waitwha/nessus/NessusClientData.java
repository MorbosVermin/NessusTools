package com.waitwha.nessus;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.waitwha.nessus.Report.ReportHost;
import com.waitwha.xml.ElementNotFoundException;
import com.waitwha.xml.ElementUtils;

/**
 * <b>NessusTools</b>: NessusClientData<br/>
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
 * @package com.waitwha.nessus
 */
public class NessusClientData implements Comparable<NessusClientData> {

	private Policy policy;
	private Report report;
	
	private NessusClientData(Document d) throws SAXException  {
		Element e = (Element)d.getFirstChild(); //root
		try  {
			this.policy = new Policy(ElementUtils.getFirstElementByName(e, "Policy"));
			this.report = new Report(ElementUtils.getFirstElementByName(e, "Report"));
			
		}catch(ElementNotFoundException ex)  {
			throw new SAXException("Could not find either Policy/Report elements: "+ ex.getMessage());
			
		}
	}
	
	public Report getReport()  {
		return this.report;
	}
	
	public Policy getPolicy()  {
		return this.policy;
	}
	
	/**
	 * Returns, if parsed successfully, a NessusClientData object from the File given.
	 * 
	 * @param f	File to parse.
	 * @return	NessusClientData
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static final NessusClientData getInstance(File f) throws ParserConfigurationException, SAXException, IOException  {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(f);
		return new NessusClientData(document);
	}
	
	/**
	 * Returns, if parsed successfully, a NessusClientData object from the File given.
	 * 
	 * @param f	File to parse.
	 * @return NessusClientData
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static final NessusClientData parse(File f) throws ParserConfigurationException, SAXException, IOException {
		return NessusClientData.getInstance(f);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			NessusClientData scan = NessusClientData.parse(new File(args[0]));
			for(ReportHost host : scan.getReport().getReportHosts())
				System.out.println(host);
			
		}catch(Exception e) {
			e.printStackTrace();
			
		}
	}

	
	@Override
	public int compareTo(NessusClientData o) {
		return this.getReport().compareTo(o.getReport());
	}

}
