/**
 * EasyBeans
 * Copyright (C) 2009 Bull S.A.S.
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
 * $Id: JarFileArchive.java 5371 2010-02-24 15:02:00Z benoitf $
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

/**
 * This is an implementation of {@link Archive} when container returns a
 * file: url that refers to a jar file. e.g. file:/tmp/a_ear/lib/pu.jar
 *
 * @author Sanjeeb.Sahoo@Sun.COM
 */
public class JarFileArchive implements Archive {
    private final JarFile jarFile;

    private final URL rootURL;

    @SuppressWarnings("unused")
    private final Logger logger;

    private static final String WAR_SUFFIX = ".war";

    private static final String CLASS_SUFFIX = ".class";

    private static final String WEB_PREFIX = "WEB-INF/classes";

    private static final int WEb_PREFIX_AFTER_INDEX = WEB_PREFIX.length();

    private boolean isWar() {
        String rootPath = rootURL.getPath();
        if (rootPath.endsWith(WAR_SUFFIX)) {
            return true;
        }
        return false;
    }

    private String treat(final String name) {
        // treat WEB-INF/classes case
        if (name.endsWith(CLASS_SUFFIX)) {
            if (name.startsWith(WEB_PREFIX)) {
                return name.substring(WEb_PREFIX_AFTER_INDEX + 1);
            }
        }
        return name;
    }


    @SuppressWarnings("deprecation")
    public JarFileArchive(JarFile jarFile) throws MalformedURLException {
        this(jarFile, Logger.global);
    }

    public JarFileArchive(JarFile jarFile, Logger logger)
    throws MalformedURLException {
        logger.entering("JarFileArchive", "JarFileArchive", // NOI18N
                new Object[]{jarFile});
        this.logger = logger;
        this.jarFile = jarFile;
        rootURL = new File(jarFile.getName()).toURI().toURL();
        logger.logp(Level.FINER, "JarFileArchive", "JarFileArchive", // NOI18N
                "rootURL = {0}", rootURL); // NOI18N
    }

    public Iterator<String> getEntries() {
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        ArrayList<String> result = new ArrayList<String>();
        while (jarEntries.hasMoreElements()) {
            final JarEntry jarEntry = jarEntries.nextElement();
            if(!jarEntry.isDirectory()) { // exclude directory entries
                String entryName = jarEntry.getName();
                if (isWar()) {
                    // Check if entry name needs treatment
                    entryName = treat(entryName);
                }
                result.add(entryName);
            }
        }
        return result.iterator();
    }

    public InputStream getEntry(String entryPath) throws IOException {
        InputStream is = null;
        final ZipEntry entry = jarFile.getEntry(entryPath);
        if (entry != null) {
            is = jarFile.getInputStream(entry);
        }
        return is;
    }

    public URL getEntryAsURL(String entryPath) throws IOException {
        return jarFile.getEntry(entryPath)!= null ?
                new URL("jar:"+rootURL+"!/"+entryPath) : null; // NOI18N
    }

    public URL getRootURL() {
        return rootURL;
    }
}
