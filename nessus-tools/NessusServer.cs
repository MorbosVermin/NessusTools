using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Security;
using System.Security.Cryptography.X509Certificates;
using System.IO;
using System.Xml.Linq;
using System.Net;
using System.Web;
using System.Xml.Serialization;
using System.Collections;

namespace nessus_tools
{

    public sealed class User
    {
        [XmlElement("name")]
        public string Name { get; set; }

        //[XmlElement("admin")]
        //public bool isAdmin { get; set; }

    }

    public sealed class ServerLoad
    {
        [XmlElement("num_scans")]
        public int ScanCount { get; set; }

        [XmlElement("num_sessions")]
        public int SessionCount { get; set; }

        [XmlElement("num_hosts")]
        public int HostCount { get; set; }

        [XmlElement("num_tcp_sessions")]
        public int TcpSessionCount { get; set; }

        [XmlElement("loadavg")]
        public double Average { get; set; }

    }

    public sealed class Content
    {
        [XmlElement("token")]
        public string Token { get; set; }

        [XmlElement("user")]
        public User User { get; set; }

        [XmlElement("server_uuid")]
        public string ServerUUID { get; set; }

        [XmlElement("plugin_set")]
        public string PluginSet { get; set; }

        [XmlElement("loaded_plugin_set")]
        public string LoadedPluginSet { get; set; }

        [XmlElement("scanner_boottime")]
        public int ScannerBoottime { get; set; }

        [XmlElement("load")]
        public ServerLoad ServerLoad { get; set; }

        [XmlElement("platform")]
        public string Platform { get; set; }

        //TODO Need to figure out how to handle boolean values correctly.
        //[XmlElement("msp")]
        //public bool MSP { get; set; }

        [XmlElement("idle_timeout")]
        public int IdleTimeout { get; set; }

        [XmlText]
        public string Text { get; set; }

        [XmlArray("reports")]
        [XmlArrayItem("report")]
        public List<NessusReport> Reports;

    }

    public sealed class NessusReport
    {
        [XmlElement("name")]
        public string Name { get; set; }

        [XmlElement("status")]
        public string Status { get; set; }

        [XmlElement("readableName")]
        public string ReadableName { get; set; }

        [XmlElement("timestamp")]
        public int Timestamp { get; set; }

    }

    [XmlRoot("reply")]
    public sealed class Reply
    {
        [XmlElement("seq")]
        public int Sequence { get; set; }

        [XmlElement("status")]
        public string Status { get; set; }

        [XmlElement("contents")]
        public Content Content { get; set; }

        public bool isError()
        {
            return Status.Equals("ERROR");
        }

        public bool isOK()
        {
            return Status.Equals("OK");
        }

        /// <summary>
        /// Parses an XDocument object given and returns a Reply object.
        /// </summary>
        /// <param name="doc">XDocument object to parse.</param>
        /// <returns>Reply</returns>
        public static Reply Parse(XDocument doc)
        {
            Reply reply = null;
            XmlSerializer serializer = new XmlSerializer(typeof(Reply));
            try
            {
                reply = (Reply)serializer.Deserialize(new StringReader(doc.ToString()));
            }
            catch (Exception e)
            {
                Console.WriteLine("Error: {0} {1}", e.GetType(), e.Message);
            }

            return reply;
        }
    
    }

    /// <summary>
    /// See http://static.tenable.com/documentation/nessus_5.0_XMLRPC_protocol_guide.pdf for more information.
    /// </summary>
    public class NessusServer : IDisposable
    {

        public string URI
        {
            get;
            set;
        }

        private int Random
        {
            get { return new Random().Next(1,9999); }
        }

        public string Token { get; set; }
        private bool disposing;

        public NessusServer(string URI)
        {
            this.URI = URI;
            if (this.URI.EndsWith("/"))
                this.URI = this.URI.Substring(0, this.URI.Length - 1);

            //Ensure that SSL will succeed through certificate validation.
            ServicePointManager.ServerCertificateValidationCallback += new RemoteCertificateValidationCallback(checkPolicy);
            this.disposing = false;
        }

        public NessusServer() : this("https://localhost:8834") { }

        /// <summary>
        /// Returns TRUE always. This is delegated to the ServerCertificateValidationCallback to ensure that all
        /// SSL/TLS trust issues are nullified before the connection begins.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="certificate"></param>
        /// <param name="chain"></param>
        /// <param name="sslPolicyErrors"></param>
        /// <returns>bool; TRUE always!</returns>
        private bool checkPolicy(object sender, X509Certificate certificate, X509Chain chain, SslPolicyErrors sslPolicyErrors)
        {
            return true;
        }

        /// <summary>
        /// Sends a request to the server and returns a XDocument as a reply.
        /// </summary>
        /// <param name="uri"></param>
        /// <param name="parameters"></param>
        /// <returns>XDocument</returns>
        private XDocument SendRequest(string uri, string parameters)
        {
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(uri);
            request.KeepAlive = true;
            request.ProtocolVersion = HttpVersion.Version10;
            request.Method = "POST";
            request.CookieContainer = new CookieContainer();

            if (Token != null)  
            {
                Uri u = new Uri(uri);
                string domain = u.Host;
                if (u.Host.Contains('.'))
                    domain = u.Host.Substring(u.Host.IndexOf('.'));
                
                request.CookieContainer.Add(new Cookie("token", Token, "/", domain));
            }

            byte[] postBytes = Encoding.ASCII.GetBytes(parameters);

            request.ContentType = "application/x-www-form-urlencoded";
            request.ContentLength = postBytes.Length;
            Stream requestStream = request.GetRequestStream();
            requestStream.Write(postBytes, 0, postBytes.Length);
            requestStream.Close();

            Logging.Log(LogLevel.Debug, "POST " + uri);

            XDocument xmldoc;
            // Watch for this, Nessus wont return XML in case of an error (like authentication)
            using (HttpWebResponse response = request.GetResponse() as HttpWebResponse)
            {
                Stream strm = response.GetResponseStream();

                StreamReader reader = new StreamReader(strm);
                xmldoc = XDocument.Parse(reader.ReadToEnd());
            }

            if (xmldoc != null)
                Logging.Log(LogLevel.Debug, xmldoc.ToString());

            return xmldoc;
        }

        /// <summary>
        /// Logon to the Nessus Server using the given username and password.
        /// </summary>
        /// <param name="username">Username</param>
        /// <param name="password">Password</param>
        /// <returns>bool</returns>
        public bool Login(string username, string password)
        {
            username = HttpUtility.UrlEncode(username);
            password = HttpUtility.UrlEncode(password);
            string p = String.Format("login={0}&password={1}&seq={2}", username, password, Random);

            bool ok = false;
            try
            {
                Reply reply = Reply.Parse(SendRequest(URI + "/login", p));
                Token = reply.Content.Token;
                ok = true;
            }
            catch (Exception e)
            {
                Console.WriteLine("Error: {0} {1}", e.GetType(), e.Message);
            }
            
            return ok;
        }

        /// <summary>
        /// Logs out of the Nessus Server.
        /// </summary>
        public void Logout()
        {
            SendRequest(URI + "/logout", "seq=" + Random);
        }

        /// <summary>
        /// Returns the ServerLoad object for this Nessus Server.
        /// </summary>
        /// <returns>ServerLoad</returns>
        public ServerLoad GetServerLoad()
        {
            Reply reply = Reply.Parse(SendRequest(URI + "/server/load", "seq=" + Random));
            return reply.Content.ServerLoad;
        }

        /// <summary>
        /// Returns the OS/Platform the Nessus Server is running on.
        /// </summary>
        /// <returns>string</returns>
        public string GetPlatform()
        {
            Reply reply = Reply.Parse(SendRequest(URI + "/server/load", "seq=" + Random));
            return reply.Content.Platform;
        }

        /// <summary>
        /// Returns a list of reports on the Nessus Server.
        /// </summary>
        /// <returns>List containing NessusReport objects.</returns>
        public List<NessusReport> GetReports()
        {
            Reply reply = Reply.Parse(SendRequest(URI + "/report/list", "seq=" + Random));
            if (reply != null)
                return reply.Content.Reports;

            return null;
        }

        /// <summary>
        /// Downloads the specified report to the given path.
        /// </summary>
        /// <param name="uuid">Name/UUID of the report to download.</param>
        /// <param name="path">Path to save the nessusv2 file.</param>
        /// <returns>bool</returns>
        public bool DownloadReport(string uuid, string path)
        {
            bool ok = false;
            XDocument xdoc = SendRequest(URI + "/file/report/download", "seq=" + Random + "&report=" + uuid);
            try
            {
                File.WriteAllText(path, xdoc.ToString());
                ok = true;
            }
            catch (Exception e)
            {
                Logging.Log(LogLevel.Warn, String.Format("Could not save XDocument to '{0}': {1} {2}", path, e.GetType(), e.Message));
            }

            return ok;
        }

        /// <summary>
        /// IDisposable implementation
        /// </summary>
        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        protected virtual void Dispose(bool disposing)
        {
            if (!this.disposing)
            {
                if (disposing)
                    Logout();

                disposing = true;
            }
        }
    }
}
