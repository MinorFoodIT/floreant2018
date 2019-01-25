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
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import com.floreantpos.model.MenuItemShift;
import com.floreantpos.model.Shift;
import com.floreantpos.model.dao.ShiftDAO;
import com.floreantpos.swing.ListComboBoxModel;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

public class MenuItemShiftDialog extends POSDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox cbShifts;
    private JTextField tfPrice;

    private MenuItemShift menuItemShift;

    public MenuItemShiftDialog(Frame owner) {
    	super(owner,true);
        setContentPane(contentPane);

        ShiftDAO dao = new ShiftDAO();
        List<Shift> shifts = dao.findAll();
        cbShifts.setModel(new ListComboBoxModel(shifts));
        if (shifts.size() == 0) {
            buttonOK.setEnabled(false);
        }

        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

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


        setMenuItemShift(menuItemShift);
    }

    private void onOK() {
        if (!updateModel()) return;

        try {
            //MenuItemShiftDAO dao = new MenuItemShiftDAO();
            //dao.saveOrUpdate(menuItemShift);
            setCanceled(false);
            dispose();
        } catch (Exception e) {
            POSMessageDialog.showError(this, com.floreantpos.POSConstants.ERROR_MESSAGE, e);
        }
    }

    private void onCancel() {
        setCanceled(true);
        dispose();
    }


    private void updateView() {
        if (menuItemShift == null) return;

        cbShifts.setSelectedItem(menuItemShift.getShift());
        tfPrice.setText(String.valueOf(menuItemShift.getShiftPrice()));
    }

    public boolean updateModel() {
        double price = 0;
        try {
            price = Double.parseDouble(tfPrice.getText());
        } catch (Exception x) {
            POSMessageDialog.showError(this, com.floreantpos.POSConstants.PRICE_IS_NOT_VALID_);
            return false;
        }
        if (menuItemShift == null) {
            menuItemShift = new MenuItemShift();
        }
        menuItemShift.setShift((Shift) cbShifts.getSelectedItem());
        menuItemShift.setShiftPrice(price);

        return true;
    }

    public MenuItemShift getMenuItemShift() {
        return menuItemShift;
    }

    public void setMenuItemShift(MenuItemShift menuItemShift) {
        this.menuItemShift = menuItemShift;

        updateView();
    }

}
