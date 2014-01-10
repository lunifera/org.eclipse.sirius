/*******************************************************************************
 * Copyright (c) 2011 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.editor.tools.internal.property.section;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.sirius.business.api.query.EObjectQuery;
import org.eclipse.sirius.diagram.description.DescriptionPackage;
import org.eclipse.sirius.diagram.description.DiagramDescription;
import org.eclipse.sirius.diagram.description.DiagramImportDescription;
import org.eclipse.sirius.ext.base.Option;

/**
 * Helper for AbstractSiriusPropertySection generated class.
 * 
 * @author fbarbin
 * 
 */
public final class SiriusPropertySectionHelper {
    private SiriusPropertySectionHelper() {
        // Prevent instanciation.
    }

    /**
     * Check if the given eObject is a child of the diagram description.
     * 
     * @param eObject
     *            the eObject.
     * @param diagramDescription
     *            the diagram description.
     * @return true if eObject is a child or is equals to diagram description
     *         otherwise false.
     */
    public static boolean isChildOfDiagramDescription(EObject eObject, DiagramDescription diagramDescription) {
        if (eObject.equals(diagramDescription)) {
            return true;
        } else {
            Option<EObject> desc = new EObjectQuery(eObject).getFirstAncestorOfType(DescriptionPackage.Literals.DIAGRAM_DESCRIPTION);
            return desc.some() && desc.get().equals(diagramDescription);
        }
    }

    /**
     * Provides the first DiagramImportDescription found into given selection.
     * 
     * @param selection
     *            the selection.
     * @return the diagram import description or <code>null</code> if not found.
     */
    public static DiagramImportDescription getDiagramImportDescriptionInSelection(ISelection selection) {
        if (selection instanceof ITreeSelection) {
            TreePath treePath = ((ITreeSelection) selection).getPaths()[0];
            for (int i = 0; i < treePath.getSegmentCount(); i++) {
                if (treePath.getSegment(i) instanceof DiagramImportDescription) {
                    return (DiagramImportDescription) treePath.getSegment(i);
                }
            }
        }
        return null;
    }
}