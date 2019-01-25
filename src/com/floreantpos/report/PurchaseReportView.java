package com.floreantpos.report;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JRViewer;

import org.jdesktop.swingx.JXDatePicker;

import com.floreantpos.PosLog;
import com.floreantpos.main.Application;
import com.floreantpos.model.InventoryItem;
import com.floreantpos.model.InventoryVendor;
import com.floreantpos.model.Restaurant;
import com.floreantpos.model.dao.InventoryItemDAO;
import com.floreantpos.model.dao.TerminalDAO;
import com.floreantpos.swing.TransparentPanel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

public class PurchaseReportView extends TransparentPanel {
	private JButton btnGo;
	// private JComboBox cbTerminal;
	private JXDatePicker fromDatePicker;
	private JXDatePicker toDatePicker;
	private JPanel reportPanel;
	private JPanel contentPane;

	// private JComboBox cbUserType;

	public PurchaseReportView() {
		// cbUserType.setModel(new DefaultComboBoxModel(new
		// String[]{com.floreantpos.POSConstants.ALL,
		// com.floreantpos.POSConstants.SERVER,
		// com.floreantpos.POSConstants.CASHIER,
		// com.floreantpos.POSConstants.MANAGER}));

		TerminalDAO terminalDAO = new TerminalDAO();
		List terminals = terminalDAO.findAll();
		terminals.add(0, com.floreantpos.POSConstants.ALL);
		// cbTerminal.setModel(new ListComboBoxModel(terminals));

		setLayout(new BorderLayout());
		add(contentPane);

		btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InventoryItemDAO dao = new InventoryItemDAO();
				List<InventoryItem> findPayroll = dao.findAll();
				viewReport(findPayroll);
			}
		});
	}

	public PurchaseReportView(final List<InventoryItem> inventoryList) {
		// cbUserType.setModel(new DefaultComboBoxModel(new
		// String[]{com.floreantpos.POSConstants.ALL,
		// com.floreantpos.POSConstants.SERVER,
		// com.floreantpos.POSConstants.CASHIER,
		// com.floreantpos.POSConstants.MANAGER}));

		TerminalDAO terminalDAO = new TerminalDAO();
		List terminals = terminalDAO.findAll();
		terminals.add(0, com.floreantpos.POSConstants.ALL);
		// cbTerminal.setModel(new ListComboBoxModel(terminals));

		setLayout(new BorderLayout());
		add(contentPane);
		viewReport(inventoryList);

		/*
		 * btnGo.addActionListener(new ActionListener() { public void
		 * actionPerformed(ActionEvent e) {
		 * 
		 * } });
		 */
	}

	private void viewReport(List<InventoryItem> inventList) {

		try {
			JasperReport report = ReportUtil.getReport("purchaseReport"); //$NON-NLS-1$

			HashMap properties = new HashMap();
			ReportUtil.populateRestaurantProperties(properties);
			properties.put("reportDate", new Date()); //$NON-NLS-1$
			properties.put("reportTitle", "Purchase Order");

			Restaurant restaurant = Application.getInstance().getRestaurant();

			properties.put("companyName", restaurant.getName());
			properties.put("address", restaurant.getAddressLine1());
			properties.put("city", restaurant.getAddressLine2());
			properties.put("phone", restaurant.getTelephone());
			properties.put("fax", restaurant.getZipCode());
			properties.put("email", restaurant.getAddressLine3());

			InventoryItem inventoryItem = inventList.get(0);
			InventoryVendor itemVendor = inventoryItem.getItemVendor();

			if (itemVendor != null) {
				properties.put("vCompanyName", itemVendor.getName());
				properties.put("vAddress", itemVendor.getAddress());
				properties.put("vCity", itemVendor.getCity());
				properties.put("vPhone", itemVendor.getPhone());
				properties.put("vFax", itemVendor.getFax());
				properties.put("vEmail", itemVendor.getEmail());
			}

			PurchaseReportModel reportModel = new PurchaseReportModel();
			reportModel.setRows(inventList);

			JasperPrint print = JasperFillManager.fillReport(report, properties, new JRTableModelDataSource(reportModel));

			JRViewer viewer = new JRViewer(print);
			reportPanel.removeAll();
			reportPanel.add(viewer);
			reportPanel.revalidate();
		} catch (JRException e) {
			PosLog.error(getClass(), e);
		}
	}

}
