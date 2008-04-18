package actions;

/*
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2007 University of Dundee. All rights reserved.
 *
 *
 * 	This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 *	author Will Moore will@lifesci.dundee.ac.uk
 */

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;

import tree.Tree.Actions;
import ui.IModel;

/**
 * This Action class uses the editCurrentTree() method to pass an Enumerated instance of 
 * Tree.Actions to the Tree, via the model. 
 * The Tree will perform the stated action on the currently highlighted fields, or on all fields,
 * depending on the Action. 
 * 
 * @author will
 *
 */
public class LoadDefaultsHighltdAction extends ProtocolEditorAction {
	
	public LoadDefaultsHighltdAction(IModel model) {

		super(model);
	
		putValue(Action.NAME, "Load Defaults for Highlighted fields (and all child fields)");
		putValue(Action.SHORT_DESCRIPTION, null);
		//putValue(Action.SMALL_ICON, ImageFactory.getInstance().getIcon(ImageFactory.LOAD_DEFAULTS_ICON)); 
	}
	
	public void actionPerformed(ActionEvent e) {
		
		/*
		 * Check to see if you will overwrite any existing values with defaults...
		 */
		if (model.isAnyHighlightedDefaultFieldFilled()) {
			// ..if so, give user option to cancel
			int okCancel = JOptionPane.showConfirmDialog(frame, 
					"Loading defaults will over-write existing values.\n" +
					"Are you sure you want to continue?", "Overwrite existing values?",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (okCancel == JOptionPane.CANCEL_OPTION)
				return;
		}
		
		model.editCurrentTree(Actions.LOAD_DEFAULTS_HIGHLIGHTED_FIELDS);
	}
	
	public void stateChanged(ChangeEvent e) {
		
		/*
		 * This action should only be enabled if a file is open and the
		 * currently highlighted fields have editable values (not locked).
		 */
		setEnabled(fieldValuesEditable());
	}
}
