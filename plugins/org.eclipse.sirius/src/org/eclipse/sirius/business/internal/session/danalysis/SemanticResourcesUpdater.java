/*******************************************************************************
 * Copyright (c) 2012, 2015 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.business.internal.session.danalysis;

import java.util.Collection;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sirius.business.api.session.SessionListener;
import org.eclipse.sirius.viewpoint.DAnalysis;
import org.eclipse.sirius.viewpoint.ViewpointPackage;

import com.google.common.collect.Lists;

/**
 * A {@link Adapter} to update the collection of semantic resources in a
 * Session.
 * 
 * @author <a href="mailto:esteban.dugueperoux@obeo.fr">Esteban Dugueperoux</a>
 */
public class SemanticResourcesUpdater extends AdapterImpl implements Adapter {

    private static final Collection<EStructuralFeature> FEATURES_OF_INTEREST = Lists.newArrayList(ViewpointPackage.Literals.DANALYSIS_SESSION_EOBJECT__ANALYSES,
            ViewpointPackage.Literals.DANALYSIS__REFERENCED_ANALYSIS, ViewpointPackage.Literals.DANALYSIS_SESSION_EOBJECT__ANALYSES, ViewpointPackage.Literals.DANALYSIS__SEMANTIC_RESOURCES,
            ViewpointPackage.Literals.DANALYSIS_SESSION_EOBJECT__CONTROLLED_RESOURCES);;

    private DAnalysisSessionImpl session;

    private Collection<Resource> semanticResources;

    /**
     * Default constructor.
     * 
     * @param session
     *            the {@link DAnalysisSessionImpl} referencing the semantic
     *            resource
     */
    public SemanticResourcesUpdater(DAnalysisSessionImpl session) {
        this.session = session;
    }

    /**
     * Initialize the semantic resources of the session.
     * 
     * @param semanticResources
     *            the collection of semantic resources of the Session
     */
    public void setSemanticResources(Collection<Resource> semanticResources) {
        for (DAnalysis dAnalysis : this.session.allAnalyses()) {
            if (!dAnalysis.eAdapters().contains(this)) {
                dAnalysis.eAdapters().add(this);
            }
        }
        this.semanticResources = semanticResources;
    }

    @Override
    public void notifyChanged(Notification msg) {
        if (isEventOfInterest(msg)) {
            Collection<Resource> updatedSemanticResources = SemanticResourceGetter.collectTopLevelSemanticResources(session);

            boolean newSemanticResourceAdded = false;
            for (Resource semanticResource : updatedSemanticResources) {
                if (!semanticResources.contains(semanticResource)) {
                    newSemanticResourceAdded = true;

                    // Ensure that the cross referencer adapter is on the
                    // semantic resource.
                    session.registerResourceInCrossReferencer(semanticResource);
                }
            }

            // The size comparison is useful when a semantic resource is removed
            // (newSemanticResourceAdded is false but the size has changed)
            if (newSemanticResourceAdded || semanticResources.size() != updatedSemanticResources.size()) {
                semanticResources.clear();
                semanticResources.addAll(updatedSemanticResources);

                session.notifyListeners(SessionListener.SEMANTIC_CHANGE);
            }
        }
    }

    private boolean isEventOfInterest(Notification msg) {
        return (msg.getEventType() != Notification.REMOVING_ADAPTER) && FEATURES_OF_INTEREST.contains(msg.getFeature());
    }

    /**
     * Dispose this updater.
     */
    public void dispose() {
        for (DAnalysis dAnalysis : this.session.allAnalyses()) {
            if (dAnalysis.eAdapters().contains(this)) {
                dAnalysis.eAdapters().remove(this);
            }
        }
        session = null;
        semanticResources = null;
    }
}
