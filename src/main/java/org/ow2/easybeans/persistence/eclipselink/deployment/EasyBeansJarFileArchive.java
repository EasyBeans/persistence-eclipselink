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
 * $Id: EasyBeansJarFileArchive.java 6005 2011-10-17 12:59:28Z benoitf $
 * --------------------------------------------------------------------------
 */

package org.ow2.easybeans.persistence.eclipselink.deployment;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.persistence.internal.jpa.deployment.JarFileArchive;

public class EasyBeansJarFileArchive extends JarFileArchive {


    private static final String WAR_SUFFIX = ".war";

    private static final String CLASS_SUFFIX = ".class";

    private static final String WEB_PREFIX = "WEB-INF/classes";

    private static final int WEb_PREFIX_AFTER_INDEX = WEB_PREFIX.length();

    private JarFile jarFile = null;


    public EasyBeansJarFileArchive(JarFile jarFile, String descriptorLocation) throws MalformedURLException {
        this(jarFile, descriptorLocation, Logger.global);
    }

    public EasyBeansJarFileArchive(JarFile jarFile, String descriptorLocation, Logger logger)
            throws MalformedURLException {
        super(jarFile, descriptorLocation, logger);
        this.jarFile = jarFile;
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

    private boolean isWar() {
        String rootPath = getRootURL().getPath();
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
}
