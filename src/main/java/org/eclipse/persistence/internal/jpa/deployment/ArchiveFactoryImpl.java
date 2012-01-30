/**
 * EasyBeans
 * Copyright (C) 2010 Bull S.A.S.
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
 * $Id: ArchiveFactoryImpl.java 5371 2010-02-24 15:02:00Z benoitf $
 * --------------------------------------------------------------------------
 */

/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.deployment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.eclipse.persistence.internal.helper.Helper;

/**
 * This class is written to deal with various URLs that can be returned by
 * {@link javax.persistence.spi.PersistenceUnitInfo#getPersistenceUnitRootUrl()}
 *
 * @author Sanjeeb.Sahoo@Sun.COM
 */
public class ArchiveFactoryImpl {
    /*
     * Implementation Note: This class does not have any dependency on TopLink
     * or GlassFish implementation classes. Please retain this searation.
     */

    private final Logger logger;

    @SuppressWarnings("deprecation")
    public ArchiveFactoryImpl() {
        this(Logger.global);
    }

    public ArchiveFactoryImpl(Logger logger) {
        this.logger = logger;
    }

    public Archive createArchive(URL url) throws URISyntaxException, IOException {
        logger.entering("ArchiveFactoryImpl", "createArchive", new Object[]{url});
        Archive result;
        String protocol = url.getProtocol();
        logger.logp(Level.FINER, "ArchiveFactoryImpl", "createArchive", "protocol = {0}", protocol);

        if ("bundle".equals(protocol) || "bundleentry".equals(protocol)) {
            result = new BundleArchive(url);
        } else if ("file".equals(protocol)) {
            URI uri = Helper.toURI(url);

            File f;
            try {
                // Attempt to create the file with the uri. The pre-conditions
                // are checked in the constructor and an exception is thrown
                // if the uri does not meet them.
                f = new File(uri);
            } catch (IllegalArgumentException e) {
                // Invalid uri for File. Go our back up route of using the
                // path from the url.
                f = new File(url.getPath());
            }

            if (f.isDirectory()) {
                // e.g. file:/tmp/a_ear/ejb_jar
                result = new DirectoryArchive(f);
            } else {
                // e.g. file:/tmp/a_ear/lib/pu.jar
                // It's not a directory. Then it must be a jar file.
                result = new JarFileArchive(new JarFile(f));
            }
        } else if ("jar".equals(protocol)) { // NOI18N
            JarURLConnection conn = JarURLConnection.class.cast(url.openConnection());
            JarEntry je = conn.getJarEntry();
            if (je == null) {
                // e.g. jar:file:/tmp/a_ear/lib/pu.jar!/
                // No entryName specified, hence URL points to a JAR file and
                // not to any entry inside it. Ideally this should have been
                // file:/tmp/a_ear/lib/pu.jar,
                // but containers (e.g.) WebLogic return this kind of URL,
                // so we better handle this in our code to imrove pluggability.
                // Read the entire jar file.
                result = new JarFileArchive(conn.getJarFile());
            } else if (je.isDirectory()) {
                // e.g. jar:file:/tmp/a_ear/b.war!/WEB-INF/classes/
                // entryName [je.getName()] is a directory
                result = new DirectoryInsideJarURLArchive(url);
            } else {
                // some URL (e.g.) jar:file:/tmp/a_ear/b.war!/WEB-INF/lib/pu.jar
                // entryName [je.getName()] is a file, so treat this URL as a
                // URL from which  a JAR format InputStream can be obtained.
                result = new JarInputStreamURLArchive(url);
            }
        } else if (isJarInputStream(url)){
            result = new JarInputStreamURLArchive(url);
        } else {
            result = new URLArchive(url);
        }
        logger.exiting("ArchiveFactoryImpl", "createArchive", result);
        return result;
    }

    /**
     * This method is called for a URL which has neither jar nor file protocol.
     * This attempts to find out if we can treat it as a URL from which a JAR
     * format InputStream can be obtained.
     * @param url
     */
    private boolean isJarInputStream(URL url) throws IOException {
        InputStream in = null;
        try {
            in = url.openStream();
            if (in == null) { // for directories, we may get InputStream as null
                return false;
            }
            JarInputStream jis = new JarInputStream(in);
            jis.close();
            return true; // we are successful in creating a Jar format IS
        } catch (IOException ioe) {
            if (in != null) {
                in.close();
            }
            return false;
        }
    }
}
