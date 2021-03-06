/*******************************************************************************
 * Copyright (c) 2010, 2013 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.sample.interactions;

/**
 * <!-- begin-user-doc --> A representation of the model object '
 * <em><b>State End</b></em>'. <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.sirius.sample.interactions.StateEnd#getState <em>State
 * </em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.sirius.sample.interactions.InteractionsPackage#getStateEnd()
 * @model
 * @generated
 */
public interface StateEnd extends AbstractEnd {
    /**
     * Returns the value of the '<em><b>State</b></em>' reference. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>State</em>' reference isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>State</em>' reference.
     * @see #setState(State)
     * @see org.eclipse.sirius.sample.interactions.InteractionsPackage#getStateEnd_State()
     * @model required="true"
     * @generated
     */
    State getState();

    /**
     * Sets the value of the '
     * {@link org.eclipse.sirius.sample.interactions.StateEnd#getState
     * <em>State</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value
     *            the new value of the '<em>State</em>' reference.
     * @see #getState()
     * @generated
     */
    void setState(State value);

} // StateEnd
