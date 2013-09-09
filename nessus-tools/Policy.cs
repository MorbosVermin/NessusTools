using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;
using System.Xml.Serialization;
using System.IO;

namespace nessus_tools
{
    /// <summary>
    /// Simple collection of Policy objects.
    /// </summary>
    public sealed class Policies : CollectionBase
    {
        public int Add(Policy p)
        {
            return List.Add(p);
        }

        public bool Contains(Policy p)
        {
            foreach (Policy policy in this)
            {
                if (policy.Name.Equals(p.Name))
                {
                    return true;
                }
            }

            return false;
        }

        public Policy this[int index]
        {
            get { return (Policy)List[index]; }
            set { List[index] = value; }
        }

        public Policy this[string name]
        {
            get
            {
                Policy p = null;
                foreach (Policy policy in this)
                {
                    if (policy.Name.Equals(name))
                        p = policy;

                    if (p != null)
                        break;

                }

                return p;
            }
        }
    }

    /// <summary>
    /// Policy element.
    /// </summary>
    public sealed class Policy
    {
        /// <summary>
        /// Name of the policy.
        /// </summary>
        [XmlElement("policyName")]
        public string Name { get; set; }

        /// <summary>
        /// Policy comments (if any).
        /// </summary>
        [XmlElement("policyComments")]
        public string Comments { get; set; }
        
        /// <summary>
        /// ServerPreferences of the Policy.
        /// </summary>
        [XmlElement("ServerPreferences")]
        public ServerPreferences ServerPreferences { get; set; }

        /// <summary>
        /// PluginPreferences of the Policy.
        /// </summary>
        [XmlElement("PluginPreferences")]
        public PluginPreferences PluginPreferences { get; set; }

        /// <summary>
        /// FamilySelection of the Policy.
        /// </summary>
        [XmlElement("FamilySection")]
        public FamilySelection FamilySelection { get; set; }

        public Policy()
        {
            Name = "";
            Comments = "";
            ServerPreferences = new ServerPreferences();
            PluginPreferences = new PluginPreferences();
            FamilySelection = new FamilySelection();
        }

        /// <summary>
        /// Returns the Policy for the given nessus report file (*.nessus).
        /// </summary>
        /// <param name="path">Path to the nessus report file to parse.</param>
        /// <returns>Policy; null if an error occurred.</returns>
        public static Policy Parse(string path)
        {
            Policy policy = null;
            XmlSerializer serializer = new XmlSerializer(typeof(NessusClientData_v2));
            TextReader reader = null;
            try
            {
                reader = new StreamReader(File.OpenRead(path));
                NessusClientData_v2 data = (NessusClientData_v2)serializer.Deserialize(reader);
                policy = data.Policy;
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

            return policy;
        }

    }

    /// <summary>
    /// Represents the ServerPreferences section of the Policy element. 
    /// </summary>
    public sealed class ServerPreferences : CollectionBase
    {
        public int Add(Preference p)
        {
            if (!Contains(p))
            {
                return List.Add(p);
            }

            return -1;
        }

        public bool Contains(Preference p)
        {
            foreach (Preference pref in this)
            {
                if (pref.Name.Equals(p.Name))
                {
                    return true;
                }
            }

            return false;
        }

        public Preference this[int index]
        {
            get { return (Preference)List[index]; }
            set { List[index] = value; }
        }
    }

    /// <summary>
    /// Represents a ServerPreferences/Preference element.
    /// </summary>
    public sealed class Preference
    {

        [XmlElement("name")]
        public string Name { get; set; }

        [XmlElement("value")]
        public string Value { get; set;}

    }

    /// <summary>
    /// PluginPreferences element which is a collection of PluginPreferenceItem objects.
    /// </summary>
    public sealed class PluginPreferences : CollectionBase
    {
        public int Add(Item item)
        {
            return List.Add(item);
        }

        public Item this[int index]
        {
            get { return (Item)List[index]; }
            set { List[index] = value; }
        }
    }

    /// <summary>
    /// PluginPreferences/Item object
    /// </summary>
    public sealed class Item
    {

        [XmlElement("fullName")]
        public string FullName { get; set; }

        [XmlElement("pluginName")]
        public string PluginName { get; set; }

        [XmlElement("pluginId")]
        public string PluginId { get; set; }

        /// <summary>
        /// pluginID of 21745 appears to be unauthenticated. From sources of Jason 
        /// Oliver's Java code for XMLVulnStatsV4 at http://blackhat.org. 
        /// </summary>
        public bool Authenticated { get { return (!PluginId.Equals("21745")); } }

        [XmlElement("preferenceName")]
        public string PreferenceName { get; set; }

        [XmlElement("preferenceType")]
        public string PreferenceType { get; set; }

        [XmlElement("preferenceValues")]
        public string PreferenceValues { get; set; }

        [XmlElement("selectedValue")]
        public string SelectedValue { get; set; }

        public Item()
        {
            FullName = "";
            PluginName = "";
            PluginId = "0";
            PreferenceName = "";
            PreferenceType = "";
            PreferenceValues = "";
            SelectedValue = "";
        }
    }

    /// <summary>
    /// Represents the FamilySelection element containing FamilyItem objects.
    /// </summary>
    public sealed class FamilySelection : CollectionBase
    {
        public int Add(FamilyItem item)
        {
            return List.Add(item);
        }

        public FamilyItem this[int index]
        {
            get { return (FamilyItem)List[index]; }
            set { List[index] = value; }
        }
    }

    /// <summary>
    /// Represents a FamilyItem element.
    /// </summary>
    public sealed class FamilyItem
    {
        [XmlElement("FamilyName")]
        public string Name { get; set; }

        [XmlElement("Status")]
        public string Status { get; set; }

        public FamilyItem()
        {
            Name = "";
            Status = "";
        }
    }

}
