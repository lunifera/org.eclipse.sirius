/**
 * Copyright (c) 2012, 2014 THALES GLOBAL SERVICES
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Obeo - Initial API and implementation
 */
package org.eclipse.sirius.tests.swtbot.support.api.widget;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefFigureCanvas;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;

/**
 * Specific FigureCanvas to:
 * <UL>
 * <LI>override mouseMoveLeftClick method to avoid to pass SWT.BUTTON1 in mouse
 * button down event. Indeed this disable the direct edit because the test
 * getCurrentInput().getModifiers() == 0 fails in SelectEditPartTracker l.168
 * (NoCopyDragEditPartsTrackerEx.performConditionalSelection)</LI>
 * <LI>add typeSuffixText method to allow to edit by adding text at the end of
 * the existing one.</LI>
 * <UL>
 * 
 * @author lredor
 */
public class SWTBotSiriusFigureCanvas extends SWTBotGefFigureCanvas {

    /**
     * Constructs a new instance from a {@link FigureCanvas}.
     * 
     * @param canvas
     *            the canvas to wrap
     * @throws WidgetNotFoundException
     *             if the widget is <code>null</code> or widget has been
     *             disposed.
     */
    public SWTBotSiriusFigureCanvas(FigureCanvas canvas) throws WidgetNotFoundException {
        super(canvas);
    }

    /**
     * Constructs a new instance from a {@link Canvas} and a
     * {@link LightweightSystem}. If the canvas is an instance of
     * {@link FigureCanvas}, use
     * {@link SWTBotGefFigureCanvas#SWTBotGefFigureCanvas(FigureCanvas)}
     * instead.
     * 
     * @param canvas
     *            the canvas to wrap
     * @param lightweightSystem
     *            the lightweight system to use
     * @throws WidgetNotFoundException
     *             if the widget is <code>null</code> or widget has been
     *             disposed.
     */
    public SWTBotSiriusFigureCanvas(Canvas canvas, LightweightSystem lightweightSystem) throws WidgetNotFoundException {
        super(canvas, lightweightSystem);
    }

    /**
     * Duplicate : Only SWT.None is passed instead of SWT.BUTTON1 in mouse down
     * event.
     * 
     * @param xPosition
     *            the relative x position
     * @param yPosition
     *            the relative y position
     * 
     * @see org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefFigureCanvas#mouseMoveLeftClick(int,
     *      int)
     */
    @Override
    public void mouseMoveLeftClick(final int xPosition, final int yPosition) {
        UIThreadRunnable.asyncExec(new VoidResult() {
            @Override
            public void run() {
                org.eclipse.swt.events.MouseEvent meMove = wrapMouseEvent(xPosition, yPosition, 0, 0, 0);
                eventDispatcher.dispatchMouseMoved(meMove);
                org.eclipse.swt.events.MouseEvent meDown = wrapMouseEvent(xPosition, yPosition, 1, SWT.None, 1);
                eventDispatcher.dispatchMousePressed(meDown);
                org.eclipse.swt.events.MouseEvent meUp = wrapMouseEvent(xPosition, yPosition, 1, SWT.BUTTON1, 1);
                eventDispatcher.dispatchMouseReleased(meUp);
            }
        });
    }

    private org.eclipse.swt.events.MouseEvent wrapMouseEvent(int x, int y, int button, int stateMask, int count) {
        return new org.eclipse.swt.events.MouseEvent(createMouseEvent(x, y, button, stateMask, count));
    }

    /**
     * Type the given text at the end of the given textControl.
     * 
     * @param textControl
     *            The Text field to modify
     * @param text
     *            The suffix to add
     */
    public void typeSuffixText(final Text textControl, final String text) {
        for (int x = 0; x < text.length(); ++x) {
            final char c = text.charAt(x);
            UIThreadRunnable.syncExec(new VoidResult() {
                @Override
                public void run() {
                    textControl.setFocus();
                    textControl.notifyListeners(SWT.KeyDown, keyEvent(SWT.NONE, c, 0));
                    textControl.notifyListeners(SWT.KeyUp, keyEvent(SWT.NONE, c, 0));
                    textControl.setText(textControl.getText() + c);
                }
            });
            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) {
                // Do nothing
            }
        }

        // apply the value with a default selection event
        UIThreadRunnable.syncExec(new VoidResult() {
            @Override
            public void run() {
                textControl.setFocus();
                textControl.notifyListeners(SWT.DefaultSelection, createEvent());
            }
        });
    }

    private Event keyEvent(int modificationKey, char c, int keyCode) {
        Event keyEvent = createEvent();
        keyEvent.stateMask = modificationKey;
        keyEvent.character = c;
        keyEvent.keyCode = keyCode;
        return keyEvent;
    }
}
