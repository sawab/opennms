//
// This file is part of the OpenNMS(R) Application.
//
// OpenNMS(R) is Copyright (C) 2002-2003 The OpenNMS Group, Inc.  All rights reserved.
// OpenNMS(R) is a derivative work, containing both original code, included code and modified
// code that was published under the GNU General Public License. Copyrights for modified 
// and included code are below.
//
// OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
//
// Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.                                                            
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//       
// For more information contact: 
//      OpenNMS Licensing       <license@opennms.org>
//      http://www.opennms.org/
//      http://www.opennms.com/
//

package org.opennms.netmgt.eventd.adaptors.udp;

import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;

/**
 * <p>UdpEventReceiverMBean interface.</p>
 *
 * @author <a href="mailto:weave@oculan.com">Brian Weaver </a>
 * @author <a href="http://www.oculan.com">Oculan Corporation </a>
 * @author <a href="mailto:weave@oculan.com">Brian Weaver </a>
 * @author <a href="http://www.oculan.com">Oculan Corporation </a>
 * @version $Id: $
 */
public interface UdpEventReceiverMBean {
    /**
     * <p>init</p>
     */
    public void init();

    /**
     * <p>destroy</p>
     */
    public void destroy();

    /**
     * <p>start</p>
     */
    public void start();

    /**
     * <p>stop</p>
     */
    public void stop();

    /**
     * <p>setPort</p>
     *
     * @param port a {@link java.lang.Integer} object.
     */
    public void setPort(Integer port);

    /**
     * <p>getPort</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getPort();

    /**
     * <p>getStatus</p>
     *
     * @return a int.
     */
    public int getStatus();

    /**
     * <p>addEventHandler</p>
     *
     * @param name a {@link java.lang.String} object.
     * @throws javax.management.MalformedObjectNameException if any.
     * @throws javax.management.InstanceNotFoundException if any.
     */
    public void addEventHandler(String name) throws MalformedObjectNameException, InstanceNotFoundException;

    /**
     * <p>removeEventHandler</p>
     *
     * @param name a {@link java.lang.String} object.
     * @throws javax.management.MalformedObjectNameException if any.
     * @throws javax.management.InstanceNotFoundException if any.
     */
    public void removeEventHandler(String name) throws MalformedObjectNameException, InstanceNotFoundException;

    /**
     * <p>setLogPrefix</p>
     *
     * @param prefix a {@link java.lang.String} object.
     */
    public void setLogPrefix(String prefix);
}
