/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.builder.tests.compatibility;

import junit.framework.Test;

import org.eclipse.core.runtime.IPath;

/**
 * Tests that the builder correctly reports compatibility problems
 * for classes.
 * 
 * @since 3.4
 */
public class ClassCompatibilityTests extends CompatibilityTest {

	/**
	 * Constructor
	 * @param name
	 */
	public ClassCompatibilityTests(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.builder.tests.ApiBuilderTests#getTestSourcePath()
	 */
	protected IPath getTestSourcePath() {
		return super.getTestSourcePath().append("class");
	}
	
	/**
	 * @return the tests for this class
	 */
	public static Test suite() {
		return buildTestSuite(ClassCompatibilityTests.class);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.builder.tests.ApiBuilderTest#getDefaultProblemId()
	 */
	protected int getDefaultProblemId() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.builder.tests.ApiBuilderTests#getTestingProjectName()
	 */
	protected String getTestingProjectName() {
		return "classcompat";
	}
}
