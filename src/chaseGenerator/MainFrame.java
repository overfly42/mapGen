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
import java.util.jar.JarFile;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class MainFrame {

	private JFrame frame;
	private FieldPanel fp;
	private SidePanel sp;
	private JPanel panel;
	private JButton btnLoad;
	private JButton btnSave;
	private JButton btnPrint;
	private JFileChooser fc;
	private EnvData eviroment;

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
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		fp = new FieldPanel();
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);

		sp = new SidePanel();
		frame.getContentPane().add(sp, BorderLayout.EAST);

		btnLoad = new JButton("load");
		panel.add(btnLoad);
		btnLoad.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				load();

			}

		});

		btnSave = new JButton("save");
		panel.add(btnSave);
		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				save();

			}

		});

		JButton generate = new JButton("generate Terrain");
		panel.add(generate);

		btnPrint = new JButton("print");
		btnPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				print();
			}
		});
		panel.add(btnPrint);
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
		frame.getContentPane().add(fp, BorderLayout.CENTER);
		frame.setTitle("World Generator");
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
			m.marshal(eviroment, fos);
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
			eviroment = (EnvData) u.unmarshal(fis);
			fp.setEnv(eviroment);
			sp.setEnviroment(eviroment);
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
}