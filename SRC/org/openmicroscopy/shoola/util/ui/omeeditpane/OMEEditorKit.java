/*
 * org.openmicroscopy.shoola.util.ui.omeeditpane.OMEEditorKit 
 *
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
 */
package org.openmicroscopy.shoola.util.ui.omeeditpane;


//Java imports

import java.awt.Font;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JEditorPane;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

//Third-party libraries

//Application-internal dependencies

/** 
 * 
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 	<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author	Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * 	<a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since OME3.0
 */
public class OMEEditorKit
	extends StyledEditorKit
	implements ViewFactory
{
	/** The editor pane for the kit. */
	private JEditorPane editorPane;

	/** The format map of the editor pane, mapping regex, and format.*/
	Map<String, FormatSelectionAction>			formatMap;
	
	/** The view of the editorpane. */
	private WikiView view;
	
	/**
	 * Create the editor kit.
	 * @param formatters
	 */
	public OMEEditorKit(Map<String, FormatSelectionAction> formatters)
	{
		this.formatMap = formatters;
	}
	  
	@Override
	public ViewFactory getViewFactory() 
	{
		return this;
	}

	/**
	 * Create a new instance for element.
	 * @param element see above.
	 * @return see above.
	 */
	public View create(Element element) 
	{
		view = new WikiView(element, formatMap, editorPane);
		return view;
	}
	
	/**
	 * Get the view.
	 * @return see above.
	 */
	public WikiView getView()
	{
		return view;
	}
	
	/**
	 * Install the View on the given EditorPane.  This is called by Swing and
	 * can be used to do anything you need on the JEditorPane control.  Here
	 * I set some default Actions.
	 * 
	 * @param editorPane
	 */
	@Override
	public void install(JEditorPane editorPane) 
	{
		super.install(editorPane);
		this.editorPane = editorPane;
		Keymap km_parent = JTextComponent.getKeymap(JTextComponent.DEFAULT_KEYMAP);
		Keymap km_new = JTextComponent.addKeymap(null, km_parent);
		editorPane.setKeymap(km_new);
	}
	
}


