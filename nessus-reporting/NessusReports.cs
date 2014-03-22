using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using nessus_tools;

namespace nessus_reporting
{
    public sealed class NessusReports
    {

        public enum OutputType
        {
            HTML,
            //PDF,
            //XML,
            CSV
        }

        public enum ReportType
        {
            HostsOnly,
            VulnsByHost,
            VulnsByFamily
        }

        public static string OutputTypeName(OutputType type)
        {
            switch (type)
            {
                case OutputType.HTML:
                    return "html";

                //case OutputType.PDF:
                //    return "pdf";

                default:
                    return "csv";

            }
        }

        public static string ReportTypeName(ReportType type)
        {
            switch (type)
            {
                case ReportType.HostsOnly:
                    return "inventory";

                case ReportType.VulnsByHost:
                    return "vulnsbyhost";

            }

            return "vulnsbyfam";
        }
    }
}
