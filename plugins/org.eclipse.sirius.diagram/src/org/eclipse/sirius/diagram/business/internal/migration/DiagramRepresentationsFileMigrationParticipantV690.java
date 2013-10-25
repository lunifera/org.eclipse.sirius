/*******************************************************************************
 * Copyright (c) 2013 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.diagram.business.internal.migration;

import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.gmf.runtime.notation.ConnectorStyle;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.Routing;
import org.eclipse.sirius.viewpoint.DEdge;
import org.eclipse.sirius.viewpoint.EdgeRouting;
import org.eclipse.sirius.viewpoint.EdgeStyle;
import org.eclipse.sirius.viewpoint.ViewpointPackage;
import org.eclipse.sirius.viewpoint.description.DescriptionPackage;
import org.osgi.framework.Version;

import com.google.common.collect.Iterables;

/**
 * The migration code of Sirius 6.9.0.
 * 
 * @author fbarbin
 * 
 */
public class DiagramRepresentationsFileMigrationParticipantV690 {

    /**
     * The VP version for this migration.
     */
    public static final Version MIGRATION_VERSION = new Version("6.9.0.201309101020");

    /**
     * Provides AdditionalLayer instead of OptionalLayer.
     * 
     * @param ePackage
     *            the package of the type.
     * @param name
     *            the name of the type.
     * @return an AdditionalLayer or null if the given type is not an
     *         OptionalLayer.
     */
    public EClassifier getType(EPackage ePackage, String name) {
        if (ePackage.getNsURI() != null && ePackage.getNsURI().equals(DescriptionPackage.eINSTANCE.getNsURI()) && name.equals("OptionalLayer")) {
            return DescriptionPackage.eINSTANCE.getAdditionalLayer();
        }
        return null;
    }

    /**
     * Migrate the edge routing style according to new connection preference
     * page behavior. Before VP 6.9.0, the connection preference page is the GMF
     * default one. But it was unsuitable for Viewpoint use. Indeed, in
     * Viewpoint the default routing style is defined in VSM. Some users have
     * used this preference page to override the style defined in the VSM, but
     * it is not really correctly done:
     * <UL>
     * <LI>The end-user can not retrieve the style defined in the VSM (no undo
     * mechanism similar to the Reset style properties to default values button)
     * </LI>
     * <LI>The current preference page does not allow to choose the "Tree"
     * routing style.</LI>
     * <LI>It works only if the Rectilinear is set. The Oblique is the default
     * value and has no effect.</LI>
     * <LI>Inconsistent state between GMF data and Viewpoint data</LI>
     * </UL>
     * This migration converts edge styles that have been "customized" by the
     * previous preference page. It concerns only edges with
     * <UL>
     * <LI>a GMF routing style set to Rectilinear,</LI>
     * <LI>without routingStyle in customFeatures list,</LI>
     * <LI>and with a DEdge style set to Oblique.</LI>
     * </UL>
     * For this edges, the routingStyle will be added in the customFeatures list
     * of the style of these edges and the DEdge style will be set to
     * Rectilinear.<BR>
     * 
     * @param diagrams
     *            list of GMF Diagram to migrate.
     */
    public void migrateEdgeRoutingStyle(List<Diagram> diagrams) {
        // Migrate the concerned edges.
        for (Diagram diagram : diagrams) {
            for (Edge edge : Iterables.filter(diagram.getEdges(), Edge.class)) {
                if (isConcernedEdge(edge)) {
                    DEdge dEdge = (DEdge) edge.getElement();
                    EdgeStyle edgeStyle = dEdge.getOwnedStyle();
                    edgeStyle.setRoutingStyle(EdgeRouting.MANHATTAN_LITERAL);
                    edgeStyle.getCustomFeatures().add(ViewpointPackage.Literals.EDGE_STYLE__ROUTING_STYLE.getName());
                }
            }
        }
    }

    /**
     * Check if this edge is concerned by the migration. Edge is concerned by
     * this migration if:
     * <UL>
     * <LI>it has a GMF routing style set to Rectilinear,</LI>
     * <LI>the corresponding DEgde has not routingStyle in customFeatures list,</LI>
     * <LI>and the corresponding DEgde style is set to Oblique.</LI>
     * </UL>
     * 
     * @param edge
     *            The edge to check.
     */
    private boolean isConcernedEdge(Edge edge) {
        ConnectorStyle connectorStyle = (ConnectorStyle) edge.getStyle(NotationPackage.eINSTANCE.getConnectorStyle());
        if (connectorStyle != null && Routing.RECTILINEAR_LITERAL.equals(connectorStyle.getRouting())) {
            if (edge.getElement() instanceof DEdge) {
                DEdge dEdge = (DEdge) edge.getElement();
                if (EdgeRouting.STRAIGHT_LITERAL.equals(dEdge.getOwnedStyle().getRoutingStyle())) {
                    if (!dEdge.getOwnedStyle().getCustomFeatures().contains(ViewpointPackage.Literals.EDGE_STYLE__ROUTING_STYLE.getName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}