/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2012-2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
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

package org.opennms.features.topology.plugins.topo.linkd.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.opennms.features.topology.api.browsers.ContentType;
import org.opennms.features.topology.api.browsers.SelectionAware;
import org.opennms.features.topology.api.browsers.SelectionChangedListener;
import org.opennms.features.topology.api.topo.AbstractTopologyProvider;
import org.opennms.features.topology.api.topo.Defaults;
import org.opennms.features.topology.api.topo.GraphProvider;
import org.opennms.features.topology.api.topo.Vertex;
import org.opennms.features.topology.api.topo.VertexRef;
import org.opennms.netmgt.enlinkd.service.api.ProtocolSupported;
import org.opennms.netmgt.topologies.service.api.OnmsTopology;
import org.opennms.netmgt.topologies.service.api.OnmsTopologyDao;
import org.opennms.netmgt.topologies.service.api.OnmsTopologyException;
import org.opennms.netmgt.topologies.service.api.OnmsTopologyVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.collect.Lists;

public class LinkdTopologyProvider extends AbstractTopologyProvider implements GraphProvider {

    private static Logger LOG = LoggerFactory.getLogger(LinkdTopologyProvider.class);

    private OnmsTopologyDao m_onmsTopologyDao;

    private final Timer m_loadFullTimer;
    private final Timer m_loadLldpLinksTimer;
    private final Timer m_loadOspfLinksTimer;
    private final Timer m_loadCdpLinksTimer;
    private final Timer m_loadIsisLinksTimer;
    private final Timer m_loadBridgeLinksTimer;
    private final Timer m_loadVerticesTimer;
    private final Timer m_loadEdgesTimer;

    public static final String TOPOLOGY_NAMESPACE_LINKD = "nodes";
    
    private SelectionAware selectionAwareDelegate = new LinkdSelectionAware();

    public LinkdTopologyProvider(MetricRegistry registry) {
        super(TOPOLOGY_NAMESPACE_LINKD);
        Objects.requireNonNull(registry);
        m_loadFullTimer = registry.timer(MetricRegistry.name("enlinkd", "load", "full"));
        m_loadLldpLinksTimer = registry.timer(MetricRegistry.name("enlinkd", "load", "links", "lldp"));
        m_loadOspfLinksTimer = registry.timer(MetricRegistry.name("enlinkd", "load", "links", "ospf"));
        m_loadCdpLinksTimer = registry.timer(MetricRegistry.name("enlinkd", "load", "links", "cdp"));
        m_loadIsisLinksTimer = registry.timer(MetricRegistry.name("enlinkd", "load", "links", "isis"));
        m_loadBridgeLinksTimer = registry.timer(MetricRegistry.name("enlinkd", "load", "links", "bridge"));
        m_loadVerticesTimer = registry.timer(MetricRegistry.name("enlinkd", "load", "vertices", "none"));
        m_loadEdgesTimer = registry.timer(MetricRegistry.name("enlinkd", "load", "edges", "none"));
    }
    
    @Override
    public SelectionChangedListener.Selection getSelection(List<VertexRef> selectedVertices, ContentType type) {
       return selectionAwareDelegate.getSelection(selectedVertices, type);
    }

    @Override
    public boolean contributesTo(ContentType type) {
        return selectionAwareDelegate.contributesTo(type);
    }
    
    private void loadEdges() {

        Timer.Context context = m_loadLldpLinksTimer.time();
        try{
            loadTopology(ProtocolSupported.LLDP);
            LOG.info("loadEdges: LldpLink loaded");
        } catch (OnmsTopologyException e) {
            LOG.info("loadEdges: {}", e.getMessage() );
        } catch (Exception e){
            LOG.error("Loading LldpLink failed: {}",e.getMessage(),e);
        } finally {
            context.stop();
        }

        context = m_loadOspfLinksTimer.time();
        try{
            loadTopology(ProtocolSupported.OSPF);
            LOG.info("loadEdges: OspfLink loaded");
        } catch (OnmsTopologyException e) {
            LOG.info("loadEdges: {}", e.getMessage() );
        } catch (Exception e){
            LOG.error("Loading OspfLink failed: {}",e.getMessage(),e);
        } finally {
            context.stop();
        }

        context = m_loadCdpLinksTimer.time();
        try{
            loadTopology(ProtocolSupported.CDP);
            LOG.info("loadEdges: CdpLink loaded");
        } catch (OnmsTopologyException e) {
            LOG.info("loadEdges: {}", e.getMessage() );
        } catch (Exception e){
            LOG.error("Loading CdpLink failed: {}",e.getMessage(),e);
        } finally {
            context.stop();
        }

        context = m_loadIsisLinksTimer.time();
        try{
            loadTopology(ProtocolSupported.ISIS);
            LOG.info("loadEdges: IsIsLink loaded");
        } catch (OnmsTopologyException e) {
            LOG.info("loadEdges: {}", e.getMessage() );
        } catch (Exception e){
            LOG.error("Exception getting IsIs link: "+e.getMessage(),e);
        } finally {
            context.stop();
        }

        context = m_loadBridgeLinksTimer.time();
        try{
            loadTopology(ProtocolSupported.BRIDGE);
            LOG.info("loadEdges: BridgeLink loaded");
        } catch (OnmsTopologyException e) {
            LOG.info("loadEdges: {}", e.getMessage() );
        } catch (Exception e){
            LOG.error("Loading BridgeLink failed: {}",e.getMessage(),e);
        } finally {
            context.stop();
        }
    }

    private void loadTopology(ProtocolSupported protocol) throws OnmsTopologyException {
        OnmsTopology topology =   m_onmsTopologyDao.getTopology(protocol.name());
        
        final Map<String, LinkdVertex> vmap = new HashMap<>();
        topology.getVertices().stream().forEach(tvertex -> {
            LinkdVertex vertex = (LinkdVertex) getVertex(TOPOLOGY_NAMESPACE_LINKD, tvertex.getId());
            if (vertex == null) {
                vertex = LinkdVertex.create(tvertex);
                addVertices(vertex);
            } 
            vertex.getProtocolSupported().add(protocol);
            vmap.put(vertex.getId(), vertex);
        });
        
        topology.getEdges().stream().forEach(tedge -> addEdges(
                                       LinkdEdge.create(
                                                tedge.getId(), 
                                                LinkdPort.create(tedge.getSource(), vmap.get(tedge.getSource().getVertex().getId())),
                                                LinkdPort.create(tedge.getTarget(), vmap.get(tedge.getTarget().getVertex().getId())),
                                                protocol)
                                       )
                                             );
    }
        
    @Override
    public Defaults getDefaults() {
        return new Defaults()
                .withSemanticZoomLevel(Defaults.DEFAULT_SEMANTIC_ZOOM_LEVEL)
                .withPreferredLayout("D3 Layout") // D3 Layout
                .withCriteria(() -> {
                    OnmsTopologyVertex node;
                    try {
                        node = m_onmsTopologyDao.getTopology(ProtocolSupported.NODES.name()).getDefaultVertex();
                    } catch (OnmsTopologyException e) {
                        return Lists.newArrayList();
                    }

                    if (node != null) {
                        final Vertex defaultVertex = getVertex(TOPOLOGY_NAMESPACE_LINKD, node.getId());
                        if (defaultVertex != null) {
                            return Lists.newArrayList(LinkdHopCriteria.createCriteria(node.getId(), node.getLabel()));
                        }
                    }
                    return Lists.newArrayList();
                });
    }
    
    private void doRefresh() {        
        Timer.Context vcontext = m_loadVerticesTimer.time();
        try {
            for (OnmsTopologyVertex tvertex : m_onmsTopologyDao.getTopology(ProtocolSupported.NODES.name()).getVertices()) {
                addVertices(LinkdVertex.create(tvertex));
            }
            LOG.info("refresh: Loaded Vertices");
        } catch (Exception e){
            LOG.error("Exception Loading Vertices: {}",e.getMessage(),e);
        } finally {
            vcontext.stop();
        }
        
        vcontext = m_loadEdgesTimer.time();
        try {
            loadEdges();
            LOG.info("refresh: Loaded Edges");
        } catch (Exception e){
            LOG.error("Exception Loading Edges: {}",e.getMessage(),e);
        } finally {
            vcontext.stop();
        }
    }

    @Override
    public void refresh() {
        final Timer.Context context = m_loadFullTimer.time();
        try {
            resetContainer();
            doRefresh();
        } finally {
            context.stop();
        }
        
        LOG.info("refresh: Found {} groups", getGroups().size());
        LOG.info("refresh: Found {} vertices", getVerticesWithoutGroups().size());
        LOG.info("refresh: Found {} edges", getEdges().size());
    }
    public OnmsTopologyDao getOnmsTopologyDao() {
        return m_onmsTopologyDao;
    }

    public void setOnmsTopologyDao(OnmsTopologyDao onmsTopologyDao) {
        m_onmsTopologyDao = onmsTopologyDao;
    }
}