/*******************************************************************************
 * Copyright (c) 2013, 2015 THALES GLOBAL SERVICES.
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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.sirius.business.api.query.ResourceQuery;
import org.eclipse.sirius.common.tools.api.util.SiriusCrossReferenceAdapterImpl;

/**
 * A {@link IResourceCollector} for local {@link Resource}.
 * 
 * @author <a href="mailto:esteban.dugueperoux@obeo.fr">Esteban Dugueperoux</a>
 */
public class LocalResourceCollector extends SiriusCrossReferenceAdapterImpl implements IResourceCollector {

    private ResourceSet resourceSet;

    private Map<Resource, Collection<Resource>> directlyReferencingResources = new WeakHashMap<Resource, Collection<Resource>>();

    private Map<Resource, Collection<Resource>> directlyReferencedResources = new WeakHashMap<Resource, Collection<Resource>>();

    private boolean initialized;

    /**
     * Default constructor.
     * 
     * @param resourceSet
     *            the {@link ResourceSet} on which to listens
     *            {@link ResourceSet#getResources()} changes
     */
    public LocalResourceCollector(ResourceSet resourceSet) {
        this.resourceSet = resourceSet;
    }

    @Override
    protected InverseCrossReferencer createInverseCrossReferencer() {
        return new LocalInverseCrossReferencer();
    }

    /**
     * Add a inter resource reference.
     * 
     * @param referencingResource
     *            the specified referencing {@link Resource}
     * @param referencedResource
     *            the specified referenced {@link Resource}
     */
    private void addInterResourceResourceReference(Resource referencingResource, Resource referencedResource) {
        if (!new ResourceQuery(referencingResource).isRepresentationsResource() && !new ResourceQuery(referencedResource).isRepresentationsResource()) {
            // Update referenced resources
            Collection<Resource> allReferencedResourcesByResource = directlyReferencedResources.get(referencingResource);
            if (allReferencedResourcesByResource == null) {
                allReferencedResourcesByResource = new LinkedHashSet<Resource>();
                directlyReferencedResources.put(referencingResource, allReferencedResourcesByResource);
            }
            allReferencedResourcesByResource.add(referencedResource);

            // Update referencing resources
            Collection<Resource> allReferencingResourcesByResource = directlyReferencingResources.get(referencedResource);
            if (allReferencingResourcesByResource == null) {
                allReferencingResourcesByResource = new LinkedHashSet<Resource>();
                directlyReferencingResources.put(referencedResource, allReferencingResourcesByResource);
            }
            allReferencingResourcesByResource.add(referencingResource);
        }
    }

    /**
     * Remove new inter resource reference.
     * 
     * @param referencingResource
     *            the specified referencing {@link Resource}
     * @param referencedResource
     *            the specified referenced {@link Resource}
     */
    private void removeInterResourceResourceReference(Resource referencingResource, Resource referencedResource) {
        directlyReferencedResources.remove(referencingResource);
        directlyReferencingResources.remove(referencedResource);
    }

    @Override
    public Collection<Resource> getAllReferencedResources(Resource resource) {
        if (!initialized) {
            resourceSet.eAdapters().add(this);
            initialized = true;
        }
        Collection<Resource> allReferencedResources = getTransitivelyAllResoures(directlyReferencedResources, resource, Collections.<Resource> emptyList());
        return allReferencedResources;
    }

    @Override
    public Collection<Resource> getAllReferencingResources(Resource resource) {
        if (!initialized) {
            resourceSet.eAdapters().add(this);
            initialized = true;
        }
        Collection<Resource> allReferencingResources = getTransitivelyAllResoures(directlyReferencingResources, resource, Collections.<Resource> emptyList());
        return allReferencingResources;
    }

    private Collection<Resource> getTransitivelyAllResoures(Map<Resource, Collection<Resource>> map, Resource resource, Collection<Resource> resourcesAlreadyVisited) {
        Collection<Resource> allTransitiveResources = new LinkedHashSet<Resource>(resourcesAlreadyVisited);
        Collection<Resource> transitiveResources = map.get(resource);
        if (transitiveResources != null) {
            allTransitiveResources.addAll(transitiveResources);
            for (Resource transitiveResource : transitiveResources) {
                if (!resourcesAlreadyVisited.contains(transitiveResource)) {
                    allTransitiveResources.addAll(getTransitivelyAllResoures(map, transitiveResource, allTransitiveResources));
                }
            }
        }
        return allTransitiveResources;
    }

    @Override
    public void dispose() {
        if (initialized) {
            resourceSet.eAdapters().remove(this);
            initialized = false;
        }
        resourceSet = null;
        directlyReferencingResources = null;
        directlyReferencedResources = null;
    }

    private class LocalInverseCrossReferencer extends InverseCrossReferencer {

        private static final long serialVersionUID = 1L;

        /**
         * A Map for each Resource to have all resource externally referenced
         * EObjects through resource local EObject through which EReferences .
         */
        private Map<Resource, Map<EObject, Map<EObject, EStructuralFeature>>> resourcesRefs = new WeakHashMap<Resource, Map<EObject, Map<EObject, EStructuralFeature>>>();

        @Override
        protected void add(InternalEObject eObject, EReference eReference, EObject crossReferencedEObject) {
            super.add(eObject, eReference, crossReferencedEObject);
            Resource referencingResource = eObject.eResource();
            Resource referencedResource = crossReferencedEObject.eResource();
            if (isReferenceOfInterest(referencedResource, referencingResource)) {
                Map<EObject, Map<EObject, EStructuralFeature>> referencedEObjects = resourcesRefs.get(referencingResource);
                if (referencedEObjects == null) {
                    referencedEObjects = new LinkedHashMap<EObject, Map<EObject, EStructuralFeature>>();
                    resourcesRefs.put(referencingResource, referencedEObjects);
                }
                Map<EObject, EStructuralFeature> settings = referencedEObjects.get(crossReferencedEObject);
                if (settings == null) {
                    settings = new LinkedHashMap<EObject, EStructuralFeature>();
                    referencedEObjects.put(crossReferencedEObject, settings);
                }
                Setting setting = eObject.eSetting(eReference);
                // Does not add
                // EcorePackage.Literals.ETYPED_ELEMENT__EGENERIC_TYPE
                // reference because it is not removed from crossReference
                if (setting.getEStructuralFeature() != EcorePackage.Literals.EGENERIC_TYPE__ECLASSIFIER) {
                    settings.put(setting.getEObject(), setting.getEStructuralFeature());
                }
                addInterResourceResourceReference(referencingResource, referencedResource);
            }
        }

        @Override
        public void remove(EObject eObject, EReference eReference, EObject crossReferencedEObject) {
            super.remove(eObject, eReference, crossReferencedEObject);
            Resource referencingResource = eObject.eResource();
            Resource referencedResource = crossReferencedEObject.eResource();
            if (isReferenceOfInterest(referencedResource, referencingResource)) {
                removeInMap(referencedResource, referencingResource, eObject, crossReferencedEObject);
            }
        }

        /**
         * The only references we are concerned with are between two different
         * non-null and non-representation resources.
         */
        private boolean isReferenceOfInterest(Resource referencedResource, Resource referencingResource) {
            if (referencedResource == null || referencingResource == null || referencedResource == referencingResource) {
                return false;
            } else {
                // TODO We should also ignore other non-semantic resources
                return !new ResourceQuery(referencingResource).isRepresentationsResource() && !new ResourceQuery(referencedResource).isRepresentationsResource();
            }
        }

        private void removeInMap(Resource referencedResource, Resource referencingResource, EObject eObject, EObject crossReferencedEObject) {
            Map<EObject, Map<EObject, EStructuralFeature>> referencedEObjects = resourcesRefs.get(referencingResource);
            if (referencedEObjects != null) {
                Map<EObject, EStructuralFeature> settings = referencedEObjects.get(crossReferencedEObject);
                if (settings != null) {
                    settings.remove(eObject);
                }
                if (settings == null || settings.isEmpty()) {
                    referencedEObjects.remove(crossReferencedEObject);
                    if (referencedEObjects.isEmpty()) {
                        resourcesRefs.remove(referencingResource);
                        removeInterResourceResourceReference(referencingResource, referencedResource);
                    }
                }
            }
        }

    }

}
