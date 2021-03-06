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
package net.sf.taverna.t2.workbench.ui.impl.configuration;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.sf.taverna.t2.workbench.configuration.ConfigurationManager;
import net.sf.taverna.t2.workbench.helper.Helper;
import net.sf.taverna.t2.workbench.icons.WorkbenchIcons;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class WorkbenchConfigurationPanel extends JPanel {

	private static Logger logger = Logger
			.getLogger(WorkbenchConfigurationUIFactory.class);

	private JTextField dotLocation = new JTextField(25);	
	private JTextField menuItems = new JTextField(10);	
	private JCheckBox warnInternal = new JCheckBox("Warn on internal errors");
	private JCheckBox captureConsole = new JCheckBox("Capture output on stdout/stderr to log file");

	private ConfigurationManager configurationManager = ConfigurationManager.getInstance();
	
	private WorkbenchConfiguration workbenchConfiguration = WorkbenchConfiguration.getInstance();

	public WorkbenchConfigurationPanel() {
		super();
		initComponents();
	}

	private void initComponents() {
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Title describing what kind of settings we are configuring here
        JTextArea descriptionText = new JTextArea(
        "General Workbench configuration");
        descriptionText.setLineWrap(true);
        descriptionText.setWrapStyleWord(true);
        descriptionText.setEditable(false);
        descriptionText.setFocusable(false);
        descriptionText.setBorder(new EmptyBorder(10, 10, 10, 10));
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add(descriptionText, gbc);
		
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(10, 5, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        this.add(new JLabel("<html><body>Path to Graphviz executable <code>dot</code>:</body></html>"), gbc);

		dotLocation.setText((workbenchConfiguration
				.getDotLocation()));
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add(dotLocation, gbc);
		
		JButton browseButton=new JButton();
        gbc.gridx = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        this.add(browseButton, gbc);
		browseButton.setAction(new AbstractAction() {
			
			public void actionPerformed(ActionEvent e) {
				System.setProperty("com.apple.macos.use-file-dialog-packages", "false");
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.putClientProperty("JFileChooser.appBundleIsTraversable", "always");
				fileChooser.putClientProperty("JFileChooser.packageIsTraversable", "always");
				
				fileChooser.setDialogTitle("Browse for dot");

				fileChooser.resetChoosableFileFilters();
				fileChooser.setAcceptAllFileFilterUsed(false);
				
				fileChooser.setMultiSelectionEnabled(false);
				
				int returnVal = fileChooser.showOpenDialog(WorkbenchConfigurationPanel.this);
				if (returnVal==JFileChooser.APPROVE_OPTION) {
					dotLocation.setText(fileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		browseButton.setIcon(WorkbenchIcons.openIcon);	

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(10, 5, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add(new JLabel("<html><body>Maximum number of services/ports in right-click menu:</body></html>"), gbc);

        menuItems.setText(Integer.toString(workbenchConfiguration
				.getMaxMenuItems()));
        gbc.gridy++;        
        gbc.weightx = 1.0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add(menuItems, gbc);
		
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 0, 0);
        warnInternal.setSelected(workbenchConfiguration.getWarnInternalErrors());
        this.add(warnInternal, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 10, 0);
        captureConsole.setSelected(workbenchConfiguration.getCaptureConsole());
        this.add(captureConsole, gbc);		
		
		// Add the buttons panel
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.SOUTH;
		this.add(getButtonsPanel(), gbc);
	}
	
	private Component getButtonsPanel() {
		final JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER));	
		
		/**
		 * The helpButton shows help about the current component
		 */
		JButton helpButton = new JButton(new AbstractAction("Help") {
			public void actionPerformed(ActionEvent arg0) {
				Helper.showHelp(panel);
			}
		});
		panel.add(helpButton);

		/**
		 * The resetButton changes the property values shown to those
		 * corresponding to the configuration currently applied.
		 */
		JButton resetButton = new JButton(new AbstractAction("Reset") {
			public void actionPerformed(ActionEvent arg0) {
				resetFields();
			}
		});
		panel.add(resetButton);
		
		JButton applyButton = new JButton(new AbstractAction("Apply") {
			public void actionPerformed(ActionEvent arg0) {
				String menus = menuItems.getText();
				try {
					workbenchConfiguration.setMaxMenuItems(Integer.valueOf(menus));
				} catch (IllegalArgumentException e) {
					String message = "Invalid menu items number " + menus + ":\n" + e.getLocalizedMessage();
					JOptionPane.showMessageDialog(panel, message, "Invalid menu items", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				workbenchConfiguration.setCaptureConsole(captureConsole.isSelected());
				workbenchConfiguration.setWarnInternalErrors(warnInternal.isSelected());
				workbenchConfiguration.setDotLocation(dotLocation.getText());
				try {
					configurationManager.store(workbenchConfiguration);
					String message = "For the new configuration to be fully applied, it is advised to restart Taverna.";
					JOptionPane.showMessageDialog(panel, message, "Restart adviced", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e) {
					logger.error("Error storing updated configuration", e);
				}
			}
		});
		panel.add(applyButton);
		return panel;
	}
	
	/**
	 * Resets the shown field values to those currently set (last saved) in the configuration.
	 * 
	 * @param configurable
	 */
	private void resetFields() {
		menuItems.setText(Integer.toString(workbenchConfiguration
				.getMaxMenuItems()));
		dotLocation.setText(workbenchConfiguration.getDotLocation());
        warnInternal.setSelected(workbenchConfiguration.getWarnInternalErrors());
        captureConsole.setSelected(workbenchConfiguration.getCaptureConsole());

	}

}
