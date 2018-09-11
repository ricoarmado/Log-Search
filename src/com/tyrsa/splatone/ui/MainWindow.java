package com.tyrsa.splatone.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.print.attribute.standard.JobMessageFromOperator;
import javax.swing.BoxLayout;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JInternalFrame;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;
import javax.swing.tree.DefaultTreeModel;

import com.tyrsa.splatone.model.AsyncReader;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainWindow extends JFrame {

	private JPanel contentPane;
	private JTextField pathTextField;
	private String initPath;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		initPath = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
		
	
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 550);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
		
		JDesktopPane desktopPane = new JDesktopPane();
		desktopPane.setBackground(Color.WHITE);
		contentPane.add(desktopPane);
		
		JLabel lblNewLabel = new JLabel("\u041A\u043E\u0440\u043D\u0435\u0432\u043E\u0439 \u043A\u0430\u0442\u0430\u043B\u043E\u0433");
		lblNewLabel.setBounds(12, 13, 167, 16);
		desktopPane.add(lblNewLabel);
		
		JTree tree = new JTree();
		tree.setBorder(new LineBorder(new Color(0, 0, 0)));
		tree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("Root") {
				{
				}
			}
		));
		tree.setBounds(12, 136, 256, 344);
		desktopPane.add(tree);
		
		pathTextField = new JTextField();
		pathTextField.setEditable(false);
		pathTextField.setText("C:\\");
		pathTextField.setBounds(12, 42, 256, 22);
		desktopPane.add(pathTextField);
		pathTextField.setColumns(10);
		pathTextField.setText(initPath);
		
		JButton selectButton = new JButton("\u0412\u044B\u0431\u0440\u0430\u0442\u044C");
		selectButton.setBounds(280, 41, 97, 25);
		desktopPane.add(selectButton);
		selectButton.addActionListener((event) -> {
			JFileChooser jfc = new JFileChooser(initPath);
			int returnValue = jfc.showOpenDialog(null);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File selectedFile = jfc.getSelectedFile();
				initPath = selectedFile.getAbsolutePath();
				pathTextField.setText(initPath);
			}
		});
		
		JButton btnNewButton_1 = new JButton("\u0417\u0430\u043F\u0443\u0441\u043A");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					AsyncReader.run(initPath);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Ошибка при чтении файла", "ОШИБКА", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				} catch (InterruptedException e) {
					JOptionPane.showMessageDialog(null, "Ошибка при работе с потоком", "ОШИБКА", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		});
		btnNewButton_1.setBounds(280, 79, 97, 25);
		desktopPane.add(btnNewButton_1);
		
		JLabel label = new JLabel("\u041D\u0430\u0439\u0434\u0435\u043D\u043D\u044B\u0439 \u0442\u0435\u0441\u0442");
		label.setBounds(12, 107, 167, 16);
		desktopPane.add(label);
		
		JTextArea textArea = new JTextArea();
		textArea.setBackground(UIManager.getColor("Button.background"));
		textArea.setWrapStyleWord(true);
		textArea.setBounds(553, 47, 307, 326);
		desktopPane.add(textArea);
	}
}
