/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2018 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2018 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.enlinkd.topogen.protocol;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.opennms.enlinkd.topogen.util.InetAddressGenerator;
import org.opennms.enlinkd.topogen.TopologyGenerator;
import org.opennms.enlinkd.topogen.TopologyGenerator.Topology;
import org.opennms.enlinkd.topogen.TopologyPersister;
import org.opennms.enlinkd.topogen.topology.LinkedPairGenerator;
import org.opennms.enlinkd.topogen.topology.PairGenerator;
import org.opennms.enlinkd.topogen.topology.RandomConnectedPairGenerator;
import org.opennms.enlinkd.topogen.topology.UndirectedPairGenerator;
import org.opennms.enlinkd.topogen.util.RandomUtil;
import org.opennms.netmgt.model.OnmsIpInterface;
import org.opennms.netmgt.model.OnmsNode;
import org.opennms.netmgt.model.OnmsSnmpInterface;
import org.opennms.netmgt.model.monitoringLocations.OnmsMonitoringLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class Protocol<Element> {

    private final static Logger LOG = LoggerFactory.getLogger(CdpProtocol.class);

    protected final Topology topology;
    private final int amountNodes;
    private final int amountLinks;
    private final int amountElements;
    private final int amountSnmpInterfaces;
    private final int amountIpInterfaces;
    private final TopologyPersister persister;
    private final RandomUtil random = new RandomUtil();

    public Protocol(Topology topology, int amountNodes, int amountLinks, int amountElements,
        int amountSnmpInterfaces, int amountIpInterfaces, TopologyPersister persister) {
        this.topology = topology;
        this.amountNodes = amountNodes;
        this.amountLinks = amountLinks;
        this.amountElements = amountElements;
        this.amountSnmpInterfaces = amountSnmpInterfaces;
        this.amountIpInterfaces = amountIpInterfaces;
        this.persister = persister;
    }

    public void createAndPersistNetwork() throws SQLException {
        LOG.info("creating {} {} topology with {} Nodes, {} Elements, {} Links, {} SnmpInterfaces, {} IpInterfaces.",
                this.topology,
                this.getProtocol(),
                this.amountNodes,
                this.amountElements,
                this.amountLinks,
                this.amountSnmpInterfaces,
                this.amountIpInterfaces);
        List<OnmsNode> nodes = createNodes(amountNodes);
        persister.persistNodes(nodes);

        createAndPersistProtocolSpecificEntities(nodes);

        List<OnmsSnmpInterface> snmpInterfaces = createSnmpInterfaces(nodes);
        persister.persistOnmsInterfaces(snmpInterfaces);
        List<OnmsIpInterface> ipInterfaces = createIpInterfaces(snmpInterfaces);
        persister.persistIpInterfaces(ipInterfaces);
    }

    protected abstract void createAndPersistProtocolSpecificEntities(List<OnmsNode> nodes) throws SQLException;

    protected abstract TopologyGenerator.Protocol getProtocol();

    private OnmsMonitoringLocation createMonitoringLocation() {
        OnmsMonitoringLocation location = new OnmsMonitoringLocation();
        location.setLocationName("Default");
        location.setMonitoringArea("localhost");
        return location;
    }

    protected List<OnmsNode> createNodes(int amountNodes) {
        OnmsMonitoringLocation location = createMonitoringLocation();
        ArrayList<OnmsNode> nodes = new ArrayList<>();
        for (int i = 0; i < amountNodes; i++) {
            nodes.add(createNode(i, location));
        }
        return nodes;
    }

    protected OnmsNode createNode(int count, OnmsMonitoringLocation location) {
        OnmsNode node = new OnmsNode();
        node.setId(count); // we assume we have an empty database and can just generate the ids
        node.setLabel("Node" + count);
        node.setLocation(location);
        return node;
    }

    protected List<OnmsSnmpInterface> createSnmpInterfaces(List<OnmsNode> nodes) {
        ArrayList<OnmsSnmpInterface> interfaces = new ArrayList<>();
        for (int i=0; i<this.amountSnmpInterfaces; i++) {
            interfaces.add(createSnmpInterface(i, random.getRandom(nodes)));
        }
        return interfaces;
    }

    private OnmsSnmpInterface createSnmpInterface(int id, OnmsNode node) {
        OnmsSnmpInterface onmsSnmpInterface = new OnmsSnmpInterface();
        onmsSnmpInterface.setId(id);
        onmsSnmpInterface.setNode(node);

        onmsSnmpInterface.setIfIndex(id);
        onmsSnmpInterface.setIfType(4);
        onmsSnmpInterface.setIfSpeed(5L);
        onmsSnmpInterface.setIfAdminStatus(6);
        onmsSnmpInterface.setIfOperStatus(7);
        onmsSnmpInterface.setLastCapsdPoll(new Date());
        onmsSnmpInterface.setLastSnmpPoll(new Date());


        return onmsSnmpInterface;
    }

    protected List<OnmsIpInterface> createIpInterfaces(List<OnmsSnmpInterface> snmps) {
        ArrayList<OnmsIpInterface> interfaces = new ArrayList<>();
        InetAddressGenerator inetGenerator = new InetAddressGenerator();
        for(int i=0; i<this.amountIpInterfaces; i++){
            interfaces.add(createIpInterface(i, random.getRandom(snmps), inetGenerator.next()));
        }
        return interfaces;
    }

    private OnmsIpInterface createIpInterface(int id, OnmsSnmpInterface snmp, InetAddress inetAddress) {
        OnmsIpInterface ip = new OnmsIpInterface();
        ip.setId(id);
        ip.setSnmpInterface(snmp);
        ip.setIpLastCapsdPoll(new Date());
        ip.setNode(snmp.getNode());
        ip.setIpAddress(inetAddress);
        return ip;
    }



    protected <E> PairGenerator<E> createPairGenerator(List<E> elements){
        if(TopologyGenerator.Topology.complete == topology){
            return new UndirectedPairGenerator<>(elements);
        } else if(TopologyGenerator.Topology.ring == topology) {
            return new LinkedPairGenerator<>(elements);
        } else if (TopologyGenerator.Topology.random == topology){
            return new RandomConnectedPairGenerator<>(elements);
        } else {
            throw new IllegalArgumentException("unknown topology: "+ topology);
        }
    }
}
