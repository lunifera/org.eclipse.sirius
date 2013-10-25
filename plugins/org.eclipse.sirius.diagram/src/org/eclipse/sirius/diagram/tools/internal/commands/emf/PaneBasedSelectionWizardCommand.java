/*******************************************************************************
 * Copyright (c) 2008, 2009 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.diagram.tools.internal.commands.emf;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.sirius.common.tools.api.interpreter.IInterpreter;
import org.eclipse.sirius.common.tools.api.interpreter.IInterpreterSiriusVariables;
import org.eclipse.sirius.common.tools.api.util.StringUtil;
import org.eclipse.sirius.common.tools.api.util.TreeItemWrapper;
import org.eclipse.sirius.common.ui.tools.api.selection.EObjectPaneBasedSelectionWizard;
import org.eclipse.sirius.business.api.helper.SiriusUtil;
import org.eclipse.sirius.business.api.logger.RuntimeLoggerManager;
import org.eclipse.sirius.diagram.business.api.view.SiriusLayoutDataManager;
import org.eclipse.sirius.diagram.part.SiriusDiagramEditorPlugin;
import org.eclipse.sirius.tools.api.command.IDiagramCommandFactory;
import org.eclipse.sirius.viewpoint.AbstractDNode;
import org.eclipse.sirius.viewpoint.DSemanticDecorator;
import org.eclipse.sirius.viewpoint.SiriusPlugin;
import org.eclipse.sirius.viewpoint.description.tool.PaneBasedSelectionWizardDescription;
import org.eclipse.sirius.viewpoint.description.tool.ToolPackage;

/**
 * A command to display a selection wizard.
 * 
 * @author mchauvin
 */
public class PaneBasedSelectionWizardCommand extends AbstractSelectionWizardCommand {

    private final IDiagramCommandFactory factory;

    private final PaneBasedSelectionWizardDescription tool;

    private final TreeItemWrapper input;

    private final DSemanticDecorator containerView;

    /**
     * Default constructor.
     * 
     * @param factory
     *            the command factory.
     * @param tool
     *            the wizard description tool reference.
     * @param input
     *            the candidates objects to select in the wizard.
     * @param containerView
     *            the view of the container.
     */
    public PaneBasedSelectionWizardCommand(final IDiagramCommandFactory factory, final PaneBasedSelectionWizardDescription tool, final TreeItemWrapper input, final DSemanticDecorator containerView) {
        super(TransactionUtil.getEditingDomain(containerView));
        this.factory = factory;
        this.tool = tool;
        this.input = input;
        this.containerView = containerView;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.transaction.RecordingCommand#doExecute()
     */
    @Override
    public void doExecute() {
        computeInput();
        final Collection<EObject> preSelection = computePreSelection();
        final Shell shell = new Shell();
        final EObjectPaneBasedSelectionWizard wizard = new EObjectPaneBasedSelectionWizard(this.tool.getWindowTitle(), this.tool.getMessage(), getImage(), this.tool.getChoiceOfValuesMessage(),
                this.tool.getSelectedValuesMessage(), SiriusDiagramEditorPlugin.getInstance().getItemProvidersAdapterFactory());
        wizard.init(input, preSelection);
        final WizardDialog dlg = new WizardDialog(shell, wizard);
        final int result = dlg.open();
        if (result == Window.OK) {
            final Collection<EObject> selectedElements = wizard.getSelectedEObjects();
            final IInterpreter interpreter = SiriusPlugin.getDefault().getInterpreterRegistry().getInterpreter(containerView.getTarget());
            // variables
            interpreter.setVariable(tool.getContainerView().getName(), containerView);
            interpreter.setVariable(IInterpreterSiriusVariables.DIAGRAM, SiriusUtil.findDiagram(containerView));
            interpreter.setVariable(IInterpreterSiriusVariables.CONTAINER_VIEW, containerView);
            interpreter.setVariable(IInterpreterSiriusVariables.CONTAINER, containerView.getTarget());

            final org.eclipse.emf.common.command.Command command = factory.buildPaneBasedSelectionWizardCommandFromTool(tool, containerView, selectedElements);
            command.execute();

            interpreter.unSetVariable(tool.getContainerView().getName());
            interpreter.unSetVariable(IInterpreterSiriusVariables.DIAGRAM);
            interpreter.unSetVariable(IInterpreterSiriusVariables.CONTAINER_VIEW);
            interpreter.unSetVariable(IInterpreterSiriusVariables.CONTAINER);
        } else {
            if (containerView instanceof AbstractDNode) {
                SiriusLayoutDataManager.INSTANCE.getData((AbstractDNode) containerView);
            }
        }
        shell.dispose();
    }

    private ImageDescriptor getImage() {
        if (StringUtil.isEmpty(tool.getWindowImagePath())) {
            return null;
        } else {
            return SiriusDiagramEditorPlugin.findImageDescriptor(tool.getWindowImagePath());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.common.command.AbstractCommand#getLabel()
     */
    @Override
    public String getLabel() {
        return this.tool.getName();
    }

    private void computeInput() {
        EObject container;
        container = containerView.getTarget();
        final IInterpreter interpreter = SiriusPlugin.getDefault().getInterpreterRegistry().getInterpreter(container);

        if (AbstractSelectionWizardCommand.checkPrecondition(tool, containerView, container)) {

            // variables
            interpreter.setVariable(tool.getContainerView().getName(), containerView);
            interpreter.setVariable(IInterpreterSiriusVariables.DIAGRAM, SiriusUtil.findDiagram(containerView));
            interpreter.setVariable(IInterpreterSiriusVariables.CONTAINER_VIEW, containerView);
            interpreter.setVariable(IInterpreterSiriusVariables.CONTAINER, container);

            PaneBasedSelectionWizardCommand.computeInput(tool, container, interpreter, input);

            interpreter.unSetVariable(tool.getContainerView().getName());
            interpreter.unSetVariable(IInterpreterSiriusVariables.DIAGRAM);
            interpreter.unSetVariable(IInterpreterSiriusVariables.CONTAINER_VIEW);
            interpreter.unSetVariable(IInterpreterSiriusVariables.CONTAINER);

        }

    }

    private Collection<EObject> computePreSelection() {
        EObject container;
        Collection<EObject> preSelection = Collections.<EObject> emptyList();
        container = containerView.getTarget();
        final IInterpreter interpreter = SiriusPlugin.getDefault().getInterpreterRegistry().getInterpreter(container);
        if (AbstractSelectionWizardCommand.checkPrecondition(tool, containerView, container)) {
            // variables
            interpreter.setVariable(tool.getContainerView().getName(), containerView);
            interpreter.setVariable(IInterpreterSiriusVariables.DIAGRAM, SiriusUtil.findDiagram(containerView));
            interpreter.setVariable(IInterpreterSiriusVariables.CONTAINER_VIEW, containerView);
            interpreter.setVariable(IInterpreterSiriusVariables.CONTAINER, container);

            preSelection = RuntimeLoggerManager.INSTANCE.decorate(interpreter).evaluateCollection(container, tool,
                    ToolPackage.eINSTANCE.getPaneBasedSelectionWizardDescription_PreSelectedCandidatesExpression());

            interpreter.unSetVariable(tool.getContainerView().getName());
            interpreter.unSetVariable(IInterpreterSiriusVariables.CONTAINER_VIEW);
            interpreter.unSetVariable(IInterpreterSiriusVariables.CONTAINER);
            interpreter.unSetVariable(IInterpreterSiriusVariables.DIAGRAM);
        }
        return preSelection;
    }

    /**
     * Compute the TreeItemWrapper corresponding to this
     * {@link PaneBasedSelectionWizardDescription}.
     * 
     * @param paneBasedSelectionWizardDescription
     *            the selection description
     * @param container
     *            the semantic element
     * @param interpreter
     *            the interpreter used to compute expressions of the selection
     *            description
     * @param input
     *            the TreeItemWrapper to complete
     */
    private static void computeInput(final PaneBasedSelectionWizardDescription paneBasedSelectionWizardDescription, final EObject container, final IInterpreter interpreter, final TreeItemWrapper input) {

        final Collection<EObject> referencingENode = RuntimeLoggerManager.INSTANCE.decorate(interpreter).evaluateCollection(container, paneBasedSelectionWizardDescription,
                ToolPackage.eINSTANCE.getPaneBasedSelectionWizardDescription_CandidatesExpression());
        if (paneBasedSelectionWizardDescription.isTree()) {
            final Collection<EObject> referencingRoots = RuntimeLoggerManager.INSTANCE.decorate(interpreter).evaluateCollection(container, paneBasedSelectionWizardDescription,
                    ToolPackage.eINSTANCE.getPaneBasedSelectionWizardDescription_RootExpression());
            final Iterator<EObject> iterRoots = referencingRoots.iterator();
            while (iterRoots.hasNext()) {
                final EObject refRoot = iterRoots.next();
                if (referencingENode.contains(refRoot)) {
                    final TreeItemWrapper treeItem = new TreeItemWrapper(refRoot, input);
                    input.getChildren().add(treeItem);
                    PaneBasedSelectionWizardCommand.computeChildren(paneBasedSelectionWizardDescription, referencingENode, interpreter, treeItem, refRoot);
                }
            }
        } else {
            final Iterator<EObject> iterRoots = referencingENode.iterator();
            while (iterRoots.hasNext()) {
                final EObject refRoot = iterRoots.next();
                if (referencingENode.contains(refRoot)) {
                    final TreeItemWrapper treeItem = new TreeItemWrapper(refRoot, input);
                    input.getChildren().add(treeItem);
                }
            }
        }
    }

    private static void computeChildren(final PaneBasedSelectionWizardDescription paneBasedSelectionWizardDescription, final Collection<EObject> referencingENode, final IInterpreter interpreter,
            final TreeItemWrapper parent, final EObject refParent) {
        final Collection<EObject> referencingChilds = RuntimeLoggerManager.INSTANCE.decorate(interpreter).evaluateCollection(refParent, paneBasedSelectionWizardDescription,
                ToolPackage.eINSTANCE.getPaneBasedSelectionWizardDescription_ChildrenExpression());
        final Iterator<EObject> iterchilds = referencingChilds.iterator();
        while (iterchilds.hasNext()) {
            final EObject refElement = iterchilds.next();
            if (referencingENode.contains(refElement) && !parent.knownThisAsAncestor(refElement)) {
                final TreeItemWrapper treeItem = new TreeItemWrapper(refElement, parent);
                parent.getChildren().add(treeItem);
                PaneBasedSelectionWizardCommand.computeChildren(paneBasedSelectionWizardDescription, referencingENode, interpreter, treeItem, refElement);
            }
        }
    }
}