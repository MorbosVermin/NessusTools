Nessus Tools
=================================

Java, C# and PHP code to parse Nessus report (XML) files. 

* nessus-reporting - Small Nessus reporting library.

* nessus-tools - The Nessus report parsing library. There is also some code to connect to a Nessus server and use the API to download reports. This code was developed using Visual Studio 2012 Desktop Express Edition using .Net Framework v4.5.

* php - PHP-based library to parse Nessus reports. The PHP library uses the simplexml_load_file function to load the XML and then parses it into objects.

* java - Java port of the NessusClientData class from PHP/C#. The Java library uses no external libs/deps and primarily uses the org.w3c.dom package (Java 6 or 7).


Example Usage
=================================

Java

    # java -jar NessusTools-0.1-0.jar ../php/225.nessus 

    192.168.50.101 (35 ports, 'Linux Kernel 3.10 				Linux Kernel 3.5 				Linux Kernel 3.8 				Linux Kernel 3.9', 20 overall severity)
    192.168.50.100 (42 ports, 'Linux Kernel 3.10 Linux Kernel 3.5 Linux Kernel 3.8 Linux Kernel 3.9', 26 overall severity)
    192.168.50.151 (105 ports, 'Microsoft Windows Server 2008 R2 Standard Service Pack 1', 67 overall severity)
    192.168.50.5 (30 ports, 'Linux Kernel 3.10 Linux Kernel 3.5 Linux Kernel 3.8 Linux Kernel 3.9', 17 overall severity)
    192.168.50.4 (34 ports, 'Linux Kernel 3.10 Linux Kernel 3.5 Linux Kernel 3.8 Linux Kernel 3.9', 19 overall severity)
    192.168.50.1 (102 ports, 'Linux Kernel 2.6 on Ubuntu 10.04 (lucid)', 45 overall severity)
    192.168.50.108 (20 ports, 'Linux Kernel 3.10 Linux Kernel 3.5 Linux Kernel 3.8 Linux Kernel 3.9', 9 overall severity)
    192.168.50.105 (6 ports, '(unknown)', 4 overall severity)
    192.168.50.21 (23 ports, 'Linux Kernel 3.10 Linux Kernel 3.5 Linux Kernel 3.8 Linux Kernel 3.9', 9 overall severity)
    192.168.100.101 (11 ports, '(unknown)', 9 overall severity)
    192.168.100.100 (11 ports, '(unknown)', 9 overall severity)
    192.168.50.205 (49 ports, 'Microsoft Windows Server 2008 R2 Enterprise Service Pack 1', 33 overall severity)
    192.168.50.201 (86 ports, 'Microsoft Windows Server 2008 R2 Standard Service Pack 1', 68 overall severity)
    192.168.50.117 (37 ports, 'Linux Kernel 2.6 on CentOS Linux release 6', 9 overall severity)
    192.168.50.123 (23 ports, 'Linux Kernel 3.10 Linux Kernel 3.5 Linux Kernel 3.8 Linux Kernel 3.9', 12 overall severity)
    192.168.50.137 (76 ports, 'Linux Kernel 2.6 on Ubuntu 10.04 (lucid)', 37 overall severity)
    192.168.50.166 (73 ports, 'Microsoft Windows Server 2008 R2 Standard Service Pack 1', 55 overall severity)
    192.168.50.159 (49 ports, 'Microsoft Windows Server 2008 R2 Standard Service Pack 1', 33 overall severity)
    192.168.50.158 (42 ports, 'Microsoft Windows 7 Professional', 33 overall severity)
    192.168.50.157 (45 ports, 'Microsoft Windows Server 2008 R2 Standard Service Pack 1', 29 overall severity)
    192.168.50.156 (47 ports, 'Microsoft Windows Server 2008 R2 Standard Service Pack 1', 31 overall severity)
    192.168.50.254 (26 ports, 'Linux Kernel 3.5 on Ubuntu 12.10 (quantal)', 15 overall severity)
    192.168.50.253 (6 ports, '(unknown)', 4 overall severity)
    192.168.50.252 (6 ports, '(unknown)', 4 overall severity)
    192.168.50.213 (18 ports, '(unknown)', 7 overall severity)
    192.168.50.212 (11 ports, 'Linux Kernel 3.10 Linux Kernel 3.5 Linux Kernel 3.8 Linux Kernel 3.9', 9 overall severity)
    192.168.50.211 (11 ports, 'Linux Kernel 3.10 Linux Kernel 3.5 Linux Kernel 3.8 Linux Kernel 3.9', 9 overall severity)
    192.168.50.210 (18 ports, '(unknown)', 7 overall severity)
    192.168.50.209 (33 ports, 'Linux Kernel 3.0 on Ubuntu 12.04 (precise)', 11 overall severity)
    192.168.50.15 (34 ports, 'Linux Kernel 3.10 Linux Kernel 3.5 Linux Kernel 3.8 Linux Kernel 3.9', 19 overall severity)
    192.168.50.238 (55 ports, 'Linux Kernel 2.6.32-042stab072.10 on CentOS release 6.4 (Final)', 26 overall severity)
    192.168.50.235 (33 ports, 'Linux Kernel 2.6 on CentOS Linux release 6', 11 overall severity)
    192.168.50.233 (33 ports, 'Linux Kernel 2.6 on CentOS Linux release 6', 11 overall severity)
    192.168.50.231 (20 ports, 'Linux Kernel 3.10 Linux Kernel 3.5 Linux Kernel 3.8 Linux Kernel 3.9', 9 overall severity)
    192.168.50.230 (42 ports, 'Linux Kernel 3.10 Linux Kernel 3.5 Linux Kernel 3.8 Linux Kernel 3.9', 24 overall severity)

PHP

    # php -a
    Interactive shell
    
    php > include("./Nessus.lib.php");
    php > $scan = NessusClientData::parse("./225.nessus");
    php > foreach($scan->getReport()->getReportHosts() as $host)  { echo $host ."\n"; }

    192.168.50.101 (35 ports, 'Linux Kernel 3.10 				Linux Kernel 3.5 				Linux Kernel 3.8 				Linux Kernel 3.9', 20 overall severity)
    192.168.50.100 (42 ports, 'Linux Kernel 3.10 Linux Kernel 3.5 Linux Kernel 3.8 Linux Kernel 3.9', 26 overall severity)
    192.168.50.151 (105 ports, 'Microsoft Windows Server 2008 R2 Standard Service Pack 1', 67 overall severity)
    192.168.50.5 (30 ports, 'Linux Kernel 3.10 Linux Kernel 3.5 Linux Kernel 3.8 Linux Kernel 3.9', 17 overall severity)
    192.168.50.4 (34 ports, 'Linux Kernel 3.10 Linux Kernel 3.5 Linux Kernel 3.8 Linux Kernel 3.9', 19 overall severity)
    192.168.50.1 (102 ports, 'Linux Kernel 2.6 on Ubuntu 10.04 (lucid)', 45 overall severity)
    192.168.50.108 (20 ports, 'Linux Kernel 3.10 Linux Kernel 3.5 Linux Kernel 3.8 Linux Kernel 3.9', 9 overall severity)
    192.168.50.105 (6 ports, '(unknown)', 4 overall severity)
    192.168.50.21 (23 ports, 'Linux Kernel 3.10 Linux Kernel 3.5 Linux Kernel 3.8 Linux Kernel 3.9', 9 overall severity)
    192.168.100.101 (11 ports, '(unknown)', 9 overall severity)
    192.168.100.100 (11 ports, '(unknown)', 9 overall severity)
    192.168.50.205 (49 ports, 'Microsoft Windows Server 2008 R2 Enterprise Service Pack 1', 33 overall severity)
    192.168.50.201 (86 ports, 'Microsoft Windows Server 2008 R2 Standard Service Pack 1', 68 overall severity)
    192.168.50.117 (37 ports, 'Linux Kernel 2.6 on CentOS Linux release 6', 9 overall severity)
    192.168.50.123 (23 ports, 'Linux Kernel 3.10 Linux Kernel 3.5 Linux Kernel 3.8 Linux Kernel 3.9', 12 overall severity)
    192.168.50.137 (76 ports, 'Linux Kernel 2.6 on Ubuntu 10.04 (lucid)', 37 overall severity)
    192.168.50.166 (73 ports, 'Microsoft Windows Server 2008 R2 Standard Service Pack 1', 55 overall severity)
    192.168.50.159 (49 ports, 'Microsoft Windows Server 2008 R2 Standard Service Pack 1', 33 overall severity)
    192.168.50.158 (42 ports, 'Microsoft Windows 7 Professional', 33 overall severity)
    192.168.50.157 (45 ports, 'Microsoft Windows Server 2008 R2 Standard Service Pack 1', 29 overall severity)
    192.168.50.156 (47 ports, 'Microsoft Windows Server 2008 R2 Standard Service Pack 1', 31 overall severity)
    192.168.50.254 (26 ports, 'Linux Kernel 3.5 on Ubuntu 12.10 (quantal)', 15 overall severity)
    192.168.50.253 (6 ports, '(unknown)', 4 overall severity)
    192.168.50.252 (6 ports, '(unknown)', 4 overall severity)
    192.168.50.213 (18 ports, '(unknown)', 7 overall severity)
    192.168.50.212 (11 ports, 'Linux Kernel 3.10 Linux Kernel 3.5 Linux Kernel 3.8 Linux Kernel 3.9', 9 overall severity)
    192.168.50.211 (11 ports, 'Linux Kernel 3.10 Linux Kernel 3.5 Linux Kernel 3.8 Linux Kernel 3.9', 9 overall severity)
    192.168.50.210 (18 ports, '(unknown)', 7 overall severity)
    192.168.50.209 (33 ports, 'Linux Kernel 3.0 on Ubuntu 12.04 (precise)', 11 overall severity)
    192.168.50.15 (34 ports, 'Linux Kernel 3.10 Linux Kernel 3.5 Linux Kernel 3.8 Linux Kernel 3.9', 19 overall severity)
    192.168.50.238 (55 ports, 'Linux Kernel 2.6.32-042stab072.10 on CentOS release 6.4 (Final)', 26 overall severity)
    192.168.50.235 (33 ports, 'Linux Kernel 2.6 on CentOS Linux release 6', 11 overall severity)
    192.168.50.233 (33 ports, 'Linux Kernel 2.6 on CentOS Linux release 6', 11 overall severity)
    192.168.50.231 (20 ports, 'Linux Kernel 3.10 Linux Kernel 3.5 Linux Kernel 3.8 Linux Kernel 3.9', 9 overall severity)
    192.168.50.230 (42 ports, 'Linux Kernel 3.10 Linux Kernel 3.5 Linux Kernel 3.8 Linux Kernel 3.9', 24 overall severity)
    
    php > include("NessusServerApi.lib.php");
    php > $server = new NessusServer("https://localhost:8834");
    php > $server->login("myuser", "mypass");
    php > $scans = $server->getReports();
    php > $parsed_scans = array();
    php > foreach($scans as $scan) array_push($parsed_scans, $server->downloadReport($scan->getName(), $scan->getReadableName() .".nessus"));
    
    php > $scan = $server->downloadReport($report->getName(), "/tmp/". $report->getReadableName() .".nessus");
    php > foreach($scan->getReport()->getReportHosts() as $host) echo $host ."\n"; 

C#

    NessusClientData_v2 scan = NessusClientData_v2.Parse("...");
    for(ReportHost host in scan.Report.ReportHosts)
    	Console.WriteLine("{0} ({1} ports, '{2}')", host, host.ReportItems.Count, host.OS);
    
