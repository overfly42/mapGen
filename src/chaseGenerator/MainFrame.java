package chaseGenerator;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SingleSelectionModel;
import javax.swing.filechooser.FileFilter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.SDDocument;

public class MainFrame {

	private static final String MAP = "map";
	private static final String FIELD_CONFIG = "f_config";

	// Gui
	private JFrame frame;
	private FieldPanel fp;
	private SidePanel sp;
	private JPanel controls;
	private JPanel container;
	private JTabbedPane tabs;
	private JButton btnLoad;
	private JButton btnSave;
	private JButton btnSaveEnv;
	private JButton btnPrint;
	private JFileChooser fc;
	private CardLayout layout;

	// Inteligence - Basic Terrain types
	private EnvData enviroment;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame window = new MainFrame();
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
	public MainFrame() {
		initialize();
		fc = new JFileChooser();
		fc.addChoosableFileFilter(new FileFilter() {

			@Override
			public String getDescription() {

				return "*.data";
			}

			@Override
			public boolean accept(File arg0) {
				if (arg0.isDirectory())
					return true;
				return arg0.getName().endsWith(".data");
			}
		});
		loadElements();
		initTabs();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		layout = new CardLayout();
		container = new JPanel();
		container.setLayout(layout);

		tabs = new JTabbedPane();

		// tabs.add(new TerrainConfig(new TerrainModel(),enviroment), "+");

		fp = new FieldPanel();
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		controls = new JPanel();
		frame.getContentPane().add(controls, BorderLayout.SOUTH);

		sp = new SidePanel();
		frame.getContentPane().add(sp, BorderLayout.EAST);

		btnLoad = new JButton("load");
		controls.add(btnLoad);
		btnLoad.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				load();

			}

		});

		btnSave = new JButton("save");
		controls.add(btnSave);
		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				save();

			}

		});

		btnSaveEnv = new JButton("save terrain types");
		controls.add(btnSaveEnv);
		btnSaveEnv.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveElements();

			}
		});

		JButton generate = new JButton("generate Terrain");
		controls.add(generate);

		btnPrint = new JButton("print");
		btnPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				print();
			}
		});
		controls.add(btnPrint);
		generate.addActionListener(fp);

		fp.setBounds(0, 0, fp.getPreferredSize().height, fp.getPreferredSize().width);
		fp.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				fp.click(e);

			}
		});

		container.add(fp, MAP);
		container.add(tabs, FIELD_CONFIG);
		frame.getContentPane().add(container, BorderLayout.CENTER);
		frame.setTitle("World Generator");
		createMenuBar();
	}

	private void createMenuBar() {
		JMenuBar jmb = new JMenuBar();
		JMenu mainMenu = new JMenu("Menu");
		JMenu addMenu = new JMenu("add");
		JMenu help = new JMenu("Help");

		jmb.add(mainMenu);
		jmb.add(addMenu);
		jmb.add(help);

		help.add(new JMenuItem("There is no help where you will go"));

		JMenuItem jmi = new JMenuItem("Show Map");
		mainMenu.add(jmi);
		jmi.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				showTab(MAP);
			}
		});
		jmi = new JMenuItem("Show Terrain");
		jmi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showTab(FIELD_CONFIG);

			}
		});
		mainMenu.add(jmi);
		jmi = new JMenuItem("Exit");
		jmi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				exit();

			}
		});
		mainMenu.addSeparator();
		mainMenu.add(jmi);

		jmi = new JMenuItem("new Terrain");
		jmi.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addTerrain();

			}
		});
		addMenu.add(jmi);

		frame.setJMenuBar(jmb);
	}

	private void showTab(String tap) {
		layout.show(container, tap);
	}

	private void exit() {

		frame.dispose();
	}

	private void load() {
		fc.showOpenDialog(frame);

		File f = fc.getSelectedFile();
		if (fc == null)
			return;
		JAXBContext c;
		try {
			c = JAXBContext.newInstance(Field.class);
			Unmarshaller u = c.createUnmarshaller();
			// u.setProperty(Unmar, "true");
			FileInputStream fis = new FileInputStream(f);
			fp.setData((Field) u.unmarshal(fis));
			fis.close();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void save() {
		try {
			JAXBContext c = JAXBContext.newInstance(Field.class);
			Marshaller m = c.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			FileOutputStream fos = new FileOutputStream(new File("field.data"));
			m.marshal(fp.getData(), fos);
			fos.close();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void print() {
		FileOutputStream fos;
		try {
			fos = (new FileOutputStream(new File("words.txt")));
		} catch (FileNotFoundException e) {
			System.out.println("Fehler beim print");
			return;
		}
		PrintStream ps = new PrintStream(fos);
		Field f = fp.getData();
		String[] dir = new String[4];
		dir[0] = "Norden";
		dir[1] = "Osten ";
		dir[2] = "Süden ";
		dir[3] = "Westen";
		for (int i = 0; i < f.getFields(); i++) {
			for (int n = 0; n < f.getFields(); n++) {
				FieldObject d = f.get(i, n);

				ps.print("Pos x=" + (i + 1) + " y=" + (n + 1) + " ");
				ps.println("\t" + d.getArea().toString());
				ps.println("\t\t" + "Überlebenskunst SG: 10+ " + d.survival);
				ps.println("\t\t" + "Wahrnehmung     SG: 10+ " + d.perception);
				for (int j = 0; j < 4; j++) {
					ps.println("\t\t" + dir[j] + "\t(" + d.nextField[j][0].type.toString() + ") SG: "
							+ d.nextField[j][0].sg);
					ps.println("\t\t\t(" + d.nextField[j][1].type.toString() + ") SG: " + d.nextField[j][1].sg);
				}
				if (d.getTrap() != null)
					ps.println("\t\t\tFalle: " + d.getTrap().toString());
				ps.println();
			}
		}
		ps.close();
	}

	private void saveElements() {
		try {
			JAXBContext c = JAXBContext.newInstance(EnvData.class);
			Marshaller m = c.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			FileOutputStream fos = new FileOutputStream(new File("env.data"));
			m.marshal(enviroment, fos);
			fos.close();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadElements() {
		JAXBContext c;
		try {
			c = JAXBContext.newInstance(EnvData.class);
			Unmarshaller u = c.createUnmarshaller();
			FileInputStream fis = new FileInputStream(new File("env.data"));
			enviroment = (EnvData) u.unmarshal(fis);
			fp.setEnv(enviroment);
			sp.setEnviroment(enviroment);
			fis.close();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Run only once at start time
	 */
	private void initTabs() {
		if (enviroment == null)
			return;
		for (TerrainModel tm : enviroment.fields) {
			tabs.add(tm.getName(), new TerrainConfig(tm, enviroment, this));
		}
	}

	public void changeTabName(String name) {
		System.out.println(tabs.getTitleAt(tabs.getSelectedIndex()));
		tabs.setTitleAt(tabs.getSelectedIndex(), name);
	}

	public void deleteTab() {
		String name = tabs.getTitleAt(tabs.getSelectedIndex());
		tabs.remove(tabs.getSelectedIndex());
		TerrainModel tmDel = null;
		for (TerrainModel tm : enviroment.fields)
			if (tm.getName().equals(name))
				tmDel = tm;
		if (tmDel != null)
			enviroment.fields.remove(tmDel);
		sp.setEnviroment(enviroment);
		enviroment.update();
	}

	private void addTerrain() {
		String showInputDialog = JOptionPane.showInputDialog("Please set the new Name" );
		if (showInputDialog == null)
			return;
		System.out.println("Adding Terrain: "+showInputDialog);
		TerrainModel tm = new TerrainModel();
		tm.setChoosen(false);
		tm.setName(showInputDialog);
		enviroment.fields.add(tm);
		tabs.add(showInputDialog, new TerrainConfig(tm, enviroment, this));
		sp.setEnviroment(enviroment);
		tabs.setSelectedIndex(tabs.getTabCount()-1);
		enviroment.update();
	}
}