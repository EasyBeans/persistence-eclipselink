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
 * $Id: DirectoryArchive.java 5371 2010-02-24 15:02:00Z benoitf $
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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is an implementation of {@link Archive} when container returns a file:
 * url that refers to a directory that contains an exploded jar file. e.g.
 * file:/tmp/a_ear/ejb_jar
 * @author Sanjeeb.Sahoo@Sun.COM
 */
public class DirectoryArchive implements Archive {
    /*
     * Implementation Note: This class does not have any dependency on TopLink
     * or GlassFish implementation classes. Please retain this searation.
     */

    /**
     * The directory this archive represents.
     */
    private final File directory;

    /**
     * The URL representation of this archive.
     */
    private final URL rootURL;

    /**
     * The file entries that this archive contains.
     */
    private final List<String> entries = new ArrayList<String>();

    @SuppressWarnings("unused")
    private final Logger logger;

    private static final String WEBINF_CLASSES = "WEB-INF/classes";

    private boolean isWar() {
        return directory.getName().endsWith(".war");
    }

    @SuppressWarnings("deprecation")
    public DirectoryArchive(File directory) throws MalformedURLException {
        this(directory, Logger.global);
    }

    public DirectoryArchive(File directory, Logger logger) throws MalformedURLException {
        logger.entering("DirectoryArchive", "DirectoryArchive", new Object[] {directory});
        this.logger = logger;
        if (!directory.isDirectory()) {
            // should never reach here, hence the msg is not internationalized.
            throw new IllegalArgumentException(directory + " is not a directory." + // NOI18N
            "If it is a jar file, then use JarFileArchive."); // NOI18N
        }
        this.directory = directory;
        rootURL = directory.toURI().toURL();
        logger.logp(Level.FINER, "DirectoryArchive", "DirectoryArchive", "rootURL = {0}", rootURL);
        init(this.directory, this.directory); // initialize entries
    }

    private void init(File top, File directory) {
        File[] dirFiles = directory.listFiles();
        for (File file : dirFiles) {
            if (file.isDirectory()) {
                continue; // exclude dir entries
            }

            // add only the relative path from the top.
            // note: we use unix style path
            String entryName = file.getPath().replace(File.separator, "/") // NOI18N
            .substring(top.getPath().length() + 1);
            if (isWar() && entryName.endsWith(".class")) {
                if (entryName.startsWith(WEBINF_CLASSES)) {
                    entryName = entryName.substring(WEBINF_CLASSES.length() + 1);
                }
            }
            entries.add(entryName);
        }
        File[] subDirs = directory.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        for (File subDir : subDirs) {
            init(top, subDir); // recursion
        }
    }

    public Iterator<String> getEntries() {
        return entries.iterator();
    }

    public InputStream getEntry(String entryPath) throws IOException {
        File f = getFile(entryPath);
        InputStream is = f.exists() ? new FileInputStream(f) : null;
        return is;
    }

    public URL getEntryAsURL(String entryPath) throws IOException {
        File f = getFile(entryPath);
        URL url = f.exists() ? f.toURI().toURL() : null;
        return url;
    }

    public URL getRootURL() {
        return rootURL;
    }

    private File getFile(String entryPath) {
        File f = new File(directory, entryPath);
        return f;
    }

}
