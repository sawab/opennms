/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2019 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2019 The OpenNMS Group, Inc.
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

package org.opennms.enlinkd.topogen;

import java.sql.SQLException;

import org.opennms.enlinkd.topogen.protocol.CdpProtocol;
import org.opennms.enlinkd.topogen.protocol.IsIsProtocol;
import org.opennms.enlinkd.topogen.protocol.LldpProtocol;
import org.opennms.enlinkd.topogen.protocol.OspfProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Can be used to generate a Linkd Topology for testing purposes.
 * Usage:
 * <code>
 * TopologyGenerator generator = TopologyGenerator.builder()
 * .persister(topologyPersister)
 * ...
 * .build();
 * generator.generateTopology();
 * </code>
 */
public class TopologyGenerator {
    private final static Logger LOG = LoggerFactory.getLogger(TopologyGenerator.class);

    public static TopologyGeneratorBuilder builder() {
        return new TopologyGeneratorBuilder();
    }

    public enum Topology {
        ring, random, complete
    }

    public enum Protocol {
        cdp, isis, lldp, ospf
    }

    private TopologyContext topologyContext;

    private final Integer amountNodes;

    private final int amountElements;

    private final int amountLinks;

    private final int amountSnmpInterfaces;

    private final int amountIpInterfaces;

    private final Topology topology;

    private final Protocol protocol;

    private final boolean deleteExistingTolology;

    private TopologyGenerator(
            TopologyPersister persister,
            ProgressCallback progressCallback,
            Integer amountNodes,
            Integer amountElements,
            Integer amountLinks,
            Integer amountSnmpInterfaces,
            Integer amountIpInterfaces,
            Topology topology,
            Protocol protocol,
            Boolean deleteExistingTolology
    ) {
        this.topologyContext = new TopologyContext(progressCallback, persister);
        this.amountNodes = setToDefaultIfNotSet(amountNodes, 10);
        this.amountElements = setToDefaultIfNotSet(amountElements, this.amountNodes);
        this.amountLinks = setToDefaultIfNotSet(amountLinks, this.amountElements);
        this.amountSnmpInterfaces = setToDefaultIfNotSet(amountSnmpInterfaces, this.amountNodes * 18);
        this.amountIpInterfaces = setToDefaultIfNotSet(amountIpInterfaces, this.amountNodes * 2);
        this.topology = setToDefaultIfNotSet(topology, Topology.random);
        this.protocol = setToDefaultIfNotSet(protocol, Protocol.cdp);
        this.deleteExistingTolology = setToDefaultIfNotSet(deleteExistingTolology, false);

        // do basic checks to get configuration right:
        assertMoreOrEqualsThan("we need at least as many nodes as elements", this.amountElements, this.amountNodes);
        assertMoreOrEqualsThan("we need at least 2 nodes", 2, this.amountNodes);
        assertMoreOrEqualsThan("we need at least 2 elements", 2, this.amountElements);
        assertMoreOrEqualsThan("we need at least 1 link", 1, this.amountLinks);
    }

    private <T> T setToDefaultIfNotSet(T value, T defaultValue) {
        return (value == null) ? defaultValue : value;
    }

    public void generateTopology() throws SQLException {
        topologyContext.currentProgress("Generating a topology with the following settings:%n  amountNodes=%s,"
                        + "%n  amountElements=%s,%n  amountLinks=%s,%n  amountSnmpInterfaces=%s,%n  amountIpInterfaces=%s,"
                        + "%n  topology=%s,%n  protocol=%s,%n  deleteExistingTolology=%s", this.amountNodes, this.amountElements,
                this.amountLinks, this.amountSnmpInterfaces, this.amountIpInterfaces, this.topology, this.protocol,
                this.deleteExistingTolology);

        if (deleteExistingTolology) {
            this.topologyContext.getTopologyPersister().deleteTopology();
        }
        getProtocol().createAndPersistNetwork();
    }

    private org.opennms.enlinkd.topogen.protocol.Protocol getProtocol() {
        if (Protocol.cdp == this.protocol) {
            return new CdpProtocol(this.topology,
                    amountNodes, amountLinks, amountElements, amountSnmpInterfaces, amountIpInterfaces, topologyContext);
        } else if (Protocol.isis == this.protocol) {
            return new IsIsProtocol(this.topology,
                    amountNodes, amountLinks, amountElements, amountSnmpInterfaces, amountIpInterfaces, topologyContext);
        } else if (Protocol.lldp == this.protocol) {
            return new LldpProtocol(this.topology,
                    amountNodes, amountLinks, amountElements, amountSnmpInterfaces, amountIpInterfaces, topologyContext);
        } else if (Protocol.ospf == this.protocol) {
            return new OspfProtocol(this.topology,
                    amountNodes, amountLinks, amountElements, amountSnmpInterfaces, amountIpInterfaces, topologyContext);
        } else {
            throw new IllegalArgumentException("Don't know this protocol: " + this.protocol);
        }
    }

    private static void assertMoreOrEqualsThan(String message, int expected, int actual) {
        if (actual < expected) {
            throw new IllegalArgumentException(message + String.format(" minimum expected=%s but found actual=%s", expected, actual));
        }
    }

    public static class TopologyGeneratorBuilder {

        private TopologyPersister persister;
        private ProgressCallback progressCallback;
        private Integer amountNodes;
        private Integer amountElements;
        private Integer amountLinks;
        private Integer amountSnmpInterfaces;
        private Integer amountIpInterfaces;
        private Topology topology;
        private Protocol protocol;
        private Boolean deleteExistingTolology;

        private TopologyGeneratorBuilder() {
        }

        public TopologyGeneratorBuilder persister(TopologyPersister persister) {
            this.persister = persister;
            return this;
        }

        public TopologyGeneratorBuilder amountNodes(Integer amountNodes) {
            this.amountNodes = amountNodes;
            return this;
        }

        public TopologyGeneratorBuilder amountElements(Integer amountElements) {
            this.amountElements = amountElements;
            return this;
        }

        public TopologyGeneratorBuilder amountLinks(Integer amountLinks) {
            this.amountLinks = amountLinks;
            return this;
        }

        public TopologyGeneratorBuilder amountSnmpInterfaces(Integer amountSnmpInterfaces) {
            this.amountSnmpInterfaces = amountSnmpInterfaces;
            return this;
        }

        public TopologyGeneratorBuilder amountIpInterfaces(Integer amountIpInterfaces) {
            this.amountIpInterfaces = amountIpInterfaces;
            return this;
        }

        public TopologyGeneratorBuilder topology(Topology topology) {
            this.topology = topology;
            return this;
        }

        public TopologyGeneratorBuilder protocol(Protocol protocol) {
            this.protocol = protocol;
            return this;
        }

        public TopologyGeneratorBuilder progressCallback(ProgressCallback progressCallback) {
            this.progressCallback = progressCallback;
            return this;
        }

        public TopologyGeneratorBuilder deleteExistingTolology(
                Boolean deleteExistingTolology) {
            this.deleteExistingTolology = deleteExistingTolology;
            return this;
        }

        public TopologyGenerator build() {
            if(progressCallback == null){
                progressCallback = new ProgressCallback() {
                    // Default: log the progress
                    private Logger log = LoggerFactory.getLogger(TopologyGenerator.class);
                    @Override
                    public void currentProgress(String progress) {
                        log.info(progress);
                    }
                };
            }
            return new TopologyGenerator(persister, progressCallback, amountNodes, amountElements, amountLinks,
                    amountSnmpInterfaces, amountIpInterfaces, topology, protocol, deleteExistingTolology);
        }
    }

    /** Used to record the current progress of the generation. */
    public abstract static class ProgressCallback {

        public abstract void currentProgress(String progress);

        void currentProgress(String progress, Object ... args) {
            currentProgress(String.format(progress, args));
        }
    }
}
