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
 * $Id: EasyBeansTransactionController.java 5369 2010-02-24 14:58:19Z benoitf $
 * --------------------------------------------------------------------------
 */

package org.ow2.easybeans.persistence.eclipselink;

import javax.naming.InitialContext;
import javax.transaction.TransactionManager;

import org.eclipse.persistence.transaction.JTATransactionController;

/**
* TransactionController for EasyBeans (By default, it is JOTM which is the TM of EasyBeans by default).
* @author Florent Benoit
*/
public class EasyBeansTransactionController extends JTATransactionController {

    /**
     * JOTM binding in registry.
     */
    private static final String JOTM_BINDING = "javax.transaction.UserTransaction";

       /**
        * Default constructor.
        */
       public EasyBeansTransactionController() {
           super();
       }

       /**
        * Gets the transaction Manager of EasyBeans.
        * @return The TM.
        * @throws Exception (for example if the lookup fails)
        */
       @Override
       protected TransactionManager acquireTransactionManager() throws Exception {
           return (TransactionManager) new InitialContext().lookup(JOTM_BINDING);
       }
}


