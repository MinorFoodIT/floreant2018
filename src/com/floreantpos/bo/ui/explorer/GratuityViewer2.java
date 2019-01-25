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
package com.floreantpos.bo.ui.explorer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;

import com.floreantpos.POSConstants;
import com.floreantpos.PosException;
import com.floreantpos.bo.ui.BOMessageDialog;
import com.floreantpos.main.Application;
import com.floreantpos.model.ActionHistory;
import com.floreantpos.model.Gratuity;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.ActionHistoryDAO;
import com.floreantpos.model.dao.GratuityDAO;
import com.floreantpos.model.dao.UserDAO;
import com.floreantpos.swing.ListComboBoxModel;
import com.floreantpos.swing.ListTableModel;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.util.NumberUtil;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

/**
 * Created by IntelliJ IDEA.
 * User: mshahriar
 * Date: Feb 19, 2007
 * Time: 12:06:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class GratuityViewer2 extends TransparentPanel implements ActionListener {
    private JComboBox cbUsers;
    private JButton btnGo;
    private JLabel lblUserId;
    private JLabel lblUserName;
    private JLabel lblTotalGratuity;
    private JTable tableGratuityViewer;
    private JButton btnPay;
    private JPanel contentPane;

    private GratuityTableModel gratuityTableModel;

    /**
     * Creates new form GratuityViewer
     */
    public GratuityViewer2() {
        UserDAO userDAO = new UserDAO();
        List<User> users = userDAO.findAll();

        cbUsers.setModel(new ListComboBoxModel(users));
        tableGratuityViewer.setModel(gratuityTableModel = new GratuityTableModel(null));

        btnGo.addActionListener(this);
        btnPay.setEnabled(false);
        btnPay.addActionListener(this);

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLayout(new BorderLayout());
        add(contentPane);
    }

    private class GratuityTableModel extends ListTableModel {

        public GratuityTableModel(List<Gratuity> gratuities) {
            super(new String[]{com.floreantpos.POSConstants.FIRST_NAME, com.floreantpos.POSConstants.LAST_NAME, com.floreantpos.POSConstants.TICKET_ID, com.floreantpos.POSConstants.AMOUNT}, gratuities);
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            Gratuity gratuity = (Gratuity) rows.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return gratuity.getOwner().getFirstName();

                case 1:
                    return gratuity.getOwner().getLastName();

                case 2:
                    return gratuity.getTicket().getId();

                case 3:
                    return NumberUtil.formatNumber(gratuity.getAmount());
            }
            return null;
        }

    }

    public void showGratuity(User user) {
        GratuityDAO dao = new GratuityDAO();
        List<Gratuity> gratuities = dao.findByUser(user);

        double totalGratuity = 0;
        for (Gratuity gratuity : gratuities) {
            totalGratuity += gratuity.getAmount();
        }
        lblUserId.setText(String.valueOf(user.getUserId()));
        lblUserName.setText(user.getFirstName() + " " + user.getLastName()); //$NON-NLS-1$
        lblTotalGratuity.setText(NumberUtil.formatNumber(totalGratuity));
        gratuityTableModel.setRows(gratuities);

        if (gratuities.size() > 0) {
            btnPay.setEnabled(true);
        } else {
            btnPay.setEnabled(false);
        }
    }

    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (com.floreantpos.POSConstants.GO.equalsIgnoreCase(actionCommand)) {
            User user = (User) cbUsers.getSelectedItem();
            if (user != null) {
                showGratuity(user);
            }
        }

        if (com.floreantpos.POSConstants.PAY.equalsIgnoreCase(actionCommand)) {
            try {
                List rows = gratuityTableModel.getRows();
                if (rows != null) {
                    new GratuityDAO().payGratuities(rows);
                }
                btnPay.setEnabled(false);

//				PAY TIPS ACTION
                String actionMessage = com.floreantpos.POSConstants.PAY_TIPS;
                ActionHistoryDAO.getInstance().saveHistory(Application.getCurrentUser(), ActionHistory.PAY_TIPS, actionMessage);

            } catch (PosException ex) {
                BOMessageDialog.showError(contentPane, ex.getMessage(), ex);
            }
        }
    }

}
