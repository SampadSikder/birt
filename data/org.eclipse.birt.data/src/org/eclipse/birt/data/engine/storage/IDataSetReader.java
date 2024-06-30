
/*******************************************************************************
 * Copyright (c) 2004, 2012 Actuate Corporation.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/.
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/
package org.eclipse.birt.data.engine.storage;

import org.eclipse.birt.data.engine.core.DataException;
import org.eclipse.birt.data.engine.impl.document.viewing.IDataSetResultSet;
import org.eclipse.birt.data.engine.impl.index.IOrderedIntSet;

/**
 *
 */

public interface IDataSetReader {
	IDataSetResultSet load(IOrderedIntSet targetRows) throws DataException;

	void close() throws DataException;
}
