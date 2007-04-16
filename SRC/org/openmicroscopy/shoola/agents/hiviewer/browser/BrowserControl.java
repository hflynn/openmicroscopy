/*
 * org.openmicroscopy.shoola.agents.hiviewer.browser.BrowserControl
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006 University of Dundee. All rights reserved.
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

package org.openmicroscopy.shoola.agents.hiviewer.browser;


//Java imports
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JComponent;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.hiviewer.Colors;
import org.openmicroscopy.shoola.agents.hiviewer.cmd.ViewCmd;
import pojos.DatasetData;
import pojos.ImageData;

/** 
 * Handles input events originating from the {@link Browser}'s View.
 * That is, from the {@link RootDisplay} containing all the visualization
 * trees. 
 * This class takes on the role of the browser's Controller (as in MVC).
 *
 * @see BrowserModel
 * @see RootDisplay
 * 
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author  <br>Andrea Falconi &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:a.falconi@dundee.ac.uk">
 * 					a.falconi@dundee.ac.uk</a>
 * @version 2.2
 * <small>
 * (<b>Internal version:</b> $Revision$ $Date$)
 * </small>
 * @since OME2.2
 */
class BrowserControl
    implements MouseListener, ImageDisplayVisitor, PropertyChangeListener
{
    
    //TODO: Implement scroll listener.  When the currently selected node is 
    //scrolled out of the parent's viewport then it has to be deselected. 
    
    /** The Model controlled by this Controller. */
    private BrowserModel    model;
    
    /** The View controlled by this Controller.*/
    private RootDisplay     view;
    
    /** Flag to indicate that a popupTrigger event occured. */
    private boolean         popupTrigger;
    
    /**
     * Finds the first {@link ImageDisplay} in <code>x</code>'s containement
     * hierarchy.
     * 
     * @param x A component.
     * @return The parent {@link ImageDisplay} or <code>null</code> if none
     *         was found.
     */
    private ImageDisplay findParentDisplay(Object x)
    {
        while (true) {
            if (x instanceof ImageDisplay) return (ImageDisplay) x;
            if (x instanceof JComponent) x = ((JComponent) x).getParent();
            else break;
        }
        return null;
    }
    
    /**
     * Creates a new Controller for the specified <code>model</code> and
     * <code>view</code>.
     * You need to call the {@link #initialize() initialize} method after
     * creation to complete the MVC set up.
     * 
     * @param model The Model.
     * @param view The View.
     */
    BrowserControl(BrowserModel model, RootDisplay view)
    {
        if (model == null) throw new NullPointerException("No model.");
        if (view == null) throw new NullPointerException("No view.");
        model.addPropertyChangeListener(Browser.SELECTED_DISPLAY_PROPERTY,
                                        this);
        model.addPropertyChangeListener(Browser.ROLL_OVER_PROPERTY,
                                            this);
        this.model = model;
        this.view = view;
        popupTrigger = false;
    }
    
    /**
     * Subscribes for mouse events notification with each node in the
     * various visualization trees.
     */
    void initialize() { model.accept(this); }

    /**
     * Registers this object as mouse listeners with each node.
     * @see ImageDisplayVisitor#visit(ImageNode)
     */
    public void visit(ImageNode node) 
    { 
        node.getTitleBar().addMouseListener(this);
        node.getCanvas().addMouseListener(this);
        node.addPropertyChangeListener(ImageNode.CLASSIFY_NODE_PROPERTY, this);
        node.addPropertyChangeListener(ImageNode.PIN_THUMBNAIL_PROPERTY, this);
        node.addPropertyChangeListener(ImageDisplay.ANNOTATE_NODE_PROPERTY, 
                                    this);
    }

    /**
     * Registers this object as mouse listeners with each node.
     * @see ImageDisplayVisitor#visit(ImageSet)
     */
    public void visit(ImageSet node) 
    {
        node.getTitleBar().addMouseListener(this);
        node.getInternalDesktop().addMouseListener(this);
        if (node.getHierarchyObject() instanceof DatasetData) {
            node.addPropertyChangeListener(ImageDisplay.ANNOTATE_NODE_PROPERTY, 
                                            this);
        }
    }
    
    /** 
     * Listens to the property event fired by {@link Browser} and 
     * {@link ImageDisplay}.
     * Necessary for clarity.
     * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */ 
    public void propertyChange(PropertyChangeEvent evt)
    {
        String name = evt.getPropertyName();
        if (ImageNode.CLASSIFY_NODE_PROPERTY.equals(name)) {
            model.setNodeForProperty(Browser.CLASSIFIED_NODE_PROPERTY, 
                        evt.getNewValue());
        } else if (ImageDisplay.ANNOTATE_NODE_PROPERTY.equals(name)) {
            model.setNodeForProperty(Browser.ANNOTATED_NODE_PROPERTY, 
                                    evt.getNewValue());
        } else if (Browser.ROLL_OVER_PROPERTY.equals(name)) {
            ImageDisplay newNode = (ImageDisplay) evt.getNewValue();
            view.setTitle(model.currentPathString(newNode));
        } else if (Browser.SELECTED_DISPLAY_PROPERTY.equals(name)) {
            ImageDisplay newNode = (ImageDisplay) evt.getNewValue();
            if (newNode == null) return;
            view.setTitle(model.currentPathString(newNode));
            //paint the nodes
            Colors colors = Colors.getInstance();
            newNode.setHighlight(colors.getSelectedHighLight(newNode));
            Set nodes = (Set) evt.getOldValue();
            if (nodes != null && !model.isMultiSelection()) {
                Iterator i = nodes.iterator();
                ImageDisplay n;
                while (i.hasNext()) {
                    n = (ImageDisplay) i.next();
                    if (n != null)
                    	n.setHighlight(colors.getDeselectedHighLight(n));
                }
            }
        } else if (ImageNode.PIN_THUMBNAIL_PROPERTY.equals(name)) {
        	ImageNode node = (ImageNode) evt.getNewValue();
        	model.setThumbSelected(true, node);
        }
    }
    
    /**
     * Sets the currently selected display.
     * @see MouseListener#mousePressed(MouseEvent)
     */
    public void mousePressed(MouseEvent me)
    {
        ImageDisplay d = findParentDisplay(me.getSource());
        d.moveToFront();
        ImageDisplay previousDisplay = model.getLastSelectedDisplay();
        boolean b = me.isShiftDown();
        if (!(d.equals(previousDisplay))) {
            if (d instanceof ImageNode) {
                if (!(previousDisplay instanceof ImageNode)) b = false;
                model.setSelectedDisplay(d, b);
            } else model.setSelectedDisplay(d);
        }
        if (me.isPopupTrigger()) popupTrigger = true;
    }

    /**
     * Tells the model that either a popup point or a thumbnail selection
     * was detected.
     * @see MouseListener#mouseReleased(MouseEvent)
     */
    public void mouseReleased(MouseEvent me) 
    {
        if (popupTrigger || me.isPopupTrigger())
                model.setPopupPoint(me.getPoint());
        else {
            Object src = me.getSource();
            ImageDisplay d = findParentDisplay(src);
            if (d instanceof ImageNode && !(d.getTitleBar() == src) 
                && me.getClickCount() == 2) {
                ViewCmd cmd = new ViewCmd((ImageData) d.getHierarchyObject());
                cmd.execute();
            }   
        }
        
        popupTrigger = false; 
    }

    /**
     * Sets the node which has to be zoomed when the roll over flag
     * is turned on. Note that the {@link ImageNode}s are the only nodes
     * considered.
     * @see MouseListener#mouseEntered(MouseEvent)
     */
    public void mouseEntered(MouseEvent me)
    {
        Object src = me.getSource();
        ImageDisplay d = findParentDisplay(src);
        if (d instanceof RootDisplay) {
        	ImageDisplay lastSelected = model.getLastSelectedDisplay();
        	if (lastSelected != null) {
        		view.setTitle(model.currentPathString(lastSelected));
        	} else lastSelected = null;
        	model.setNodeForProperty(Browser.MOUSE_OVER_PROPERTY, 
					lastSelected);
        	return;
        }
        if (!(d instanceof RootDisplay))
            view.setTitle(model.currentPathString(d));
        model.setNodeForProperty(Browser.MOUSE_OVER_PROPERTY, d);
        if (!model.isRollOver()) return;
        if (d instanceof ImageNode && !(d.getTitleBar() == src)) {
            model.setRollOverNode((ImageNode) d);
        } else model.setRollOverNode(null);
    }

    /**
     * Displays the name of the selected node if any when the mouse exited.
     * @see MouseListener#mouseExited(MouseEvent)
     */
    public void mouseExited(MouseEvent me)
    {
    	if (model.isRollOver()) return;
        ImageDisplay d = model.getLastSelectedDisplay();
        if (d != null) view.setTitle(model.currentPathString(d));
        else view.setTitle("");
        model.setNodeForProperty(Browser.SELECTED_DISPLAY_PROPERTY, d);
    }

    /**
     * Required by the {@link MouseListener} I/F but no-op implementation
     * in our case.
     * @see MouseListener#mouseClicked(MouseEvent)
     */
    public void mouseClicked(MouseEvent me) {}
    
}
