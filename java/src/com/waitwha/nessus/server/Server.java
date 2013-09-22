package com.waitwha.nessus.server;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

/**
 * <b>NessusTools</b>: Server<br/>
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
public class Server {
	
	private class AcceptAllHostnameVerifier implements HostnameVerifier  {

		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
		
	}
	
	private class AcceptAllX509TrustManager implements X509TrustManager  {

		private ArrayList<X509Certificate> certs;
		
		public AcceptAllX509TrustManager()  {
			this.certs = new ArrayList<X509Certificate>();
		}
		
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			for(X509Certificate cert : chain)
				this.certs.add(cert);
			
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			for(X509Certificate cert : chain)
				this.certs.add(cert);
			
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			X509Certificate[] certs = new X509Certificate[this.certs.size()];
			this.certs.toArray(certs);
			return certs;
		}
		
	}

	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
