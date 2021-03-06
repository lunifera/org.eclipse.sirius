/*******************************************************************************
 * Copyright (c) 2015 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.common.acceleo.aql.business.internal;

import org.eclipse.sirius.common.acceleo.aql.business.api.AQLConstants;
import org.eclipse.sirius.common.tools.api.interpreter.IInterpreter;
import org.eclipse.sirius.common.tools.api.interpreter.IInterpreterProvider;

/**
 * This will be used in order to provides interpreters for Acceleo Query
 * Language expressions to the compound interpreter.
 * 
 * @author <a href="mailto:cedric.brun@obeo.fr">Cedric Brun</a>
 */
public class AcceleoAQLInterpreterProvider implements IInterpreterProvider {

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.common.tools.api.interpreter.IInterpreterProvider#createInterpreter()
     */
    public IInterpreter createInterpreter() {
        return new AQLSiriusInterpreter();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.common.tools.api.interpreter.IInterpreterProvider#provides(java.lang.String)
     */
    public boolean provides(String expression) {
        if (expression != null) {
            return expression.startsWith(AQLConstants.AQL_PREFIX);
        }
        return false;
    }
}
