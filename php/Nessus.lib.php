<?php
/**
 * PHP - Nessus Report (NessusClientData_v2) Parsing Library 
 * Copyright (c)2013 Mike Duncan <mike.duncan@waitwha.com>
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/**
 * NessusClientData (v2) Class
 * Represents the top-level of the Nessus report.
 *
 */
class NessusClientData  {
  
  private $policy;
  private $report;
  
  private function __construct($file)  {
    if(! file_exists($file))
      throw new FileNotFoundException($file);
    
    $dom = simplexml_load_file($file);
    $this->policy = new Policy($dom->Policy);
    $this->report = new Report($dom->Report);
  }
  
  public function getPolicy()  {
    return $this->policy;
  }
  
  public function getReport()  {
    return $this->report;
  }
  
  /**
   * Parses the given $file and returns a NessusClientData object.
   *
   * @param		string	$file 	Nessus Report file to parse.
   * @return	object	NessusClientData
   */
  public static function parse($file)  {
    return new NessusClientData($file);
  }
  
}

if(!class_exists("FileNotFoundException"))  {
  
  /**
   * Simple FileNotFoundException class.
   */
  class FileNotFoundException extends Exception  {
    
    public function __construct($file)  {
      parent::__construct(sprintf("File '%s' not found.", $file), 404);
    }
    
  }
  
}

/**
 * Simple parsing exception class.
 *
 */
class NessusParseException extends Exception  {
  
  public function __construct($msg)  {
    parent::__construct($msg, -1);
  }
  
}

/**
 * Represents a Policy element.
 *
 */
class Policy {
  
  private $policyName;
  private $policyComments;
  private $serverPreferences;
  private $pluginPreferences;
  private $familySection;
  
  public function __construct($Policy)  {
    if(is_null($Policy))
      throw new NessusParseException("Could not find Policy element.");
    
    $this->policyName = $Policy->policyName;
    trigger_error(sprintf("Parsed policy '%s' successfully.", $this->policyName), E_USER_NOTICE);
    
    $this->policyComments = ""; //TODO
    
    $this->serverPreferences = new ServerPreferences($Policy->Preferences->ServerPreferences);
    $this->pluginPreferences = new PluginPreferences($Policy->Preferences->PluginPreferences);
    //$this->familySection = new FamilySection($Policy->FamilySection);
  }
  
  public function getPolicyName()  {
    return $this->policyName;
  }
  
  public function getPolicyComments()  {
    return $this->policyComments;
  }
  
  public function getServerPreferences()  {
    return $this->serverPreferences;
  }
  
  public function getPluginPreferences()  {
    return $this->pluginPreferences;
  }
  
  public function getFamilySection()  {
    return $this->familySection;
  }
  
  public function __toString()  {
    return $this->policyName;
  }
  
}

/**
 * Represents a Preference element which is contained within a 
 * ServerPreferences element.
 *
 */
class Preference  {
  
  private $name;
  private $value;
  
  public function __construct($name, $value=null)  {
    if(is_string($name))  {
      $this->name = $name;
      $this->value = $value;
    }else{
      $Property = $name;
      $this->name = $Property->name;
      $this->value = $Property->value;
    }
  }
  
  public function getName()  {
    return $this->name;
  }
  
  protected function setName($name)  {
    $this->name = $name;
  }
  
  public function getValue()  {
    return $this->value;
  }
  
  protected function setValue($value)  {
    $this->value = $value;
  }
  
  public function __toString()  {
    return $this->getName();
  }
  
}

if(!class_exists("ArrayList"))  {
  /**
   * General-use ArrayList implementation using PHP's Iterator
   * interface. Once extended, use the $this->add() to add to the 
   * underlying collection.
   *
   */
   class ArrayList implements Iterator  {
  
     private $collection;
     private $index;
     
     /**
      * Constructor
      *
      */
     public function __construct()  {
       $this->collection = array();
       $this->index = 0;
     }
  
     /**
      * @see Iterator::current
      */
     public function current()  {
       return $this->collection[$this->index];
     }
     
     /**
      * @see Iterator::key
      */
     public function key()  {
       return $this->index;
     }
  
     /**
      * @see Iterator::next
      */
     public function next()  {
       $this->index++;
     }
  
     /**
      * @see Iterator::rewind
      */
     public function rewind()  {
       $this->index = 0;
     }
  
     /**
      * @see Iterator::valid
      */
     public function valid()  {
       return isset($this->collection[$this->index]);
     }
  
     /**
      * Adds a object to the collection.
      *
      * @param	object	$object to add.
      */
     protected function add($object)  {
       array_push($this->collection, $object);
     }
     
     /**
      * Clears the collection and resets the index pointer.
      *
      */
     public function clear()  {
       $this->collection = array();
       $this->index = 0;
     }
  
     /**
      * Returns the size of the collection.
      *
      * @return	int		Size of the collection.
      */
     public function size()  {
       return count($this->collection);
     }
  
   }
} //End ArrayList check and implementation.

/**
 * Represents the ServerPreferences element which contains 
 * Preference elements within.
 *
 */
class ServerPreferences extends ArrayList {
  
  public function __construct($ServerPreferences)  {
    parent::__construct();
    foreach($ServerPreferences->children() as $property)
      $this->add(new Preference($property));
    
  }
    
}

/**
 * Represents the PluginPreferences element which contains
 * Item elements within.
 *
 */ 
class PluginPreferences extends ArrayList {
  
  public function __construct($PluginPreferences)  {
    parent::__construct();
    //TODO
  }
  
}

/**
 * Represents a Item element which is contained within a
 * PluginPreferences element.
 *
 */
class Item  {
  
  private $fullName;
  private $pluginName;
  private $pluginId;
  private $authenticated;
  private $preferenceName;
  private $preferenceType;
  private $preferenceValues;
  private $selectedValue;
  
  public function __construct()  {
    $this->fullName = "";
    $this->pluginName = "";
    $this->pluginId = -1;
    $this->authenticated = false;
    $this->preferenceName = "";
    $this->preferenceType = "";
    $this->preferenceValues = "";
    $this->selectedValue = "";
  }
  
  public function getFullName()  {
    return $this->fullName;
  }
  
  public function getPluginName()  {
    return $this->pluginName;
  }
  
  public function getPluginId()  {
    return $this->pluginId;
  }
  
  public function isAuthenticated()  {
    return $this->authenticated;
  }
  
  public function getPreferenceName()  {
    return $this->preferenceName;
  }
  
  public function getPreferenceType()  {
    return $this->preferenceType;
  }
  
  public function getPreferenceValues()  {
    return $this->preferenceValues;
  }
  
  public function getSelectedValue()  {
    return $this->selectedValue;
  }
  
  public function __toString()  {
    return $this->getFullName();
  }
  
}

/**
 * Represents the FamilySection element which contains FamilyItem
 * objects/elements.
 *
 */
class FamilySection extends ArrayList  {
  
  public function __construct()  {
    parent::__construct();
  }
  
  public function addFamilyItem($familyItem)  {
    $this->add($familyItem);
  }
  
}

/**
 * Represents a FamilyItem element which is contained within a
 * FamilySection element.
 *
 */
class FamilyItem  {
  
  private $familyName;
  private $status;
  
  public function __construct($familyName, $status)  {
    $this->familyName = $familyName;
    $this->status = $status;
  }
  
  public function getFamilyName()  {
    return $this->familyName;
  }
  
  public function getStatus()  {
    return $this->status;
  }
  
  public function __toString()  {
    return $this->getFamilyName();
  }
  
}

/**
 * Represents a Report element.
 *
 */
class Report {
  
  private $name;
  private $reportHosts;
  
  public function __construct($Report)  {
    $this->name = $Report["name"];
    trigger_error(sprintf("Parsed report '%s' successfully.", $this->name), E_USER_NOTICE);
    
    $this->reportHosts = new ReportHosts();
    foreach($Report->ReportHost as $reportHost)
      $this->reportHosts->addReportHost($reportHost);
    
  }
  
  public function getName()  {
    return $this->name;
  }
  
  public function getReportHosts()  {
    return $this->reportHosts;
  }
  
  /**
   * Returns the number of ReportHost objects within 
   * this Report.
   *
   * @return	int		Size of the underlying ReportHosts object.
   */
  public function size()  {
    return $this->reportHosts->size();
  }
  
  public function __toString()  {
    return sprintf("%d host(s) within report '%s'",
      $this->size(),
      $this->getName());
  }
  
}

/**
 * Represents a ReportHosts element which contains ReportHost 
 * element(s) within.
 *
 */
class ReportHosts extends ArrayList  {
  
  public function __construct()  {
    parent::__construct();
  }
  
  public function addReportHost($reportHost)  {
    $this->add(new ReportHost($reportHost));
  }
    
}

/**
 * Criticality Class
 */
class Criticality  {
  
  const CRITICAL = 0;
  const HIGH = 1;
  const MEDIUM = 2;
  const LOW = 3;
  const NONE = 4;
  
  private function __construct()  {}
  
  public static function toString($criticality)  {
    switch($criticality)  {
      case Criticality::CRITICAL:
        return "critical";
        
      case Criticality::HIGH:
        return "high";
        
      case Criticality::MEDIUM:
        return "medium";
        
      case Criticality::LOW:
        return "low";
        
    }
    
    return "info";
  }
  
}

/**
 * Represents a ReportHost element which is contained within a ReportHosts
 * element.
 *
 */
class ReportHost  {
  
  private $name;
  private $hostProperties;
  private $reportItems;
  
  public function __construct($ReportHost)  {
    $this->name = $ReportHost["name"];
    $this->hostProperties = new HostProperties($ReportHost->HostProperties);
    $this->reportItems = new ReportItems();
    foreach($ReportHost->ReportItem as $reportItem)
      $this->reportItems->addReportItem($reportItem);
    
  }
  
  public function getName()  {
    return $this->name;
  }
  
  public function getOS()  {
    foreach($this->hostProperties as $tag)
      if(strcmp($tag->getName(), "operating-system") == 0)
        return $tag->getValue();
        
    return "(unknown)";
  }
  
  public function getNetbiosName()  {
    foreach($this->hostProperties as $tag)
      if(strcmp($tag->getName(), "netbios-name") == 0)
        return $tag->getValue();
        
    return "(unknown)";
  }
  
  public function getHostProperties()  {
    return $this->hostProperties;
  }
  
  public function getReportItems()  {
    return $this->reportItems;
  }
  
  public function __toString()  {
    return sprintf("%d report items/findings for host %s.", 
      $this->reportItems->size(),
      $this->getName());
  }
  
}

/**
 * Represents a HostProperties element which contains Tag elements and
 * is contained within a ReportHost element.
 *
 */
class HostProperties extends ArrayList  {
  
  public function __construct($HostProperties)  {
    parent::__construct();
    foreach($HostProperties->children() as $tag)
      $this->add(new Tag($tag));
    
  }
  
}

/**
 * Represents a Tag element which is contained within a 
 * HostProperties element.
 *
 */
class Tag extends Preference {
  
  public function __construct($tag)  {
    parent::__construct("", "");
    $this->setName($tag["name"]);
    $this->setValue($tag);
  }
  
}

/**
 * Represents a ReportItems element which contains ReporItem elements
 * and is contained within a ReportHost element.
 *
 */
class ReportItems extends ArrayList  {
  
  public function __construct()  {
    parent::__construct();
  }
  
  public function addReportItem($reportItem)  {
    $this->add(new ReportItem($reportItem));
  }
    
}

/**
 * Represents a ReportItem element which is contained within a 
 * ReportItems element.
 *
 */
class ReportItem  {
  
  private $port;
  private $serviceName;
  private $protocol;
  private $severity;
  private $description;
  private $fileName;
  private $scriptVersion;
  private $pluginId;
  private $pluginFamily;
  private $pluginModificationDate;
  private $pluginPublicationDate;
  private $pluginName;
  private $pluginType;
  private $riskFactor;
  private $solution;
  private $synopsis;
  private $pluginOutput;
  
  public function __construct($ReportItem)  {
    $this->port = $ReportItem["port"];
    $this->serviceName = $ReportItem["svc_name"];
    $this->severity = $ReportItem["severity"];
    $this->protocol = $ReportItem["protocol"];
    $this->pluginId= $ReportItem["pluginID"];
    $this->pluginName = $ReportItem["pluginName"];
    $this->pluginFamily = $ReportItem["pluginFamily"];
    $this->description = $ReportItem->description;
    $this->fileName = $ReportItem->fname;
    $this->scriptVersion = $ReportItem->script_version;
    $this->pluginModificationDate = $ReportItem->plugin_modification_date;
    $this->pluginType = $ReportItem->plugin_type;
    $this->pluginPublicationDate = $ReportItem->plugin_publication_date;
    $this->riskFactor = $ReportItem->risk_factor;
    $this->solution = $ReportItem->solution;
    $this->synopsis = $ReportItem->synopsis;
    $this->pluginOutput = $ReportItem->plugin_output;
  }
  
  public function getPort()  {
    return $this->port;
  }
  
  public function getServiceName()  {
    return $this->serviceName;
  }
  
  public function getProtocol()  {
    return $this->protocol;
  }
  
  public function getSeverity()  {
    return $this->severity;
  }
  
  public function getDescription()  {
    return $this->description;
  }
  
  public function getFileName()  {
    return $this->fileName;
  }
  
  public function getScriptVersion()  {
    return $this->scriptVersion;
  }
  
  public function getPluginModificationDate()  {
    return $this->pluginModificationDate;
  }
  
  public function getPluginPublicationDate()  {
    return $this->pluginPublicationDate;
  }
  
  public function getPluginName()  {
    return $this->pluginName;
  }
  
  public function getPluginType()  {
    return $this->pluginType;
  }
  
  public function getPluginId()  {
    return $this->pluginId;
  }
  
  public function getPluginFamily()  {
    return $this->pluginFamily;
  }
  
  public function getRiskFactor()  {
    return $this->riskFactor;
  }
  
  public function getSoltuion()  {
    return $this->solution;
  }
  
  public function getSynopsis()  {
    return $this->synopsis;
  }
  
  public function getPluginOutput()  {
    return $this->pluginOutput;
  }
  
  public function getCriticality()  {
    return Criticality::toString($this->severity);
  }
  
  public function __toString()  {
    return sprintf("[%s] %s on port %s/%d (%s)", 
      strtoupper($this->getCriticality()),
      $this->getDescription(),
      $this->getProtocol(),
      $this->getPort(),
      $this->getServiceName());
  }
  
}

?>