/*
 * org.openmicroscopy.shoola.agents.executions.ExecutionsCanvas
 *
 *------------------------------------------------------------------------------
 *
 *  Copyright (C) 2004 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *------------------------------------------------------------------------------
 */

package org.openmicroscopy.shoola.agents.executions.ui;

//Java imports
import java.awt.Dimension;

import java.awt.event.MouseEvent;
import java.awt.Font;
import java.awt.FontMetrics;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;
import java.text.SimpleDateFormat;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;
import javax.swing.JPanel;



//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.executions.ui.model.BoundedLongRangeModel;
import org.openmicroscopy.shoola.agents.executions.ui.model.ExecutionsModel;
import org.openmicroscopy.shoola.agents.executions.ui.model.GridModel;
import org.openmicroscopy.shoola.env.data.model.ChainExecutionData;
import org.openmicroscopy.shoola.util.ui.Constants;

/** 
 * A panel for drawing executions..
 * 
 * @author  Harry Hochheiser &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:hsh@nih.gov">hsh@nih.gov</a>
 * @version 2.2
 * <small>
 * (<b>Internal version:</b> $Revision$ $Date$)
 * </smalbl>
 * @since OME2.2
 */
public class ExecutionsCanvas extends JPanel implements ChangeListener, 
	MouseInputListener {
	
	public static final int WIDTH =300;
	public static final int HEIGHT=150;
	public static final int SPACING=5;
	
	private static Font tipFont = new Font("Helvetica",Font.PLAIN,10); 

	
	/* hashes of executions */
	private ExecutionsModel model;
	
	/* the model of the grid */
	private GridModel gridModel;
	
	private Vector executionViews;
	
	/** 
	 *  for tool tips
	 */
	private int xLoc;
	private int yLoc;
	private ExecutionView currentExecution;
	
	/**
	 * Creates a new instance.
	 */
	public ExecutionsCanvas(ExecutionsModel model)
	{
		super();
		this.model = model;
		BoundedLongRangeModel blrm = model.getRangeModel();
		blrm.addChangeListener(this);
		gridModel = new GridModel(blrm.getMinimum(),blrm.getMaximum(),
				model.getLastRowIndex());
		setBackground(Constants.CANVAS_BACKGROUND_COLOR);
		
		// build execution views
		executionViews = new Vector();
		Iterator iter = model.executionIterator();
		while (iter.hasNext()) {
			ChainExecutionData exec = (ChainExecutionData) iter.next();
			ExecutionView view = new ExecutionView(exec,gridModel,model);
			executionViews.add(view);
		}
		addMouseMotionListener(this);
		addMouseListener(this);
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(WIDTH,HEIGHT);
	}
	
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	
	public void stateChanged(ChangeEvent e) {
		if (!(e.getSource() instanceof BoundedLongRangeModel))
			return;
		BoundedLongRangeModel blrm = (BoundedLongRangeModel) e.getSource();
		Date min = new Date(blrm.getMinimum());
		Date max = new Date(blrm.getMaximum());
		Date val = new Date(blrm.getValue());
		Date endRange = new Date(blrm.getMax());
		gridModel.setExtent(blrm.getMinimum(),blrm.getMaximum());
	}
	
	public void setBounds(int x,int y,int w,int h) {
		super.setBounds(x,y,w,h);
		if (gridModel != null) 
			gridModel.setDimensions(w,h);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		if (gridModel == null)
			return;
		gridModel.drawAxes(g2);
		drawExecutions(g2);
		if (currentExecution != null) {
			System.err.println("doing tool tip");
			drawExecutionTip(g2);
		}
	}
	
	public void drawExecutions(Graphics2D g) {
		Iterator iter = executionViews.iterator();
		ExecutionView view;
		boolean current;
		while (iter.hasNext()) {
			view = (ExecutionView) iter.next();
			current = (view == currentExecution);
			view.paint(g,current);
		}
	}
	
	public void mouseClicked(MouseEvent e) {
	}
	
	public void mousePressed(MouseEvent e) {
		
	}
	
	public void mouseReleased(MouseEvent e) {
		
	}
	
	public void mouseEntered(MouseEvent e) {
	}
	
	public void mouseExited(MouseEvent e) {
	}
	
	public void mouseDragged(MouseEvent e) {
		
	}
	
	public void mouseMoved(MouseEvent e) {
		displayHint(e);
	}
	
	
	private void displayHint(MouseEvent e) {
		xLoc = e.getX();
		yLoc = e.getY();
		ExecutionView exec = getViewAt(xLoc,yLoc);
		if ( exec != currentExecution) {
			currentExecution = exec;
			repaint();
		}
	}
	
	private ExecutionView getViewAt(int x,int y) {
		ExecutionView view;
		
		Iterator iter = executionViews.iterator();
		while (iter.hasNext()) {
			view = (ExecutionView) iter.next();
			if (view.isAt(x,y) == true)
				return view;
		}
		return null;
	}
	
	private void drawExecutionTip(Graphics2D g) {
		
		ChainExecutionData exec= currentExecution.getChainExecution();
		System.err.println("getting tip for exection.."+exec);
		System.err.println("chain objecdt is "+exec.getChain());
		String chain = exec.getChain().getName();
		String dataset = exec.getDataset().getName();
		
		g.setFont(tipFont);
		FontMetrics metrics = g.getFontMetrics(tipFont);
		
		int height = 3* metrics.getHeight();
		System.err.println("getting width of "+chain);
		int width = metrics.stringWidth(chain);
		int newWidth = metrics.stringWidth(dataset);
		if (newWidth > width)
			width = newWidth;
		
		// date string
		Date date = exec.getDate();
		// formaat is like "Sat Jan 24 2004"
		SimpleDateFormat strFormat = new SimpleDateFormat("EEE MMM dd yyyy");
		
		String date1 = strFormat.format(date);
		newWidth = metrics.stringWidth(date1.toString());
		//newWidth = metrics.stringWidth(date.toString());
		if (newWidth > width)
			width = newWidth;
		
		//this format is "18:41:41 EST" 
		strFormat = new SimpleDateFormat("kk:mm:ss zzz");
		String date2 = strFormat.format(date);
		newWidth = metrics.stringWidth(date2.toString());
		if (newWidth > width)
			width = newWidth;
		

		int x = xLoc;
		int y = yLoc;
		
		//		 eventually, adjust xLoc,yLoc
		// to account for going over side
		if (x+width > gridModel.getHorizMax())
			x -= width;
		else // give it some spacing to the right, as
			// cursor goes to the right
			x += SPACING;
			
		
		if (y+height > gridModel.getVertStart())
			y -= height;
			
		g.drawString(chain,x,y);
		y+=metrics.getHeight();
		g.drawString(dataset,x,y);
		y+=metrics.getHeight();
		g.drawString(date1,x,y);
		y+=metrics.getHeight();
		g.drawString(date2,x,y);
		
	}
}
