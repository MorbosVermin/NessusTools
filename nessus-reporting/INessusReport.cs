using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using nessus_tools;
using System.IO;
using System.Collections;

namespace nessus_reporting
{
    /// <summary>
    /// Base Interface for all NessusReport implementations.
    /// </summary>
    public interface INessusReport
    {

        bool Export(NessusClientData_v2 data, NessusReports.ReportType type);

    }

    /// <summary>
    /// File base of INessusReport implementation, of which most of the following classes are 
    /// derived (i.e. HtmlNessusReport, CsvNessusReport, etc.)
    /// </summary>
    public abstract class FileBasedNessusReport : INessusReport
    {

        public string Path
        {
            get;
            set;
        }

        public FileBasedNessusReport()
        {
            Path = "";
        }

        public FileBasedNessusReport(string path)
        {
            Path = path;
        }


        public abstract bool Export(NessusClientData_v2 data, NessusReports.ReportType type);

    }

    /// <summary>
    /// HTML based INessusReport implementation.
    /// </summary>
    public sealed class HtmlNessusReport : FileBasedNessusReport
    {
        /// <summary>
        /// Title of the HTML document.
        /// </summary>
        public string Title { get; set; }

        public override bool Export(NessusClientData_v2 data, NessusReports.ReportType type)
        {
            StringBuilder buffer = new StringBuilder();

            if (Title.Length == 0)
                Title = "Nessus Report (HTML)";

            buffer.Append(HtmlTemplate.Header(Title));

            /*
             * CSS Reference:
             *  .title = Title of some section/page.
             *  .header = Column Headers
             *  .subtitle = Subtitle of some section/page.
             *  .critical = Critical
             *  .high = High
             *  .medium = Medium
             *  .low = Low
             *  .none = None
             */
            switch (type)
            {
                case NessusReports.ReportType.HostsOnly:
                    buffer.Append("<tr><td class=\"header\">Name</td><td class=\"header\">IP</td><td class=\"header\">FQDN</td><td class=\"header\">MAC</td><td class=\"header\">OS</td></tr>");
                    foreach (ReportHost host in data.Report.ReportHosts)
                    {
                        string fqdn = (host.FQDN.Length > 0) ? host.FQDN : host.NetbiosName;
                        string mac = host.MacAddress;
                        string os = host.OS;
                        buffer.Append(String.Format("<tr><td>{0}</td><td>{1}</td><td>{2}</td><td>{3}</td><td>{4}</td></tr>", host.Name, host.IpAddress, fqdn, mac, os));
                    }
                    break;

                case NessusReports.ReportType.VulnsByHost:
                    foreach(ReportHost host in data.Report.ReportHosts)
                    {
                        buffer.Append(String.Format("<tr><td colspan=\"9\" class=\"title\">{0} ({1})</td></tr>", host.Name, host.IpAddress));
                        buffer.Append("<tr><td class=\"header\">Port</td><td class=\"header\">Protocol</td><td class=\"header\">Service</td><td class=\"header\">Description</td><td class=\"header\">Plugin</td><td class=\"header\">Risk</td><td class=\"header\">Severity</td><td class=\"header\">Solution</td><td class=\"header\">Synopsis</td></tr>");
                        foreach (ReportItem item in host.ReportItems)
                        {
                            buffer.Append(String.Format("<tr><td>{0}</td><td>{1}</td><td>{2}</td><td>{3}</td><td>{4}</td><td>{5}</td><td>{6}</td><td>{7}</td><td>{8}</td></tr>",
                                item.Port,
                                item.Protocol,
                                item.ServiceName,
                                item.Description,
                                item.Plugin_Name,
                                item.Risk_Factor,
                                item.Severity,
                                item.Solution,
                                item.Synopsis));
                            buffer.Append(Environment.NewLine);
                        }
                    }
                    
                    break;

                case NessusReports.ReportType.VulnsByFamily:
                    VulnsByFamily vulns = new VulnsByFamily(data);

                    //Now, we can do the HTML...
                    buffer.Append("<tr><td colspan=\"5\"><ul id=\"families\">");
                    foreach (Plugin plugin in vulns.Keys)
                    {
                        buffer.Append(String.Format("<li><a href=\"#{0}\" title=\"{1}\">{2} {3}</a> ({4})</li>", plugin.GetHashCode(), plugin.Description, plugin.Criticality.ToString().ToUpper(), plugin.Name, vulns[plugin].Count));
                    }
                    buffer.Append("</ul></td></tr>" + Environment.NewLine);

                    foreach (Plugin plugin in vulns.Keys)
                    {
                        buffer.Append(String.Format("<tr><td colspan=\"5\" class=\"title {0}\"><a name=\"{1}\"></a>{2} {3} ({4})</td></tr>", plugin.Criticality.ToString().ToLower(), plugin.GetHashCode(), plugin.Criticality.ToString().ToUpper(), plugin.Name, vulns[plugin].Count));
                        buffer.Append(String.Format("<tr><td colspan=\"5\" class=\"subtitle\">{0}</td></tr>", plugin.Description));
                        foreach (ReportHost host in vulns[plugin])
                        {
                            buffer.Append(String.Format("<tr><td>{0}</td><td>{1}</td><td>{2}</td><td>{3}</td><td>{4}</td></tr>", host.Name, host.IpAddress, host.FQDN, host.MacAddress, host.OS));
                        }
                    }

                    break;

            }

            buffer.Append(HtmlTemplate.Footer());

            /*
             * Write contents to file.
             */
            TextWriter writer = null;
            bool ok = false;
            try
            {
                writer = new StreamWriter(File.Open(Path, FileMode.Create, FileAccess.Write, FileShare.None));
                writer.WriteLine(buffer);
                writer.Flush();
                ok = true;
            }
            catch (Exception e)
            {
                Console.WriteLine("Error: Could not export/write file {0}: {1} {2}", Path, e.GetType(), e.Message);
            }
            finally
            {
                if (writer != null)
                {
                    writer.Close();
                }
            }

            return ok;
        }
    }

    /// <summary>
    /// CSV based INessusReport implementation.
    /// </summary>
    public sealed class CsvNessusReport : FileBasedNessusReport
    {
        /// <summary>
        /// Attempts to escape double-quotes by adding slashes. This will also Trim() the 
        /// subject given. 
        /// </summary>
        /// <param name="subject">string Subject to transform.</param>
        /// <returns>string Transformed subject</returns>
        private string AddSlashes(string subject)
        {
            if (subject == null)
                return "";

            return subject.Replace("\"", "\\\"").Trim();
        }

        public override bool Export(NessusClientData_v2 data, NessusReports.ReportType type)
        {
            StringBuilder buffer = new StringBuilder();
            switch (type)
            {
                case NessusReports.ReportType.HostsOnly:
                    buffer.Append("Name,IP,FQDN,MAC,OS" + Environment.NewLine);
                    foreach (ReportHost host in data.Report.ReportHosts)
                    {
                        buffer.Append(String.Format("{0},{1},{2},\"{3}\",\"{4}\"{5}", 
                            host.Name, 
                            host.IpAddress, 
                            host.FQDN, 
                            AddSlashes(host.MacAddress), 
                            AddSlashes(host.OS), 
                            Environment.NewLine));
                    }
                    break;

                case NessusReports.ReportType.VulnsByHost:
                    buffer.Append("Name,IP,FQDN,Mac,Port,Protocol,Service,Description,Plugin,Risk,Severity,Solution,Synopsis" + Environment.NewLine);
                    foreach (ReportHost host in data.Report.ReportHosts)
                    {
                        //buffer.Append(String.Format("{0} {1}{2}", host.Name, host.FQDN, Environment.NewLine));
                        foreach(ReportItem item in host.ReportItems)  
                        {
                            buffer.Append(String.Format("{0},{1},{2},{3},{4},{5},{6},\"{7}\",{8},{9},{10},\"{11}\",\"{12}\"",
                                host.Name,
                                host.IpAddress,
                                host.FQDN,
                                host.MacAddress,
                                item.Port,
                                item.Protocol,
                                item.ServiceName,
                                AddSlashes(item.Description),
                                item.Plugin_Name,
                                item.Risk_Factor,
                                item.Severity,
                                AddSlashes(item.Solution),
                                AddSlashes(item.Synopsis)));
                            buffer.Append(Environment.NewLine);
                        }
                        buffer.Append(Environment.NewLine);

                    }
                    break;

                case NessusReports.ReportType.VulnsByFamily:
                    VulnsByFamily vulns = new VulnsByFamily(data);
                    buffer.Append("Plugin,Criticality,HostName,IP,FQDN,MacAddress,Description,Severity,Synopsis,Solution"+ Environment.NewLine);
                    foreach (Plugin plugin in vulns.Keys)
                    {
                        foreach (ReportHost host in vulns[plugin])
                        {
                            buffer.Append(String.Format("\"{0}\",{1},{2},{3},{4},\"{5}\",\"{6}\",{7},\"{8}\",\"{9}\"{10}",
                                AddSlashes(plugin.Name),
                                plugin.Criticality.ToString(),
                                host.Name,
                                host.IpAddress,
                                host.FQDN,
                                AddSlashes(host.MacAddress),
                                AddSlashes(plugin.Description),
                                plugin.Severity + "",
                                AddSlashes(plugin.Synopsis),
                                AddSlashes(plugin.Solution),
                                Environment.NewLine));
                        }
                    }
                    break;

            }

            bool ok = false;
            TextWriter writer = null;
            try
            {
                writer = new StreamWriter(File.Open(Path, FileMode.Create, FileAccess.Write, FileShare.None));
                writer.WriteLine(buffer);
                writer.Flush();
                ok = true;
            }
            catch (Exception e)
            {
                Console.WriteLine("Error: Could not export/write file {0}: {1} {2}", Path, e.GetType(), e.Message);
            }
            finally
            {
                if (writer != null)
                {
                    writer.Close();
                }
            }

            return ok;
        }
    }

    /// <summary>
    /// Dictionary derived class of key-value pairs; keyed by Plugin and values
    /// are ReportHosts (collection) instances which hold the ReportHost 
    /// objects for the Plugin.
    /// </summary>
    public sealed class VulnsByFamily : Dictionary<Plugin, ReportHosts>
    {
        public VulnsByFamily(NessusClientData_v2 data)
            : base(new PluginComparer())
        {
            foreach (ReportHost host in data.Report.ReportHosts)
            {
                foreach (ReportItem item in host.ReportItems)
                {
                    Plugin plugin = new Plugin(item);
                    if (plugin.Criticality == Criticality.None)
                        continue;

                    if (!ContainsKey(plugin))
                    {
                        Add(plugin, new ReportHosts());
                    }

                    if (!this[plugin].Contains(host))
                        this[plugin].Add(host);

                }
            }
        }

    }

}
