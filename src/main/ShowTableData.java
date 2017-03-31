package main;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import ff.db.SQLOperation;

import javax.swing.JTable;
import java.awt.Font;
import javax.swing.ScrollPaneConstants;
import java.awt.FlowLayout;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.ListSelectionModel;
import java.awt.GridLayout;

public class ShowTableData extends JFrame {

	private JPanel contentPane;
	private Connection con;
	private String tableName;
	private JTable table;
	JScrollPane scrollPane;

	public ShowTableData(Connection con, String tableName) {
		this.con = con;
		this.tableName = tableName;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 978, 387);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		try {
			table = new JTable(fillData());
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setColumnSelectionAllowed(true);
			table.setBorder(new LineBorder(new Color(0, 0, 0)));
			table.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
			table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		contentPane.setLayout(new GridLayout(0, 1, 0, 0));

		scrollPane = new JScrollPane(table);
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		contentPane.add(scrollPane);

	}

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
}
