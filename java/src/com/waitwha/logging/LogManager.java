package com.waitwha.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * <b>ApacheCheck</b><br/>
 * <small>Copyright (c)2013 Mike Duncan <a href="mailto:mike.duncan@waitwha.com">mike.duncan@waitwha.com</a></small><p />
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
 * @package com.waitwha.logging
 */
public class LogManager {
	
	private static boolean setup = false;
	
	/**
	 * Checks to see if a file exists within user.dir by the name of
	 * logging.properties. If so, the file is read using 
	 * LogManager.readConfiguration. Otherwise, an included resource 
	 * is read for this configuration.
	 * 
	 * @see java.util.logging.LogManager#readConfiguration(java.io.InputStream)
	 */
	private static final void setup()  {
		//Initial configuration
		try {
			String path = LogManager.class.getPackage().getName().replace(".", "/");
			java.util.logging.LogManager.getLogManager().readConfiguration(
					LogManager.class.getResourceAsStream(String.format("/%s/logging.properties", path)));
		
		}catch(SecurityException e) {
			e.printStackTrace();
		
		}catch(IOException e) {
			e.printStackTrace();
		
		}
		
		//User overrides
		File altConfig = new File(System.getProperty("user.dir"), "logging.properties");
		if(altConfig.exists())  {
			try {
				java.util.logging.LogManager.getLogManager().readConfiguration(new FileInputStream(altConfig));
			
			}catch(SecurityException e) {	
				e.printStackTrace();
				
			}catch(FileNotFoundException e) {
				e.printStackTrace();
			
			}catch(IOException e) {
				e.printStackTrace();
			
			}
			
		}
		
		setup = true;
	}
	
	/**
	 * Generates a Logger instance from the clazzName given. This will 
	 * ensure that (1) the Logger instance is set to Level.INFO and (2)
	 * create a FileHandler instance for logging to a file (if possible)
	 * and (3) ensure that all Handler's are using the 
	 * StandardLoggingFormatter Formatter implementation.
	 * 
	 * @param clazzName	Name of the class generating logging.
	 * @return	Logger
	 */
	public static final Logger getLogger(String clazzName)  {
		if(!setup)
			setup();
		
		return java.util.logging.Logger.getLogger(clazzName);
	}
	
	public static final Logger getLogger(Class<?> clazz)  {
		return LogManager.getLogger(clazz.getClass().getName());
	}
}
