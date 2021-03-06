 /*
 * org.openmicroscopy.shoola.agents.editor.model.undoableEdits.AttributeEdit 
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2008 University of Dundee. All rights reserved.
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
 */
package org.openmicroscopy.shoola.agents.editor.model.undoableEdits;

//Java imports

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.undo.AbstractUndoableEdit;

//Third-party libraries

//Application-internal dependencies

import org.openmicroscopy.shoola.agents.editor.model.IAttributes;
import org.openmicroscopy.shoola.agents.editor.model.TreeModelMethods;

/** 
* This is the UndoableEdit for editing an attribute.
* Edits the named attribute, saving the old value for undo. 
* 
* Following undo and redo operations, it will attempt (if tree and node are  
* not null) to notify the TreeModel of the JTree that a node has 
* changed, so that other JTrees using this model will update. 
*
* @author  William Moore &nbsp;&nbsp;&nbsp;&nbsp;
* <a href="mailto:will@lifesci.dundee.ac.uk">will@lifesci.dundee.ac.uk</a>
* @version 3.0
* <small>
* (<b>Internal version:</b> $Revision: $Date: $)
* </small>
* @since OME3.0
*/
public class AttributeEdit 
	extends AbstractUndoableEdit 
{

	/**
	 * A name to display in the Undo/Redo buttons, that describes the edit.
	 */
	private String 			displayName;
	
	/**
	 * The data source which will be edited, using setAttribute()
	 */
	private IAttributes 	attributes;
	
	/**
	 * The name of the attribute being edited
	 */
	private String 			attributeName;
	
	/**
	 * The new Value of the attribute
	 */
	private String 			newValue;
	
	/**
	 * The old value of the attribute. This will be retrieved from the 
	 * data source before being replaced with the new value. 
	 * Used for the undo() operation.
	 */
	private String 			oldValue;
	
	/**
	 * The JTree which displays the data being edited. If this is not null,
	 * it's TreeModel will be notified of changes to the node, and 
	 * the JTree selection path will be set to the edited node. 
	 */
	private JTree 			tree; 
	
	/**
	 * The node that contains the data being edited. 
	 * This is used to notify the TreeModel that nodeChanged() and to 
	 * set the selected node in the JTree after editing. 
	 */
	private TreeNode 		node;
	
	/**
	 * This should be called after undo or redo.
	 * It notifies listeners to the treeModel that a change has been made,
	 * then selects the edited node in the JTree specified in the constructor. 
	 * Finally, startEditingAtPath(path) to the edited node is called on 
	 * this JTree. This means that after an undo/redo, the edited node is 
	 * "active", so that a user can edit directly, rather than needing a 
	 * click before editing. 
	 */
	private void notifySelectStartEdit() 
	{
		notifyNodeChanged();
		//tree.clearSelection();
		TreeModelMethods.selectNode(node, tree);
		
		DefaultMutableTreeNode dmtNode = (DefaultMutableTreeNode)node;
		tree.startEditingAtPath(new TreePath(dmtNode.getPath()));
	}

	/**
	 * This notifies all listeners to the TreeModel of a change to the node
	 * in which the attribute has been edited. 
	 * This will update any JTrees that are currently displaying the model. 
	 */
	private void notifyNodeChanged() 
	{
		if ((tree != null) && (node != null)) {
			
			DefaultTreeModel treeModel = (DefaultTreeModel)tree.getModel();
			treeModel.nodeChanged(node);
		}
	}

	/**
	 * Performs the edit attribute operation, 
	 * first saving the old value of the attribute.
	 *
	 */
	private void doEdit() 
	{	
		if (canDo()) {
			oldValue = attributes.getAttribute(attributeName);
			attributes.setAttribute(attributeName, newValue);
			
			// if node changed (eg template edited), need to notify
			notifyNodeChanged();
		}
	}

	/**
	 * Creates an instance of this class.
	 * The Attribute editing occurs within the constructor. 
	 * 
	 * @param attributes	The data source
	 * @param name		Attribute name being edited
	 * @param value		New value of the attribute
	 * @param tree		The JTree displaying the data
	 * @param node		The node of JTree which contains the data
	 */
	public AttributeEdit(IAttributes attributes, String attributeName, 
			String value, String displayName, JTree tree, TreeNode node) 
	{	
		this.attributes = attributes;
		this.attributeName = attributeName;
		this.displayName = displayName;
		this.newValue = value;
		this.tree = tree;
		
		this.node = node;
		
		doEdit();
	}

	/**
	 * Can edit attributes as long as the data source and the
	 * attribute name are not null. 
	 * If the JTree and node are null, updates will not occur, but 
	 * the data can still be edited. 
	 * 
	 * @return boolean 		True if this edit can be done. 
	 */
	public boolean canDo() 
	{	
		if ((attributes == null) || (attributeName == null)) return false;
		
		return true;
	}

	/**
	 * Sets the attribute back to it's old value
	 */
	public void undo() 
	{
		attributes.setAttribute(attributeName, oldValue);
		
		notifySelectStartEdit();
	}
	
	/**
	 * Re-edits the attribute
	 */
	public void redo() 
	{
		attributes.setAttribute(attributeName, newValue);
		
		notifySelectStartEdit();
	}
	
	/**
	 * Presentation name is..
	 * 
	 * @see AbstractUndoableEdit#getPresentationName()
	 */
	public String getPresentationName() { return displayName; }
	
	/**
	 * Can always undo
	 * 
	 * @see AbstractUndoableEdit#canUndo()
	 */
	 public boolean canUndo() { return true; }

	 /**
	  * Can always redo
	  * 
	  * @see AbstractUndoableEdit#canRedo()
	  */
	  public boolean canRedo() { return true; }
}
