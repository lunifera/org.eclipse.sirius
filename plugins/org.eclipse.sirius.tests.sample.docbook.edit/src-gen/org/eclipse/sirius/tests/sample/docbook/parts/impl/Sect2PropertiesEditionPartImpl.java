/**
 * Copyright (c) 2015 Obeo
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *
 */
package org.eclipse.sirius.tests.sample.docbook.parts.impl;

// Start of user code for imports
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.eef.runtime.api.component.IPropertiesEditionComponent;
import org.eclipse.emf.eef.runtime.api.notify.IPropertiesEditionEvent;
import org.eclipse.emf.eef.runtime.api.parts.ISWTPropertiesEditionPart;
import org.eclipse.emf.eef.runtime.impl.notify.PropertiesEditionEvent;
import org.eclipse.emf.eef.runtime.impl.parts.CompositePropertiesEditionPart;
import org.eclipse.emf.eef.runtime.ui.parts.PartComposer;
import org.eclipse.emf.eef.runtime.ui.parts.sequence.BindingCompositionSequence;
import org.eclipse.emf.eef.runtime.ui.parts.sequence.CompositionSequence;
import org.eclipse.emf.eef.runtime.ui.parts.sequence.CompositionStep;
import org.eclipse.emf.eef.runtime.ui.utils.EditingUtils;
import org.eclipse.emf.eef.runtime.ui.widgets.ReferencesTable;
import org.eclipse.emf.eef.runtime.ui.widgets.ReferencesTable.ReferencesTableListener;
import org.eclipse.emf.eef.runtime.ui.widgets.SWTUtils;
import org.eclipse.emf.eef.runtime.ui.widgets.referencestable.ReferencesTableContentProvider;
import org.eclipse.emf.eef.runtime.ui.widgets.referencestable.ReferencesTableSettings;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.sirius.tests.sample.docbook.parts.DocbookViewsRepository;
import org.eclipse.sirius.tests.sample.docbook.parts.Sect2PropertiesEditionPart;
import org.eclipse.sirius.tests.sample.docbook.providers.DocbookMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

// End of user code

/**
 *
 *
 */
public class Sect2PropertiesEditionPartImpl extends CompositePropertiesEditionPart implements ISWTPropertiesEditionPart, Sect2PropertiesEditionPart {

    protected ReferencesTable para;

    protected List<ViewerFilter> paraBusinessFilters = new ArrayList<ViewerFilter>();

    protected List<ViewerFilter> paraFilters = new ArrayList<ViewerFilter>();

    protected Text id;

    protected ReferencesTable sect3;

    protected List<ViewerFilter> sect3BusinessFilters = new ArrayList<ViewerFilter>();

    protected List<ViewerFilter> sect3Filters = new ArrayList<ViewerFilter>();

    /**
     * Default constructor
     * 
     * @param editionComponent
     *            the {@link IPropertiesEditionComponent} that manage this part
     * 
     */
    public Sect2PropertiesEditionPartImpl(IPropertiesEditionComponent editionComponent) {
        super(editionComponent);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.eef.runtime.api.parts.ISWTPropertiesEditionPart#
     *      createFigure(org.eclipse.swt.widgets.Composite)
     * 
     */
    @Override
    public Composite createFigure(final Composite parent) {
        view = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        view.setLayout(layout);
        createControls(view);
        return view;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.eef.runtime.api.parts.ISWTPropertiesEditionPart#
     *      createControls(org.eclipse.swt.widgets.Composite)
     * 
     */
    @Override
    public void createControls(Composite view) {
        CompositionSequence sect2Step = new BindingCompositionSequence(propertiesEditionComponent);
        CompositionStep propertiesStep = sect2Step.addStep(DocbookViewsRepository.Sect2.Properties.class);
        propertiesStep.addStep(DocbookViewsRepository.Sect2.Properties.para);
        propertiesStep.addStep(DocbookViewsRepository.Sect2.Properties.id);
        propertiesStep.addStep(DocbookViewsRepository.Sect2.Properties.sect3);

        composer = new PartComposer(sect2Step) {

            @Override
            public Composite addToPart(Composite parent, Object key) {
                if (key == DocbookViewsRepository.Sect2.Properties.class) {
                    return createPropertiesGroup(parent);
                }
                if (key == DocbookViewsRepository.Sect2.Properties.para) {
                    return createParaAdvancedTableComposition(parent);
                }
                if (key == DocbookViewsRepository.Sect2.Properties.id) {
                    return createIdText(parent);
                }
                if (key == DocbookViewsRepository.Sect2.Properties.sect3) {
                    return createSect3AdvancedTableComposition(parent);
                }
                return parent;
            }
        };
        composer.compose(view);
    }

    /**
     *
     */
    protected Composite createPropertiesGroup(Composite parent) {
        Group propertiesGroup = new Group(parent, SWT.NONE);
        propertiesGroup.setText(DocbookMessages.Sect2PropertiesEditionPart_PropertiesGroupLabel);
        GridData propertiesGroupData = new GridData(GridData.FILL_HORIZONTAL);
        propertiesGroupData.horizontalSpan = 3;
        propertiesGroup.setLayoutData(propertiesGroupData);
        GridLayout propertiesGroupLayout = new GridLayout();
        propertiesGroupLayout.numColumns = 3;
        propertiesGroup.setLayout(propertiesGroupLayout);
        return propertiesGroup;
    }

    /**
     * @param container
     * 
     */
    protected Composite createParaAdvancedTableComposition(Composite parent) {
        this.para = new ReferencesTable(getDescription(DocbookViewsRepository.Sect2.Properties.para, DocbookMessages.Sect2PropertiesEditionPart_ParaLabel), new ReferencesTableListener() {
            @Override
            public void handleAdd() {
                propertiesEditionComponent.firePropertiesChanged(new PropertiesEditionEvent(Sect2PropertiesEditionPartImpl.this, DocbookViewsRepository.Sect2.Properties.para,
                        PropertiesEditionEvent.COMMIT, PropertiesEditionEvent.ADD, null, null));
                para.refresh();
            }

            @Override
            public void handleEdit(EObject element) {
                propertiesEditionComponent.firePropertiesChanged(new PropertiesEditionEvent(Sect2PropertiesEditionPartImpl.this, DocbookViewsRepository.Sect2.Properties.para,
                        PropertiesEditionEvent.COMMIT, PropertiesEditionEvent.EDIT, null, element));
                para.refresh();
            }

            @Override
            public void handleMove(EObject element, int oldIndex, int newIndex) {
                propertiesEditionComponent.firePropertiesChanged(new PropertiesEditionEvent(Sect2PropertiesEditionPartImpl.this, DocbookViewsRepository.Sect2.Properties.para,
                        PropertiesEditionEvent.COMMIT, PropertiesEditionEvent.MOVE, element, newIndex));
                para.refresh();
            }

            @Override
            public void handleRemove(EObject element) {
                propertiesEditionComponent.firePropertiesChanged(new PropertiesEditionEvent(Sect2PropertiesEditionPartImpl.this, DocbookViewsRepository.Sect2.Properties.para,
                        PropertiesEditionEvent.COMMIT, PropertiesEditionEvent.REMOVE, null, element));
                para.refresh();
            }

            @Override
            public void navigateTo(EObject element) {
            }
        });
        for (ViewerFilter filter : this.paraFilters) {
            this.para.addFilter(filter);
        }
        this.para.setHelpText(propertiesEditionComponent.getHelpContent(DocbookViewsRepository.Sect2.Properties.para, DocbookViewsRepository.SWT_KIND));
        this.para.createControls(parent);
        this.para.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (e.item != null && e.item.getData() instanceof EObject) {
                    propertiesEditionComponent.firePropertiesChanged(new PropertiesEditionEvent(Sect2PropertiesEditionPartImpl.this, DocbookViewsRepository.Sect2.Properties.para,
                            PropertiesEditionEvent.CHANGE, PropertiesEditionEvent.SELECTION_CHANGED, null, e.item.getData()));
                }
            }

        });
        GridData paraData = new GridData(GridData.FILL_HORIZONTAL);
        paraData.horizontalSpan = 3;
        this.para.setLayoutData(paraData);
        this.para.setLowerBound(0);
        this.para.setUpperBound(-1);
        para.setID(DocbookViewsRepository.Sect2.Properties.para);
        para.setEEFType("eef::AdvancedTableComposition"); //$NON-NLS-1$
        // Start of user code for createParaAdvancedTableComposition

        // End of user code
        return parent;
    }

    protected Composite createIdText(Composite parent) {
        createDescription(parent, DocbookViewsRepository.Sect2.Properties.id, DocbookMessages.Sect2PropertiesEditionPart_IdLabel);
        id = SWTUtils.createScrollableText(parent, SWT.BORDER);
        GridData idData = new GridData(GridData.FILL_HORIZONTAL);
        id.setLayoutData(idData);
        id.addFocusListener(new FocusAdapter() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.FocusAdapter#focusLost(org.eclipse.swt.events.FocusEvent)
             * 
             */
            @Override
            @SuppressWarnings("synthetic-access")
            public void focusLost(FocusEvent e) {
                if (propertiesEditionComponent != null) {
                    propertiesEditionComponent.firePropertiesChanged(new PropertiesEditionEvent(Sect2PropertiesEditionPartImpl.this, DocbookViewsRepository.Sect2.Properties.id,
                            PropertiesEditionEvent.COMMIT, PropertiesEditionEvent.SET, null, id.getText()));
                }
            }

        });
        id.addKeyListener(new KeyAdapter() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.KeyAdapter#keyPressed(org.eclipse.swt.events.KeyEvent)
             * 
             */
            @Override
            @SuppressWarnings("synthetic-access")
            public void keyPressed(KeyEvent e) {
                if (e.character == SWT.CR) {
                    if (propertiesEditionComponent != null) {
                        propertiesEditionComponent.firePropertiesChanged(new PropertiesEditionEvent(Sect2PropertiesEditionPartImpl.this, DocbookViewsRepository.Sect2.Properties.id,
                                PropertiesEditionEvent.COMMIT, PropertiesEditionEvent.SET, null, id.getText()));
                    }
                }
            }

        });
        EditingUtils.setID(id, DocbookViewsRepository.Sect2.Properties.id);
        EditingUtils.setEEFtype(id, "eef::Text"); //$NON-NLS-1$
        SWTUtils.createHelpButton(parent, propertiesEditionComponent.getHelpContent(DocbookViewsRepository.Sect2.Properties.id, DocbookViewsRepository.SWT_KIND), null);
        // Start of user code for createIdText

        // End of user code
        return parent;
    }

    /**
     * @param container
     * 
     */
    protected Composite createSect3AdvancedTableComposition(Composite parent) {
        this.sect3 = new ReferencesTable(getDescription(DocbookViewsRepository.Sect2.Properties.sect3, DocbookMessages.Sect2PropertiesEditionPart_Sect3Label), new ReferencesTableListener() {
            @Override
            public void handleAdd() {
                propertiesEditionComponent.firePropertiesChanged(new PropertiesEditionEvent(Sect2PropertiesEditionPartImpl.this, DocbookViewsRepository.Sect2.Properties.sect3,
                        PropertiesEditionEvent.COMMIT, PropertiesEditionEvent.ADD, null, null));
                sect3.refresh();
            }

            @Override
            public void handleEdit(EObject element) {
                propertiesEditionComponent.firePropertiesChanged(new PropertiesEditionEvent(Sect2PropertiesEditionPartImpl.this, DocbookViewsRepository.Sect2.Properties.sect3,
                        PropertiesEditionEvent.COMMIT, PropertiesEditionEvent.EDIT, null, element));
                sect3.refresh();
            }

            @Override
            public void handleMove(EObject element, int oldIndex, int newIndex) {
                propertiesEditionComponent.firePropertiesChanged(new PropertiesEditionEvent(Sect2PropertiesEditionPartImpl.this, DocbookViewsRepository.Sect2.Properties.sect3,
                        PropertiesEditionEvent.COMMIT, PropertiesEditionEvent.MOVE, element, newIndex));
                sect3.refresh();
            }

            @Override
            public void handleRemove(EObject element) {
                propertiesEditionComponent.firePropertiesChanged(new PropertiesEditionEvent(Sect2PropertiesEditionPartImpl.this, DocbookViewsRepository.Sect2.Properties.sect3,
                        PropertiesEditionEvent.COMMIT, PropertiesEditionEvent.REMOVE, null, element));
                sect3.refresh();
            }

            @Override
            public void navigateTo(EObject element) {
            }
        });
        for (ViewerFilter filter : this.sect3Filters) {
            this.sect3.addFilter(filter);
        }
        this.sect3.setHelpText(propertiesEditionComponent.getHelpContent(DocbookViewsRepository.Sect2.Properties.sect3, DocbookViewsRepository.SWT_KIND));
        this.sect3.createControls(parent);
        this.sect3.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (e.item != null && e.item.getData() instanceof EObject) {
                    propertiesEditionComponent.firePropertiesChanged(new PropertiesEditionEvent(Sect2PropertiesEditionPartImpl.this, DocbookViewsRepository.Sect2.Properties.sect3,
                            PropertiesEditionEvent.CHANGE, PropertiesEditionEvent.SELECTION_CHANGED, null, e.item.getData()));
                }
            }

        });
        GridData sect3Data = new GridData(GridData.FILL_HORIZONTAL);
        sect3Data.horizontalSpan = 3;
        this.sect3.setLayoutData(sect3Data);
        this.sect3.setLowerBound(0);
        this.sect3.setUpperBound(-1);
        sect3.setID(DocbookViewsRepository.Sect2.Properties.sect3);
        sect3.setEEFType("eef::AdvancedTableComposition"); //$NON-NLS-1$
        // Start of user code for createSect3AdvancedTableComposition

        // End of user code
        return parent;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.eef.runtime.api.notify.IPropertiesEditionListener#firePropertiesChanged(org.eclipse.emf.eef.runtime.api.notify.IPropertiesEditionEvent)
     * 
     */
    @Override
    public void firePropertiesChanged(IPropertiesEditionEvent event) {
        // Start of user code for tab synchronization

        // End of user code
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tests.sample.docbook.parts.Sect2PropertiesEditionPart#initPara(EObject
     *      current, EReference containingFeature, EReference feature)
     */
    @Override
    public void initPara(ReferencesTableSettings settings) {
        if (current.eResource() != null && current.eResource().getResourceSet() != null) {
            this.resourceSet = current.eResource().getResourceSet();
        }
        ReferencesTableContentProvider contentProvider = new ReferencesTableContentProvider();
        para.setContentProvider(contentProvider);
        para.setInput(settings);
        boolean eefElementEditorReadOnlyState = isReadOnly(DocbookViewsRepository.Sect2.Properties.para);
        if (eefElementEditorReadOnlyState && para.isEnabled()) {
            para.setEnabled(false);
            para.setToolTipText(DocbookMessages.Sect2_ReadOnly);
        } else if (!eefElementEditorReadOnlyState && !para.isEnabled()) {
            para.setEnabled(true);
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tests.sample.docbook.parts.Sect2PropertiesEditionPart#updatePara()
     * 
     */
    @Override
    public void updatePara() {
        para.refresh();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tests.sample.docbook.parts.Sect2PropertiesEditionPart#addFilterPara(ViewerFilter
     *      filter)
     * 
     */
    @Override
    public void addFilterToPara(ViewerFilter filter) {
        paraFilters.add(filter);
        if (this.para != null) {
            this.para.addFilter(filter);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tests.sample.docbook.parts.Sect2PropertiesEditionPart#addBusinessFilterPara(ViewerFilter
     *      filter)
     * 
     */
    @Override
    public void addBusinessFilterToPara(ViewerFilter filter) {
        paraBusinessFilters.add(filter);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tests.sample.docbook.parts.Sect2PropertiesEditionPart#isContainedInParaTable(EObject
     *      element)
     * 
     */
    @Override
    public boolean isContainedInParaTable(EObject element) {
        return ((ReferencesTableSettings) para.getInput()).contains(element);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tests.sample.docbook.parts.Sect2PropertiesEditionPart#getId()
     * 
     */
    @Override
    public String getId() {
        return id.getText();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tests.sample.docbook.parts.Sect2PropertiesEditionPart#setId(String
     *      newValue)
     * 
     */
    @Override
    public void setId(String newValue) {
        if (newValue != null) {
            id.setText(newValue);
        } else {
            id.setText(""); //$NON-NLS-1$
        }
        boolean eefElementEditorReadOnlyState = isReadOnly(DocbookViewsRepository.Sect2.Properties.id);
        if (eefElementEditorReadOnlyState && id.isEnabled()) {
            id.setEnabled(false);
            id.setToolTipText(DocbookMessages.Sect2_ReadOnly);
        } else if (!eefElementEditorReadOnlyState && !id.isEnabled()) {
            id.setEnabled(true);
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tests.sample.docbook.parts.Sect2PropertiesEditionPart#initSect3(EObject
     *      current, EReference containingFeature, EReference feature)
     */
    @Override
    public void initSect3(ReferencesTableSettings settings) {
        if (current.eResource() != null && current.eResource().getResourceSet() != null) {
            this.resourceSet = current.eResource().getResourceSet();
        }
        ReferencesTableContentProvider contentProvider = new ReferencesTableContentProvider();
        sect3.setContentProvider(contentProvider);
        sect3.setInput(settings);
        boolean eefElementEditorReadOnlyState = isReadOnly(DocbookViewsRepository.Sect2.Properties.sect3);
        if (eefElementEditorReadOnlyState && sect3.isEnabled()) {
            sect3.setEnabled(false);
            sect3.setToolTipText(DocbookMessages.Sect2_ReadOnly);
        } else if (!eefElementEditorReadOnlyState && !sect3.isEnabled()) {
            sect3.setEnabled(true);
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tests.sample.docbook.parts.Sect2PropertiesEditionPart#updateSect3()
     * 
     */
    @Override
    public void updateSect3() {
        sect3.refresh();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tests.sample.docbook.parts.Sect2PropertiesEditionPart#addFilterSect3(ViewerFilter
     *      filter)
     * 
     */
    @Override
    public void addFilterToSect3(ViewerFilter filter) {
        sect3Filters.add(filter);
        if (this.sect3 != null) {
            this.sect3.addFilter(filter);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tests.sample.docbook.parts.Sect2PropertiesEditionPart#addBusinessFilterSect3(ViewerFilter
     *      filter)
     * 
     */
    @Override
    public void addBusinessFilterToSect3(ViewerFilter filter) {
        sect3BusinessFilters.add(filter);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tests.sample.docbook.parts.Sect2PropertiesEditionPart#isContainedInSect3Table(EObject
     *      element)
     * 
     */
    @Override
    public boolean isContainedInSect3Table(EObject element) {
        return ((ReferencesTableSettings) sect3.getInput()).contains(element);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.emf.eef.runtime.api.parts.IPropertiesEditionPart#getTitle()
     * 
     */
    @Override
    public String getTitle() {
        return DocbookMessages.Sect2_Part_Title;
    }

    // Start of user code additional methods

    // End of user code

}
