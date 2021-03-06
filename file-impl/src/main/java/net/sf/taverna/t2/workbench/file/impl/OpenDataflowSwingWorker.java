/*******************************************************************************
 * Copyright (C) 2009 The University of Manchester   
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
package net.sf.taverna.t2.workbench.file.impl;

import net.sf.taverna.t2.workflowmodel.Dataflow;

import org.apache.log4j.Logger;
import javax.swing.SwingWorker;

import net.sf.taverna.t2.workbench.file.DataflowInfo;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.file.FileType;
import net.sf.taverna.t2.workbench.file.exceptions.OpenException;

public class OpenDataflowSwingWorker extends SwingWorker<Dataflow, Object>{

	private Logger logger = Logger.getLogger(OpenDataflowSwingWorker.class);
	private FileType fileType;
	private Object source;
	private FileManagerImpl fileManagerImpl;
	private Dataflow dataflow;
    private OpenException e = null;

	public OpenDataflowSwingWorker(FileType fileType, Object source, FileManagerImpl fileManagerImpl){
		this.fileType = fileType;
		this.source = source;
		this.fileManagerImpl = fileManagerImpl;
	}
	
	@Override
	protected Dataflow doInBackground() throws Exception {

	    try {
		dataflow = fileManagerImpl.performOpenDataflow(fileType, source);
	    } catch (OpenException e) {
		this.e = e;
	    }
		return dataflow;
	}

	public Dataflow getDataflow() {
		return dataflow;
	}

    public OpenException getException() {
	return e;
    }
}
