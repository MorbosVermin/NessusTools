package com.waitwha.logging;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

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
public class StandardLoggingFormatter extends Formatter {

	public static final SimpleDateFormat UNIX_DATE_FORMAT = 
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
	 */
	@Override
	public String format(LogRecord record) {
		return String.format("[%s] %s %s (%s.%s)\n", 
				UNIX_DATE_FORMAT.format(new Date()), 
				record.getLevel().toString().toUpperCase(), 
				record.getMessage(),
				record.getSourceClassName(),
				record.getSourceMethodName());
	}

}
