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
 * $Id: EasyBeansArchiveFactoryImpl.java 6005 2011-10-17 12:59:28Z benoitf $
 * --------------------------------------------------------------------------
 */

package org.ow2.easybeans.persistence.eclipselink.deployment;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.jpa.deployment.ArchiveFactoryImpl;
import org.eclipse.persistence.jpa.Archive;

public class EasyBeansArchiveFactoryImpl extends ArchiveFactoryImpl {


    private Logger logger;

    public EasyBeansArchiveFactoryImpl() {
        this(Logger.global);
    }

    public EasyBeansArchiveFactoryImpl(Logger logger) {
        super(logger);
        this.logger = logger;
    }



    @Override
    public Archive createArchive(URL rootUrl, String descriptorLocation, Map properties) throws URISyntaxException, IOException {
        logger.entering("EasyBeansArchiveFactoryImpl", "createArchive", new Object[]{rootUrl, descriptorLocation});
        String protocol = rootUrl.getProtocol();
        logger.logp(Level.FINER, "EasyBeansArchiveFactoryImpl", "createArchive", "protocol = {0}", protocol);

        if ("bundle".equals(protocol) || "bundleentry".equals(protocol)) {
            return new BundleArchive(rootUrl, descriptorLocation);
        } else if ("file".equals(protocol)) {
            URI uri = Helper.toURI(rootUrl);

            File f;
            try {
                // Attempt to create the file with the uri. The pre-conditions
                // are checked in the constructor and an exception is thrown
                // if the uri does not meet them.
                f = new File(uri);
            } catch (IllegalArgumentException e) {
                // Invalid uri for File. Go our back up route of using the 
                // path from the url.
                f = new File(rootUrl.getPath());
            }

            if (f.isDirectory()) {
                return  new EasyBeansDirectoryArchive(f, descriptorLocation);
            } else {
                return new EasyBeansJarFileArchive(rootUrl, new JarFile(f), descriptorLocation);
            }
        }
        return super.createArchive(rootUrl, descriptorLocation, properties);
    }
}

