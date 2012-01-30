/**
 * EasyBeans
 * Copyright (C) 2008-2012 Bull S.A.S.
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
 * $Id: EasyBeansServerPlatform.java 5369 2010-02-24 14:58:19Z benoitf $
 * --------------------------------------------------------------------------
 */

package org.ow2.easybeans.persistence.eclipselink;

import org.eclipse.persistence.platform.server.ServerPlatformBase;
import org.eclipse.persistence.sessions.DatabaseSession;

/**
 * This class allows EclipseLink to get the TransactionManager.<br/>
 * This is the class used as integration class.
 * @author Florent Benoit
 */
public class EasyBeansServerPlatform extends ServerPlatformBase {

    /**
     * Name of the server.
     */
    private static final String EASYBEANS_NAME = "EasyBeans";

    /**
     * Default constructor.
     * @param databaseSession The instance of DatabaseSession.
     */
    public EasyBeansServerPlatform(final DatabaseSession databaseSession) {
        super(databaseSession);
    }

    /**
     * INTERNAL: getExternalTransactionControllerClass(): Answer the class of
     * external transaction controller to use for Oc4j. This is read-only.
     * @return Class externalTransactionControllerClass
     * @see oracle.toplink.essentials.transaction.JTATransactionController
     * @see oracle.toplink.essentials.platform.server.ServerPlatformBase#isJTAEnabled()
     * @see oracle.toplink.essentials.platform.server.ServerPlatformBase#disableJTA()
     * @see oracle.toplink.essentials.platform.server.ServerPlatformBase#initializeExternalTransactionController()
     */
    @Override
    public Class<?> getExternalTransactionControllerClass() {
        return EasyBeansTransactionController.class;

    }

    /**
     * Gets the EasyBeans server name.
     * @return String serverNameAndVersion
     */
    @Override
    public String getServerNameAndVersion() {
        return EASYBEANS_NAME;
    }

}

