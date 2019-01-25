/**
 * ************************************************************************
 * * The contents of this file are subject to the MRPL 1.2
 * * (the  "License"),  being   the  Mozilla   Public  License
 * * Version 1.1  with a permitted attribution clause; you may not  use this
 * * file except in compliance with the License. You  may  obtain  a copy of
 * * the License at http://www.floreantpos.org/license.html
 * * Software distributed under the License  is  distributed  on  an "AS IS"
 * * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * * License for the specific  language  governing  rights  and  limitations
 * * under the License.
 * * The Original Code is FLOREANT POS.
 * * The Initial Developer of the Original Code is OROCUBE LLC
 * * All portions are Copyright (C) 2015 OROCUBE LLC
 * * All Rights Reserved.
 * ************************************************************************
 */
package com.floreantpos.ui.model;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import com.floreantpos.model.Shift;
import com.floreantpos.model.dao.ShiftDAO;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.util.POSUtil;
import com.floreantpos.util.ShiftUtil;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

public class ShiftEntryDialog extends POSDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JComboBox startHour;
	private JComboBox startMin;
	private JComboBox startAmPm;
	private JComboBox endHour;
	private JComboBox endAmPm;
	private JComboBox endMin;
	private JTextField tfShiftName;

	private Vector<Integer> hours;
	private Vector<Integer> mins;

	private Shift shift;
	private Date shiftStart, shiftEnd;

	public ShiftEntryDialog() {
		this(null);
	}

	public ShiftEntryDialog(Shift shift) {
		super(POSUtil.getBackOfficeWindow(), true);
		setTitle(com.floreantpos.POSConstants.NEW_SHIFT);

		setContentPane(contentPane);
		getRootPane().setDefaultButton(buttonOK);

		hours = new Vector<Integer>();
		for (int i = 1; i <= 12; i++) {
			hours.add(Integer.valueOf(i));
		}

		mins = new Vector<Integer>();
		for (int i = 0; i < 60; i++) {
			mins.add(Integer.valueOf(i));
		}

		startHour.setModel(new DefaultComboBoxModel(hours));
		endHour.setModel(new DefaultComboBoxModel(hours));

		startMin.setModel(new DefaultComboBoxModel(mins));
		endMin.setModel(new DefaultComboBoxModel(mins));

		buttonOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});

		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});

		// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

		// call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		setSize(350, 250);

		setShift(shift);
	}

	private boolean calculateShifts() {
		int hour1 = ((Integer) startHour.getSelectedItem()).intValue();
		int hour2 = ((Integer) endHour.getSelectedItem()).intValue();
		int min1 = ((Integer) startMin.getSelectedItem()).intValue();
		int min2 = ((Integer) endMin.getSelectedItem()).intValue();
		int ampm1 = startAmPm.getSelectedIndex();
		int ampm2 = endAmPm.getSelectedIndex();

		shiftStart = ShiftUtil.buildShiftStartTime(hour1, min1, ampm1, hour2, min2, ampm2);
		shiftEnd = ShiftUtil.buildShiftEndTime(hour1, min1, ampm1, hour2, min2, ampm2);

		if (!shiftEnd.after(shiftStart)) {
			POSMessageDialog.showError(this, com.floreantpos.POSConstants.SHIFT_END_TIME_MUST_BE_GREATER_THAN_SHIFT_START_TIME);
			return false;
		}
		return true;
	}

	private void onOK() {
		try {
			if (!updateModel())
				return;

			ShiftDAO dao = new ShiftDAO();
			if (shift.getId() == null && dao.exists(shift.getName())) {
				POSMessageDialog.showError(this, com.floreantpos.POSConstants.SHIFT_NAME_ALREADY_EXISTS);
				return;
			}
			dao.saveOrUpdate(shift);

			setCanceled(false);

			dispose();
		} catch (Exception e) {
			POSMessageDialog.showError(this, com.floreantpos.POSConstants.ERROR_SAVING_SHIFT_STATE, e);
		}
	}

	private void onCancel() {
		setCanceled(true);
		dispose();
	}

	public Date getShiftStart() {
		return shiftStart;
	}

	public Date getShiftEnd() {
		return shiftEnd;
	}

	public void updateView() {
		if (shift == null) {
			return;
		}

		tfShiftName.setText(shift.getName());

		Date startTime = shift.getStartTime();
		Date endTime = shift.getEndTime();

		Calendar c = Calendar.getInstance();
		c.setTime(startTime);

		startHour.setSelectedIndex(c.get(Calendar.HOUR) - 1);
		startMin.setSelectedIndex(c.get(Calendar.MINUTE));
		startAmPm.setSelectedIndex(c.get(Calendar.AM_PM) == Calendar.AM ? 0 : 1);

		c.setTime(endTime);
		endHour.setSelectedIndex(c.get(Calendar.HOUR) - 1);
		endMin.setSelectedIndex(c.get(Calendar.MINUTE));
		endAmPm.setSelectedIndex(c.get(Calendar.AM_PM) == Calendar.AM ? 0 : 1);
	}

	public boolean updateModel() {
		if (!calculateShifts())
			return false;

		if (shift == null) {
			shift = new Shift();
		}
		shift.setName(tfShiftName.getText());
		shift.setStartTime(shiftStart);
		shift.setEndTime(shiftEnd);

		Calendar c = Calendar.getInstance();
		c.setTime(shiftStart);

		long length = Math.abs(shiftStart.getTime() - shiftEnd.getTime());

		shift.setShiftLength(Long.valueOf(length));

		return true;
	}

	public Shift getShift() {
		return shift;
	}

	public void setShift(Shift shift) {
		this.shift = shift;

		updateView();
	}

}
