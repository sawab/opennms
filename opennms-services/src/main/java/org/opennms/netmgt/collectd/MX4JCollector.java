//
//This file is part of the OpenNMS(R) Application.
//
//OpenNMS(R) is Copyright (C) 2002-2003 The OpenNMS Group, Inc.  All rights reserved.
//OpenNMS(R) is a derivative work, containing both original code, included code and modified
//code that was published under the GNU General Public License. Copyrights for modified 
//and included code are below.
//
//OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation; either version 2 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.                                                            
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//
//For more information contact: 
//OpenNMS Licensing       <license@opennms.org>
//http://www.opennms.org/
//http://www.opennms.com/
//

package org.opennms.netmgt.collectd;

import java.net.InetAddress;
import java.util.Map;

import org.opennms.protocols.jmx.connectors.ConnectionWrapper;
import org.opennms.protocols.jmx.connectors.MX4JConnectionFactory;

/*
* The MX4JCollector class manages the querying and storage of data into RRD files.  The list of 
* MBeans to be queried is read from the jmx-datacollection-config.xml file using the "mx4j" service name.
* The super class, JMXCollector, performs all the work. 
* 
* @author <A HREF="mailto:mike@opennms.org">Mike Jamison </A>
* @author <A HREF="http://www.opennms.org/">OpenNMS </A>
*/
/**
 * <p>MX4JCollector class.</p>
 *
 * @author ranger
 * @version $Id: $
 */
public class MX4JCollector extends JMXCollector {

  /**
   * <p>Constructor for MX4JCollector.</p>
   */
  public MX4JCollector() {
      setServiceName("mx4j");
      setUseFriendlyName(true);
  }

  /* Return a ConnectionWrapper object using the factory.
   * 
   * @see org.opennms.netmgt.collectd.JMXCollector#getMBeanServerConnection(java.util.Map, java.net.InetAddress)
   */
  /** {@inheritDoc} */
  public ConnectionWrapper getMBeanServerConnection(Map parameterMap, InetAddress address) {
      return MX4JConnectionFactory.getMBeanServerConnection(parameterMap, address);
  }
}
