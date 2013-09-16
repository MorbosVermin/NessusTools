package com.waitwha.logging;

import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <b>ApacheCheck</b><br/>
 * <small>Copyright (c)2013 Mike Duncan <a href="mailto:mike.duncan@waitwha.com">mike.duncan@waitwha.com</a></small><p />
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
 * @package com.waitwha.logging
 */
public class LogManager {

	public static String APP_NAME = "appName";
	public static Level DEFAULT_LOG_LEVEL = Level.ALL;
	
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
		Logger log = java.util.logging.Logger.getLogger(clazzName);
		log.setLevel(LogManager.DEFAULT_LOG_LEVEL);
		
		StandardLoggingFormatter formatter = new StandardLoggingFormatter();
		for(Handler handler : log.getHandlers())  {
			handler.setLevel(LogManager.DEFAULT_LOG_LEVEL);
			handler.setFormatter(formatter);
		}
		
		if(APP_NAME.equals("appName"))
			return log; //No file logging enabled.
		
		try  {
			FileHandler logFileHandler = new FileHandler(APP_NAME +".log");
			logFileHandler.setLevel(LogManager.DEFAULT_LOG_LEVEL);
			logFileHandler.setFormatter(formatter);
			log.addHandler(logFileHandler);
			
		}catch(Exception e)  {
			log.warning("Could not open/read log file '"+ APP_NAME +".log'");
		}
		
		return log;
	}
	
}
