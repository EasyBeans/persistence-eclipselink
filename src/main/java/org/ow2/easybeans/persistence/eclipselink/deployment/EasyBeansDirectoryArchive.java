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
 * $Id: EasyBeansDirectoryArchive.java 6005 2011-10-17 12:59:28Z benoitf $
 * --------------------------------------------------------------------------
 */
package org.ow2.easybeans.persistence.eclipselink.deployment;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.persistence.internal.jpa.deployment.DirectoryArchive;

/**
 * This class is removing WEB-INF/classes from entries for war file.
 * @author Florent Benoit
 */
public class EasyBeansDirectoryArchive extends DirectoryArchive {

    private static final String WEBINF_CLASSES = "WEB-INF/classes";

    private File directory = null;

    /**
     * The file entries that this archive contains.
     */
    private final List<String> entries = new ArrayList<String>();


    public EasyBeansDirectoryArchive(File directory, String descriptorLocation) throws MalformedURLException {
        this(directory, descriptorLocation, Logger.global);
    }

    public EasyBeansDirectoryArchive(File directory, String descriptorLocation, Logger logger)
            throws MalformedURLException {
        super(directory, descriptorLocation, logger);
        this.directory = directory;
        init(directory, directory);
    }

    public Iterator<String> getEntries() {
        return entries.iterator();
    }

    private boolean isWar() {
        return directory.getName().endsWith(".war");
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

}
