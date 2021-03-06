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
// OpenNMS Licensing       <license@opennms.org>
//     http://www.opennms.org/
//     http://www.opennms.com/
//

package org.opennms.web.svclayer.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.opennms.netmgt.config.siteStatusViews.Category;
import org.opennms.netmgt.config.siteStatusViews.RowDef;
import org.opennms.netmgt.config.siteStatusViews.Rows;
import org.opennms.netmgt.config.siteStatusViews.View;
import org.opennms.netmgt.dao.CategoryDao;
import org.opennms.netmgt.dao.NodeDao;
import org.opennms.netmgt.model.AggregateStatusDefinition;
import org.opennms.netmgt.model.AggregateStatusView;
import org.opennms.netmgt.model.OnmsCategory;
import org.opennms.netmgt.model.OnmsNode;
import org.opennms.web.svclayer.AggregateStatus;
import org.opennms.web.svclayer.dao.SiteStatusViewConfigDao;

import junit.framework.TestCase;

public class DefaultSiteStatusServiceTest extends TestCase {
    
    private NodeDao m_nodeDao;
    private CategoryDao m_categoryDao;
    private SiteStatusViewConfigDao m_siteStatusViewConfigDao;
    
    public void setUp() throws Exception {
        super.setUp();
        m_nodeDao = createMock(NodeDao.class);
        m_categoryDao = createMock(CategoryDao.class);
        m_siteStatusViewConfigDao = createMock(SiteStatusViewConfigDao.class);
    }
    
    public void testBogus() {
        // Empty test so JUnit doesn't complain about not having any tests to run
    }
    
    public void FIXMEtestCreateAggregateStatusUsingNodeId() {
        Collection<AggregateStatus> aggrStati;
        Collection<AggregateStatusDefinition> defs = new HashSet<AggregateStatusDefinition>();
        
        OnmsCategory catRouters = new OnmsCategory("routers");
        OnmsCategory catSwitches = new OnmsCategory("switches");
        
        AggregateStatusDefinition definition = 
            new AggregateStatusDefinition("Routers/Switches", new HashSet<OnmsCategory>(Arrays.asList(new OnmsCategory[]{ catRouters, catSwitches })));
        defs.add(definition);
        
        OnmsCategory catServers = new OnmsCategory("servers");
        
        definition = 
            new AggregateStatusDefinition("Servers", new HashSet<OnmsCategory>(Arrays.asList(new OnmsCategory[]{ catServers })));
        defs.add(definition);
        
        DefaultSiteStatusViewService aggregateSvc = new DefaultSiteStatusViewService();
        aggregateSvc.setNodeDao(m_nodeDao);
        aggregateSvc.setCategoryDao(m_categoryDao);
        aggregateSvc.setSiteStatusViewConfigDao(m_siteStatusViewConfigDao);
        
        OnmsNode node = new OnmsNode();
        node.setId(1);
        node.getAssetRecord().setBuilding("HQ");
        Collection<OnmsNode> nodes = new ArrayList<OnmsNode>();
        nodes.add(node);
        
        for (AggregateStatusDefinition def : defs) {
            expect(m_nodeDao.findAllByVarCharAssetColumnCategoryList("building", "HQ", def.getCategories())).andReturn(nodes);
        }
        for (OnmsNode n : nodes) {
            expect(m_nodeDao.load(n.getId())).andReturn(n);
        }
        replay(m_nodeDao);
        
        expect(m_categoryDao.findByName("switches")).andReturn(catSwitches);
        expect(m_categoryDao.findByName("routers")).andReturn(catRouters);
        expect(m_categoryDao.findByName("servers")).andReturn(catServers);
        replay(m_categoryDao);
        
        Rows rows = new Rows();
        RowDef rowDef = new RowDef();
        Category category = new Category();
        category.setName("servers");
        rowDef.addCategory(category);
        rows.addRowDef(rowDef);
        
        rows = new Rows();
        rowDef = new RowDef();
        category = new Category();
        category.setName("switches");
        rowDef.addCategory(category);
        category = new Category();
        category.setName("routers");
        rowDef.addCategory(category);
        rows.addRowDef(rowDef);

        View view = new View();
        view.setRows(rows);
        expect(m_siteStatusViewConfigDao.getView("building")).andReturn(view);
        replay(m_siteStatusViewConfigDao);
        
        aggrStati = aggregateSvc.createAggregateStatusesUsingNodeId(node.getId(), "building");
        
        verify(m_nodeDao);
        verify(m_categoryDao);
        verify(m_siteStatusViewConfigDao);
        
        assertNotNull(aggrStati);
    }
    
    public void FIXMEtestCreateAggregateStatusUsingBuilding() {
        
        Collection<AggregateStatus> aggrStati;
        Collection<AggregateStatusDefinition> defs = new HashSet<AggregateStatusDefinition>();
        
        AggregateStatusDefinition definition = 
            new AggregateStatusDefinition("Routers/Switches", new HashSet<OnmsCategory>(Arrays.asList(new OnmsCategory[]{ new OnmsCategory("routers"), new OnmsCategory("switches") })));
        defs.add(definition);
        
        definition = 
            new AggregateStatusDefinition("Servers", new HashSet<OnmsCategory>(Arrays.asList(new OnmsCategory[]{ new OnmsCategory("servers") })));
            
        defs.add(definition);
        
        DefaultSiteStatusViewService aggregateSvc = new DefaultSiteStatusViewService();
        aggregateSvc.setNodeDao(m_nodeDao);
        
        OnmsNode node = new OnmsNode();
        Collection<OnmsNode> nodes = new ArrayList<OnmsNode>();
        nodes.add(node);
        
        for (AggregateStatusDefinition def : defs) {
            expect(m_nodeDao.findAllByVarCharAssetColumnCategoryList("building", "HQ", def.getCategories())).andReturn(nodes);
        }
        replay(m_nodeDao);
        
        AggregateStatusView view = new AggregateStatusView();
        view.setName("building");
        view.setColumnValue("HQ");
        view.setTableName("assets");
        view.setStatusDefinitions(new LinkedHashSet<AggregateStatusDefinition>(defs));
        aggrStati = aggregateSvc.createAggregateStatusUsingAssetColumn(view);
        verify(m_nodeDao);
        
        assertNotNull(aggrStati);

    }

}
