import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.net.MalformedURLException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JList;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.xml.sax.SAXException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import javax.swing.JTabbedPane;

public class Interface extends JFrame {
	/*
	 * Interface is the main class of the program It launches the interface of
	 * the program and allows the user to search essay mills
	 */

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private PrintWriter searchWriter;
	private DefaultListModel<String> searchlistM;
	private DefaultListModel<String> refListM;
	private JFileChooser fChooser;
	public static File assignmentFile;
	private static JTable table;
	private static JTable upworkTable;
	private DefaultTableModel tableModel;
	private DefaultTableModel upworkTableModel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Interface frame = new Interface();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Interface() throws IOException {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 676, 422);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnAddSearchWord = new JButton("Add");
		/* search word list add button actionlistener */
		btnAddSearchWord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				searchWriter = null;
				try {
					searchWriter = new PrintWriter(new FileWriter(
							"searchWords.txt", true));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				String keyword = (String) JOptionPane.showInputDialog(
						contentPane, "Enter keyword:");
				searchWriter.println(keyword);

				searchWriter.close();
				System.out.println(keyword);
				searchlistM.addElement(keyword);

			}
		});
		btnAddSearchWord.setBounds(10, 148, 89, 23);
		contentPane.add(btnAddSearchWord);

		BufferedReader searchReader = null;
		try {
			searchReader = new BufferedReader(new FileReader(new File(
					"searchWords.txt")));
		} catch (FileNotFoundException e) {
			System.out.println("Search file not found");
			e.printStackTrace();
		}

		String line;
		searchlistM = new DefaultListModel<String>();
		while ((line = searchReader.readLine()) != null) {
			searchlistM.addElement(line);
		}

		BufferedReader refReader = null;
		try {
			refReader = new BufferedReader(new FileReader(new File(
					"refWords.txt")));
		} catch (FileNotFoundException e) {
			System.out.println("Search file not found");
			e.printStackTrace();
		}

		String refLine;
		refListM = new DefaultListModel<String>();
		while ((refLine = refReader.readLine()) != null) {
			refListM.addElement(refLine);
		}

		JScrollPane searchScrollPane = new JScrollPane();
		searchScrollPane.setBounds(10, 30, 188, 118);
		contentPane.add(searchScrollPane);

		JList<String> searchList = new JList<String>();
		searchScrollPane.setViewportView(searchList);
		searchList.setModel(searchlistM);

		JScrollPane refScrollPane = new JScrollPane();
		refScrollPane.setBounds(224, 30, 188, 118);
		contentPane.add(refScrollPane);

		JList<String> refList = new JList<String>();
		refList.setModel(refListM);
		refScrollPane.setViewportView(refList);

		JButton btnRefAdd = new JButton("Add");
		btnRefAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				searchWriter = null;
				try {
					searchWriter = new PrintWriter(new FileWriter(
							"refWords.txt", true));
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				String keyword = (String) JOptionPane.showInputDialog(
						contentPane, "Enter keyword:");
				searchWriter.println(keyword);

				searchWriter.close();
				System.out.println(keyword);
				refListM.addElement(keyword);
			}
		});
		btnRefAdd.setBounds(224, 148, 89, 23);
		contentPane.add(btnRefAdd);

		JLabel lblSearchWords = new JLabel("Search Words");
		lblSearchWords.setBounds(10, 13, 89, 14);
		contentPane.add(lblSearchWords);

		JLabel lblNewLabel = new JLabel("Refined Search Words");
		lblNewLabel.setBounds(224, 13, 175, 14);
		contentPane.add(lblNewLabel);

		JButton btnSearchClear = new JButton("Clear List");
		/* action listener to clear search words lsit */
		btnSearchClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					new PrintWriter("searchWords.txt").close();
				} catch (FileNotFoundException e) {
					System.out
							.println("Cannot clear searchWords.txt. File not found");
					e.printStackTrace();
				}
				searchlistM.clear();

			}
		});
		btnSearchClear.setBounds(109, 148, 89, 23);
		contentPane.add(btnSearchClear);

		JButton btnRefClear = new JButton("Clear List");
		/* action listener to clear the refined keywords */
		btnRefClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					new PrintWriter("refWords.txt").close();
				} catch (FileNotFoundException e1) {
					System.out
							.println("Cannot clear refWords.txt. File not found");
					e1.printStackTrace();
				}
				refListM.clear();

			}
		});
		btnRefClear.setBounds(323, 148, 89, 23);
		contentPane.add(btnRefClear);

		JLabel lblAssignmentFile = new JLabel("Assignment File:");
		lblAssignmentFile.setBounds(422, 32, 125, 14);
		contentPane.add(lblAssignmentFile);

		JButton btnSelectFile = new JButton("Select File");
		/* action listener to select file */
		btnSelectFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// file selection
				fChooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"Text Files", "txt", "pdf");
				fChooser.setFileFilter(filter);
				int returnVal = fChooser.showOpenDialog(contentPane);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					System.out.println("You chose to open this file: "
							+ fChooser.getSelectedFile().getName());

				}
				assignmentFile = fChooser.getSelectedFile();
			}

		});
		btnSelectFile.setBounds(422, 52, 144, 23);
		contentPane.add(btnSelectFile);

		String col[] = { "URL", "Number of keyword matches", "File found",
				"File Matched" }; // column names for rent-acoder

		tableModel = new DefaultTableModel(col, 0);

		String upworkCol[] = { "URL", "Number of keyword matches",
				"File found", "File Matched" }; // column names for upwork

		upworkTableModel = new DefaultTableModel(upworkCol, 0);

		JButton btnSearchRent = new JButton("Search Rent-aCoder.com");
		/* Action listener to start the rent-aCoder search */
		btnSearchRent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// start search
				try {
					try {
						RacLaunch.go();
					} catch (SAXException e1) {
						e1.printStackTrace();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				System.out.println("Search has finished");
				populateTable();
				table.repaint();

				tableModel.fireTableDataChanged();
			}
		});
		btnSearchRent.setBounds(422, 171, 215, 29);
		contentPane.add(btnSearchRent);

		JButton btnSaveResults = new JButton("Save Results to File");
		/* Action listener to save results to text */
		btnSaveResults.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!RacRequest.racList.isEmpty()) {
					PrintWriter out = null;
					for (RacRequest descObj : RacRequest.racList) {
						try {
							out = new PrintWriter("results.txt");
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
						out.println(descObj.toString());

					}
					out.close();
				}

				if (!UpworkRequest.upworkList.isEmpty()) {
					PrintWriter upworkOut = null;
					for (UpworkRequest upworkReq : UpworkRequest.upworkList) {
						try {
							upworkOut = new PrintWriter("upworkResults.txt");

						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
						upworkOut.println(upworkReq.toString());
					}
					upworkOut.close();
					JOptionPane
							.showMessageDialog(contentPane,
									"Results saved to results.txt and upworkResults.txt");
				}
			}

		});
		btnSaveResults.setBounds(422, 86, 144, 23);
		contentPane.add(btnSaveResults);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 188, 640, 185);
		contentPane.add(tabbedPane);

		JScrollPane scrollPane = new JScrollPane();
		tabbedPane.addTab("Rent-acoder Results", null, scrollPane, null);
		table = new JTable();
		table.setModel(tableModel);
		scrollPane.setViewportView(table);

		JScrollPane scrollPane_1 = new JScrollPane();
		tabbedPane.addTab("Upwork Results", null, scrollPane_1, null);

		upworkTable = new JTable();
		upworkTable.setModel(upworkTableModel);
		scrollPane_1.setViewportView(upworkTable);

		JButton btnNewButton = new JButton("Search Upwork.com");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					UpworkLaunch.go();
				} catch (FailingHttpStatusCodeException | IOException
						| SAXException e) {
					e.printStackTrace();

				}
				populateTable();
				upworkTable.repaint();
				upworkTableModel.fireTableDataChanged();
			}
		});
		btnNewButton.setBounds(422, 131, 215, 29);
		contentPane.add(btnNewButton);

	}

	/*
	 * This method populates the tables with the gathered results
	 */
	private void populateTable() {
		for (RacRequest descObj : RacRequest.racList) {

			if (descObj.getMatch()) {
				Object data[] = { descObj.getUrl(), descObj.getHitCount(),
						descObj.getFoundFile(), descObj.getFileMatched() };
				tableModel.addRow(data);
			}
		}
		for (UpworkRequest reqObj : UpworkRequest.upworkList) {
			if (reqObj.getMatch()) {
				Object data[] = { reqObj.getUrl(), reqObj.getHitCount(),
						reqObj.getFoundFile(), reqObj.getFileMatched() };
				upworkTableModel.addRow(data);
			}
		}

	}

	public static void repaintTable() {
		table.repaint();
		upworkTable.repaint();
	}
}
