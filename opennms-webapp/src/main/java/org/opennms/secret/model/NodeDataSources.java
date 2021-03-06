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
//      OpenNMS Licensing       <license@opennms.org>
//      http://www.opennms.org/
//      http://www.opennms.com/
//
package org.opennms.secret.model;

import java.util.List;
import java.util.Set;

/**
 * <p>NodeDataSources class.</p>
 *
 * @author ranger
 * @version $Id: $
 * @since 1.6.12
 */
public class NodeDataSources {
    private Node m_node;
    private List m_dataSources;
    private Set m_interfaces;
    
    /**
     * <p>getInterfaces</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set getInterfaces() {
        return m_interfaces;
    }
    /**
     * <p>setInterfaces</p>
     *
     * @param interfaces a {@link java.util.Set} object.
     */
    public void setInterfaces(Set interfaces) {
        m_interfaces = interfaces;
    }
    /**
     * <p>getNode</p>
     *
     * @return a {@link org.opennms.secret.model.Node} object.
     */
    public Node getNode() {
        return m_node;
    }
    /**
     * <p>setNode</p>
     *
     * @param node a {@link org.opennms.secret.model.Node} object.
     */
    public void setNode(Node node) {
        m_node = node;
    }
    /**
     * <p>getDataSources</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List getDataSources() {
        return m_dataSources;
    }
    /**
     * <p>setDataSources</p>
     *
     * @param dataSources a {@link java.util.List} object.
     */
    public void setDataSources(List dataSources) {
        m_dataSources = dataSources;
    }

    
}
