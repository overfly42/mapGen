package chaseGenerator.gui;

import java.awt.EventQueue;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SingleSelectionModel;
import javax.swing.filechooser.FileFilter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.sun.activation.registries.MailcapFile;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.SDDocument;

import chaseGenerator.data.EnvData;
import chaseGenerator.data.Field;
import chaseGenerator.data.FieldObject;
import chaseGenerator.models.AllDataTable;
import chaseGenerator.models.ObjectModel;
import chaseGenerator.models.TerrainModel;

public class MainFrame {

	private static final String MAP = "map";
	private static final String FIELD_CONFIG = "f_config";
	private static final String CONFIG = "cfg";
	private static final String OBJ_CONFIG = "o_config";

	// Gui
	private JFrame frame;
	private JPanel controls;
	private JPanel container;
	private JTabbedPane tabTerrain;
	private JTabbedPane tabObjects;
	private JFileChooser mapFile;
	private JFileChooser envFile;
	private CardLayout layout;

	// GUI - customized
	private SidePanel sp;
	private Config cfg;

	// Inteligence - Basic Terrain types
	private EnvData enviroment;
	private AllDataTable allData;
	private FieldPanel fp;

	// Startup
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
		createFileChoosers();
		loadElements();
		initialize();
		allData = new AllDataTable(enviroment);
		initTabs();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		layout = new CardLayout();
		container = new JPanel();
		container.setLayout(layout);

		tabTerrain = new JTabbedPane();
		tabObjects = new JTabbedPane();

		fp = new FieldPanel();
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fp.setEnv(enviroment);

		controls = new JPanel();
		frame.getContentPane().add(controls, BorderLayout.SOUTH);

		sp = new SidePanel();
		frame.getContentPane().add(sp, BorderLayout.EAST);
		sp.setEnviroment(enviroment);

		cfg = new Config(fp.getData(), enviroment);

		JButton generateRandom = new JButton("generate random Terrain");
		generateRandom.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fp.generateRandomTerrain();

			}
		});
		controls.add(generateRandom);

		JButton generateConfig = new JButton("generate Terrain from Config");
		generateConfig.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fp.generateConfigTerrain();

			}
		});
		controls.add(generateConfig);

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
		container.add(tabTerrain, FIELD_CONFIG);
		container.add(cfg, CONFIG);
		container.add(tabObjects, OBJ_CONFIG);
		frame.getContentPane().add(container, BorderLayout.CENTER);
		frame.setTitle("World Generator");
		createMenuBar();

	}

	private void createFileChoosers() {
		mapFile = new JFileChooser(".");
		mapFile.setSelectedFile(new File("field.data"));
		mapFile.addChoosableFileFilter(new FileFilter() {

			@Override
			public String getDescription() {

				return "*.dat (XML - Format)";
			}

			@Override
			public boolean accept(File arg0) {
				if (arg0.getAbsolutePath().endsWith(".data"))
					return true;
				if (arg0.isDirectory())
					return true;
				return false;
			}
		});
		envFile = new JFileChooser(".");
		envFile.setSelectedFile(new File("env.data"));
		envFile.addChoosableFileFilter(mapFile.getChoosableFileFilters()[0]);
	}

	private void createMenuBar() {
		JMenuBar jmb = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenu viewMenu = new JMenu("View");
		JMenu addMenu = new JMenu("add");
		JMenu help = new JMenu("Help");

		jmb.add(fileMenu);
		jmb.add(viewMenu);
		jmb.add(addMenu);
		jmb.add(help);

		help.add(new JMenuItem("There is no help where you will go"));

		///////////////////// View//////////////////////////
		JMenuItem jmi = new JMenuItem("Show Map");
		viewMenu.add(jmi);
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
		viewMenu.add(jmi);
		jmi = new JMenuItem("Show Field Objects");
		jmi.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				showTab(OBJ_CONFIG);

			}
		});
		viewMenu.add(jmi);
		jmi = new JMenuItem("Config");
		jmi.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				showTab(CONFIG);

			}
		});
		viewMenu.add(jmi);
		///////////////////// File//////////////////////////
		jmi = new JMenuItem("Load Map");
		jmi.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int ua = mapFile.showOpenDialog(frame);
				if (ua == JFileChooser.CANCEL_OPTION)
					return;

				load();

			}
		});
		fileMenu.add(jmi);
		jmi = new JMenuItem("Save Map");
		jmi.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int showSaveDialog = mapFile.showSaveDialog(frame);
				if (showSaveDialog == JFileChooser.CANCEL_OPTION)
					return;
				save();

			}
		});
		fileMenu.add(jmi);
		jmi = new JMenuItem("Print");
		jmi.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				print();

			}
		});
		fileMenu.add(jmi);
		fileMenu.addSeparator();
		jmi = new JMenuItem("Load Terrain Config");
		jmi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int userInput = envFile.showOpenDialog(frame);
				if (userInput == JFileChooser.CANCEL_OPTION)
					return;
				loadElements();
			}
		});
		fileMenu.add(jmi);
		jmi = new JMenuItem("Save Terrain Config");
		jmi.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int userInput = envFile.showSaveDialog(frame);
				if (userInput == JFileChooser.CANCEL_OPTION)
					return;

				saveElements();

			}
		});
		fileMenu.add(jmi);
		jmi = new JMenuItem("Exit");
		jmi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				exit();

			}
		});
		fileMenu.addSeparator();
		fileMenu.add(jmi);
		///////////////////// add///////////////////////////
		jmi = new JMenuItem("new Terrain");
		jmi.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addTerrain();

			}
		});
		addMenu.add(jmi);
		jmi = new JMenuItem("new Field Object");
		jmi.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				addObject();

			}
		});
		addMenu.add(jmi);
		frame.setJMenuBar(jmb);
	}

	// Load and Save
	private void exit() {

		frame.dispose();
	}

	private void load() {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(mapFile.getSelectedFile()));
			Object o = ois.readObject();
			if (Field.class.isAssignableFrom(o.getClass()))
				fp.setData((Field) o);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void save() {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(mapFile.getSelectedFile()));
			oos.writeObject(fp.getData());
			oos.close();
		} catch (IOException e) {
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
			FileOutputStream fos = new FileOutputStream(envFile.getSelectedFile());
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
		if (!envFile.getSelectedFile().exists()) {
			enviroment = new EnvData();
			return;
		}
		JAXBContext c;
		try {
			c = JAXBContext.newInstance(EnvData.class);
			Unmarshaller u = c.createUnmarshaller();
			FileInputStream fis = new FileInputStream(envFile.getSelectedFile());
			enviroment = (EnvData) u.unmarshal(fis);
			fis.close();
			for (TerrainModel tm : enviroment.fields)
				tm.setEnviroment(enviroment);
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
		// Terrains
		for (TerrainModel tm : enviroment.fields) {
			ScrollPane sp = new ScrollPane();
			sp.add(new TerrainConfig(tm, enviroment, this));
			tabTerrain.add(tm.getName(), sp);
		}
		JTable jt = new JTable(allData);
		jt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tabTerrain.add("All Terrains", new JScrollPane(jt));
		// Objects
		for (ObjectModel om : enviroment.objects) {
			ScrollPane sp = new ScrollPane();
			sp.add(new ObjectConfig(om, enviroment, this));
			tabObjects.add(om.getName(), sp);
		}
	}

	// Tab usages
	private void showTab(String tap) {
		layout.show(container, tap);
	}

	public void changeTerrainTabName(String name) {

		tabTerrain.setTitleAt(tabTerrain.getSelectedIndex(), name);
	}

	public void changeObjectTabName(String name) {
		tabObjects.setTitleAt(tabObjects.getSelectedIndex(), name);
	}

	public void deleteTab() {
		String name = tabTerrain.getTitleAt(tabTerrain.getSelectedIndex());
		tabTerrain.remove(tabTerrain.getSelectedIndex());
		TerrainModel tmDel = null;
		for (TerrainModel tm : enviroment.fields)
			if (tm.getName().equals(name))
				tmDel = tm;
		if (tmDel != null)
			enviroment.fields.remove(tmDel);
		sp.setEnviroment(enviroment);
		enviroment.update();
	}

	// Adding ...

	private void addTerrain() {
		String showInputDialog = JOptionPane.showInputDialog("Please set the new Name");
		if (showInputDialog == null)
			return;
		System.out.println("Adding Terrain: " + showInputDialog);
		TerrainModel tm = new TerrainModel();
		tm.setEnviroment(enviroment);
		tm.setChoosen(false);
		tm.setName(showInputDialog);
		enviroment.fields.add(tm);
		ScrollPane sp = new ScrollPane();
		sp.add(new TerrainConfig(tm, enviroment, this));
		tabTerrain.add(showInputDialog, sp);
		this.sp.setEnviroment(enviroment);
		tabTerrain.setSelectedIndex(tabTerrain.getTabCount() - 1);
		enviroment.update();
	}

	private void addObject() {
		String showInputDialog = JOptionPane.showInputDialog("Please set the new Name");
		if (showInputDialog == null)
			return;
		System.out.println("Adding Object: " + showInputDialog);
		ObjectModel om = new ObjectModel();
		om.setChoosen(false);
		om.setName(showInputDialog);
		enviroment.objects.add(om);
		ScrollPane sp = new ScrollPane();
		sp.add(new ObjectConfig(om, enviroment, this));
		tabObjects.add(showInputDialog, sp);
		this.sp.setEnviroment(enviroment);
		tabObjects.setSelectedComponent(sp);

	}
}