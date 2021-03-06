//
// This file is part of the OpenNMS(R) Application.
//
// OpenNMS(R) is Copyright (C) 2006 The OpenNMS Group, Inc.  All rights reserved.
// OpenNMS(R) is a derivative work, containing both original code, included code and modified
// code that was published under the GNU General Public License. Copyrights for modified 
// and included code are below.
//
// OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
//
// Modifications:
//
// 2007 Jun 24: Use Java 5 generics. - dj@opennms.org
//
// Original code base Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
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
//   OpenNMS Licensing       <license@opennms.org>
//   http://www.opennms.org/
//   http://www.opennms.com/
//
// Tab Size = 8

package org.opennms.netmgt.importer.operations;

import java.util.LinkedList;
import java.util.List;

import org.opennms.netmgt.model.EntityVisitor;
import org.opennms.netmgt.model.OnmsDistPoller;
import org.opennms.netmgt.xml.event.Event;

/**
 * <p>InsertOperation class.</p>
 *
 * @author ranger
 * @version $Id: $
 */
public class InsertOperation extends AbstractSaveOrUpdateOperation {
    
    /**
     * <p>Constructor for InsertOperation.</p>
     *
     * @param foreignSource a {@link java.lang.String} object.
     * @param foreignId a {@link java.lang.String} object.
     * @param nodeLabel a {@link java.lang.String} object.
     * @param building a {@link java.lang.String} object.
     * @param city a {@link java.lang.String} object.
     * @param nonIpInterfaces a {@link java.lang.Boolean} object.
     * @param nonIpSnmpPrimary a {@link java.lang.String} object.
     */
    public InsertOperation(String foreignSource, String foreignId, String nodeLabel, String building, String city,
            Boolean nonIpInterfaces, String nonIpSnmpPrimary) {
		super(foreignSource, foreignId, nodeLabel, building, city, nonIpInterfaces, nonIpSnmpPrimary);
	}

	/**
	 * <p>doPersist</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<Event> doPersist() {
        OnmsDistPoller distPoller = getDistPollerDao().get("localhost");
        getNode().setDistPoller(distPoller);
        getNodeDao().save(getNode());
        
    	final List<Event> events = new LinkedList<Event>();

    	EntityVisitor eventAccumlator = new AddEventVisitor(events);

    	getNode().visit(eventAccumlator);
        
    	return events;
    }

    /**
     * <p>toString</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String toString() {
	return "INSERT: Node: "+getNode().getLabel();
    }

}
