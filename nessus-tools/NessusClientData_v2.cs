using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;
using System.IO;

namespace nessus_tools
{
    /// <summary>
    /// Represents a NessusClientData_v2 (root) element within a NESSUS report file (*.nessus).
    /// See http://static.tenable.com/documentation/nessus_v2_file_format.pdf for more 
    /// information.
    /// </summary>
    public sealed class NessusClientData_v2
    {
        /// <summary>
        /// Policy for this scan/report.
        /// </summary>
        [XmlElement("Policy")]
        public Policy Policy
        {
            get;
            set;
        }

        /// <summary>
        /// Report for this scan/report.
        /// </summary>
        [XmlElement("Report")]
        public Report Report
        {
            get;
            set;
        }

        /// <summary>
        /// Parses the File at the given path and returns a NessusClientData_v2 object.
        /// </summary>
        /// <param name="path">Path of file to parse.</param>
        /// <returns>NessusClientData_v2 object; null if an error occurred.</returns>
        public static NessusClientData_v2 Parse(string path)
        {
            NessusClientData_v2 data = null;
            XmlSerializer serializer = new XmlSerializer(typeof(NessusClientData_v2));
            TextReader reader = null;
            try
            {
                reader = new StreamReader(File.OpenRead(path));
                data = (NessusClientData_v2)serializer.Deserialize(reader);
            }
            catch (Exception e)
            {
                Console.WriteLine("Error: Could not parse (NESSUS?) file {0}: {1} {2}", path, e.GetType(), e.Message);
            }
            finally
            {
                if (reader != null)
                {
                    reader.Close();
                }
            }

            return data;
        }

        /// <summary>
        /// Saves the NessusClientData_v2 object (a NESSUS report file) to a file at the given
        /// path.
        /// </summary>
        /// <param name="path">Path to save the report to.</param>
        /// <returns>bool; whether or not we are successful at the save.</returns>
        public static bool Save(NessusClientData_v2 data, string path)
        {
            XmlSerializer serializer = new XmlSerializer(typeof(NessusClientData_v2));
            TextWriter writer = null;
            bool ok = false;
            try
            {
                writer = new StreamWriter(path);
                serializer.Serialize(writer, data);
                ok = true;
            }
            catch (Exception e)
            {
                Console.WriteLine("Error: Could not write NESSUS/XML file {0}: {1} {2}", path, e.GetType(), e.Message);
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
}
