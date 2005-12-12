/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.core;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.CompatibleEnvironment;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.jdt.launching.environments.IExecutionEnvironmentAnalyzerDelegate;
import org.eclipse.jdt.launching.environments.IExecutionEnvironmentsManager;

public class ExecutionEnvironmentAnalyzer implements IExecutionEnvironmentAnalyzerDelegate {
	
	public static final String J2SE_1_5 = "J2SE-1.5"; //$NON-NLS-1$
	public static final String J2SE_1_4 = "J2SE-1.4"; //$NON-NLS-1$
	public static final String J2SE_1_3 = "J2SE-1.3"; //$NON-NLS-1$
	public static final String J2SE_1_2 = "J2SE-1.2"; //$NON-NLS-1$
	public static final String JRE_1_1 = "JRE-1.1"; //$NON-NLS-1$
	
	public static final String CDC_FOUNDATION_1_1 = "CDC-1.1/Foundation-1.1"; //$NON-NLS-1$
	public static final String CDC_FOUNDATION_1_0 = "CDC-1.0/Foundation-1.0"; //$NON-NLS-1$
	
	public static final String OSGI_MINIMUM_1_0 = "OSGi/Minimum-1.0"; //$NON-NLS-1$
	public static final String OSGI_MINIMUM_1_1 = "OSGi/Minimum-1.1"; //$NON-NLS-1$
	
	public static String[] getKnownExecutionEnvironments() {
		return new String[] {
				JRE_1_1,
				J2SE_1_2,
				OSGI_MINIMUM_1_0,
				OSGI_MINIMUM_1_1,
				CDC_FOUNDATION_1_0,
				J2SE_1_3,
				CDC_FOUNDATION_1_1,
				J2SE_1_4,
				J2SE_1_5};
	}

	public CompatibleEnvironment[] analyze(IVMInstall vm, IProgressMonitor monitor)
			throws CoreException {
		
		ArrayList result = new ArrayList();

		if (vm instanceof IVMInstall2) {
			IVMInstall2 vm2 = (IVMInstall2) vm;
			
			String javaVersion = vm2.getJavaVersion();
			if (javaVersion == null)
				return new CompatibleEnvironment[0];
			
			if (javaVersion.compareTo("1.5") >= 0) //$NON-NLS-1$
				addEnvironment(result, J2SE_1_5, javaVersion.startsWith("1.5")); //$NON-NLS-1$
			
			if (javaVersion.compareTo("1.4") >= 0) { //$NON-NLS-1$
				addEnvironment(result, J2SE_1_4, javaVersion.startsWith("1.4")); //$NON-NLS-1$
				addEnvironment(result, CDC_FOUNDATION_1_1, false); 
			}
			
			if (javaVersion.compareTo("1.3") >= 0) { //$NON-NLS-1$
				addEnvironment(result, J2SE_1_3, javaVersion.startsWith("1.3")); //$NON-NLS-1$
				addEnvironment(result, CDC_FOUNDATION_1_0, false); 
				addEnvironment(result, OSGI_MINIMUM_1_1, false);
				addEnvironment(result, OSGI_MINIMUM_1_0, false);
			}
			
			if (javaVersion.compareTo("1.2") >= 0) //$NON-NLS-1$
				addEnvironment(result, J2SE_1_2, javaVersion.startsWith("1.2")); //$NON-NLS-1$
			
			if (javaVersion.compareTo("1.1") >= 0)  //$NON-NLS-1$
				addEnvironment(result, JRE_1_1, javaVersion.startsWith("1.1"));				 //$NON-NLS-1$
		}
		return (CompatibleEnvironment[])result.toArray(new CompatibleEnvironment[result.size()]);
	}
	
	private void addEnvironment(ArrayList result, String id, boolean strict) {
		IExecutionEnvironmentsManager manager = JavaRuntime.getExecutionEnvironmentsManager();
		IExecutionEnvironment env = manager.getEnvironment(id);
		if (env != null)
			result.add(new CompatibleEnvironment(env, strict));
	}

}
