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
package org.eclipse.pde.internal.core.search;

import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.pde.core.plugin.*;
import org.eclipse.pde.internal.core.util.*;


public class PluginSearchOperation {
	protected PluginSearchInput input;
	private IPluginSearchResultCollector collector;
	private StringMatcher stringMatcher;
	
	public PluginSearchOperation(
		PluginSearchInput input,
		IPluginSearchResultCollector collector) {
		this.input = input;
		this.collector = collector;
		collector.setOperation(this);
		this.stringMatcher =new StringMatcher(input.getSearchString(),!input.isCaseSensitive(),false);
	}
	
	public void execute(IProgressMonitor monitor) {
		IPluginModelBase[] entries = input.getSearchScope().getMatchingModels();
		collector.searchStarted();
		collector.setProgressMonitor(monitor);
		monitor.beginTask("", entries.length); //$NON-NLS-1$

		try {
			for (int i = 0; i < entries.length; i++) {
				IPluginModelBase candidate = entries[i];
				visit(candidate);
				monitor.worked(1);
			}
		} finally {
			monitor.done();
			collector.done();
		}
	}
	
	private void visit(IPluginModelBase model) {
		ArrayList matches = findMatch(model);
		for (int i = 0; i < matches.size(); i++) {
			collector.accept((IPluginObject)matches.get(i));
		}
	}
	
	private ArrayList findMatch(IPluginModelBase model) {
		ArrayList result = new ArrayList();
		int searchLimit = input.getSearchLimit();
		switch (input.getSearchElement()) {
			case PluginSearchInput.ELEMENT_PLUGIN :
				if (searchLimit != PluginSearchInput.LIMIT_REFERENCES)
					findPluginDeclaration(model, result);
				if (searchLimit != PluginSearchInput.LIMIT_DECLARATIONS)
					findPluginReferences(model, result);
				break;
			case PluginSearchInput.ELEMENT_FRAGMENT :
				findFragmentDeclaration(model, result);
				break;
			case PluginSearchInput.ELEMENT_EXTENSION_POINT :
				if (searchLimit != PluginSearchInput.LIMIT_REFERENCES)
					findExtensionPointDeclarations(model, result);
				if (searchLimit != PluginSearchInput.LIMIT_DECLARATIONS)
					findExtensionPointReferences(model, result);
				break;
		}
		return result;
	}
	
	private void findFragmentDeclaration(
		IPluginModelBase model,
		ArrayList result) {
		IPluginBase pluginBase = model.getPluginBase();
		if (pluginBase instanceof IFragment
			&& stringMatcher.match(pluginBase.getId())) {
			result.add(pluginBase); }
	}
				
	private void findPluginDeclaration(IPluginModelBase model, ArrayList result) {
		IPluginBase pluginBase = model.getPluginBase();
		if (pluginBase instanceof IPlugin && stringMatcher.match(pluginBase.getId()))
			result.add(pluginBase);
	}
	
	private void findPluginReferences(
		IPluginModelBase model,
		ArrayList result) {
		IPluginBase pluginBase = model.getPluginBase();
		if (pluginBase instanceof IFragment) {
			if (stringMatcher.match(((IFragment) pluginBase).getPluginId()))
				result.add(pluginBase);
		}
		IPluginImport[] imports = pluginBase.getImports();
		for (int i = 0; i < imports.length; i++) {
			if (stringMatcher.match(imports[i].getId()))
				result.add(imports[i]);
		}
	}

	private void findExtensionPointDeclarations(
		IPluginModelBase model,
		ArrayList result) {
		IPluginExtensionPoint[] extensionPoints =
			model.getPluginBase().getExtensionPoints();
		for (int i = 0; i < extensionPoints.length; i++) {
			if (stringMatcher.match(extensionPoints[i].getFullId()))
				result.add(extensionPoints[i]);
		}
	}
	
	private void findExtensionPointReferences(IPluginModelBase model, ArrayList result) {
		IPluginExtension[] extensions = model.getPluginBase().getExtensions();
		for (int i = 0; i < extensions.length; i++) {
			if (stringMatcher.match(extensions[i].getPoint()))
				result.add(extensions[i]);
		}
	}
		
}
