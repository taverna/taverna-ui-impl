/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
 * 
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *    
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package net.sf.taverna.t2.workbench.ui.views.contextualviews.annotated;

import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import net.sf.taverna.raven.appconfig.ApplicationRuntime;
import net.sf.taverna.raven.repository.Repository;
import net.sf.taverna.raven.repository.impl.LocalArtifactClassLoader;
import net.sf.taverna.raven.spi.SpiRegistry;
import net.sf.taverna.t2.annotation.Annotated;
import net.sf.taverna.t2.annotation.AppliesTo;
import net.sf.taverna.t2.annotation.annotationbeans.AbstractTextualValueAssertion;
import net.sf.taverna.t2.lang.ui.DialogTextArea;
import net.sf.taverna.t2.lang.ui.ReadOnlyTextArea;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualViewComponent;
import net.sf.taverna.t2.workflowmodel.CompoundEdit;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.Edit;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.EditsRegistry;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.NestedDataflow;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.ParallelizeConfig;
import net.sf.taverna.t2.workflowmodel.utils.AnnotationTools;

import org.apache.log4j.Logger;

/**
 * 
 * This is a ContextualView that should be able to display and allow editing of
 * Annotation information for any Annotated. At the moment it is only used for
 * Dataflow.
 * 
 * @author Alan R Williams
 * 
 */
@SuppressWarnings("serial")
public class AnnotatedContextualView extends ContextualView {

	private static final int WORKFLOW_NAME_LENGTH = 20;

	public static final String VIEW_TITLE = "Annotations";

	private static Logger logger = Logger
			.getLogger(AnnotatedContextualView.class);

	private static AnnotationTools annotationTools = new AnnotationTools();
	
	/**
	 * The object to which the Annotations apply
	 */
	private Annotated<?> annotated;

	private static PropertyResourceBundle prb = (PropertyResourceBundle) ResourceBundle
	.getBundle("annotatedcontextualview");;

	private static String MISSING_VALUE = "Type here to give details";

	private static int DEFAULT_AREA_WIDTH = 60;
	private static int DEFAULT_AREA_ROWS = 8;
	
	private FileManager fileManager = FileManager.getInstance();
	private EditManager editManager = EditManager.getInstance();

	private boolean isStandalone = false;

	private JPanel panel;

	@SuppressWarnings("unchecked")
	private static Map<Annotated, JPanel> annotatedToPanelMap = new HashMap<Annotated, JPanel>();


	
	public AnnotatedContextualView(Annotated<?> annotated) {
		super();
		this.annotated = annotated;
		initialise();
		initView();
	}

	public AnnotatedContextualView(Annotated<?> annotated, boolean standAlone) {
		super();
		this.isStandalone = standAlone;
		this.annotated = annotated;
		initialise();
		initView();
	}
	
	public void refreshView() {
			initialise();
	}

	private void initialise() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new BoxLayout(panel,
					BoxLayout.Y_AXIS));
		} else {
			panel.removeAll();
		}
		populatePanel();
		revalidate();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView#
	 * getMainFrame()
	 */
	@Override
	public JComponent getMainFrame() {
		return panel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView#
	 * getViewTitle()
	 */
	@Override
	public String getViewTitle() {
		return VIEW_TITLE;
	}
	
	public void populatePanel() {
		JPanel scrollPanel = new JPanel();
		scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		for (Class<?> c : annotationTools.getAnnotatingClasses(annotated)) {
			String name = "";
			try {
				name = prb.getString(c.getCanonicalName());
			} catch (MissingResourceException e) {
				name = c.getCanonicalName();
			}
			JPanel subPanel = new JPanel();
			subPanel.setBorder(new TitledBorder(name));
            String value = annotationTools.getAnnotationString(annotated, c, MISSING_VALUE);
            
//			Annotated<?> suitableAnnotated = findSuitableAnnotated(annotated, c);
//			String value = MISSING_VALUE;
//			if (suitableAnnotated != null) {
//				value = annotationTools.getAnnotationString(suitableAnnotated, c, MISSING_VALUE);
//			}
//			if (suitableAnnotated == null) {
//				subPanel.setBorder(new TitledBorder(name + " (default)"));				
//			}
//			else if (annotated.equals(suitableAnnotated)) {
//				subPanel.setBorder(new TitledBorder(name));				
//			} else {
//				subPanel.setBorder(new TitledBorder(name + " (inferred)"));
//			}
			subPanel.add(createTextArea(c, value));
			scrollPanel.add(subPanel);
		}
		JScrollPane scrollPane = new JScrollPane(scrollPanel);
		panel.add(scrollPane);
	}
	
	private Annotated<?> findSuitableAnnotated(Annotated<?> annotatedUnderConsideration, Class<?> c) {
		String value = annotationTools.getAnnotationString(annotatedUnderConsideration, c, MISSING_VALUE);
		if (!value.equals(MISSING_VALUE)) {
			return annotatedUnderConsideration;
		}
		if (annotatedUnderConsideration instanceof Processor) {
			List<? extends Activity<?>> activities = ((Processor) annotatedUnderConsideration).getActivityList();
			if (!activities.isEmpty()) {
				return findSuitableAnnotated(activities.get(0), c);
			}
		}
		if (annotatedUnderConsideration instanceof NestedDataflow) {
			return findSuitableAnnotated(((NestedDataflow) annotatedUnderConsideration).getNestedDataflow(), c);
		}
		return null;
	}

	private JScrollPane createTextArea(final Class<?> c, final String value) {
		DialogTextArea area = new DialogTextArea(value);
		area.setFocusable(true);
		area.addFocusListener(new TextAreaFocusListener(area, c));
		area.setColumns(DEFAULT_AREA_WIDTH);
		area.setRows(DEFAULT_AREA_ROWS);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		return new JScrollPane(area);
	}

	private class TextAreaFocusListener implements FocusListener {

		String oldValue = null;
		Class<?> annotationClass;
		DialogTextArea area = null;

		public TextAreaFocusListener(DialogTextArea area, Class<?> c) {
			annotationClass = c;
			oldValue = area.getText();
			this.area = area;
		}

		public void focusGained(FocusEvent e) {
			if (area.getText().equals(MISSING_VALUE)) {
				area.setText("");
			}
		}

		public void focusLost(FocusEvent e) {
			String currentValue = area.getText();
			if (currentValue.equals("") || currentValue.equals(MISSING_VALUE)) {
				currentValue = MISSING_VALUE;
				area.setText(currentValue);
			}
			if (!currentValue.equals(oldValue)) {
				if (currentValue == MISSING_VALUE) {
					currentValue = "";
				}
				try {
					Edits edits = EditsRegistry.getEdits();
					Dataflow currentDataflow = fileManager.getCurrentDataflow();
					List<Edit<?>> editList = new ArrayList<Edit<?>>();
					editList.add(annotationTools.setAnnotationString(annotated, annotationClass, currentValue));
					if ((annotated == currentDataflow) && (prb.getString(annotationClass.getCanonicalName()).equals("Title")) &&!currentValue.isEmpty()) {
						editList.add(edits.getUpdateDataflowNameEdit(currentDataflow,
								sanitiseName(currentValue)));
					}
					if (!isStandalone) {
						ContextualViewComponent.selfGenerated = true;
					}
					editManager.doDataflowEdit(currentDataflow, new CompoundEdit(editList));
					ContextualViewComponent.selfGenerated = false;
				} catch (EditException e1) {
					logger.warn("Can't set annotation", e1);
				}
				oldValue = area.getText();
			}
		}

	}

	/**
	 * Checks that the name does not have any characters that are invalid for a
	 * processor name.
	 * 
	 * The name must contain only the chars[A-Za-z_0-9].
	 * 
	 * @param name
	 *            the original name
	 * @return the sanitised name
	 */
	private static String sanitiseName(String name) {
		String result = name;
		if (result.length() > WORKFLOW_NAME_LENGTH) {
			result = result.substring(0,WORKFLOW_NAME_LENGTH);
		}
		if (Pattern.matches("\\w++", result) == false) {
			String temp = "";
			for (char c : result.toCharArray()) {
				if (Character.isLetterOrDigit(c) || c == '_') {
					temp += c;
				} else {
					temp += "_";
				}
			}
			result = temp;
		}
		return result;
	}

	@Override
	public int getPreferredPosition() {
		return 500;
	}



}
