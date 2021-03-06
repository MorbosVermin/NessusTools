<?php

require_once("Nessus.lib.php");

/**
 * Nessus Server API for PHP
 * Copyright (c)2013 Mike Duncan <mike.duncan@waitwha.com>
 *
 * See http://static.tenable.com/documentation/nessus_5.0_XMLRPC_protocol_guide.pdf for more information.
 *
 * @author	Mike Duncan <mike.duncan@waitwha.com>
 * @version	$Id$
 */
class NessusUser  {
  
  private $name;
  private $admin;
  
  public function __construct($user)  {
    $this->name = $user->name;
    $this->admin = (bool)$user->admin;
  }
  
  public function getName()  {
    return $this->name;
  }
  
  public function isAdmin()  {
    return $this->admin;
  }
  
  public function __toString()  {
    return $this->getName();
  }
  
}

class NessusServerLoad  {
  
  private $numScans;
  private $numSessions;
  private $numHosts;
  private $numTcpSessions;
  private $loadAvg;
  
  public function __construct($server_load)  {
    $this->numScans = $server_load->num_scans;
    $this->numSessions = $server_load->num_sessions;
    $this->numHosts = $server_load->num_hosts;
    $this->numTcpSessions = $server_load->num_tcp_sessions;
    $this->loadAvg = $server_load->loadavg;
  }
  
  public function getNumScans()  {
    return $this->numScans;
  }
  
  public function getNumSessions()  {
    return $this->numSessions;
  }
  
  public function getNumHosts()  {
    return $this->numHosts;
  }
  
  public function getNumTcpSessions()  {
    return $this->numTcpSessions;
  }
  
  public function getLoadAvg()  {
    return $this->loadAvg;
  }
  
  public function __toString()  {
    return sprintf("%d scan(s); %d%% avg load", $this->getNumScans(), $this->getLoadAvg());
  }
  
}

class NessusReports extends ArrayList  {
  
  public function __construct($reports)  {
    parent::__construct();
    foreach($reports->report as $report)
      $this->add(new NessusReport($report));
    
  }
  
}

/**
 * Nessus Report Class
 * 
 * Example Reply...
 * <pre>
 * <reply>
 *  <seq>7604</seq>
 *  <status>OK</status>
 *  <contents>
 *    <reports>
 *      <report>
 *        <name>15132e77-bed3-063d-a2cd-8c9762adc0c660cd999668f6fcd7</name>
 *        <status>running</status>
 *        <readableName>TheArk</readableName>
 *        <timestamp>1379801845</timestamp>
 *     </report>
 *    </reports>
 *  </contents>
 * </reply>
 * </pre>
 */
class NessusReport  {
  
  private $name;
  private $status;
  private $readableName;
  private $timestamp;
  
  public function __construct($report)  {
    $this->name = $report->name;
    $this->status = $report->status;
    $this->readableName = $report->readableName;
    $this->timestamp = $report->timestamp;
  }
  
  public function getName()  {
    return $this->name;
  }
  
  public function getStatus()  {
    return $this->status;
  }
  
  public function getReadableName()  {
    return $this->readableName;
  }
  
  public function getTimestamp()  {
    return $this->timestamp;
  }
  
  public function __toString()  {
    return sprintf("%s (%s; %s)", $this->getReadableName(), $this->getStatus(), $this->getTimestamp());
  }
  
}

/**
 * Nessus Server Reply Class<p />
 *
 * Example logon reply...
 * <pre>
 * <?xml version="1.0" encoding="UTF-8"?>
 * <reply>
 *   <seq>3956</seq>
 * 	 <status>OK</status>
 * 	 <contents>
 * 		<token>530ae8209e0fa0999a546a35b416a8a581acf867213b0d66</token>
 * 	 	<server_uuid>e0b24d09-8226-a56f-1827-d3589dc2f624cd04046183a79a63</server_uuid>
 * 	 	<plugin_set>201309202015</plugin_set>
 * 	 	<loaded_plugin_set>201309202015</loaded_plugin_set>
 * 	 	<scanner_boottime>1379801507</scanner_boottime>
 * 	 	<msp>FALSE</msp>
 * 	 	<idle_timeout>30</idle_timeout>
 *   	<user>
 * 		 <name>ncdc.its</name>
 * 		 <admin>TRUE</admin>
 * 	 	</user>
 * 	 </contents>
 * </reply>
 * </pre>
 */
class NessusServerReply  {
  
  private $seq;
  private $status;
  private $contents;
  
  public function __construct($reply)  {
    $this->seq = $reply->seq;
    $this->status = $reply->status;
    $this->contents = new NessusServerReplyContents($reply->contents);
  }
  
  public function getSeq()  {
    return $this->seq;
  }
  
  public function getStatus()  {
    return $this->status;
  }
  
  public function getContents()  {
    return $this->contents;
  }
  
  public function getToken()  {
    return $this->getContents()->getToken();
  }
  
  public function isOk()  {
    return (strcmp($this->status, "OK") == 0);
  }
  
}

class NessusServerReplyContents  {
  
  private $token;
  private $user;
  private $serverUuid;
  private $pluginSet;
  private $loadedPluginSet;
  private $scannerBootTime;
  private $load;
  private $platform;
  private $idleTimeout;
  private $text;
  private $reports;
  
  public function __construct($contents)  {
    $this->token = $contents->token;
    $this->user = new NessusUser($contents->user);
    $this->serverUuid = $contents->server_uuid;
    $this->pluginSet = $contents->plugin_set;
    $this->loadedPluginSet = $contents->loaded_plugin_set;
    $this->scannerBootTime = $contents->scanner_boot_time;
    $this->load = $contents->load;
    $this->platform = $contents->platform;
    $this->idleTimeout = $contents->idle_timeout;
    $this->text = (string)$contents;
    
    if(isset($contents->reports))
      $this->reports = new NessusReports($contents->reports);
    
  }
  
  public function getToken()  {
    return $this->token;
  }
  
  public function getUser()  {
    return $this->user;
  }
  
  public function getServerUuid()  {
    return $this->serverUuid;
  }
  
  public function getPluginSet()  {
    return $this->pluginSet;
  }
  
  public function getLoadedPluginSet()  {
    return $this->loadedPluginSet;
  }	
  
  public function getLoad()  {
    return $this->load;
  }
  
  public function getPlatform()  {
    return $this->platform;
  }
  
  public function getIdleTimeout()  {
    return $this->idleTimeout;
  }
  
  public function getReports()  {
    return $this->reports;
  }
  
  public function __toString()  {
    return $this->text;
  }
  
}

class NessusServerException extends Exception  {
  
  private $headers;
  private $errno;
  private $errmsg;
  
  public function __construct($headers, $errno, $errmsg)  {
    parent::__construct(sprintf("Error '%s (%d)' occurred while communicating/connecting to %s (request=%db, http_code=%d)",
                          $errmsg,
                          $errno,
                          $headers["url"],
                          $headers["request_size"],
                          $headers["http_code"]), -1);
    $this->headers = $headers;
    $this->errno = $errno;
    $this->errmsg = $errmsg;
  }
  
  public function get($name)  {
    return $this->headers[$name];
  }
  
  public function getErrorNo()  {
    return $this->errno;
  }
  
  public function getError()  {
    return $this->errmsg;
  }
  
}

class NessusServer  {
  
  const USER_AGENT = "NessusServerAPI4PHP 1.0";
  const CONNECT_TIMEOUT = 30;
  const TIMEOUT = 30;
  const ENCODING = "UTF-8";
  private $uri;
  private $token;
  
  public function __construct($uri="https://localhost:8834")  {
    $this->uri = $uri;
    $this->token = null;
  }
  
  public function getReply($path, $params, $raw=false)  {
    
    /*
     * If we are given a string, then I will assume we have taken care of 
     * encoding. However, if you send an array, I will do this for you. 
     * Nice of me, eh? Remember to tip the server.
     */
    if(is_array($params))  {
      $p = $params;
      $params = "";
      foreach($p as $k => $v)
        $params .= urlencode($k) ."=". urlencode($v) ."&";
      
      $params = substr(0, strlen($params) - 1, $params);
    }
    
    //cURL options
    $options = array( 
        CURLOPT_RETURNTRANSFER => true, 
        CURLOPT_HEADER         => false, 
        CURLOPT_FOLLOWLOCATION => true, 
        CURLOPT_ENCODING       => "", 
        CURLOPT_USERAGENT      => NessusServer::USER_AGENT,
        CURLOPT_AUTOREFERER    => true,
        CURLOPT_CONNECTTIMEOUT => NessusServer::CONNECT_TIMEOUT,
        CURLOPT_TIMEOUT        => NessusServer::TIMEOUT,
        CURLOPT_MAXREDIRS      => 10,
        CURLOPT_POST           => 1,
        CURLOPT_POSTFIELDS     => $params,
        CURLOPT_SSL_VERIFYHOST => 0,
        CURLOPT_SSL_VERIFYPEER => false,
        CURLOPT_VERBOSE        => 1
    );
    
    //If we are logged in, a Token is needed in the request cookies.
    if(!is_null($this->token))  {
      $options[CURLOPT_COOKIE] = sprintf("token=%s", $this->token);
      trigger_error(sprintf("Set Token cookie: %s", $options[CURLOPT_COOKIE]), E_USER_NOTICE);
    }
    
    //cURL initialization, configuration, and exec call.
    trigger_error(sprintf("[cURL] Connecting to %s%s...", $this->uri, $path), E_USER_NOTICE);
    $ch      = curl_init($this->uri . $path);
    curl_setopt_array($ch,$options);
    
    /*
     * Example Login Reply
     *
     <?xml version="1.0" encoding="UTF-8"?>
     <reply>
       <seq>3956</seq>
       <status>OK</status>
       <contents>
         <token>530ae8209e0fa0999a546a35b416a8a581acf867213b0d66</token>
         <server_uuid>e0b24d09-8226-a56f-1827-d3589dc2f624cd04046183a79a63</server_uuid>
         <plugin_set>201309202015</plugin_set>
         <loaded_plugin_set>201309202015</loaded_plugin_set>
         <scanner_boottime>1379801507</scanner_boottime>
         <msp>FALSE</msp>
         <idle_timeout>30</idle_timeout>
         <user>
           <name>ncdc.its</name>
           <admin>TRUE</admin>
         </user>
       </contents>
     </reply>
     */
    $content = curl_exec($ch); 
    
    $err     = curl_errno($ch); 
    $errmsg  = curl_error($ch); 
    $header  = curl_getinfo($ch); 
    trigger_error(sprintf("[cURL] Received HTTP code %d from %s%s (%db)", $header["http_code"], $this->uri, $path, strlen($content)), E_USER_NOTICE);
    trigger_error(str_replace("\n", "\n [cURL] ", "[cURL] ". $content), E_USER_NOTICE);
    
    curl_close($ch);
    
    if($err > 0)
      throw new NessusServerException($header, $errno, $errmsg);
    
    if($header["http_code"] != 200)
      throw new NessusServerException($header, $header["http_code"], "Did not receive HTTP OK (200) from server.");
    
    return ($raw) ? $content : new NessusServerReply(simplexml_load_string($content));
  }
  
  private function r()  {
    return mt_rand(1, 9999);
  }
  
  public function login($username, $password)  {
    $params = sprintf("login=%s&password=%s&seq=%d",
      urlencode($username),
      urlencode($password),
      $this->r());
    try  {
      $reply = $this->getReply("/login", $params);
      $this->token = $reply->getToken();
      trigger_error(sprintf("Logged on successfully as user: %s", $username), E_USER_NOTICE);
      return true;
    
    }catch(NessusServerException $e)  {
      trigger_error(sprintf("Could not logon to server %s: %s", $this->uri, $e->getMessage()), E_USER_ERROR);
    }
    
    return false;
  }
  
  public function logout()  {
    try  {
      $this->getReply("/logout", sprintf("seq=%d", $this->r()));
    
    }catch(NessusServerException $e)  {
      trigger_error(sprintf("Could not logout of server %s: %s", $this->uri, $e->getMessage()), E_USER_WARNING);
    
    }
  }
  
  /**
   * Returns a NessusReports object for the reports on the server. You will 
   * need to login prior to this call which will set the appropriate token.
   *
   * @return	object NessusReports
   */
  public function getReports()  {
    $reply = null;
    try  {
      $reply = $this->getReply("/report/list", sprintf("seq=%d", $this->r()));
    
    }catch(NessusServerException $e)  {
      trigger_error(sprintf("Could not get list of reports from %s: %s", $this->uri, $e->getMessage()), E_USER_WARNING);
      
    }
    
    return (!is_null($reply)) ? $reply->getContents()->getReports() : null;
  }
  
  /**
   * Will download a report by the given UUID and store the report at given $path.
   * This will then return the report as well. 
   *
   * @param	string	UUID of the report to download.
   * @param	string	Path to store the report file.
   * @return object NessusReport
   */
  public function downloadReport($uuid, $path)  {
    $scan = null;
    try  {
      $scan_xml = $this->getReply("/file/report/download", sprintf("seq=%d&report=%s", $this->r(), $uuid), true);
      file_put_contents($path, $scan_xml);
      trigger_error(sprintf("Downloaded %dbytes to %s", filesize($path), $path), E_USER_NOTICE); 
      
      $scan = NessusClientData::parse($path);
    
    }catch(NessusServerException $e)  {
      trigger_error(sprintf("Could not download report '%s' from %s: %s", $uuid, $this->uri, $e->getMessage()), E_USER_WARNING);    
    
    }catch(FileNotFoundException $e)  {
      trigger_error(sprintf("Could not open/read %s. Perhaps the XML did not save properly.", $path), E_USER_ERROR);
      
    }
    
    return $scan;
  }
  
}

?>