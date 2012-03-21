/**
 * EasyBeans
 * Copyright (C) 2012 Bull S.A.S.
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
 * $Id:
 * --------------------------------------------------------------------------
 */

package org.ow2.easybeans.persistence.eclipselink;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.SingleTableMultitenantPolicy;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.sessions.Session;

import java.util.Map;

/**
 * This Customizer class allows Eclipselink to set all entities classes
 * as multitenant.
 * @author Mohammed Boukada
 */
public class MultitenantEntitiesSessionCustomizer implements SessionCustomizer {

    public void customize(Session session) throws Exception {
        Map<Class, ClassDescriptor> descs = session.getDescriptors();
        for(Map.Entry<Class, ClassDescriptor> desc : descs.entrySet()){
            SingleTableMultitenantPolicy policy = new SingleTableMultitenantPolicy(desc.getValue());
            policy.addTenantDiscriminatorField("eclipselink.tenant-id", new DatabaseField("TENANT_ID"));
            desc.getValue().setMultitenantPolicy(policy);
        }
    }
}
