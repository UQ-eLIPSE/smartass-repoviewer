/* This file is part of SmartAss and contains the PdfView class that represents the
 * PDF viewer.
 *
 * Copyright (C) 2008 The University of Queensland
 * SmartAss is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2, or
 * (at your option) any later version.
 * GNU program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with program;
 * see the file COPYING. If not, write to the
 * Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package au.edu.uq.smartassrepoeditor.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PagePanel;

/**
 * The PdfView class is the PDF viewer.
 */
public class PdfView extends JPanel {
	PagePanel panel;
	JPanel button_panel;
	JButton next, prev, plus, minus;

	PDFFile pdffile;
	int current_page;

	boolean first_show= true;

	public PdfView(File file) {
		super(new BorderLayout());
		button_panel = new JPanel();
		button_panel.setLayout(new BoxLayout(button_panel, BoxLayout.Y_AXIS));
		add(button_panel, BorderLayout.WEST);
		JButton bt;
		button_panel.add(prev = new JButton("Previous"));
		prev.setPreferredSize(new Dimension(90, 20));
		prev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prevPage();
			}
		});
		button_panel.add(Box.createRigidArea(new Dimension(0,10)));
		button_panel.add(next = new JButton("Next"));
		next.setPreferredSize(new Dimension(90, 20));
		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nextPage();
			}
		});
		button_panel.add(Box.createRigidArea(new Dimension(0,10)));
		button_panel.add(plus = new JButton("+"));
		plus.setPreferredSize(new Dimension(90, 20));
		plus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Dimension pd = panel.getCurSize();
				pd = new Dimension(pd.width*4/3, pd.height*4/3);
				panel.setPreferredSize(pd);
				panel.setSize(pd);
				panel.repaint();
			}
		});
		button_panel.add(Box.createRigidArea(new Dimension(0,10)));
		button_panel.add(minus = new JButton("-"));
		minus.setMinimumSize(new Dimension(90, 20));
		minus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Dimension pd = panel.getCurSize();
				pd = new Dimension(pd.width/4*3, pd.height/4*3);
				panel.setPreferredSize(pd);
				panel.setSize(pd);
				panel.validate();
			}
		});

		panel = new PagePanel();
		try {
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			FileChannel channel = raf.getChannel();
			ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY,
					0, channel.size());
			pdffile = new PDFFile(buf);
		} catch(Exception e) {}
		add(new JScrollPane(panel));
		panel.repaint();
		//panel.useZoomTool(true);

		// show the first page
		showPage(1);
	}

	public void showPage(int pageno) {
		if(pageno>pdffile.getNumPages())
			pageno = pdffile.getNumPages();
		if(pageno<1)
			pageno=1;
		PDFPage page = pdffile.getPage(pageno);
		panel.showPage(page);
		current_page = pageno;
		prev.setEnabled(pageno!=1);
		next.setEnabled(pageno!=pdffile.getNumPages());
		panel.repaint();
	}

	public void refresh() {
		showPage(current_page);
	}

	public int getPageNo() {
		return current_page;
	}

	public void nextPage() {
		showPage(current_page+1);
	}

	public void prevPage() {
		showPage(current_page-1);
	}

	@Override
	protected void paintChildren(Graphics g) {
		//workaround the bug in PagePanel - if component is invisible during
		//showPage, PdfRenderer says "No page selected"
		if(first_show) {
			Dimension sz = panel.getSize();
			if(sz.height!=0 || sz.width!=0) {
				refresh();
				first_show = false;
			}
		}
		super.paintChildren(g);
	}


}
