/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.core.osgi.bundle;

import java.io.PrintWriter;
import java.util.*;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.pde.core.ModelChangedEvent;
import org.eclipse.pde.core.osgi.bundle.IBundle;
import org.eclipse.pde.core.plugin.*;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.osgi.OSGiWorkspaceModelManager;

public class Bundle extends BundleObject implements IBundle {
	private Hashtable headers;

	public Bundle() {
		headers = new Hashtable();
	}

	public String getHeader(String key) {
		return (String) headers.get(key);
	}

	public void setHeader(String key, String value) throws CoreException {
		ensureModelEditable();
		String oldValue = getHeader(key);
		if (value == null)
			headers.remove(key);
		else
			headers.put(key, value);
		getModel().fireModelChanged(
			new ModelChangedEvent(this, key, oldValue, value));
	}

	public void processHeader(String name, String value) {
		if (value!=null)
			headers.put(name, value);
	}

	public void reset() {
		headers.clear();
	}

	public boolean isValid() {
		// must have an id and a name
		return headers.containsValue(KEY_NAME)
			&& headers.containsValue(KEY_DESC)
			&& headers.containsValue(KEY_VERSION);
	}

	public void load(IPluginBase plugin, IProgressMonitor monitor) {
		reset();
		monitor.beginTask("", 2);
		// migrate from a plug-in
		headers.put(KEY_LEGACY, "true");
		headers.put(KEY_NAME, plugin.getId());
		headers.put(KEY_UNIQUEID, plugin.getId());
		headers.put(KEY_DESC, plugin.getName());
		headers.put(KEY_VENDOR, plugin.getProviderName());
		headers.put(KEY_VERSION, plugin.getVersion());
		if (plugin instanceof IFragment)
			loadFragment((IFragment) plugin);
		else
			loadPlugin((IPlugin) plugin);
		loadLibraries(
			plugin.getLibraries(),
			new SubProgressMonitor(monitor, 1));
		loadImports(plugin.getImports(), new SubProgressMonitor(monitor, 1));
		if (plugin.getModel().getUnderlyingResource() != null)
			loadExports(plugin.getModel().getUnderlyingResource().getProject());
	}

	private void loadPlugin(IPlugin plugin) {
		String pluginClass = plugin.getClassName();
		if (pluginClass != null) {
			headers.put(
				KEY_ACTIVATOR,
				"org.eclipse.core.runtime.compatibility.PluginActivator");
			headers.put(KEY_PLUGIN, pluginClass);
		}
	}

	private void loadFragment(IFragment fragment) {
		FragmentUtil futil = new FragmentUtil(fragment);
		headers.put(KEY_HOST_BUNDLE, futil.getHeader());
	}

	private void loadLibraries(
		IPluginLibrary[] libraries,
		IProgressMonitor monitor) {
		StringBuffer classpath = new StringBuffer();
		//StringBuffer packageExport = new StringBuffer();
		for (int i = 0; i < libraries.length; i++) {
			IPluginLibrary library = libraries[i];
			String name = library.getName();
			if (i > 0)
				classpath.append(",");
			classpath.append(name);
		}
		headers.put(KEY_CLASSPATH, classpath.toString());
	}

	private void loadImports(
		IPluginImport[] imports,
		IProgressMonitor monitor) {
		StringBuffer requires = new StringBuffer();

		for (int i = 0; i < imports.length; i++) {
			IPluginImport iimport = imports[i];
			String id = iimport.getId();
			String version = iimport.getVersion();
			if (i > 0)
				requires.append(", ");
			requires.append(id);
			if (version != null) {
			}
		}
		headers.put(KEY_REQUIRE_BUNDLE, requires.toString());
	}

	private void loadExports(IProject project) {
		if (!OSGiWorkspaceModelManager.isJavaPluginProject(project))
			return;
		IJavaProject javaProject = JavaCore.create(project);
		StringBuffer provides = new StringBuffer();
		int added = 0;
		try {
			IPackageFragmentRoot[] roots =
				javaProject.getPackageFragmentRoots();
			for (int i = 0; i < roots.length; i++) {
				IPackageFragmentRoot root = roots[i];

				if (root.isArchive() && root.isExternal())
					continue;

				IJavaElement[] children = root.getChildren();
				for (int j = 0; j < children.length; j++) {
					IJavaElement child = children[j];
					if (child instanceof IPackageFragment) {
						IPackageFragment packageChild =
							(IPackageFragment) child;
						if (packageChild.containsJavaResources()) {
							String name = packageChild.getElementName();
							if (added > 0)
								provides.append(", ");
							provides.append(name);
							added++;
						}
					}
				}
			}

			if (added > 0)
				headers.put(KEY_PROVIDE_PACKAGE, provides.toString());
		} catch (JavaModelException e) {
			PDECore.logException(e);
		}
	}

	public void write(String indent, PrintWriter writer) {
		for (Enumeration enum = headers.keys(); enum.hasMoreElements();) {
			String key = (String) enum.nextElement();
			String value = (String) headers.get(key);
			if (isCommaSeparated(key)) {
				StringTokenizer stok = new StringTokenizer(value, ",");
				ArrayList list = new ArrayList();
				while (stok.hasMoreTokens()) {
					list.add(stok.nextToken().trim());
				}
				writeEntry(key, list, writer);
			} else {
				writeEntry(key, value, writer);
			}
		}
	}

	private void writeEntry(String key, Collection value, PrintWriter out) {
		if (value == null || value.size() == 0)
			return;
		if (value.size() == 1) {
			out.println(key + ": " + value.iterator().next());
			return;
		}
		key = key + ": ";
		out.println(key);
		out.print(' ');
		boolean first = true;
		for (Iterator i = value.iterator(); i.hasNext();) {
			if (first)
				first = false;
			else {
				out.println(',');
				out.print(' ');
			}
			out.print(i.next());
		}
		out.println();
	}

	private void writeEntry(String key, String value, PrintWriter out) {
		if (value != null && value.length() > 0)
			out.println(key + ": " + value);
	}

	private boolean isCommaSeparated(String key) {
		for (int i = 0; i < COMMA_SEPARATED_KEYS.length; i++) {
			if (COMMA_SEPARATED_KEYS[i].equals(key))
				return true;
		}
		return false;
	}
}