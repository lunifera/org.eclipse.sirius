/*******************************************************************************
 * Copyright (c) 2008, 2015 THALES GLOBAL SERVICES and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.business.internal.resource;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLLoad;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.sirius.business.api.session.resource.AirdResource;
import org.eclipse.sirius.business.api.session.resource.DResource;
import org.eclipse.sirius.business.internal.migration.AbstractSiriusMigrationService;
import org.eclipse.sirius.business.internal.migration.RepresentationsFileMigrationService;
import org.eclipse.sirius.business.internal.resource.parser.RepresentationsFileXMIHelper;
import org.eclipse.sirius.common.tools.DslCommonPlugin;
import org.eclipse.sirius.ext.base.Option;
import org.eclipse.sirius.tools.api.profiler.SiriusTasksKey;

/**
 * Aird resource to provide custom factory.
 * 
 * @author mchauvin
 */
public class AirDResourceImpl extends XMIResourceImpl implements DResource, AirdResource {

    /**
     * Use this option to abort loading a resource immediately when an error
     * occurs. The default is <code>Boolean.FALSE</code> unless set to
     * <code>Boolean.TRUE</code> explicitly.
     */
    public static final String OPTION_ABORT_ON_ERROR = "ABORT_ON_ERROR"; //$NON-NLS-1$

    /**
     * The number of current load in progress. Usefull for determine if the
     * current load is the first one or is a load triggered by a resolve (for
     * fragmented files for examples).
     */
    private static ThreadLocal<Integer> nbLoadInProgress = new ThreadLocal<Integer>() {
        @Override
        protected synchronized Integer initialValue() {
            return Integer.valueOf(0);
        }
    };

    /**
     * This constructor should be used only if the version is up to date. There
     * is no automatic migration during the resolution of an object.
     * 
     * @param uri
     *            the URI
     */
    public AirDResourceImpl(final URI uri) {
        super(uri);
    }

    @Override
    protected boolean useUUIDs() {
        return true;
    }

    @Override
    protected boolean useIDAttributes() {
        return false;
    }

    /**
     * Increment the number of load in progress.
     */
    protected static void incrementLoadInProgress() {
        nbLoadInProgress.set(Integer.valueOf(nbLoadInProgress.get().intValue() + 1));
    }

    /**
     * Decrement the number of load in progress.
     */
    protected static void decrementLoadInProgress() {
        nbLoadInProgress.set(Integer.valueOf(nbLoadInProgress.get().intValue() - 1));
    }

    /**
     * Check if there is a load in progress.
     * 
     * @return true if at least one load is in progress, false otherwise.
     */
    protected static boolean hasLoadInProgress() {
        return nbLoadInProgress.get().intValue() != 0;
    }

    /**
     * Overridden to not have {@link XMIResourceImpl} set to true in this
     * constructor because now it is
     * {@link org.eclipse.sirius.business.internal.resource.ResourceModifiedFieldUpdater}
     * which manage {@link org.eclipse.emf.ecore.resource.Resource#isModified()}
     * .
     * 
     * {@inheritDoc}
     */
    @Override
    public void setTrackingModification(boolean isTrackingModification) {
    }

    @Override
    public void load(Map<?, ?> options) throws IOException {
        DslCommonPlugin.PROFILER.startWork(SiriusTasksKey.LOAD_AIRD_KEY);
        AirDResourceImpl.incrementLoadInProgress();
        try {
            super.load(options);
        } finally {
            AirDResourceImpl.decrementLoadInProgress();
        }
        DslCommonPlugin.PROFILER.stopWork(SiriusTasksKey.LOAD_AIRD_KEY);
        // AirDResourceMigration migration = new AirDResourceMigration(this);
        // // Notify user only if there is no more load in progress.
        // migration.migrate(!AirDResourceImpl.hasLoadInProgress());
    }

    @Override
    protected XMLHelper createXMLHelper() {
        return new RepresentationsFileXMIHelper(this);
    }

    @Override
    protected XMLLoad createXMLLoad(Map<?, ?> options) {
        if (options != null && options.containsKey(AbstractSiriusMigrationService.OPTION_RESOURCE_MIGRATION_LOADEDVERSION)) {
            // LoadedVersion can be null for old aird files.
            String loadedVersion = null;
            Object mapVersion = options.get(AbstractSiriusMigrationService.OPTION_RESOURCE_MIGRATION_LOADEDVERSION);
            if (mapVersion instanceof String) {
                loadedVersion = (String) mapVersion;
            }
            return new AirdResourceXMILoad(loadedVersion, createXMLHelper());
        }

        return new AirdResourceXMILoad(createXMLHelper());
    }

    /**
     * Override to migrate fragment if necessary (when a reference has been
     * renamed) before getting the EObject.
     * 
     * {@inheritDoc}
     */
    @Override
    public EObject getEObject(String uriFragment) {
        Option<String> optionalRewrittenFragment = RepresentationsFileMigrationService.getInstance().getNewFragment(uriFragment);
        if (optionalRewrittenFragment.some()) {
            return getEObject(optionalRewrittenFragment.get());
        } else {
            return super.getEObject(uriFragment);
        }
    }

}
