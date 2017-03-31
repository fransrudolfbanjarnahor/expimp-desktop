package main;

import java.awt.Color;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import sql.DBConfig;
import sql.DBType;
import sql.Database;
import sql.SQLOperation;
import xls.XLExport;
import xls.XLReader;

import javax.swing.JFileChooser;
import javax.swing.JPasswordField;

public class MainApp {

	private JFrame frame;
	private JTextField txtSourceHost;
	private JTextField txtSourcePort;
	private JTextField txtSourceUser;
	private JPasswordField txtSourcePass;
	private JTextField txtSourceDBName;
	Database dbSource;
	Database dbTarget;
	Connection srcCon;
	Connection trgCon;
	private JTextField txtTargetHost;
	private JTextField txtTargetPort;
	private JTextField txtTargetUser;
	private JPasswordField txtTargetPass;
	private JTextField txtTargetDBName;
	JPanel panelSource;
	JComboBox<Object> cboSourceDBMS;
	JComboBox<Object> cboTargetDB;
	JLabel lblDbms;
	private JButton btnImportDataFrom;
	private JButton btnShowSourceData;
	private JButton btnShowTargetData;
	private Boolean excelSource = false;
	private String excelPath;
	List listTargetTable = new List();
	List listSourceTable = new List();
	List listMapField = new List();
	List listSourceField;
	JLabel h2PathTarget;
	JLabel h2Path;
	List listTargetField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainApp window = new MainApp();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainApp() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 786, 555);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		panelSource = new JPanel();
		panelSource.setBounds(6, 29, 360, 170);
		frame.getContentPane().add(panelSource);
		panelSource.setLayout(null);

		txtSourceHost = new JTextField("");
		txtSourceHost.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		txtSourceHost.setBounds(204, 6, 79, 28);
		panelSource.add(txtSourceHost);
		txtSourceHost.setColumns(10);

		txtSourcePort = new JTextField();
		txtSourcePort.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		txtSourcePort.setBounds(284, 6, 47, 28);
		panelSource.add(txtSourcePort);
		txtSourcePort.setColumns(10);
		txtSourceUser = new JTextField();
		txtSourceUser.setFont(new Font("Lucida Grande", Font.PLAIN, 10));

		cboSourceDBMS = new JComboBox<Object>();
		cboSourceDBMS.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		cboTargetDB = new JComboBox<Object>();
		cboTargetDB.setFont(new Font("Lucida Grande", Font.PLAIN, 10));

		for (DBType db : DBType.values()) {
			cboSourceDBMS.addItem(db);
			cboTargetDB.addItem(db);
		}

		cboSourceDBMS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch (DBType.valueOf(cboSourceDBMS.getSelectedItem().toString())) {
				case MYSQL:
					txtSourcePort.setText("3306");
					break;
				case POSTGRESQL:
					txtSourcePort.setText("5432");
					break;
				case SQLSERVER:
					txtSourcePort.setText("1433");
					break;
				default:
					break;
				}
			}
		});

		cboTargetDB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch (DBType.valueOf(cboTargetDB.getSelectedItem().toString())) {
				case MYSQL:
					txtTargetPort.setText("3306");
					break;
				case POSTGRESQL:
					txtTargetPort.setText("5432");
					break;
				case SQLSERVER:
					txtTargetPort.setText("1433");
					break;
				default:
					break;
				}
			}
		});

		cboSourceDBMS.setBounds(99, 7, 105, 27);
		panelSource.add(cboSourceDBMS);

		lblDbms = new JLabel("DBMS / Host:port");
		lblDbms.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblDbms.setBounds(6, 10, 110, 16);
		panelSource.add(lblDbms);

		listSourceField = new List();
		listSourceField.setMultipleSelections(true);
		listSourceField.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		listSourceField.setForeground(Color.MAGENTA);
		listSourceField.setBackground(Color.YELLOW);
		// listSourceField.setMultipleSelections(false);
		listSourceField.setBounds(206, 233, 160, 78);
		frame.getContentPane().add(listSourceField);

		listSourceTable.setFont(new Font("Lucida Grande", Font.PLAIN, 10));

		// listSourceTable.setMultipleSelections(false);
		listSourceTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SQLOperation sql = new SQLOperation(srcCon);
				sql.setTable(listSourceTable.getSelectedItem());
				sql.Execute();
				listSourceField.removeAll();
				listMapField.removeAll();
				System.out.println(sql.getFieldNameAndType());
				for (String s : sql.getFieldNameAndType().keySet()) {

					listSourceField.add(s);
				}
			}
		});

		listSourceTable.setBounds(10, 233, 190, 78);

		txtSourceUser.setColumns(10);
		txtSourceUser.setBounds(99, 61, 105, 28);
		panelSource.add(txtSourceUser);

		JLabel lblUserDb = new JLabel("user / pass db");
		lblUserDb.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblUserDb.setBounds(6, 66, 81, 16);
		panelSource.add(lblUserDb);

		txtSourcePass = new JPasswordField();
		txtSourcePass.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		txtSourcePass.setColumns(10);
		txtSourcePass.setBounds(204, 61, 79, 28);
		panelSource.add(txtSourcePass);

		JButton btnConnectSource = new JButton("Connect");
		btnConnectSource.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		btnConnectSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					DBConfig dbSrcCOnf = new DBConfig(DBType.valueOf(cboSourceDBMS.getSelectedItem().toString()),
							txtSourceHost.getText(), txtSourcePort.getText(), txtSourceDBName.getText(),
							txtSourceUser.getText(), new String(txtSourcePass.getPassword()));
					System.out.println(dbSrcCOnf.toString());
					dbSource = new Database(dbSrcCOnf);
					srcCon = dbSource.openConnection();
					listSourceTable.removeAll();
					for (String s : dbSource.getAllTable()) {
						listSourceTable.add(s);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, "Can't connect to database");
				}
			}
		});

		btnConnectSource.setBounds(284, 61, 73, 28);
		panelSource.add(btnConnectSource);

		txtSourceDBName = new JTextField();
		txtSourceDBName.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		txtSourceDBName.setColumns(10);
		txtSourceDBName.setBounds(99, 33, 105, 28);
		panelSource.add(txtSourceDBName);

		JLabel lblDbName = new JLabel("DB Name");
		lblDbName.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblDbName.setBounds(6, 38, 61, 16);
		panelSource.add(lblDbName);

		JButton btnH2 = new JButton("H2");
		h2Path = new JLabel("h2 path");
		btnH2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(System.getProperty("user.home")));
				int result = fc.showOpenDialog(frame);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fc.getSelectedFile();
					h2Path.setText(selectedFile.getAbsolutePath().replace(".mv.db", ""));
					DBConfig dbSrcCOnf = new DBConfig(DBType.H2, h2Path.getText(), "", "", txtSourceUser.getText(),
							new String(txtSourcePass.getPassword()));
					dbSource = new Database(dbSrcCOnf);
					srcCon = dbSource.openConnection();
					listSourceTable.removeAll();
					for (String s : dbSource.getAllTable()) {
						listSourceTable.add(s);
					}
				}

			}
		});
		btnH2.setBounds(0, 101, 87, 29);
		panelSource.add(btnH2);

		h2Path.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		h2Path.setBounds(99, 107, 255, 16);
		panelSource.add(h2Path);

		JButton xlsSource = new JButton("Excel");
		xlsSource.setBounds(0, 135, 87, 29);
		panelSource.add(xlsSource);
		xlsSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(System.getProperty("user.home")));
				int result = fc.showOpenDialog(frame);
				excelSource = true;

				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fc.getSelectedFile();
					excelPath = selectedFile.getAbsolutePath();
					java.util.List<String> kolom = new XLReader().firstRowAsColumn(selectedFile.getAbsolutePath());
					listSourceField.removeAll();
					for (String s : kolom) {
						listSourceField.add(s);
					}
				}
			}
		});

		frame.getContentPane().add(listSourceTable);

		JPanel panelTarget = new JPanel();
		panelTarget.setLayout(null);
		panelTarget.setBounds(378, 29, 360, 170);
		frame.getContentPane().add(panelTarget);

		txtTargetHost = new JTextField();
		txtTargetHost.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		txtTargetHost.setColumns(10);
		txtTargetHost.setBounds(201, 6, 86, 28);
		panelTarget.add(txtTargetHost);

		txtTargetPort = new JTextField();
		txtTargetPort.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		txtTargetPort.setColumns(10);
		txtTargetPort.setBounds(287, 6, 46, 28);
		panelTarget.add(txtTargetPort);

		cboTargetDB.setBounds(95, 6, 105, 27);
		panelTarget.add(cboTargetDB);

		JLabel label = new JLabel("DBMS / Host:port");
		label.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		label.setBounds(6, 10, 110, 16);
		panelTarget.add(label);

		txtTargetUser = new JTextField();
		txtTargetUser.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		txtTargetUser.setColumns(10);
		txtTargetUser.setBounds(95, 58, 105, 28);
		panelTarget.add(txtTargetUser);

		JLabel lblUserPass = new JLabel("user / pass db");
		lblUserPass.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblUserPass.setBounds(6, 66, 86, 16);
		panelTarget.add(lblUserPass);

		txtTargetPass = new JPasswordField();
		txtTargetPass.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		txtTargetPass.setColumns(10);
		txtTargetPass.setBounds(201, 58, 86, 28);
		panelTarget.add(txtTargetPass);

		listTargetTable.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		// listTargetTable.setMultipleSelections(false);
		listTargetTable.setBounds(542, 233, 196, 78);
		frame.getContentPane().add(listTargetTable);

		listTargetField = new List();
		listTargetField.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		// listTargetField.setMultipleSelections(false);
		listTargetField.setForeground(Color.MAGENTA);
		listTargetField.setBackground(Color.YELLOW);
		listTargetField.setBounds(376, 233, 160, 78);

		frame.getContentPane().add(listTargetField);
		listTargetTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SQLOperation sql = new SQLOperation(trgCon);
				sql.setTable(listTargetTable.getSelectedItem());
				sql.Execute();
				listTargetField.removeAll();
				listMapField.removeAll();
				for (String s : sql.getFieldNameAndType().keySet()) {
					listTargetField.add(s);
				}
			}
		});

		JButton btnConnectTarget = new JButton("Connect");
		btnConnectTarget.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		btnConnectTarget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DBConfig dbTrgCOnf = new DBConfig(DBType.valueOf(cboTargetDB.getSelectedItem().toString()),
						txtTargetHost.getText(), txtTargetPort.getText(), txtTargetDBName.getText(),
						txtTargetUser.getText(), new String(txtTargetPass.getPassword()));
				// System.out.println(dbTrgCOnf.toString());
				// if (dbTarget == null) {
				dbTarget = new Database(dbTrgCOnf);
				trgCon = dbTarget.openConnection();
				// } else {
				// try {
				// trgCon.close();
				// } catch (SQLException e1) {
				// // TODO Auto-generated catch block
				// e1.printStackTrace();
				// }
				// dbTarget.closeConnection();
				// dbTarget = new Database(dbTrgCOnf);
				// trgCon = dbTarget.openConnection();
				// }

				listTargetTable.removeAll();
				for (String s : dbTarget.getAllTable()) {
					listTargetTable.add(s);
				}
			}

		});
		btnConnectTarget.setBounds(287, 58, 74, 28);
		panelTarget.add(btnConnectTarget);

		txtTargetDBName = new JTextField();
		txtTargetDBName.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		txtTargetDBName.setColumns(10);
		txtTargetDBName.setBounds(95, 30, 105, 28);
		panelTarget.add(txtTargetDBName);

		JLabel label_2 = new JLabel("DB Name");
		label_2.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		label_2.setBounds(6, 38, 61, 16);
		panelTarget.add(label_2);

		h2PathTarget = new JLabel("h2 path");
		h2PathTarget.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		h2PathTarget.setBounds(95, 105, 259, 16);
		panelTarget.add(h2PathTarget);

		JButton btnH2Target = new JButton("H2");
		btnH2Target.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File("/Volumes/DATA/"));
				int result = fc.showOpenDialog(frame);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fc.getSelectedFile();
					h2PathTarget.setText(selectedFile.getAbsolutePath().replace(".mv.db", ""));
					DBConfig dbTrgCOnf = new DBConfig(DBType.H2, h2PathTarget.getText(), "", "",
							txtTargetUser.getText(), new String(txtTargetPass.getPassword()));
					System.out.println(dbTrgCOnf.toString());
					dbTarget = new Database(dbTrgCOnf);
					trgCon = dbTarget.openConnection();
					listTargetTable.removeAll();
					for (String s : dbTarget.getAllTable()) {
						listTargetTable.add(s);
					}
				}
			}
		});
		btnH2Target.setBounds(0, 99, 92, 29);
		panelTarget.add(btnH2Target);

		listMapField.setBounds(10, 380, 356, 112);
		frame.getContentPane().add(listMapField);

		JButton btnMapField = new JButton("Map Field");
		btnMapField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// System.out.println(listSourceField.getSelectedItem() + " - "
				// + listTargetField.getSelectedItem());
				listMapField.add(listSourceField.getSelectedItem().toString() + " - "
						+ listTargetField.getSelectedItem().toString());
				listSourceField.remove(listSourceField.getSelectedIndex());
				listTargetField.remove(listTargetField.getSelectedIndex());

			}
		});
		btnMapField.setBounds(318, 317, 117, 29);
		frame.getContentPane().add(btnMapField);

		btnImportDataFrom = new JButton("move data from source to target");
		btnImportDataFrom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String srcField = "";
				String trgField = "";
				java.util.List<String> trgListField = new ArrayList<String>();
				Map<String, String> mapField = new HashMap<String, String>();
				int x = 1;
				for (String s : listMapField.getItems()) {
					String temp[] = s.replace(" ", "").split("-");
					mapField.put(temp[1], temp[0]);
					if (x < listMapField.getItemCount()) {

						srcField += temp[0] + ",";
						trgField += temp[1] + ",";
					} else {
						srcField += temp[0];
						trgField += temp[1];
					}
					trgListField.add(temp[0]);
					x++;
				}

				// System.out.println(srcField + " " + trgField);
				if (!excelSource) {
					SQLOperation sqlSrc = new SQLOperation(srcCon);
					SQLOperation sqlTrg = new SQLOperation(trgCon);
					sqlSrc.setTable(listSourceTable.getSelectedItem(), srcField);
					sqlTrg.setTable(listTargetTable.getSelectedItem(), trgField);
					System.out.println(srcField + " -> " + trgField);
					sqlSrc.Execute();
					sqlTrg.Execute();
					ResultSet rsSrc = sqlSrc.getResultset();
					java.util.List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

					try {
						rsSrc.beforeFirst();
						while (rsSrc.next()) {
							Map<String, Object> row = new HashMap<String, Object>();
							for (String s : listMapField.getItems()) {
								String temp[] = s.replace(" ", "").split("-");
								row.put(temp[1], rsSrc.getObject(temp[0]));
								// System.out.println(temp[1] + " " +
								// rsSrc.getObject(temp[0]));
							}
							list.add(row);
						}

						sqlTrg.SQLInsertList(list, trgField);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					SQLOperation sqlTrg = new SQLOperation(trgCon);
					sqlTrg.setTable(listTargetTable.getSelectedItem(), trgField);
					sqlTrg.Execute();
					XLReader xlReader = new XLReader();
					xlReader.setFieldString(srcField);
					java.util.List<Map<String, Object>> excelData = new XLReader().readAsMap(excelPath);
					// System.out.println(excelData.size());
					// for (String f : trgListField) {
					java.util.List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
					// System.out.print(excelData.size());
					for (Map<String, Object> map : excelData) {
						Map<String, Object> row = new LinkedHashMap<String, Object>();
						for (String f : mapField.keySet()) {
							// System.out.println(map.get(mapField.get(f)) + "
							// ");
							row.put(f, map.get(mapField.get(f)));
						}
						list.add(row);
						// System.out.println("");
					}

					// System.out.print(sqlTrg.getDebugSQL() + " " +
					// excelData.size() + " " + list.size());
					sqlTrg.SQLInsertList(list, trgField);
				}

			}

		});
		btnImportDataFrom.setBounds(6, 498, 360, 29);
		frame.getContentPane().add(btnImportDataFrom);

		btnShowSourceData = new JButton("Show Source Data");
		btnShowSourceData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JFrame frameSource = new ShowTableData(srcCon, listSourceTable.getSelectedItem().toString());
				frameSource.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frameSource.setVisible(true);

			}
		});
		btnShowSourceData.setBounds(6, 317, 176, 29);
		frame.getContentPane().add(btnShowSourceData);

		btnShowTargetData = new JButton("Show Target Data");
		btnShowTargetData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JFrame frameTarget = new ShowTableData(trgCon, listTargetTable.getSelectedItem().toString());
				frameTarget.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frameTarget.setVisible(true);
			}
		});
		btnShowTargetData.setBounds(578, 317, 160, 29);
		frame.getContentPane().add(btnShowTargetData);

		JLabel lblNewLabel = new JLabel("Source Database");
		lblNewLabel.setBounds(6, 6, 360, 16);
		frame.getContentPane().add(lblNewLabel);

		JLabel lblTargetDatabase = new JLabel("Target Database");
		lblTargetDatabase.setBounds(378, 6, 360, 16);
		frame.getContentPane().add(lblTargetDatabase);

		JLabel lblSourceTable = new JLabel("Source Table");
		lblSourceTable.setBounds(10, 211, 91, 16);
		frame.getContentPane().add(lblSourceTable);

		JLabel lblSourceField = new JLabel("Source Field");
		lblSourceField.setBounds(206, 211, 158, 16);
		frame.getContentPane().add(lblSourceField);

		JLabel label_1 = new JLabel("Source Field");
		label_1.setBounds(378, 211, 158, 16);
		frame.getContentPane().add(label_1);

		JLabel label_3 = new JLabel("Source Table");
		label_3.setBounds(542, 211, 196, 16);
		frame.getContentPane().add(label_3);

		JButton btnEmptyTable = new JButton("empty table");
		btnEmptyTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// JOptionPane.showOptionDialog(null,
				// "Are you sure want to empty table", "Empty Table ",
				// optionType, messageType, icon, options, initialValue)
				SQLOperation sqlDelete = new SQLOperation(trgCon);
				sqlDelete.resetAutoIncrement();
				sqlDelete.setTable(listTargetTable.getSelectedItem().toString());
				sqlDelete.DeleteData();
				sqlDelete.Execute();
				// System.out.println(sqlDelete.getDatabaseMeta());
			}
		});
		btnEmptyTable.setBounds(463, 317, 117, 29);
		frame.getContentPane().add(btnEmptyTable);

		JLabel lblMappingSourceTo = new JLabel("Mapping source to target");
		lblMappingSourceTo.setBounds(10, 358, 337, 16);
		frame.getContentPane().add(lblMappingSourceTo);

		JButton btnExcel = new JButton("to excel");
		btnExcel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SQLOperation sql = new SQLOperation(srcCon);
				sql.setTable(listSourceTable.getSelectedItem());
				sql.Execute();
				// String[] selectedField = new
				// String[listSourceField.getSelectedItems().l];
				// for (String s: listSourceField.getSelectedItems()) {
				//
				// }
				XLExport export = new XLExport(listSourceTable.getSelectedItem().toString());
				// System.out.println(sql.getListMapRow().size());
				String EXTENSION = ".xls";

				if (listSourceField.getSelectedItems().length > 0) {

					JFileChooser fc = new JFileChooser();
					int status = fc.showSaveDialog(frame);
					fc.setCurrentDirectory(new File(System.getProperty("user.home")));
					if (status == JFileChooser.APPROVE_OPTION) {
						File selectedFile = fc.getSelectedFile();

						try {
							String fileName = selectedFile.getAbsolutePath();
							if (!fileName.endsWith(EXTENSION)) {
								selectedFile = new File(fileName + EXTENSION);
							}
							System.out.println("Export to: " + selectedFile.getCanonicalPath());
							export.toFile(selectedFile.getCanonicalPath(), listSourceField.getSelectedItems(),
									sql.getListMapRow());

							// export.toFile("/Volumes/DATA",
							// listSourceTable.getSelectedItem() + ".xls",
							// listSourceField.getSelectedItems(),
							// sql.getListMapRow());
						} catch (IOException e2) {
							e2.printStackTrace();
						}
					}
				} else {
					JOptionPane.showMessageDialog(frame, "Select Source Field", "Info", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		btnExcel.setBounds(178, 317, 78, 29);
		frame.getContentPane().add(btnExcel);

	}
}
