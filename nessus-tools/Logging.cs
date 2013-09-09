using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;
using System.IO;

namespace nessus_tools
{
    public enum LogLevel
    {
        Debug,
        Info,
        Warn,
        Error
    }

    public class Logging
    {
        public static bool ConsoleLoggingEnabled { get; set; }
        public static bool FileLoggingEnabled { get; set; }
        public static bool EventLogEnabled { get; set; }

        public static void Log(LogLevel level, string message)
        {
            string timestamp = DateTime.Now.ToString("s");
            string line = String.Format("[{0}] {1} {2}{3}", timestamp, level, message, Environment.NewLine);
            if (ConsoleLoggingEnabled)
                Console.WriteLine(line);

            if (FileLoggingEnabled)
            {
                string path = Environment.CurrentDirectory +"\\"+ Path.GetFileName(Process.GetCurrentProcess().MainModule.FileName) +".log";
                using (Stream s = File.Open(path, FileMode.Append))
                {
                    byte[] b = Encoding.ASCII.GetBytes(line);
                    s.Write(b, 0, b.Length);
                    s.Flush();
                }
            }

            if(EventLogEnabled)  
            {
                //TODO
            }
        }

    }
}
