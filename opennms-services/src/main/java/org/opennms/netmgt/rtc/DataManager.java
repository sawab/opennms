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
// Modifications:
//
// 2006 Aug 22: Use generics for Collections, call init() on factory, not
//              reload. - dj@opennms.org
// 2004 Oct 07: Added code to support RTC rescan on asset update
// 2004 Sep 08: Cleaned up the rescan node method.
// 2004 Mar 17: Fixed a number of bugs with added and deleting services within RTC.
//              Added a method to rescan a node within RTC.
// 2003 Jan 31: Cleaned up some unused imports.
// 2002 Oct 24: Replaced references to HashTable with HashMap.
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
// Tab Size = 8
//

package org.opennms.netmgt.rtc;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.log4j.Category;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.opennms.core.utils.ThreadCategory;
import org.opennms.netmgt.EventConstants;
import org.opennms.netmgt.config.CatFactory;
import org.opennms.netmgt.config.CategoryFactory;
import org.opennms.netmgt.config.DataSourceFactory;
import org.opennms.netmgt.config.categories.Categories;
import org.opennms.netmgt.config.categories.Categorygroup;
import org.opennms.netmgt.filter.FilterDaoFactory;
import org.opennms.netmgt.filter.FilterParseException;
import org.opennms.netmgt.rtc.datablock.RTCCategory;
import org.opennms.netmgt.rtc.datablock.RTCHashMap;
import org.opennms.netmgt.rtc.datablock.RTCNode;
import org.opennms.netmgt.rtc.datablock.RTCNodeKey;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.xml.sax.SAXException;

/**
 * Contains and maintains all the data for the RTC.
 *
 * The basic datablock is a 'RTCNode' that gets added to relevant
 * 'RTCCategory's. it also gets added to a map with different keys for easy
 * lookup
 *
 * The map('RTCHashMap') is keyed with 'RTCNodeKey's(a nodeid/ip/svc
 * combination), nodeid/ip combinations and nodeid and these keys either lookup
 * a single RTCNode or lists of 'RTCNode's
 *
 * Incoming events have a method in the DataManager to alter data - for e.g., a
 * 'nodeGainedService' event would result in the 'nodeGainedService()' method
 * being called by the DataUpdater(s).
 *
 * @author <A HREF="mailto:sowmya@opennms.org">Sowmya Nataraj </A>
 * @author <A HREF="http://www.opennms.org">OpenNMS.org </A>
 * @author <A HREF="mailto:sowmya@opennms.org">Sowmya Nataraj </A>
 * @author <A HREF="http://www.opennms.org">OpenNMS.org </A>
 * @version $Id: $
 */
public class DataManager extends Object {
    private class RTCNodeProcessor implements RowCallbackHandler {
		RTCNodeKey m_currentKey = null;

		Map m_categoryIpLists = new HashMap();

		public void processRow(ResultSet rs) throws SQLException {
			RTCNodeKey key = new RTCNodeKey(rs.getLong("nodeid"), rs.getString("ipaddr"), rs.getString("servicename"));
			processKey(key);
			processOutage(key, rs.getTimestamp("ifLostService"), rs.getTimestamp("ifRegainedService"));
		}

		private void processKey(RTCNodeKey key) {
			if (!matchesCurrent(key)) {
				m_currentKey = key;
				processIfService(key);
			}
		}

		private boolean matchesCurrent(RTCNodeKey key) {
			return (m_currentKey != null && m_currentKey.equals(key));
		}

		// This is called exactly once for each unique (nodeid, ipaddr, svcname) tuple
		public void processIfService(RTCNodeKey key) {
			for (Iterator it = m_categories.values().iterator(); it.hasNext();) {
				RTCCategory cat = (RTCCategory) it.next();
				if (catContainsIfService(cat, key)) {
					RTCNode rtcN = getRTCNode(key);
					addNodeToCategory(cat, rtcN);
				}
			}
		
		}

		private RTCNode getRTCNode(RTCNodeKey key) {
			RTCNode rtcN = m_map.getRTCNode(key);
			if (rtcN == null) {
				rtcN = new RTCNode(key);
				addRTCNode(rtcN);
			}
			return rtcN;
		}

		private boolean catContainsIfService(RTCCategory cat, RTCNodeKey key) {
			return cat.containsService(key.getSvcName()) && catContainsIp(cat, key.getIP());
		}

		private boolean catContainsIp(RTCCategory cat, String ip) {
			Set ips = catGetIpAddrs(cat);
			return ips.contains(ip);
		}

		private Set catGetIpAddrs(RTCCategory cat) {
			Set ips = (Set)m_categoryIpLists.get(cat.getLabel());
			if (ips == null) {
				ips = catConstructIpAddrs(cat);
				m_categoryIpLists.put(cat.getLabel(), ips);
			}
			return ips;
		}

		private Set catConstructIpAddrs(RTCCategory cat) {
			String filterRule = cat.getEffectiveRule();
			try {
				if (log().isDebugEnabled())
					log().debug("Category: " + cat.getLabel() + "\t" + filterRule);
		
				List ips = FilterDaoFactory.getInstance().getIPList(filterRule);
				
		        if (log().isDebugEnabled())
		            log().debug("Number of IPs satisfying rule: " + ips.size());
		
		        return new HashSet(ips);
		        
			} catch (FilterParseException e) {
				log().error("Unable to parse filter rule "+filterRule+" ignoring category "+cat.getLabel(), e);
				return Collections.EMPTY_SET;
			}
		}

		// This is processed for each outage, passing two null means there is not outage
		public void processOutage(RTCNodeKey key, Timestamp ifLostService, Timestamp ifRegainedService) {
			RTCNode rtcN = m_map.getRTCNode(key);
			// if we can't find the node it doesn't belong to any category
			if (rtcN == null) return;
			
			addOutageToRTCNode(rtcN, ifLostService, ifRegainedService);
			
		}
	}

	/**
     * The RTC categories
     */
    private Map<String, RTCCategory> m_categories;

    /**
     * map keyed using the RTCNodeKey or nodeid or nodeid/ip
     */
    private RTCHashMap m_map;

    /**
     * Get the 'ismanaged' status for the nodeid, ipaddr combination
     * 
     * @param nodeid
     *            the nodeid of the interface
     * @param ip
     *            the interface for which the status is required
     * @param svc
     *            the service for which status is required
     * 
     * @return the 'status' from the ifservices table
     */
    private char getServiceStatus(long nodeid, String ip, String svc) {
    	
    	JdbcTemplate template = new JdbcTemplate(getConnectionFactory());
    	String status= (String)template.queryForObject(RTCConstants.DB_GET_SERVICE_STATUS, new Object[] { new Long(nodeid), ip, svc }, String.class);

    	if (status == null) return '\0';
    	return status.charAt(0);
    	
    }

	private void addOutageToRTCNode(RTCNode rtcN, Timestamp lostTimeTS, Timestamp regainedTimeTS) {
		if (lostTimeTS == null) return;
		long lostTime = lostTimeTS.getTime();
		long regainedTime = -1;
		if (regainedTimeTS != null)
			regainedTime = regainedTimeTS.getTime();

		if (log().isDebugEnabled()) {
			log().debug("lost time for nodeid/ip/svc: " + rtcN.getNodeID() + "/" + rtcN.getIP() + "/" + rtcN.getSvcName() + ": " + lostTimeTS + "/" + lostTime);

			log().debug("regained time for nodeid/ip/svc: " + rtcN.getNodeID() + "/" + rtcN.getIP() + "/" + rtcN.getSvcName() + ": " + regainedTimeTS + "/" + regainedTime);
		}

		rtcN.addSvcTime(lostTime, regainedTime);
	}

	private void addRTCNode(RTCNode rtcN) {
		m_map.add(rtcN);
	}

	private void addNodeToCategory(RTCCategory cat, RTCNode rtcN) {

		// add the category info to the node
        rtcN.addCategory(cat.getLabel());

		// Add node to category
		cat.addNode(rtcN);

		if (log().isDebugEnabled())
		    log().debug("rtcN : " + rtcN.getNodeID() + "/" + rtcN.getIP() + "/" + rtcN.getSvcName() + " added to cat: " + cat.getLabel());
	}

    /**
     * Creates the categories map. Reads the categories from the categories.xml
     * and creates the 'RTCCategory's map
     */
    private void createCategoriesMap() {
        CatFactory cFactory = null;
        try {
            CategoryFactory.init();
            cFactory = CategoryFactory.getInstance();

        } catch (IOException ex) {
            log().error("Failed to load categories information", ex);
            throw new UndeclaredThrowableException(ex);
        } catch (MarshalException ex) {
            log().error("Failed to load categories information", ex);
            throw new UndeclaredThrowableException(ex);
        } catch (ValidationException ex) {
            log().error("Failed to load categories information", ex);
            throw new UndeclaredThrowableException(ex);
        }

        m_categories = new HashMap<String, RTCCategory>();

        Enumeration enumCG = cFactory.getConfig().enumerateCategorygroup();
        while (enumCG.hasMoreElements()) {
            Categorygroup cg = (Categorygroup) enumCG.nextElement();

            String commonRule = cg.getCommon().getRule();

            Categories cats = cg.getCategories();

            Enumeration enumCat = cats.enumerateCategory();
            while (enumCat.hasMoreElements()) {
                org.opennms.netmgt.config.categories.Category cat = (org.opennms.netmgt.config.categories.Category) enumCat.nextElement();

                RTCCategory rtcCat = new RTCCategory(cat, commonRule);
                m_categories.put(rtcCat.getLabel(), rtcCat);
            }
        }
    }

    /**
     * Poplulates nodes from the database. For each category in the categories
     * list, this reads the services and outage tables to get the initial data,
     * creates 'RTCNode' objects that are added to the map and and to the
     * appropriate category.
     * @param dbConn
     *            the database connection.
     * 
     * @throws SQLException
     *             if the database read fails due to an SQL error
     * @throws FilterParseException
     *             if filtering the data against the category rule fails due to
     *             the rule being incorrect
     * @throws RTCException
     *             if the database read or filtering the data against the
     *             category rule fails for some reason
     */
    private void populateNodesFromDB(String query, Object[] args) throws SQLException, FilterParseException, RTCException {

    	final String getOutagesInWindow = 
    			"select " + 
    			"       ifsvc.nodeid as nodeid, " + 
    			"       ifsvc.ipAddr as ipaddr, " + 
    			"       s.servicename as servicename, " + 
    			"       o.ifLostService as ifLostService, " + 
    			"       o.ifRegainedService as ifRegainedService " + 
    			"  from " + 
    			"       ifservices ifsvc " + 
    			"  join " + 
    			"       service s on (ifsvc.serviceid = s.serviceid) " + 
    			"left outer  join " + 
    			"       outages o on " +
    			"          (" + 
    			"            o.nodeid = ifsvc.nodeid " + 
    			"            and o.ipaddr = ifsvc.ipaddr " + 
    			"            and o.serviceid = ifsvc.serviceid " + 
    			"            and " +
    			"            (" + 
    			"               o.ifLostService > ? " + 
    			"               OR  o.ifRegainedService > ? " + 
    			"               OR  o.ifRegainedService is null " +
    			"            )" +
    			"          ) " +
    			" where ifsvc.status='A' " +
                (query == null ? "" : "and "+query) +
    			" order by " + 
    			"       ifsvc.nodeid, ifsvc.ipAddr, ifsvc.serviceid, o.ifLostService ";
    	
		long window = (new Date()).getTime() - RTCManager.getRollingWindow();
		Timestamp windowTS = new Timestamp(window);

    	RowCallbackHandler rowHandler = new RTCNodeProcessor();

    	Object[] sqlArgs = createArgs(windowTS, windowTS, args);
    	
    	JdbcTemplate template = new JdbcTemplate(getConnectionFactory());
    	template.query(getOutagesInWindow, sqlArgs, rowHandler);
    	
    }

	private Object[] createArgs(Object arg1, Object arg2, Object[] remaining) {
		LinkedList args = new LinkedList();
		args.add(arg1);
		args.add(arg2);
		if (remaining != null)
			args.addAll(Arrays.asList(remaining));
		return args.toArray();
	}

	private Category log() {
		return ThreadCategory.getInstance(DataManager.class);
	}

    /**
     * Constructor. Parses categories from the categories.xml and populates them
     * with 'RTCNode' objects created from data read from the database (services
     * and outage tables)
     *
     * @exception SQLException
     *                if there is an error reading initial data from the
     *                database
     * @exception FilterParseException
     *                if a rule in the categories.xml was incorrect
     * @exception RTCException
     *                if the initialization/data reading does not go through
     * @throws org.xml.sax.SAXException if any.
     * @throws java.io.IOException if any.
     * @throws java.sql.SQLException if any.
     * @throws org.opennms.netmgt.filter.FilterParseException if any.
     * @throws org.opennms.netmgt.rtc.RTCException if any.
     */
    public DataManager() throws SAXException, IOException, SQLException, FilterParseException, RTCException {
			
    	// read the categories.xml to get all the categories
    	createCategoriesMap();

    	if (m_categories == null || m_categories.isEmpty()) {
    		throw new RTCException("No categories found in categories.xml");
    	}

    	if (log().isDebugEnabled())
    		log().debug("Number of categories read: " + m_categories.size());

    	// create data holder
    	m_map = new RTCHashMap(30000);

    	// Populate the nodes initially from the database
    	populateNodesFromDB(null, null);

    }

	private DataSource getConnectionFactory() {
		DataSource connFactory;
		try {
		    DataSourceFactory.init();
		    connFactory = DataSourceFactory.getInstance();
		} catch (IOException ex) {
		    log().warn("Failed to load database config", ex);
		    throw new UndeclaredThrowableException(ex);
		} catch (MarshalException ex) {
		    log().warn("Failed to unmarshall database config", ex);
		    throw new UndeclaredThrowableException(ex);
		} catch (ValidationException ex) {
		    log().warn("Failed to unmarshall database config", ex);
		    throw new UndeclaredThrowableException(ex);
        } catch (ClassNotFoundException ex) {
            log().warn("Failed to get database connection", ex);
            throw new UndeclaredThrowableException(ex);
        } catch (SQLException ex) {
            log().warn("Failed to get database connection", ex);
            throw new UndeclaredThrowableException(ex);
        } catch (PropertyVetoException ex) {
            log().warn("Failed to get database connection", ex);
            throw new UndeclaredThrowableException(ex);
        }
		return connFactory;
	}

    /**
     * Handles a node gained service event. Add a new entry to the map and the
     * categories on a 'serviceGained' event
     *
     * @param nodeid
     *            the node id
     * @param ip
     *            the IP address
     * @param svcName
     *            the service name
     */
    public synchronized void nodeGainedService(long nodeid, String ip, String svcName) {
        //
        // check the 'status' flag for the service
        //
        char svcStatus = getServiceStatus(nodeid, ip, svcName);

        //
        // Include only service status 'A' and where service is not SNMP
        //
        if (svcStatus != 'A') {
            if (log().isInfoEnabled())
                log().info("nodeGainedSvc: " + nodeid + "/" + ip + "/" + svcName + " IGNORED because status is not active: " + svcStatus);
        } else {
            if (log().isDebugEnabled())
                log().debug("nodeGainedSvc: " + nodeid + "/" + ip + "/" + svcName + "/" + svcStatus);

            // I ran into problems with adding new services, so I just ripped
            // all that out and added
            // a call to the rescan method. -T

            // Hrm - since the rules can be based on things other than the
            // service name
            // we really need to rescan every time a new service is discovered.
            // For
            // example, if I have a category where the rule is "ipaddr =
            // 10.1.1.1 & isHTTP"
            // yet I only have ICMP in the service list, the node will not be
            // added when
            // HTTP is discovered, because it is not in the services list.
            // 
            // This is mainly useful when SNMP is discovered on a node.

            if (log().isDebugEnabled()) {
                log().debug("rtcN : Rescanning services on : " + ip);
            }
            try {
                rtcNodeRescan(nodeid);
            } catch (FilterParseException ex) {
                log().warn("Failed to unmarshall database config", ex);
                throw new UndeclaredThrowableException(ex);
            } catch (SQLException ex) {
                log().warn("Failed to get database connection", ex);
                throw new UndeclaredThrowableException(ex);
            } catch (RTCException ex) {
                log().warn("Failed to get database connection", ex);
                throw new UndeclaredThrowableException(ex);
            }

        }

    }

    /**
     * Handles a node lost service event. Add a lost service entry to the right
     * node
     *
     * @param nodeid
     *            the node id
     * @param ip
     *            the IP address
     * @param svcName
     *            the service name
     * @param t
     *            the time at which service was lost
     */
    public synchronized void nodeLostService(long nodeid, String ip, String svcName, long t) {
        RTCNodeKey key = new RTCNodeKey(nodeid, ip, svcName);
        RTCNode rtcN = m_map.getRTCNode(key);
        if (rtcN == null) {
            // oops! got a lost/regained service for a node that is not known?
            log().info("Received a nodeLostService event for an unknown/irrelevant node: " + key.toString());
            return;
        }

        // inform node
        rtcN.nodeLostService(t);

    }

    /**
     * Add a lost service entry to the right nodes.
     *
     * @param nodeid
     *            the node id
     * @param ip
     *            the IP address
     * @param t
     *            the time at which service was lost
     */
    public synchronized void interfaceDown(long nodeid, String ip, long t) {
        List nodesList = m_map.getRTCNodes(nodeid, ip);
        ListIterator listIter = nodesList.listIterator();
        while (listIter.hasNext()) {
            RTCNode rtcN = (RTCNode) listIter.next();

            // inform node
            rtcN.nodeLostService(t);
        }
    }

    /**
     * Add a lost service entry to the right nodes.
     *
     * @param nodeid
     *            the node id
     * @param t
     *            the time at which service was lost
     */
    public synchronized void nodeDown(long nodeid, long t) {
    	List nodesList = m_map.getRTCNodes(nodeid);
        ListIterator listIter = nodesList.listIterator();
        while (listIter.hasNext()) {
            RTCNode rtcN = (RTCNode) listIter.next();

            // inform node
            rtcN.nodeLostService(t);
        }
    }

    /**
     * Add a regained service entry to the right nodes.
     *
     * @param nodeid
     *            the node id
     * @param t
     *            the time at which service was regained
     */
    public synchronized void nodeUp(long nodeid, long t) {
    	List nodesList = m_map.getRTCNodes(nodeid);
        ListIterator listIter = nodesList.listIterator();
        while (listIter.hasNext()) {
            RTCNode rtcN = (RTCNode) listIter.next();

            // inform node
            rtcN.nodeRegainedService(t);
        }
    }

    /**
     * Add a regained service entry to the right nodes.
     *
     * @param nodeid
     *            the node id
     * @param ip
     *            the IP address
     * @param t
     *            the time at which service was regained
     */
    public synchronized void interfaceUp(long nodeid, String ip, long t) {
        List nodesList = m_map.getRTCNodes(nodeid, ip);
        ListIterator listIter = nodesList.listIterator();
        while (listIter.hasNext()) {
            RTCNode rtcN = (RTCNode) listIter.next();

            // inform node
            rtcN.nodeRegainedService(t);
        }
    }

    /**
     * Add a regained service entry to the right node.
     *
     * @param nodeid
     *            the node id
     * @param ip
     *            the IP address
     * @param svcName
     *            the service name
     * @param t
     *            the time at which service was regained
     */
    public synchronized void nodeRegainedService(long nodeid, String ip, String svcName, long t) {
        RTCNodeKey key = new RTCNodeKey(nodeid, ip, svcName);
        RTCNode rtcN = m_map.getRTCNode(key);
        if (rtcN == null) {
            // oops! got a lost/regained service for a node that is not known?
            log().info("Received a nodeRegainedService event for an unknown/irrelevant node: " + key.toString());
            return;
        }

        // inform node
        rtcN.nodeRegainedService(t);
    }

    /**
     * Remove node from the map and the categories on a 'serviceDeleted' event.
     *
     * @param nodeid
     *            the nodeid on which service was deleted
     * @param ip
     *            the ip on which service was deleted
     * @param svcName
     *            the service that was deleted
     */
    public synchronized void serviceDeleted(long nodeid, String ip, String svcName) {
        // create lookup key
        RTCNodeKey key = new RTCNodeKey(nodeid, ip, svcName);

        // lookup the node
        RTCNode rtcN = m_map.getRTCNode(key);
        if (rtcN == null) {
            log().warn("Received a " + EventConstants.SERVICE_DELETED_EVENT_UEI + " event for an unknown node: " + key.toString());

            return;
        }

        //
        // Go through from all the categories this node belongs to
        // and delete the service
        //
        List categories = rtcN.getCategories();
        ListIterator catIter = categories.listIterator();
        while (catIter.hasNext()) {
            String catlabel = (String) catIter.next();

            RTCCategory cat = (RTCCategory) m_categories.get(catlabel);

            // get nodes in this category
            List catNodes = cat.getNodes();

            // check if the category contains this node
            Long tmpNodeid = new Long(rtcN.getNodeID());
            int nIndex = catNodes.indexOf(tmpNodeid);
            if (nIndex != -1) {
                // remove from the category if it is the only service left.
                if (m_map.getServiceCount(nodeid, catlabel) == 1) {
                    catNodes.remove(nIndex);
                    log().info("Removing node from category: " + catlabel);
                }

                // let the node know that this category is out
                catIter.remove();
            }
        }

        // finally remove from map
        
        m_map.delete(rtcN);

    }
    
    /**
     * <p>assetInfoChanged</p>
     *
     * @param nodeid a long.
     */
    public synchronized void assetInfoChanged(long nodeid) {
        try {
        	rtcNodeRescan(nodeid);
        } catch (FilterParseException ex) {
            log().warn("Failed to unmarshall database config", ex);
            throw new UndeclaredThrowableException(ex);
        } catch (SQLException ex) {
            log().warn("Failed to get database connection", ex);
            throw new UndeclaredThrowableException(ex);
        } catch (RTCException ex) {
            log().warn("Failed to get database connection", ex);
            throw new UndeclaredThrowableException(ex);
        }

    	
    }
    
    /**
     * <p>nodeCategoryMembershipChanged</p>
     *
     * @param nodeid a long.
     */
    public synchronized void nodeCategoryMembershipChanged(long nodeid) {
        try {
        	rtcNodeRescan(nodeid);
        } catch (FilterParseException ex) {
            log().warn("Failed to unmarshall database config", ex);
            throw new UndeclaredThrowableException(ex);
        } catch (SQLException ex) {
            log().warn("Failed to get database connection", ex);
            throw new UndeclaredThrowableException(ex);
        } catch (RTCException ex) {
            log().warn("Failed to get database connection", ex);
            throw new UndeclaredThrowableException(ex);
        }
    }

    /**
     * Update the categories for a node. This method will update the categories
     * for all interfaces on a node.
     *
     * @param nodeid
     *            the nodeid on which SNMP service was added
     * @throws java.sql.SQLException
     *             if the database read fails due to an SQL error
     * @throws org.opennms.netmgt.filter.FilterParseException
     *             if filtering the data against the category rule fails due to
     *             the rule being incorrect
     * @throws org.opennms.netmgt.rtc.RTCException
     *             if the database read or filtering the data against the
     *             category rule fails for some reason
     */
    public synchronized void rtcNodeRescan(long nodeid) throws SQLException, FilterParseException, RTCException {
    	
    	for (Iterator it = m_categories.values().iterator(); it.hasNext();) {
			RTCCategory cat = (RTCCategory) it.next();
			cat.deleteNode(nodeid);
		}
    	
    	m_map.deleteNode(nodeid);
    	
    	populateNodesFromDB("ifsvc.nodeid = ?", new Object[] { new Long(nodeid) });
    	
    }

    /**
     * Reparent an interface. This effectively means updating the nodelist of
     * the categories and the map
     *
     * Use the ip/oldnodeid combination to get all nodes that will be affected -
     * for each of these nodes, remove the old entry and add a new one with new
     * keys to the map
     *
     * <em>Note:</em> Each of these nodes could belong to more than one
     * category. However, category rule evaluation is done based ONLY on the IP -
     * therefore changing the nodeID on the node should update the categories
     * appropriately
     *
     * @param ip
     *            the interface to reparent
     * @param oldNodeId
     *            the node that the ip belonged to earlier
     * @param newNodeId
     *            the node that the ip now belongs to
     */
    public synchronized void interfaceReparented(String ip, long oldNodeId, long newNodeId) {
        // get all RTCNodes with the ip/oldNodeId
    	List<RTCNode> nodesList = m_map.getRTCNodes(oldNodeId, ip);
        ListIterator<RTCNode> listIter = new LinkedList<RTCNode>(nodesList).listIterator();
        while (listIter.hasNext()) {
            RTCNode rtcN = listIter.next();

            // remove the node with the oldnode id from the map
            m_map.delete(rtcN);

            // change the nodeid on the RTCNode
            rtcN.setNodeID(newNodeId);

            // now add the node with the new nodeid
            m_map.add(rtcN);

            // remove old nodeid from the categories it belonged to
            // and the new nodeid
            Iterator catIter = rtcN.getCategories().listIterator();
            while (catIter.hasNext()) {
                String catlabel = (String) catIter.next();

                RTCCategory rtcCat = m_categories.get(catlabel);
                rtcCat.deleteNode(oldNodeId);
                rtcCat.addNode(newNodeId);
            }

        }
    }

    /**
     * Get the value(uptime) for the category in the last 'rollingWindow'
     * starting at current time
     *
     * @param catLabel
     *            the category to which the node should belong to
     * @param curTime
     *            the current time
     * @param rollingWindow
     *            the window for which value is to be calculated
     * @return the value(uptime) for the category in the last 'rollingWindow'
     *         starting at current time
     */
    public synchronized double getValue(String catLabel, long curTime, long rollingWindow) {
        return m_map.getValue(catLabel, curTime, rollingWindow);
    }

    /**
     * Get the value(uptime) for the nodeid in the last 'rollingWindow' starting
     * at current time in the context of the passed category
     *
     * @param nodeid
     *            the node for which value is to be calculated
     * @param catLabel
     *            the category to which the node should belong to
     * @param curTime
     *            the current time
     * @param rollingWindow
     *            the window for which value is to be calculated
     * @return the value(uptime) for the node in the last 'rollingWindow'
     *         starting at current time in the context of the passed category
     */
    public synchronized double getValue(long nodeid, String catLabel, long curTime, long rollingWindow) {
        return m_map.getValue(nodeid, catLabel, curTime, rollingWindow);
    }

    /**
     * Get the service count for the nodeid in the context of the passed
     * category
     *
     * @param nodeid
     *            the node for which service count is to be calculated
     * @param catLabel
     *            the category to which the node should belong to
     * @return the service count for the nodeid in the context of the passed
     *         category
     */
    public synchronized int getServiceCount(long nodeid, String catLabel) {
        return m_map.getServiceCount(nodeid, catLabel);
    }

    /**
     * Get the service down count for the nodeid in the context of the passed
     * category
     *
     * @param nodeid
     *            the node for which service down count is to be calculated
     * @param catLabel
     *            the category to which the node should belong to
     * @return the service down count for the nodeid in the context of the
     *         passed category
     */
    public synchronized int getServiceDownCount(long nodeid, String catLabel) {
        return m_map.getServiceDownCount(nodeid, catLabel);
    }

    /**
     * <p>getCategories</p>
     *
     * @return the categories
     */
    public synchronized Map<String, RTCCategory> getCategories() {
        return m_categories;
    }

}
