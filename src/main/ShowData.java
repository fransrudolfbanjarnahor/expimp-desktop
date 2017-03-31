package main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import sql.SQLOperation;

public class ShowData extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private Connection con;
	private String tableName;

	/**
	 * Launch the application.
	 */
	// public static void main(String[] args) {
	// try {
	// ShowData dialog = new ShowData();
	// dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	// dialog.setVisible(true);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	/**
	 * Create the dialog.
	 * 
	 * @throws SQLException
	 */
	public DefaultTableModel fillData() throws SQLException {

		SQLOperation sql = new SQLOperation(this.con);
		sql.setTable(this.tableName);
		sql.Execute();
		Vector<Object> columnNames = new Vector<Object>();
		Vector<Object> data = new Vector<Object>();
		ResultSet rs = sql.getResultset();
		ResultSetMetaData md = rs.getMetaData();
		int columns = md.getColumnCount();

		// Get column names

		for (int i = 1; i <= columns; i++) {
			columnNames.addElement(md.getColumnName(i));
		}

		// Get row data

		while (rs.next()) {
			Vector<Object> row = new Vector<Object>(columns);

			for (int i = 1; i <= columns; i++) {
				row.addElement(rs.getObject(i));
			}

			data.addElement(row);
		}
		DefaultTableModel model = new DefaultTableModel(data, columnNames) {
			@Override
			public Class getColumnClass(int column) {
				for (int row = 0; row < getRowCount(); row++) {
					Object o = getValueAt(row, column);

					if (o != null) {
						return o.getClass();
					}
				}

				return Object.class;
			}
		};

		return model;

	}

	public ShowData(Connection con, String tableName) throws SQLException {
		this.con = con;
		this.tableName = tableName;
		setBounds(100, 100, 533, 355);
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().setLayout(null);
		JTable table = new JTable(fillData());
		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane);
		JPanel buttonPanel = new JPanel();
		// JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

	}
}
