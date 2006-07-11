/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.ui.editor.plugin;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.pde.core.plugin.IPluginLibrary;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.internal.core.ibundle.IBundleModel;
import org.eclipse.pde.internal.core.ibundle.IBundlePluginModelBase;
import org.eclipse.pde.internal.core.ibundle.IManifestHeader;
import org.eclipse.pde.internal.core.plugin.ImportObject;
import org.eclipse.pde.internal.core.text.IDocumentKey;
import org.eclipse.pde.internal.core.text.IDocumentRange;
import org.eclipse.pde.internal.core.text.IEditingModel;
import org.eclipse.pde.internal.core.text.bundle.Bundle;
import org.eclipse.pde.internal.core.text.bundle.BundleModel;
import org.eclipse.pde.internal.core.text.bundle.ExecutionEnvironment;
import org.eclipse.pde.internal.core.text.bundle.ExportPackageObject;
import org.eclipse.pde.internal.core.text.bundle.ImportPackageObject;
import org.eclipse.pde.internal.core.text.bundle.ManifestHeader;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.PDEPluginImages;
import org.eclipse.pde.internal.ui.editor.KeyValueSourcePage;
import org.eclipse.pde.internal.ui.editor.PDEFormEditor;
import org.eclipse.pde.internal.ui.elements.DefaultContentProvider;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Constants;

public class BundleSourcePage extends KeyValueSourcePage {

	class BundleOutlineContentProvider extends DefaultContentProvider
			implements ITreeContentProvider {
		public Object[] getChildren(Object parent) {
			return new Object[0];
		}
		public boolean hasChildren(Object parent) {
			return false;
		}
		public Object getParent(Object child) {
			return null;
		}
		public Object[] getElements(Object parent) {
			if (parent instanceof BundleModel) {
				BundleModel model = (BundleModel) parent;
				Dictionary manifest = ((Bundle)model.getBundle()).getHeaders();
				ArrayList keys = new ArrayList();
				for (Enumeration elements = manifest.keys(); elements.hasMoreElements();) {
					IDocumentKey key = (IDocumentKey) manifest.get(elements.nextElement());
					if (key.getOffset() > -1)
						keys.add(key);
				}
				return keys.toArray();
			}
			return new Object[0];
		}
	}
	class BundleLabelProvider extends LabelProvider {
		public String getText(Object obj) {
			if (obj instanceof ManifestHeader) {
				return ((ManifestHeader) obj).getName();
			}
			return super.getText(obj);
		}
		public Image getImage(Object obj) {
			if (obj instanceof ManifestHeader)
				return PDEPlugin.getDefault().getLabelProvider().get(
					PDEPluginImages.DESC_BUILD_VAR_OBJ);
			return null;
		}
	}
	
	public BundleSourcePage(PDEFormEditor editor, String id, String title) {
		super(editor, id, title);
	}
	
	protected ILabelProvider createOutlineLabelProvider() {
		return new BundleLabelProvider();
	}
	
	protected ITreeContentProvider createOutlineContentProvider() {
		return new BundleOutlineContentProvider();
	}
	public IDocumentRange getRangeElement(int offset, boolean searchChildren) {
		IBundleModel model = (IBundleModel) getInputContext().getModel();
		Dictionary manifest = ((Bundle) model.getBundle()).getHeaders();

		for (Enumeration elements = manifest.elements(); elements.hasMoreElements();) {
		    IDocumentRange node = (IDocumentRange) elements.nextElement();

		    if (offset >= node.getOffset() &&
		        offset < node.getOffset() + node.getLength()) {
		        return node;
		    }
		}
		return null;
	}
	
	protected String[] collectContextMenuPreferencePages() {
		String[] ids= super.collectContextMenuPreferencePages();
		String[] more= new String[ids.length + 1];
		more[0]= "org.eclipse.pde.ui.EditorPreferencePage"; //$NON-NLS-1$
		System.arraycopy(ids, 0, more, 1, ids.length);
		return more;
	}
	
	public IDocumentRange findRange() {
		if (fSelection instanceof ImportObject) {
			IPluginModelBase base = ((ImportObject)fSelection).getImport().getPluginModel();
			if (base instanceof IBundlePluginModelBase)
				return getSpecificRange(
						((IBundlePluginModelBase)base).getBundleModel(),
						Constants.REQUIRE_BUNDLE,
						((ImportObject)fSelection).getId());
		} else if (fSelection instanceof ImportPackageObject) {
			return getSpecificRange(
					((ImportPackageObject)fSelection).getModel(),
					Constants.IMPORT_PACKAGE,
					((ImportPackageObject)fSelection).getValue());
		} else if (fSelection instanceof ExportPackageObject) {
			return getSpecificRange(
					((ExportPackageObject)fSelection).getModel(),
					Constants.EXPORT_PACKAGE,
					((ExportPackageObject)fSelection).getValue());
		} else if (fSelection instanceof IPluginLibrary) {
			IPluginModelBase base = ((IPluginLibrary)fSelection).getPluginModel();
			if (base instanceof IBundlePluginModelBase)
				return getSpecificRange(
						((IBundlePluginModelBase)base).getBundleModel(),
						Constants.BUNDLE_CLASSPATH,
						((IPluginLibrary)fSelection).getName());
		} else if (fSelection instanceof ExecutionEnvironment) {
			return getSpecificRange(
					((ExecutionEnvironment)fSelection).getModel(),
					Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT,
					((ExecutionEnvironment)fSelection).getValue());
		}
		return null;
	}
	
	public static IDocumentRange getSpecificRange(IBundleModel model, IManifestHeader header, String element) {
		if (header == null || !(model instanceof IEditingModel))
			return null;
		
		final int[] range = new int[] { -1, -1 }; // { offset, length }
		try {
			int start = header.getOffset() + header.getName().length();
			int length = header.getLength() - header.getName().length();
			String headerValue = ((IEditingModel)model).getDocument().get(start, length);
			
			int i = headerValue.indexOf(element);
			int last = headerValue.lastIndexOf(element);
			if (i > 0 && i != last) {
				char[] sChar = element.toCharArray();
				char[] headerChar = headerValue.toCharArray();
				headLoop: for (; i <= last; i++) {
					// check 1st, middle and last chars to speed things up
					if (headerChar[i] != sChar[0] && 
							headerChar[i + sChar.length / 2] != sChar[sChar.length / 2] && 
							headerChar[i + sChar.length - 1] != sChar[sChar.length - 1])
						continue headLoop;
					
					for (int j = 1; j < sChar.length - 1; j++)
						if (headerChar[i + j] != sChar[j])
							continue headLoop;

					// found match
					char c = headerChar[i - 1];
					if (!Character.isWhitespace(c) && c != ',')
						// search string is contained by another
						continue headLoop;
					
					c = headerChar[i + sChar.length];
					if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '.')
						// current match is longer than search
						continue headLoop;
					
					break;
				}
			} 
			if (i != -1) {
				range[0] = start + i;
				range[1] = element.length();
			}
		} catch (BadLocationException e) {
		}
		if (range[0] == -1) { // if un-set offset use header range
			range[0] = header.getOffset();
			range[1] = header.getLength();
		}
		return new IDocumentRange() {
			public int getOffset() { return range[0]; }
			public int getLength() { return range[1]; }
		};
	}
	
	public static IDocumentRange getSpecificRange(IBundleModel model, String headerName, String search) {
		IManifestHeader header = model.getBundle().getManifestHeader(headerName);
		return getSpecificRange(model, header, search);
	}

	protected boolean isSelectionListener() {
		return true;
	}
	
	public Object getAdapter(Class adapter) {
		if (IHyperlinkDetector.class.equals(adapter))
			return new BundleHyperlinkDetector(this);
		return super.getAdapter(adapter);
	}
}
