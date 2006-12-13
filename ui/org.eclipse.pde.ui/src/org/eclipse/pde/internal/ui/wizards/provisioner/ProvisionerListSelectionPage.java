package org.eclipse.pde.internal.ui.wizards.provisioner;

import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.jface.wizard.WizardSelectionPage;
import org.eclipse.pde.internal.ui.PDEUIMessages;
import org.eclipse.pde.internal.ui.elements.ElementList;
import org.eclipse.pde.internal.ui.wizards.ListUtil;
import org.eclipse.pde.internal.ui.wizards.WizardElement;
import org.eclipse.pde.internal.ui.wizards.WizardNode;
import org.eclipse.pde.ui.IBasePluginWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ProvisionerListSelectionPage extends WizardSelectionPage {
	
	private TableViewer fTableViewer = null;
	private Text fTextBox = null;
	private ElementList fElements = null;
	
	protected ProvisionerListSelectionPage(ElementList elements) {
		super(PDEUIMessages.ProvisionerListSelectionPage_pageName);
		fElements = elements;
		setTitle(PDEUIMessages.ProvisionerListSelectionPage_title);
		setDescription(PDEUIMessages.ProvisionerListSelectionPage_description);
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 10;
		container.setLayout(layout);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label label = new Label(container, SWT.None);
		label.setText(PDEUIMessages.ProvisionerListSelectionPage_tableLabel);
		label.setLayoutData(new GridData());
		
		SashForm sashForm = new SashForm(container, SWT.HORIZONTAL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 300;
		sashForm.setLayoutData(gd);
			
		fTableViewer = new TableViewer(sashForm, SWT.BORDER);
		
		fTableViewer.setLabelProvider(ListUtil.TABLE_LABEL_PROVIDER);
		fTableViewer.setContentProvider(new ArrayContentProvider());
		fTableViewer.setInput(fElements.getChildren());
		fTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				handleSelection();
			}
		});
		
		fTextBox = new Text(sashForm, SWT.BORDER | SWT.WRAP | SWT.READ_ONLY);
		fTextBox.setText(new String());
		fTextBox.setBackground(fTableViewer.getControl().getBackground());
		setControl(container);
		Dialog.applyDialogFont(container);

	}

	protected IWizardNode createWizardNode(WizardElement element) {
		return new WizardNode(this, element) {
			public IBasePluginWizard createWizard() throws CoreException {
				return (IBasePluginWizard) wizardElement.createExecutableExtension();
			}
		};
	}
	
	protected void setDescriptionText(String text) {
		fTextBox.setText(text);
	}
	
	protected void handleSelection() {
		setErrorMessage(null);
		IStructuredSelection selection = (IStructuredSelection) fTableViewer.getSelection();
		WizardElement currentWizardSelection = null;
		Iterator iter = selection.iterator();
		if (iter.hasNext())
			currentWizardSelection = (WizardElement) iter.next();
		if (currentWizardSelection == null) {
			setDescriptionText(""); //$NON-NLS-1$
			setSelectedNode(null);
			setPageComplete(false);
			return;
		}
		final WizardElement finalSelection = currentWizardSelection;
		setSelectedNode(createWizardNode(finalSelection));
		setDescriptionText(finalSelection.getDescription());
		setPageComplete(true);
		getContainer().updateButtons();
	}
	
	public IWizard getSelectedWizard() {
		IWizardNode node = getSelectedNode();
		if (node != null)
			return node.getWizard();
		return null;
	}

}
