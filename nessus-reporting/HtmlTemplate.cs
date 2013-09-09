using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace nessus_reporting
{
    /// <summary>
    /// Provides access to the HtmlHeader.htm and HtmlFooter.htm template
    /// files used in various reports.
    /// </summary>
    public class HtmlTemplate
    {
        /// <summary>
        /// Path to the default HTML template file for the header.
        /// </summary>
        public static string HeaderPath { get { return Environment.CurrentDirectory + "\\HtmlHeader.htm"; } }

        /// <summary>
        /// Path to the default HTML template file for the footer.
        /// </summary>
        public static string FooterPath { get { return Environment.CurrentDirectory + "\\HtmlFooter.htm"; } }

        /// <summary>
        /// Reads the file at HeaderPath and returns the contents with the 
        /// title given.
        /// </summary>
        /// <param name="title">string Title for the HTML content</param>
        /// <returns>string HTML template</returns>
        public static string Header(string title)
        {
            TextReader reader = null;
            string header = "<html><head><title>{0}</title></head><body><table>" + Environment.NewLine;
            try
            {
                reader = new StreamReader(File.Open(HeaderPath, FileMode.Open));
                header = reader.ReadToEnd();
            }
            catch (Exception e)
            {
                header += "<!-- " + String.Format("Error: Could not read header template '{0}': {1} {2}", HeaderPath, e.GetType(), e.Message) + " -->" + Environment.NewLine;
            }
            finally
            {
                if (reader != null)
                {
                    reader.Close();
                }
            }

            //Apply title
            if (header.Length > 0)
                header = header.Replace("%TITLE%", title);

            return header;
        }

        /// <summary>
        /// Returns the contents of the file at FooterPath.
        /// </summary>
        /// <returns>string HTML template</returns>
        public static string Footer()
        {
            TextReader reader = null;
            string footer =  Environment.NewLine +"</table></body></html>";
            try
            {
                reader = new StreamReader(File.Open(FooterPath, FileMode.Open));
                footer = reader.ReadToEnd();
            }
            catch (Exception e)
            {
                footer += "<!-- " + String.Format("Error: Could not read footer template '{0}': {1} {2}", FooterPath, e.GetType(), e.Message) + " -->" + Environment.NewLine;
            }
            finally
            {
                if (reader != null)
                {
                    reader.Close();
                }
            }

            return footer;
        }
    
    }
}
