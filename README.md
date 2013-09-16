Nessus Tools
=================================

Java, C# and PHP code to parse Nessus report (XML) files. 

* nessus-reporting - Small Nessus reporting library.

* nessus-tools - The Nessus report parsing library. There is also some code to connect to a Nessus server and use the API to download reports.

* php - PHP-based library to parse Nessus reports.

* java - Java port of the NessusClientData class from PHP/C#.


Example Usage
=================================

    # php -a
    Interactive shell
    
    php > include("./Nessus.lib.php");
    php > $scan = NessusClientData::parse("./225.nessus");
    php > foreach($scan->getReport()->getReportHosts() as $host)  { echo $host ."\n"; }
    35 report items/findings for host 192.168.50.101.
    42 report items/findings for host 192.168.50.100.
    105 report items/findings for host 192.168.50.151.
    30 report items/findings for host 192.168.50.5.
    34 report items/findings for host 192.168.50.4.
    102 report items/findings for host 192.168.50.1.
    20 report items/findings for host 192.168.50.108.
    6 report items/findings for host 192.168.50.105.
    23 report items/findings for host 192.168.50.21.
    11 report items/findings for host 192.168.100.101.
    11 report items/findings for host 192.168.100.100.
    49 report items/findings for host 192.168.50.205.
    86 report items/findings for host 192.168.50.201.
    37 report items/findings for host 192.168.50.117.
    23 report items/findings for host 192.168.50.123.
    76 report items/findings for host 192.168.50.137.
    73 report items/findings for host 192.168.50.166.
    49 report items/findings for host 192.168.50.159.
    42 report items/findings for host 192.168.50.158.
    45 report items/findings for host 192.168.50.157.
    47 report items/findings for host 192.168.50.156.
    26 report items/findings for host 192.168.50.254.
    6 report items/findings for host 192.168.50.253.
    6 report items/findings for host 192.168.50.252.
    18 report items/findings for host 192.168.50.213.
    11 report items/findings for host 192.168.50.212.
    11 report items/findings for host 192.168.50.211.
    18 report items/findings for host 192.168.50.210.
    33 report items/findings for host 192.168.50.209.
    34 report items/findings for host 192.168.50.15.
    55 report items/findings for host 192.168.50.238.
    33 report items/findings for host 192.168.50.235.
    33 report items/findings for host 192.168.50.233.
    20 report items/findings for host 192.168.50.231.
    42 report items/findings for host 192.168.50.230.
    php > 

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

