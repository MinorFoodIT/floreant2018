
package com.floreantpos.report;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
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
import com.floreantpos.model.Restaurant;
import com.floreantpos.model.dao.InventoryItemDAO;
import com.floreantpos.model.dao.TerminalDAO;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.util.UiUtil;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

public class InventoryOnHandReportView extends TransparentPanel {
	private JButton btnGo;
	//private JComboBox cbTerminal;
	private JXDatePicker fromDatePicker;
	private JXDatePicker toDatePicker;
	private JPanel reportPanel;
	private JPanel contentPane;

	//private JComboBox cbUserType;

	public InventoryOnHandReportView() {
		//cbUserType.setModel(new DefaultComboBoxModel(new String[]{com.floreantpos.POSConstants.ALL, com.floreantpos.POSConstants.SERVER, com.floreantpos.POSConstants.CASHIER, com.floreantpos.POSConstants.MANAGER}));

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

	public InventoryOnHandReportView(final List<InventoryItem> inventoryList) {
		//cbUserType.setModel(new DefaultComboBoxModel(new String[]{com.floreantpos.POSConstants.ALL, com.floreantpos.POSConstants.SERVER, com.floreantpos.POSConstants.CASHIER, com.floreantpos.POSConstants.MANAGER}));

		TerminalDAO terminalDAO = new TerminalDAO();
		List terminals = terminalDAO.findAll();
		terminals.add(0, com.floreantpos.POSConstants.ALL);
		// cbTerminal.setModel(new ListComboBoxModel(terminals));

		setLayout(new BorderLayout());
		add(contentPane);
		viewReport(inventoryList);

		/*btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});*/
	}

	private void viewReport(List<InventoryItem> inventList) {
		Date fromDate = fromDatePicker.getDate();
		Date toDate = toDatePicker.getDate();

		if (fromDate.after(toDate)) {
			POSMessageDialog.showError(com.floreantpos.util.POSUtil.getFocusedWindow(), com.floreantpos.POSConstants.FROM_DATE_CANNOT_BE_GREATER_THAN_TO_DATE_);
			return;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.clear();

		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(fromDate);

		calendar.set(Calendar.YEAR, calendar2.get(Calendar.YEAR));
		calendar.set(Calendar.MONTH, calendar2.get(Calendar.MONTH));
		calendar.set(Calendar.DATE, calendar2.get(Calendar.DATE));
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		fromDate = calendar.getTime();

		calendar.clear();
		calendar2.setTime(toDate);
		calendar.set(Calendar.YEAR, calendar2.get(Calendar.YEAR));
		calendar.set(Calendar.MONTH, calendar2.get(Calendar.MONTH));
		calendar.set(Calendar.DATE, calendar2.get(Calendar.DATE));
		calendar.set(Calendar.HOUR, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		toDate = calendar.getTime();

		try {
			JasperReport report = ReportUtil.getReport("inventoryOnHandReport"); //$NON-NLS-1$

			HashMap properties = new HashMap();
			ReportUtil.populateRestaurantProperties(properties);
			properties.put("fromDate", fromDate); //$NON-NLS-1$
			properties.put("toDate", toDate); //$NON-NLS-1$
			properties.put("reportDate", new Date()); //$NON-NLS-1$
			properties.put("reportTitle", "Purchase Order");

			Restaurant restaurant = Application.getInstance().getRestaurant();

			properties.put("companyName", restaurant.getName());
			properties.put("address", restaurant.getAddressLine1());
			properties.put("city", restaurant.getAddressLine2());
			properties.put("phone", restaurant.getTelephone());
			properties.put("fax", restaurant.getZipCode());
			properties.put("email", restaurant.getAddressLine3());


			InventoryOnHandReportModel reportModel = new InventoryOnHandReportModel();
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
