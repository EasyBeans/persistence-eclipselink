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
 * $Id: BundleArchive.java 5371 2010-02-24 15:02:00Z benoitf $
 * --------------------------------------------------------------------------
 */

package org.eclipse.persistence.internal.jpa.deployment;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.ow2.easybeans.util.osgi.BCMapper;

/**
 * Allows to get entries from a Bundle.
 * @author Florent Benoit
 */
public class BundleArchive implements Archive {

    /**
     * The URL representation of this archive.
     */
    private final URL url;

    /**
     * List of URLs of the bundle.
     */
    private Enumeration<URL> urlEntries = null;


    /**
     * List of entris inside this bundle.
     */
    private List<String> entries = null;


    public BundleArchive(URL url) {
        this.url = url;
        this.entries = new ArrayList<String>();

        BCMapper mapper = BCMapper.getInstance();
        BundleContext bc = mapper.get(url);

        // Get all the URLs of this bundle
        this.urlEntries = bc.getBundle().findEntries("", "*", true);

        // Adds only entries of the bundle
        while (urlEntries.hasMoreElements()) {
            URL urlEntry = urlEntries.nextElement();
            String entryName = urlEntry.toString().substring(url.toString().length());
            this.entries.add(entryName);
        }

    }


    /**
     * Returns an {@link java.util.Iterator} of the file entries. Each String represents
     * a file name relative to the root of the module.
     */
    public Iterator<String> getEntries() {
        return entries.iterator();
    }

    /**
     * Returns the InputStream for the given entry name. Returns null if no such
     * entry exists. The entry name must be relative to the root of the module.
     *
     * @param entryPath the file name relative to the root of the module.
     * @return the InputStream for the given entry name or null if not found.
     */
    public InputStream getEntry(String entryPath) throws IOException {
        URL subEntry = new URL(url, entryPath);
        InputStream is = null;
        try {
            is = subEntry.openStream();
        } catch (IOException ioe) {
            // we return null when entry does not exist
        }
        return is;
    }

    /**
     * Returns the URL for the given entry name. Returns null if no such
     * entry exists. The entry name must be relative to the root of the module.
     *
     * @param entryPath the file name relative to the root of the module.
     * @return the URL for the given entry name or null if not found.
     */
    public URL getEntryAsURL(String entryPath) throws IOException {
        URL subEntry = new URL(url, entryPath);
        try {
            InputStream is = subEntry.openStream();
            // on Knopflerfish, stream is null (no exception)
            if (is != null) {
                is.close();
            } else {
                return null;
            }
        } catch (IOException ioe) {
            return null; // return null when entry does not exist
        }
        return subEntry;
    }

    /**
     * @return the URL that this archive represents.
     */
    public URL getRootURL() {
        return url;
    }

}
