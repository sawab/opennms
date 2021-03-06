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

package org.opennms.netmgt.vulnscand;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Category;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.opennms.core.concurrent.RunnableConsumerThreadPool;
import org.opennms.core.utils.DBUtils;
import org.opennms.core.utils.ThreadCategory;
import org.opennms.netmgt.config.DataSourceFactory;
import org.opennms.netmgt.config.VulnscandConfigFactory;
import org.opennms.netmgt.config.vulnscand.ScanLevel;
import org.opennms.netmgt.config.vulnscand.VulnscandConfiguration;
import org.opennms.netmgt.daemon.AbstractServiceDaemon;

/**
 * <P>
 * Vulnerability scanning daemon. This process is used to provide continual
 * scans of target interfaces that identify possible security holes. The
 * vulnerability scanner that this version uses to identify security flaws is
 * Nessus 1.1.X (www.nessus.org).
 * </P>
 *
 * <P>
 * This code is adapted from the capsd code because its behavior is quite
 * similar; it continually scans the target ranges, enters the scan results into
 * a database table, and also supports rescans whose threads are pulled from a
 * separate thread pool so that they occur immediately.
 * </P>
 *
 * @author <A HREF="mailto:seth@opennms.org">Seth Leger </A>
 * @author <A HREF="http://www.opennms.org/">OpenNMS </A>
 * @author <A HREF="mailto:seth@opennms.org">Seth Leger </A>
 * @author <A HREF="http://www.opennms.org/">OpenNMS </A>
 * @version $Id: $
 */
public class Vulnscand extends AbstractServiceDaemon {
    /**
     * The log4j category used to log messages.
     */
    private static final String LOG4J_CATEGORY = "OpenNMS.Vulnscand";

    /**
     * Singleton instance of the Vulnscand class
     */
    private static final Vulnscand m_singleton = new Vulnscand();

    /**
     * Database synchronization lock for synchronizing write access to the
     * database between the SpecificScanProcessor and RescanProcessor thread
     * pools
     */
    private static Object m_dbSyncLock = new Object();

    /**
     * <P>
     * Contains dotted-decimal representation of the IP address where Vulnscand
     * is running. Used when vulnscand sends events out
     * </P>
     */
    private static String m_address = null;

    /**
     * Rescan scheduler thread
     */
    private Scheduler m_scheduler;

    /**
     * Event receiver.
     */
    private BroadcastEventProcessor m_receiver;

    /**
     * The pool of threads that are used to executed the SpecificScanProcessor
     * instances queued by the event processor (BroadcastEventProcessor).
     */
    private RunnableConsumerThreadPool m_specificScanRunner;

    /**
     * The pool of threads that are used to executed RescanProcessor instances
     * queued by the rescan scheduler thread.
     */
    private RunnableConsumerThreadPool m_scheduledScanRunner;

	/**
	 * SQL used to retrieve the last poll time for all the managed interfaces
	 * belonging to a particular node.
	 */
	static final String SQL_GET_LAST_POLL_TIME = "SELECT lastAttemptTime FROM vulnerabilities WHERE ipaddr=? ORDER BY lastAttemptTime DESC";

	/**
	 * The SQL statement used to retrieve all non-deleted/non-forced unamanaged
	 * IP interfaces from the 'ipInterface' table.
	 */
	static final String SQL_DB_RETRIEVE_IP_INTERFACE = "SELECT ipaddr FROM ipinterface WHERE ipaddr!='0.0.0.0' AND isManaged!='D' AND isManaged!='F'";

    /**
     * <P>
     * Static initialization
     * </P>
     */
    static {
        try {
            m_address = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException uhE) {
            m_address = "localhost";
            ThreadCategory.getInstance(LOG4J_CATEGORY).warn("Could not lookup the host name for the local host machine, address set to \"localhost\"", uhE);
        }
    } // end static class initialization

    /**
     * Constructs the Vulnscand objec
     */
    public Vulnscand() {
    	super("OpenNMS.Vulnscand");
        m_scheduler = null;
    }

    /**
     * <p>onStop</p>
     */
    protected void onStop() {
		// Stop the broadcast event receiver
        //
        m_receiver.close();

        // Stop the Suspect Event Processor thread pool
        //
        m_specificScanRunner.stop();

        // Stop the Rescan Processor thread pool
        //
        m_scheduledScanRunner.stop();
	}

    /**
     * <p>onStart</p>
     */
    protected void onStart() {
		// Initialize the Vulnscand configuration factory.
        //
        try {
            VulnscandConfigFactory.reload();
        } catch (MarshalException ex) {
            log().error("Failed to load Vulnscand configuration", ex);
            throw new UndeclaredThrowableException(ex);
        } catch (ValidationException ex) {
            log().error("Failed to load Vulnscand configuration", ex);
            throw new UndeclaredThrowableException(ex);
        } catch (IOException ex) {
            log().error("Failed to load Vulnscand configuration", ex);
            throw new UndeclaredThrowableException(ex);
        }

        // Initialize the Database configuration factory
        //
        try {
            DataSourceFactory.init();
        } catch (IOException ie) {
            log().fatal("IOException loading database config", ie);
            throw new UndeclaredThrowableException(ie);
        } catch (MarshalException me) {
            log().fatal("Marshall Exception loading database config", me);
            throw new UndeclaredThrowableException(me);
        } catch (ValidationException ve) {
            log().fatal("Validation Exception loading database config", ve);
            throw new UndeclaredThrowableException(ve);
        } catch (ClassNotFoundException ce) {
            log().fatal("Class lookup failure loading database config", ce);
            throw new UndeclaredThrowableException(ce);
        } catch (PropertyVetoException e) {
            log().fatal("Property Veto Exception loading database config", e);
            throw new UndeclaredThrowableException(e);
        } catch (SQLException e) {
            log().fatal("SQL Exception loading database config", e);
            throw new UndeclaredThrowableException(e);
        }
        

        // Create the specific and scheduled scan pools
        //
        m_specificScanRunner = new RunnableConsumerThreadPool("Vulnscand Scan Pool", 0.6f, 1.0f, VulnscandConfigFactory.getInstance().getMaxSuspectThreadPoolSize());

        m_scheduledScanRunner = new RunnableConsumerThreadPool("Vulnscand Rescan Pool", 0.6f, 1.0f, VulnscandConfigFactory.getInstance().getMaxRescanThreadPoolSize());

        // Start the suspect event and rescan thread pools
        //
        if (log().isDebugEnabled())
            log().debug("start: Starting runnable thread pools...");

        m_specificScanRunner.start();
        m_scheduledScanRunner.start();

        // Create and start the rescan scheduler
        //
        if (log().isDebugEnabled())
            log().debug("start: Creating rescan scheduler");
        try {
            // During instantiation, the scheduler will load the
            // list of known nodes from the database
            m_scheduler = new Scheduler(m_scheduledScanRunner.getRunQueue());
            initialize();
        } catch (SQLException sqlE) {
            log().error("Failed to initialize the rescan scheduler.", sqlE);
            throw new UndeclaredThrowableException(sqlE);
        } catch (Throwable t) {
            log().error("Failed to initialize the rescan scheduler.", t);
            //throw new UndeclaredThrowableException(t);
        }
    
        
        m_scheduler.start();

        // Create an event receiver.
        //
        try {
            if (log().isDebugEnabled())
                log().debug("start: Creating event broadcast event receiver");

            m_receiver = new BroadcastEventProcessor(m_specificScanRunner.getRunQueue(), m_scheduler);
        } catch (Throwable t) {
            log().error("Failed to initialized the broadcast event receiver", t);
            throw new UndeclaredThrowableException(t);
        }
	}

    /**
     * Used to retrieve the local host name/address. The name/address of the
     * machine on which Vulnscand is running.
     *
     * @return a {@link java.lang.String} object.
     */
    public static String getLocalHostAddress() {
        return m_address;
    }

    /**
     * <p>getInstance</p>
     *
     * @return a {@link org.opennms.netmgt.vulnscand.Vulnscand} object.
     */
    public static Vulnscand getInstance() {
        return m_singleton;
    }

    static Object getDbSyncLock() {
        return m_dbSyncLock;
    }

    /**
     * <p>onInit</p>
     */
    protected void onInit() {
    }


    void setInitialScheduleSleep() {
		//
	    m_scheduler.setInitialSleep(VulnscandConfigFactory.getInstance().getInitialSleepTime());
		if (log().isDebugEnabled())
	        log().debug("Scheduler: initial rescan sleep time(millis): " + m_scheduler.getInitialSleep());
	}

	/**
	 * Creates a NessusScanConfiguration object representing the specified node
	 * and adds it to the known node list for scheduling.
	 * 
	 * @param address
	 *            the internet address.
	 * @param scanLevel
	 *            the scan level.
	 * @param scheduler TODO
	 * @throws SQLException
	 *             if there is any problem accessing the database
	 */
	void addToKnownAddresses(InetAddress address, int scanLevel) throws SQLException {
	    // Retrieve last poll time for the node from the ipInterface
	    // table.
		
	    Connection db = null;
        final DBUtils d = new DBUtils(getClass());
	    try {
	        db = DataSourceFactory.getInstance().getConnection();
	        d.watch(db);
	        PreparedStatement ifStmt = db.prepareStatement(Vulnscand.SQL_GET_LAST_POLL_TIME);
	        d.watch(ifStmt);
	        ifStmt.setString(1, address.getHostAddress());
	        ResultSet rset = ifStmt.executeQuery();
	        d.watch(rset);
	        Category log = log();
			if (rset.next()) {
	            Timestamp lastPolled = rset.getTimestamp(1);
	            if (lastPolled != null && rset.wasNull() == false) {
	                if (log.isDebugEnabled())
	                    log.debug("scheduleAddress: adding node " + address + " with last poll time " + lastPolled);
						//try {
	                    m_scheduler.schedule(address, new NessusScanConfiguration(address, scanLevel, lastPolled, getInterval()));
	                //} catch (UnknownHostException ex) {
	                  //  log.error("Could not add invalid address to schedule: " + address, ex);
	                //}
	            }
	        } else {
	            if (log.isDebugEnabled())
	                log.debug("scheduleAddress: adding ipAddr " + address + " with no previous poll");
				m_scheduler.schedule(address, new NessusScanConfiguration(address, scanLevel, new Timestamp(0), getInterval()));
	        }
	    } finally {
	        d.cleanUp();
	    }
	}

	private long getInterval() {
		return VulnscandConfigFactory.getInstance().getRescanFrequency();
	}

	Set getAllManagedInterfaces() {
		Set retval = new TreeSet();
		String addressString = null;

		Connection connection = null;
		Statement selectInterfaces = null;
		ResultSet interfaces = null;
		try {
			connection = DataSourceFactory.getInstance().getConnection();
			selectInterfaces = connection.createStatement();
			interfaces = selectInterfaces.executeQuery(Vulnscand.SQL_DB_RETRIEVE_IP_INTERFACE);

			int i = 0;
			while (interfaces.next()) {
				addressString = interfaces.getString(1);
				if (addressString != null) {
					//addressString = addressString.replaceAll("/", "");
					//addressString = addressString + "/" + addressString;
					retval.add(addressString);
					m_scheduler.log().debug("JOHAN: " + addressString);
				} else {
					m_scheduler.log().warn("UNEXPECTED CONDITION: NULL string in the results of the query for managed interfaces from the ipinterface table.");
				}

				i++;
			}
			m_scheduler.log().info("Loaded " + i + " managed interfaces from the database.");
		} catch (SQLException ex) {
			m_scheduler.log().error(ex.getLocalizedMessage(), ex);
		} finally {
			try {
				if (interfaces != null)
					interfaces.close();
				if (selectInterfaces != null)
					selectInterfaces.close();
			} catch (Exception ex) {
			} finally {
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
				}
			}
		}
		return retval;
	}

	void scheduleExistingInterfaces() throws SQLException {
		// Load the list of IP addresses from the config file and schedule
		// them in the appropriate level

		VulnscandConfigFactory configFactory = VulnscandConfigFactory.getInstance();
		VulnscandConfiguration config = VulnscandConfigFactory.getConfiguration();

		// If the status of the daemon is "true" (meaning "on")...
		if (config.getStatus()) {
			Enumeration scanLevels = config.enumerateScanLevel();

			while (scanLevels.hasMoreElements()) {
				ScanLevel scanLevel = (ScanLevel) scanLevels.nextElement();
				int level = scanLevel.getLevel();

				// Grab the list of included addresses for this level
				//Set levelAddresses = configFactory.getAllIpAddresses(scanLevel);
				Set levelAddresses = new TreeSet ();

				// If scanning of the managed IPs is enabled...
				if (configFactory.getManagedInterfacesStatus()) {
					// And the managed IPs are set to be scanned at the current
					// level...
					if (configFactory.getManagedInterfacesScanLevel() == level) {
						// Then schedule those puppies to be scanned
						levelAddresses.addAll(getAllManagedInterfaces());
						log().info("Scheduled the managed interfaces at scan level " + level + ".");
					}
				}

				// Remove all of the excluded addresses (the excluded
				// addresses are cached, so this operation is lighter
				// than constructing the exclusion list each time)
				// JOHAN - THINK....
				levelAddresses.removeAll(configFactory.getAllExcludes());

				log().info("Adding " + levelAddresses.size() + " addresses to the vulnerability scan scheduler.");

				Iterator itr = levelAddresses.iterator();
				while (itr.hasNext()) {
					Object next = itr.next();
					String nextAddress = null;
					if (next instanceof String) {
						nextAddress = (String) next;
						// REMOVE SLASHES.... - 
						//nextAddress = nextAddress.replaceAll("/", "");
						//nextAddress = nextAddress + "/" + nextAddress;
						log().debug("JOHAN LevelAddresses : " + nextAddress);
					}
					try {
						// All we know right now is the IP.....
						InetAddress frump = InetAddress.getByName(nextAddress);
						addToKnownAddresses(frump, level);
					} catch (UnknownHostException ex) {
						log().error("Could not add invalid address to schedule: " + nextAddress, ex);
					}
				}
			}
		} else {
			log().info("Vulnerability scanning is DISABLED.");
		}
	}

	/**
	 * <p>initialize</p>
	 *
	 * @throws java.sql.SQLException if any.
	 */
	public void initialize() throws SQLException {
		// Get rescan interval from configuration factory
	    setInitialScheduleSleep();
	
	    scheduleExistingInterfaces();
	}
} // end Vulnscand class
