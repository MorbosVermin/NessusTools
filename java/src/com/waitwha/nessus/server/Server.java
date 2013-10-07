package com.waitwha.nessus.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.waitwha.logging.LogManager;
import com.waitwha.nessus.NessusClientData;
import com.waitwha.xml.ElementNotFoundException;
import com.waitwha.xml.ElementUtils;

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
 * Represents a Nessus Server/Scanner.
 *
 * @author Mike Duncan <mike.duncan@waitwha.com>
 * @version $Id$
 * @package com.waitwha.nessus.server
 */
@SuppressWarnings("deprecation")
public class Server extends DefaultHttpResponseParserFactory {
	
	private static final Logger log = LogManager.getLogger(Server.class.getName());
	
	private class MyTrustStrategy implements TrustStrategy  {

		@Override
		public boolean isTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
			log.finest(String.format("Authorized SSL/TLS connection using %s", arg1));
			return true;
		}
		
	}
	
	public static final String DEFAULT_URL = "https://localhost:8834";
	public static final String DEFAULT_USER_AGENT = "NessusServerAPI4Java v1.0";
	private String url;
	private BasicHttpClientConnectionManager connectionManager;
	private CookieStore cookieStore;
	private DocumentBuilder builder;
	
	/**
	 * Constructor
	 *
	 * @param url	End-point URL of the Nessus Server. (i.e. https://localhost:8834)
	 */
	public Server(final String url)  {
		this.url = url;
		
		/*
		 * Configure XML parsing.
		 */
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			this.builder = factory.newDocumentBuilder();
			log.finest(String.format("Successfully configured XML parsing using builder: %s", this.builder.getClass().getName()));
			
		}catch(ParserConfigurationException e) {
			log.warning(String.format("Could not configure XML parsing: %s", e.getMessage()));
			
		}
		
		/*
		 * Setup SSL for HttpClient configurations. Here we will configure SSL/TLS to 
		 * accept all hosts (no verification on certificates). This is because Nessus by
		 * default used a self-generate CA and certificate for the servers. So, a simple 
		 * self-signed-strategy will not work as we are not dealing with strictly 
		 * self-signed certs, but ones generated and signed by a self-generated CA. 
		 * 
		 * TODO Perhaps the serial number of the CA is always the same so in the future we
		 * could use a strategy to only accept certs by this one serial.
		 * 
		 * See http://hc.apache.org/httpcomponents-client-ga/httpclient/examples/org/apache/http/examples/client/ClientConfiguration.java.
		 * 
		 * TODO We need to work on the code here to be more up-to-date. SSLSocketFactory is deprecated, but 
		 * finding up-to-date docs on how to use SSLContext with a custom TrustStrategy and not using a KeyStore is
		 * not currently available.
		 */
		//SSLContext sslContext = SSLContexts.createSystemDefault();
		Registry<ConnectionSocketFactory> socketFactoryRegistry = null;
		try  {
			 socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
        .register("http", PlainConnectionSocketFactory.INSTANCE)
        .register("https", new SSLSocketFactory(new MyTrustStrategy(), SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER))
        .build();
			log.finest(String.format("Configured SSL/TLS connections for %s.", url));
			
		}catch(Exception e)  {
			log.warning(String.format("Could not configure SSL/TLS: %s %s", e.getClass().getName(), e.getMessage()));
			
		}
		
		SocketConfig socketConfig = SocketConfig.custom()
        .setTcpNoDelay(true)
        .build();
		this.connectionManager = new BasicHttpClientConnectionManager(socketFactoryRegistry);
		this.connectionManager.setSocketConfig(socketConfig);
		log.finest(String.format("Configured socket connections for %s.", url));
		
		this.cookieStore = new BasicCookieStore()  {

			private static final long serialVersionUID = 1L;

			/**
			 * @see org.apache.http.impl.client.BasicCookieStore#addCookie(org.apache.http.cookie.Cookie)
			 */
			@Override
			public synchronized void addCookie(Cookie cookie) {
				log.finest(String.format("[%s] Cookie added: %s=%s", url, cookie.getName(), cookie.getValue()));
				super.addCookie(cookie);
			}
			
		};
		log.finest(String.format("Configured default/basic cookie storage for connections to %s", url));
	
	}
	
	public Server()  {
		this(DEFAULT_URL);
	}
	
	public String getURL()  {
		return this.url;
	}
	

	@Override
	public String toString()  {
		return this.getURL().toString();
	}
	
	/**
	 * Returns a random number between 1 and 9999.
	 * 
	 * @return	int
	 */
	private int r()  {
		Random r = new Random(); //construct new instance with new seed.
		
		//nextInt() is inclusive of 0 (zero), however we must ensure N is at least 1.
		int n = r.nextInt(9999);
		while(n == 0)
			n = r.nextInt(9999);
		
		return n;
	}
	
	/**
	 * Returns a CloseableHttpClient object configured to use the ConnectionManager and CookieStore 
	 * built within the constructor.
	 * 
	 * @return	CloseableHttpClient
	 */
	private HttpClient getClient()  {
		return HttpClients.custom().setConnectionManager(this.connectionManager).setDefaultCookieStore(this.cookieStore).build();
	}
	
	/**
	 * Executes the given HttpPost using a HttpClient. If 200 OK is returned, 
	 * we will parse the contents and return a ServerReply implementation.
	 * 
	 * @param post	HttpPost to execute.
	 * @return	ServerReply
	 */
	public ServerReply getReply(HttpPost post)  {
		ServerReply reply = null;
		HttpClient client = this.getClient();
		
		try  {
	    log.finest(String.format("[%s] Executing POST.", post.getURI()));
			HttpResponse resp = client.execute(post);
	    
			try  {
				HttpEntity entity = resp.getEntity();
				
				if(resp.getStatusLine().getStatusCode() == 200)  {
					InputStream in = entity.getContent();
					log.finest(String.format("[%s] Received HTTP code %d: %dbytes (%s)", 
							post.getURI(), 
							resp.getStatusLine().getStatusCode(), 
							entity.getContentLength(), 
							entity.getContentType().getValue()));
					
					Document document = this.builder.parse(in);
					Element replyElement = (Element)document.getFirstChild();
					Element contents = ElementUtils.getFirstElementByName(replyElement, "contents");
					
					/*
					 * Test the first element found within the element 'contents'. If the
					 * element's name is 'token', then this is a LoginReply. However, if the
					 * name of the element is 'reports', this will mean this is a ReportListReply.
					 * 
					 */
					Element firstElement = (Element)contents.getFirstChild();
					if(firstElement == null)
						reply = new ErrorReply(replyElement);
					
					else if(firstElement.getTagName().equals("reports"))
						reply = new ReportListReply(replyElement);
					
					else
						reply = new LoginReply(replyElement);
					
					log.finest(String.format("[%s] Parsed XML succcessfully: %s", post.getURI(), reply.getClass().getName()));
					
				}else{
					log.warning(String.format("[%s] Received HTTP code %d. Skipping parsing of content.", post.getURI(), resp.getStatusLine().getStatusCode()));
					EntityUtils.consume(entity);
				}
				
			}catch(IOException | SAXException | ElementNotFoundException e)  {
				log.warning(String.format("Could read/parse reply from server %s: %s %s", this.url, e.getClass().getName(), e.getMessage()));
				
			}
			
    }catch(IOException e)  {
    	log.warning(String.format("Could not connect to server %s: %s", this.url, e.getMessage()));
    
    }
		
		return reply;
	}
	
	/**
	 * Performs a login on the server with the given username and password. If all 
	 * goes well, we should see a token cookie captured and used in further requests.
	 * Without this cookie, some requests (i.e. report listing and downloads) cannot
	 * be performed.
	 * 
	 * @param username		String username of the user to login as.
	 * @param password		String password of the user.
	 * @return	boolean
	 */
	public boolean login(String username, String password)  {
		String c = "/login";
		if(this.url.endsWith("/"))
			c = c.substring(1);
		
		/*
		 * Get a client, setup the POST, and execute.
		 */
		HttpPost post = new HttpPost(String.format("%s%s", this.url, c));
		post.addHeader("User-Agent", DEFAULT_USER_AGENT);
		
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
    nvps.add(new BasicNameValuePair("login", username));
    nvps.add(new BasicNameValuePair("password", password));
    nvps.add(new BasicNameValuePair("seq", r() +""));
    post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
		
    ServerReply reply = this.getReply(post);
    return (reply == null) ? false : reply.isOk();
	}
	
	/**
	 * Executes a logout function on the server, logging out the user mapped 
	 * internally (on the server) to the token given in the cookies.
	 * 
	 */
	public void logout()  {
		String c = "/logout";
		if(this.url.endsWith("/"))
			c = c.substring(1);
		
		/*
		 * Get a client, setup the POST, and execute.
		 */
		HttpPost post = new HttpPost(String.format("%s%s", this.url, c));
		post.addHeader("User-Agent", DEFAULT_USER_AGENT);
		
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
    nvps.add(new BasicNameValuePair("seq", r() +""));
    post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
    
    try  {
    	this.getClient().execute(post);
    }catch(IOException | IllegalStateException e) {}
	}
	
	/**
	 * Returns an ArrayList containing Report listings of scan results/reports
	 * on the server as the logged in user. It is very important that you 
	 * login prior to calling this method to actually get any results. You 
	 * can use the UUID of the report to download it using downloadReport.
	 * 
	 * @return	ArrayList<Report>
	 * @see #login(String, String)
	 * @see #downloadReport(String uuid, String path)
	 */
	public ArrayList<Report> getReports()  {
		String c = "/report/list";
		if(this.url.endsWith("/"))
			c = c.substring(1);
		
		/*
		 * Get a client, setup the POST, and execute.
		 */
		HttpPost post = new HttpPost(String.format("%s%s", this.url, c));
		post.addHeader("User-Agent", DEFAULT_USER_AGENT);
		
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
    nvps.add(new BasicNameValuePair("seq", r() +""));
    post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
    
    ReportListReply reply = (ReportListReply)this.getReply(post);
    return (reply != null) ? reply.getReports() : new ArrayList<Report>();
	}
	
	/**
	 * Downloads a report by the given uuid and saves it to the path given.
	 * 
	 * @param uuid		String UUID of the Nessus report/scan results to download.
	 * @param path		String path to save the document (XML).
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public NessusClientData downloadReport(String uuid, String path) throws ParserConfigurationException, SAXException  {
		String c = "/file/report/download";
		if(this.url.endsWith("/"))
			c = c.substring(1);
		
		NessusClientData scan = null;
		
		/*
		 * Get a client, setup the POST, and execute.
		 */
		HttpPost post = new HttpPost(String.format("%s%s", this.url, c));
		post.addHeader("User-Agent", DEFAULT_USER_AGENT);
		
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
    nvps.add(new BasicNameValuePair("seq", r() +""));
    nvps.add(new BasicNameValuePair("report", uuid));
    post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
    
		HttpClient client = this.getClient();
		
		try  {
	    log.finest(String.format("[%s] Executing POST.", post.getURI()));
			HttpResponse resp = client.execute(post);
	    
			try  {
				HttpEntity entity = resp.getEntity();
				
				if(resp.getStatusLine().getStatusCode() == 200)  {
					InputStream in = entity.getContent();
					log.finest(String.format("[%s] Received HTTP code %d: %dbytes (%s)", 
							post.getURI(), 
							resp.getStatusLine().getStatusCode(), 
							entity.getContentLength(), 
							entity.getContentType().getValue()));
					
					File file = new File(path);
					BufferedWriter writer = new BufferedWriter(new FileWriter(file));
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					String line = null;
					while((line = reader.readLine()) != null)  {
						writer.write(line);
						writer.flush();
					}
					writer.close();
					
					scan = NessusClientData.parse(file);
					EntityUtils.consumeQuietly(entity);
					
				}else{
					log.warning(String.format("[%s] Received HTTP code %d. Could not download report.", post.getURI(), resp.getStatusLine().getStatusCode()));
				}
			
			}catch(IOException e)  {
				log.warning(String.format("[%s] Could not download report: %s", post.getURI(), e.getMessage()));
			}
			
		}catch(IOException e)  {
			log.warning(String.format("[%s] Could not download report: %s", post.getURI(), e.getMessage()));
		}
		
		return scan;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String username = System.getProperty("user.name");
		String password = "";
		String url = "https://localhost:8834";
		String uuid = "";
		String output = "";
		
		for(int i = 0; i < args.length; i++)  {
			if(args[i].equals("-h"))  {
				url = args[(i + 1)];
				i++;
			}else if(args[i].equals("-p"))  {
				password = args[(i + 1)];
				i++;
			}else if(args[i].equals("-u"))  {
				username = args[(i + 1)];
				i++;
			}else if(args[i].equals("-d"))  {
				uuid = args[(i + 1)];
				i++;
			}else if(args[i].equals("-o"))  {
				output = args[(i + 1)];
				i++;
			}
		}
		
		if(uuid.length() > 0 && output.length() == 0)
			output = System.getProperty("user.dir") + File.pathSeparator + uuid +".nessus";
		
		Server server = new Server(url);
		if(server.login(username, password))  {
			System.out.println(String.format("Successfully logged in as '%s'.", username));
			
			if(uuid.length() == 0)  {
				System.out.println("Reports: ");
				ArrayList<Report> reports = server.getReports();
				for(Report report : reports)
					System.out.println(String.format(">> %s (%s)", report.getName(), report.getUuid()));
			
				if(reports.size() > 0)  {
					System.out.println();
					System.out.println("Note: Use -d <uuid> to download a report.");
				}
				
			}else{
				System.out.print("Downloading report "+ uuid +" -> "+ output +" ...");
				try {
					server.downloadReport(uuid, output);
					System.out.println("done.");
					
				}catch(ParserConfigurationException | SAXException e) {}
			}
			
			server.logout();
		}
	}

}
