package com.tyrsa.splatone.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.tyrsa.splatone.model.AsyncReader;
import com.tyrsa.splatone.model.Tree;

public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField pathTextField;
	private String initPath;
	private String type;
	private String lex;
	private JTextField inputtextField;
	private JTextField typetextField;
	private JTree tree;
	private JTabbedPane tabbedPane;
	private Tree treeRoot;
	private JButton prevButton;
	private JButton nextButton;
	private JButton selectallButton;
	
	public void displayDirectoryContents(Tree dir,DefaultMutableTreeNode root2) throws InterruptedException 
	{   
	    DefaultMutableTreeNode newdir = new DefaultMutableTreeNode();   
	    CopyOnWriteArrayList<Tree> files = dir.getLeaves();
	    if(files.isEmpty()) {
	    	JOptionPane.showMessageDialog(null, "Ничего не найдено", "Инфо", JOptionPane.OK_OPTION);
	    }
	    else{
	    	for (Tree file : files){
		        if(file == null)
		        {
		            System.out.println("NUll directory found ");
		            continue;
		        }
		        if (file.getNode().isDirectory())
		        {
		            if (file.getNode().listFiles()==null)
		            {
		                continue;
		            }

		            DefaultTreeModel model =(DefaultTreeModel) tree.getModel();
		            DefaultMutableTreeNode root=(DefaultMutableTreeNode) model.getRoot();
		            newdir = new DefaultMutableTreeNode(file.getNode().getName());
		            root2.add(newdir);
		            model.reload();
		            displayDirectoryContents(file,newdir);
		        }
		        else
		        {
		            DefaultTreeModel model =(DefaultTreeModel) tree.getModel();
		            DefaultMutableTreeNode selectednode = root2;
		            DefaultMutableTreeNode newfile =new DefaultMutableTreeNode(file.getNode().getName());
		            model.insertNodeInto(newfile, selectednode, selectednode.getChildCount());
		            model.reload();
		        }
		    }   
	    }
	}

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
		setTitle("Splat Task One");
		initPath = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
		
	
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 1262, 800);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
		
		JDesktopPane desktopPane = new JDesktopPane();
		desktopPane.setBackground(Color.WHITE);
		contentPane.add(desktopPane);
		
		JLabel lblNewLabel = new JLabel("\u041A\u043E\u0440\u043D\u0435\u0432\u043E\u0439 \u043A\u0430\u0442\u0430\u043B\u043E\u0433");
		lblNewLabel.setBounds(12, 23, 167, 16);
		desktopPane.add(lblNewLabel);
		
		tree = new JTree();
		tree.setBorder(new LineBorder(new Color(0, 0, 0)));
		tree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("Root") {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;
				{
				}
			}
		));
		tree.setBounds(12, 224, 359, 490);
		tree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				TreePath pathForLocation = tree.getPathForLocation(me.getX(), me.getY());
				if(pathForLocation != null) {
					Object[] path =  pathForLocation.getPath();
					DefaultMutableTreeNode lastName = (DefaultMutableTreeNode) path[path.length - 1];
					String lastFileName = (String) lastName.getUserObject();
					if(lastFileName.contains(type)) {
						List<String> list = new ArrayList<>();
						for(Object obj : path) {
							DefaultMutableTreeNode tmp = (DefaultMutableTreeNode) obj;
							list.add((String) tmp.getUserObject());
						}
						list.remove(0);
						Tree result = treeRoot.search(list);
						boolean found = false;
						for (int i = 0; i < tabbedPane.getTabCount(); i++) {
							CustomPane childComponent = (CustomPane) tabbedPane.getComponentAt(i);
							if(childComponent.getNode().equals(result)) {
								found = true;
								tabbedPane.setSelectedIndex(i);
							}
						}
						if(!found) {
							try {
								tabbedPane.addTab(result.getNode().getName(), new CustomPane(result, lex));
							} catch (NullPointerException e) {
								System.out.println("Вы пытаетесь открыть несуществуюший файл");
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}							
						}
					}
				}
			}
		});
		desktopPane.add(tree);
		
		pathTextField = new JTextField();
		pathTextField.setEditable(false);
		//pathTextField.setText("C:\\files\\");
		pathTextField.setBounds(12, 42, 256, 22);
		desktopPane.add(pathTextField);
		pathTextField.setColumns(10);
		pathTextField.setText(initPath);
		
		
		JButton selectButton = new JButton("\u0412\u044B\u0431\u0440\u0430\u0442\u044C");
		selectButton.setBounds(280, 41, 97, 25);
		desktopPane.add(selectButton);
		selectButton.addActionListener((event) -> {
			JFileChooser jfc = new JFileChooser(initPath);
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
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
				new Thread(() -> {
					try {
						lex = inputtextField.getText();
						type = typetextField.getText();
						AsyncReader.run(initPath,lex,type,(root) -> {
							SwingUtilities.invokeLater(new Runnable() {
								
								@Override
								public void run() {
									treeRoot = root;
									DefaultTreeModel model =(DefaultTreeModel) tree.getModel();
								    DefaultMutableTreeNode _root=(DefaultMutableTreeNode) model.getRoot();
								    _root.removeAllChildren();
								    while(tabbedPane.getTabCount() > 0) {
								    	tabbedPane.remove(0);
								    }
								    try {
										displayDirectoryContents(root,_root);
									} catch (InterruptedException e) {
										JOptionPane.showMessageDialog(null, "Ошибка при визуализации дерева", "ОШИБКА", JOptionPane.ERROR_MESSAGE);
									}
								    prevButton.setEnabled(true);
								    nextButton.setEnabled(true);
								    selectallButton.setEnabled(true);
								}
							});
						});
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, "Ошибка при чтении файла", "ОШИБКА", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					} catch (InterruptedException e) {
						JOptionPane.showMessageDialog(null, "Ошибка при работе с потоком", "ОШИБКА", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
					
				}).start();
				
			}
		});
		btnNewButton_1.setBounds(280, 98, 97, 25);
		desktopPane.add(btnNewButton_1);
		
		JLabel label = new JLabel("\u041D\u0430\u0439\u0434\u0435\u043D\u043E \u0432 \u0444\u0430\u0439\u043B\u0430\u0445");
		label.setBounds(12, 195, 167, 16);
		desktopPane.add(label);
		
		JLabel label_1 = new JLabel("\u0412\u0445\u043E\u0434\u043D\u0430\u044F \u043B\u0435\u043A\u0441\u0435\u043C\u0430");
		label_1.setBounds(12, 83, 167, 16);
		desktopPane.add(label_1);
		
		inputtextField = new JTextField();
		inputtextField.setBounds(12, 99, 256, 22);
		desktopPane.add(inputtextField);
		inputtextField.setColumns(10);
		
		JLabel label_2 = new JLabel("\u0421\u043E\u0434\u0435\u0440\u0436\u0438\u043C\u043E\u0435 \u0444\u0430\u0439\u043B\u0430");
		label_2.setBounds(473, 23, 307, 16);
		desktopPane.add(label_2);
		
		JLabel label_3 = new JLabel("\u0422\u0438\u043F \u0444\u0430\u0439\u043B\u0430\r\n");
		label_3.setBounds(12, 134, 167, 16);
		desktopPane.add(label_3);
		
		typetextField = new JTextField();
		typetextField.setText(".log");
		typetextField.setColumns(10);
		typetextField.setBounds(12, 150, 256, 22);
		desktopPane.add(typetextField);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(473, 42, 749, 634);
		tabbedPane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				CustomPane pane = (CustomPane) tabbedPane.getSelectedComponent();
				tabbedPane.setSelectedComponent(pane);
				pane.update();
			}
		});
		desktopPane.add(tabbedPane);
		
		prevButton = new JButton("<<");
		prevButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread(() -> {
					
					
				}).start();
				int index = tabbedPane.getSelectedIndex();
				Component tmp = tabbedPane.getComponentAt(index);
				if(tmp != null) {
					CustomPane selectedPane = (CustomPane) tmp;
					selectedPane.selectPrevious();
				}
			}
		});
		prevButton.setEnabled(false);
		prevButton.setBounds(473, 689, 167, 25);
		desktopPane.add(prevButton);
		
		nextButton = new JButton(">>");
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(() -> {
					int index = tabbedPane.getSelectedIndex();
					Component tmp = tabbedPane.getComponentAt(index);
					if(tmp != null) {
						CustomPane selectedPane = (CustomPane) tmp;
						try {
							selectedPane.selectNext();
						} catch (BadLocationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
					}					
				}).start();

			}
		});
		nextButton.setEnabled(false);
		nextButton.setBounds(652, 689, 167, 25);
		desktopPane.add(nextButton);
		
		selectallButton = new JButton("\u0412\u044B\u0431\u0440\u0430\u0442\u044C \u0432\u0441\u0435");
		selectallButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int index = tabbedPane.getSelectedIndex();
					Component tmp = tabbedPane.getComponentAt(index);
					if(tmp != null) {
						CustomPane selectedPane = (CustomPane) tmp;
						selectedPane.selectAll();
					}	
				}catch(Exception ex) {
					System.err.println(ex.getMessage());
				}
			}
		});
		selectallButton.setEnabled(false);
		selectallButton.setBounds(915, 689, 307, 25);
		desktopPane.add(selectallButton);
	}
}
