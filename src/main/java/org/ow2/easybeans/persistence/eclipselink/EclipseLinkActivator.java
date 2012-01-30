/**
 * EasyBeans
 * Copyright (C) 2010-2012 Bull S.A.S.
 * Contact: easybeans@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * --------------------------------------------------------------------------
 * $Id: EclipseLinkActivator.java 5371 2010-02-24 15:02:00Z benoitf $
 * --------------------------------------------------------------------------
 */

package org.ow2.easybeans.persistence.eclipselink;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.persistence.jpa.PersistenceProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Activator that is registering the EclipseLink JPA Persistence provider.
 * @author Florent Benoit
 */
public class EclipseLinkActivator implements BundleActivator {

    /**
     * The bundle context.
     */
    private BundleContext bundleContext = null;

    /**
     * Called when this bundle is started so the Framework can perform the
     * bundle-specific activities necessary to start this bundle. This method
     * can be used to register services or to allocate any resources that this
     * bundle needs.
     * <p>
     * This method must complete and return to its caller in a timely manner.
     * @param context The execution context of the bundle being started.
     * @throws Exception If this method throws an exception, this bundle is
     *         marked as stopped and the Framework will remove this bundle's
     *         listeners, unregister all services registered by this bundle, and
     *         release all services used by this bundle.
     */
    public void start(final BundleContext context) throws Exception {
        this.bundleContext = context;

        Dictionary<String, String> props = new Hashtable<String, String>();
        props.put("javax.persistence.provider", PersistenceProvider.class.getName());
        context.registerService(javax.persistence.spi.PersistenceProvider.class.getName(), PersistenceProvider.class.newInstance(), props);

    }

    /**
     * Called when this bundle is stopped so the Framework can perform the
     * bundle-specific activities necessary to stop the bundle. In general, this
     * method should undo the work that the <code>BundleActivator.start</code>
     * method started. There should be no active threads that were started by
     * this bundle when this bundle returns. A stopped bundle must not call any
     * Framework objects.
     * <p>
     * This method must complete and return to its caller in a timely manner.
     * @param context The execution context of the bundle being stopped.
     * @throws Exception If this method throws an exception, the bundle is still
     *         marked as stopped, and the Framework will remove the bundle's
     *         listeners, unregister all services registered by the bundle, and
     *         release all services used by the bundle.
     */
    public void stop(final BundleContext context) throws Exception {
        // The service is automatically unregistered.
    }

}
