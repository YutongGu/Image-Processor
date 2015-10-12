import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Stack;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class ImageEditor extends JFrame implements ActionListener, ChangeListener{
	private ImageCanvas orig, alter;
	String file = "./src/picture1.jpg";
	private Stack<int[][]> stack= new Stack();
	private JMenuItem open;
	private JMenuItem sharpen;
	private JMenuItem blur;
	private JMenu red;
	private JMenu green;
	private JMenu blue;
	private JMenu bright;
	private JSlider redSlide;
	private JSlider greenSlide;
	private JSlider blueSlide;
	private JSlider brightSlide;
	private JMenuItem flipH;
	private JMenuItem flipV;
	private JMenuItem neg;
	private JMenuItem restore;
	private JMenuItem undo;
	private JMenuItem rotate;
	private JMenuItem lengthen;
	private JMenuItem widen;
	private JMenuItem transform;
	private boolean negified=false;
	
	public ImageEditor(){
		super("IMAGE EDITOR by Yutong Gu");
		makeMenu();
		
		orig = new ImageCanvas();
		orig.setImage(new File(file));
		
		alter = new ImageCanvas();
		alter.setImage(new File(file));
		alter.tester();
		
		JPanel stuff = new JPanel();
		stuff.setLayout(new GridLayout(1,2));
		stuff.add(new JScrollPane(orig));
		stuff.add(new JScrollPane(alter));
		this.add(stuff, BorderLayout.CENTER);
		//finishing up
		this.setSize(1275,975);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
	}
	
	private void makeMenu(){
		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenu manipulations = new JMenu("Manipulations");
		JMenu effects = new JMenu("Effects");
		
		undo= new JMenuItem("Undo");
		restore = new JMenuItem("Restore");
		open = new JMenuItem("Open");
		sharpen = new JMenuItem("Sharpen");
		blur = new JMenuItem("Blur");
		red = new JMenu("Red");
		green = new JMenu("Green");
		blue = new JMenu("Blue");
		flipH = new JMenuItem("Horizontal Flip");
		flipV = new JMenuItem("Vertical Flip");
		neg = new JMenuItem("Negative");
		rotate = new JMenuItem("Rotate");
		lengthen = new JMenuItem("Lengthen");
		widen = new JMenuItem("Widen");
		transform = new JMenuItem("Transform");
		bright= new JMenu("Brightness");
		redSlide= new JSlider(0,255,255);
		greenSlide= new JSlider(0,255,255);
		blueSlide= new JSlider(0,255,255);
		brightSlide= new JSlider(-255,255,0);
		
		open.addActionListener(this);
		sharpen.addActionListener(this);
		blur.addActionListener(this);
		red.addActionListener(this);
		green.addActionListener(this);
		blue.addActionListener(this);
		flipH.addActionListener(this);
		flipV.addActionListener(this);
		neg.addActionListener(this);
		undo.addActionListener(this);
		restore.addActionListener(this);
		rotate.addActionListener(this);
		lengthen.addActionListener(this);
		widen.addActionListener(this);
		transform.addActionListener(this);
		redSlide.addChangeListener(this);
		greenSlide.addChangeListener(this);
		blueSlide.addChangeListener(this);
		brightSlide.addChangeListener(this);
		
		file.add(open);
		effects.add(red);
		effects.add(green);
		effects.add(blue);
		effects.add(bright);
		effects.add(neg);
		manipulations.add(flipH);
		manipulations.add(flipV);
		manipulations.add(blur);
		manipulations.add(sharpen);
		manipulations.add(rotate);
		manipulations.add(lengthen);
		manipulations.add(widen);
		manipulations.add(transform);
		red.add(redSlide);
		green.add(greenSlide);
		blue.add(blueSlide);
		bright.add(brightSlide);
		
		bar.add(file);
		bar.add(manipulations);
		bar.add(effects);
		bar.add(undo);
		bar.add(restore);
		
		this.setJMenuBar(bar);
		
	}
	public void undo(){
		if(stack.size()!=0)
			alter.arrayToImg(stack.pop());
	}
	public void update(){
		stack.push(alter.imgToArray(alter.getImg()));
	}
	public void restore(){
		redSlide.setValue(255);
		greenSlide.setValue(255);
		blueSlide.setValue(255);
		brightSlide.setValue(0);
		alter.arrayToImg(orig.imgToArray(orig.getImage()));
		
	}
	
	public static void main(String[] args) {
		new ImageEditor();
		JOptionPane.showMessageDialog(null,"Please select an image file to manipulate \n File-->Open");
	}

	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()!=undo) //it will automatically update the image unless undo was pressed 
			update();
		else 
			undo();
		
		if(e.getSource()==open){
			JFileChooser jfc = new JFileChooser();
			int result = jfc.showOpenDialog(this);
			if(result == JFileChooser.CANCEL_OPTION)
				return;
			File f = jfc.getSelectedFile();
			orig.setImage(f);
			alter.setImage(f);
			this.repaint();
		}
		if(e.getSource()==red){ //asks for ratio of red and calls red function
			String b= JOptionPane.showInputDialog(null, "ratio:");
			alter.red(Integer.parseInt(b));
		}
		if(e.getSource()==green){ //asks for ratio of green and calls green function
			String b= JOptionPane.showInputDialog(null, "ratio:");
			alter.green(Integer.parseInt(b));
			
		}
		if(e.getSource()==blue){ //asks for ratio of blue and calls blue function
			String b= JOptionPane.showInputDialog(null, "ratio:");
			alter.blue(Integer.parseInt(b));
		}
		if(e.getSource()==neg){
			redSlide.setValue(255); //reset all sliders
			greenSlide.setValue(255);
			blueSlide.setValue(255);
			alter.negative(); // and make it negative
		}
		if(e.getSource()==flipH){
			alter.mirrorHoriz();
			
		}
		if(e.getSource()==flipV){
			alter.mirrorVert();
			
		}
		if(e.getSource()==restore){
			restore();
		}
		if(e.getSource()==blur){ //asks for the strength for the blur (preferred 1-3)
			//String b= JOptionPane.showInputDialog(null, "Strength of blur (Integer between 1 and 4):"); 
			//alter.blur(Integer.parseInt(b));
			alter.blur(2);
		}
		if(e.getSource()==sharpen)
			alter.sharpen();
		if(e.getSource()==rotate){ //asks for the angle for the rotate and rotates it
			String b= JOptionPane.showInputDialog(null, "Angle (degrees):");
			alter.rotate(Double.parseDouble(b));
		}
		if(e.getSource()==lengthen){ //asks for a ratio to lengthen the picture
			String b= JOptionPane.showInputDialog(null, "Lengthening Ratio:");
			alter.resizeY(Double.parseDouble(b));
		}
		if(e.getSource()==widen){ //asks for a ratio to widen the picture
			String b= JOptionPane.showInputDialog(null, "Widening Ratio:");
			alter.resizeX(Double.parseDouble(b));
		}
		if(e.getSource()==transform){ //asks for a matrix to tranform with and makes a double[2][2] with it
			String a= JOptionPane.showInputDialog(null, "MATRIX 0,0:");
			String b= JOptionPane.showInputDialog(null, "MATRIX 0,1:");
			String c= JOptionPane.showInputDialog(null, "MATRIX 1,0:");
			String d= JOptionPane.showInputDialog(null, "MATRIX 1,1:");
			double[][] mat= {{Double.parseDouble(a),Double.parseDouble(b)},{Double.parseDouble(c),Double.parseDouble(d)}};
			alter.transform(mat);
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if(e.getSource()==redSlide)
			alter.red(redSlide.getValue());
		if(e.getSource()==greenSlide)
			alter.green(greenSlide.getValue());
		if(e.getSource()==blueSlide)
			alter.blue(blueSlide.getValue());
		if(e.getSource()==brightSlide){
			alter.brightness(brightSlide.getValue());
		}
			
	}
}
