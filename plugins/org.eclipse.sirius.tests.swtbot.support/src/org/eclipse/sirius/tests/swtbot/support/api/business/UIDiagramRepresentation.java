/**
 * Copyright (c) 2009, 2014 THALES GLOBAL SERVICES
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Obeo - Initial API and implementation
 */
package org.eclipse.sirius.tests.swtbot.support.api.business;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.sirius.diagram.DiagramPlugin;
import org.eclipse.sirius.diagram.tools.api.preferences.SiriusDiagramPreferencesKeys;
import org.eclipse.sirius.tests.swtbot.support.api.editor.SWTBotDesignerEditor;
import org.eclipse.sirius.tests.swtbot.support.api.editor.SWTBotDesignerHelper;
import org.eclipse.sirius.tests.swtbot.support.api.view.DesignerViews;
import org.eclipse.sirius.tests.swtbot.support.api.view.SiriusOutlineView;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarDropDownButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.hamcrest.Matcher;

/**
 * Object to manage diagram representations.
 * 
 * @author dlecan
 */
public class UIDiagramRepresentation extends AbstractUIRepresentation<SWTBotDesignerEditor> {

    /**
     * Zoom levels.
     * 
     * @author dlecan
     */
    public static enum ZoomLevel {
        /**
         * Zoom 50%.
         */
        ZOOM_50("50%", 0.5),

        /**
         * Zoom 100%.
         */
        ZOOM_100("100%", 1.0),

        /**
         * Zoom 125%.
         */
        ZOOM_125("125%", 1.25),

        /**
         * Zoom 200%.
         */
        ZOOM_200("200%", 2.0),

        /**
         * Zoom 400%.
         */
        ZOOM_400("400%", 4.0);

        private final String level;

        private final double amount;

        private ZoomLevel(final String level, final double amount) {
            this.level = level;
            this.amount = amount;
        }

        /**
         * Returns the level.
         * 
         * @return The level.
         */
        public String getLevel() {
            return level;
        }

        /**
         * Returns the amount.
         * 
         * @return The amount.
         */
        public double getAmount() {
            return amount;
        }

        /**
         * Returns the {@link ZoomLevel} corresponding to the next zoom in (e.g.
         * 2.0 if current zoom level is >= 1.25 and < to 2).
         * 
         * @param currentZoomLevel
         *            the current zoom level
         * @return the {@link ZoomLevel} corresponding to the next zoom in (e.g.
         *         2.0 if current zoom level is >= 1.25 and < to 2)
         */
        public static ZoomLevel createNextZoomInLevel(double currentZoomLevel) {
            ZoomLevel nextZoomInLevel = ZOOM_100;
            if (currentZoomLevel >= 2) {
                nextZoomInLevel = ZOOM_400;
            }
            if (currentZoomLevel >= 1.25) {
                nextZoomInLevel = ZOOM_200;
            } else if (currentZoomLevel >= 1.0) {
                nextZoomInLevel = ZOOM_125;
            }
            return nextZoomInLevel;
        }

        /**
         * Returns the {@link ZoomLevel} corresponding to the next zoom out
         * (e.g. 1.25 if current zoom level is <= 2 and > to 1.25).
         * 
         * @param currentZoomLevel
         *            the current zoom level
         * @return the {@link ZoomLevel} corresponding to the next zoom zoom out
         *         (e.g. 1.25 if current zoom level is <= 2 and > to 1.25)
         */
        public static ZoomLevel createNextZoomOutLevel(double currentZoomLevel) {
            ZoomLevel nextZoomOutLevel = ZOOM_200;
            if (currentZoomLevel <= 1.0) {
                nextZoomOutLevel = ZOOM_50;
            } else if (currentZoomLevel <= 1.25) {
                nextZoomOutLevel = ZOOM_100;
            } else if (currentZoomLevel <= 2.0) {
                nextZoomOutLevel = ZOOM_125;
            }
            return nextZoomOutLevel;
        }
    }

    /**
     * The default zoom level.
     */
    public static final ZoomLevel ZOOM_DEFAULT = ZoomLevel.ZOOM_100;

    /**
     * Constructor.
     * 
     * @param treeItem
     *            Tree item.
     * 
     * @param representationName
     *            Representation name.
     */
    public UIDiagramRepresentation(final SWTBotTreeItem treeItem, final String representationName) {
        super(treeItem, representationName);
    }

    /**
     * Open current representation. Does nothing if current diagram was created
     * in this test session instead of being simply opened.
     * 
     * @return Current representation.
     */
    public UIDiagramRepresentation open() {
        super.doOpen();
        return this;
    }

    /**
     * save current representation.
     * 
     * @return Current representation.
     */

    public UIDiagramRepresentation save() {
        super.doSave();
        return this;
    }

    /**
     * Zoom to next zoom value.
     * 
     * @return Current representation.
     */
    public UIDiagramRepresentation zoomDefault() {
        return zoom(UIDiagramRepresentation.ZOOM_DEFAULT);
    }

    /**
     * Zoom to next zoom value.
     * 
     * @param zoomLevel
     *            Zoom level
     * 
     * @return Current representation.
     */
    public UIDiagramRepresentation zoom(final ZoomLevel zoomLevel) {
        getEditor().zoom(zoomLevel);
        return this;
    }

    /**
     * .
     * 
     * @param layerName
     *            .
     * @return .
     */
    public UIDiagramRepresentation changeLayerActivation(final String layerName) {

        if (useTabbar()) {
            SWTBotToolbarDropDownButton button = designerBot.toolbarDropDownButtonWithTooltip("Layers");
            Matcher<MenuItem> withLayerName = WidgetMatcherFactory.withText(layerName);
            SWTBotMenu layerButton = button.menuItem(withLayerName);
            layerButton.click();
        } else {
            DesignerViews designerViews = new DesignerViews(designerBot);
            final SiriusOutlineView outlineView = designerViews.getOutlineView().layers();
            outlineView.activateLayer(layerName);
        }

        return this;
    }

    private boolean useTabbar() {
        IPreferencesService prefs = Platform.getPreferencesService();
        return !prefs.getBoolean(DiagramPlugin.ID, SiriusDiagramPreferencesKeys.PREF_OLD_UI.name(), false, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SWTBotDesignerEditor getEditor() {
        return SWTBotDesignerHelper.getDesignerEditorContainingName(getRepresentationName());
    }
}