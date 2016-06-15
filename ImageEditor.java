import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
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
import javax.swing.event.MouseInputListener;
import javax.swing.filechooser.FileNameExtensionFilter;


public class ImageEditor extends JFrame implements ActionListener, ChangeListener, MouseInputListener{
	private ImageCanvas orig, alter;
	public int heightConstraint;
	public int widthConstraint;
	String file = "./src/picture1.jpg";
	private Stack<int[][]> stack= new Stack();
	private Stack<int[][]> redostack= new Stack();
	private Stack<int[]> slidestack=new Stack();
	private Stack<int[]> redoslidestack=new Stack();
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
	private JMenuItem redo;
	private JMenuItem rotate;
	private JMenuItem lengthen;
	private JMenuItem widen;
	private JMenuItem transform;
	private JMenuItem save;
	private final JFileChooser jfcOpen = new JFileChooser();
	private final JFileChooser jfcSave = new JFileChooser();
	
	public ImageEditor(){
		super("IMAGE EDITOR by Yutong Gu");
		makeMenu();
		
		alter = new ImageCanvas();
		alter.setImage(new File(file));
		orig = new ImageCanvas();
		orig.setImage(new File(file));
		
		
		
		//finishing up
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    setBounds(0,0,screenSize.width, screenSize.height);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		heightConstraint=this.getBounds().height-50;
		widthConstraint=this.getBounds().width-50;
		jfcOpen.changeToParentDirectory();
		jfcOpen.setFileFilter(new FileNameExtensionFilter(null,"jpeg", "png", "jpg"));
		jfcSave.changeToParentDirectory();
		jfcSave.addChoosableFileFilter(new FileNameExtensionFilter(null,"jpeg", "png", "jpg"));
		
		JScrollPane stuff = new JScrollPane(alter);
		this.add(stuff, BorderLayout.CENTER);
		
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
		redo= new JMenuItem("Redo");
		open = new JMenuItem("Open");
		save = new JMenuItem("Save");
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
		redSlide= new JSlider(-64,64,0);
		greenSlide= new JSlider(-64,64,0);
		blueSlide= new JSlider(-64,64,0);
		brightSlide= new JSlider(-64,64,0);
		
		open.addActionListener(this);
		save.addActionListener(this);
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
		redo.addActionListener(this);
		rotate.addActionListener(this);
		lengthen.addActionListener(this);
		widen.addActionListener(this);
		transform.addActionListener(this);
		redSlide.addChangeListener(this);
		greenSlide.addChangeListener(this);
		blueSlide.addChangeListener(this);
		brightSlide.addChangeListener(this);
		greenSlide.addMouseListener(this);
		redSlide.addMouseListener(this);
		blueSlide.addMouseListener(this);
		brightSlide.addMouseListener(this);
		
		file.add(open);
		file.add(save);
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
		bar.add(redo);
		bar.add(restore);
		
		this.setJMenuBar(bar);
	}
	public void undo(){
		if(slidestack.size()!=0){
			int[] valArray=slidestack.pop();
			redoslidestack.push(valArray);
			
			if(valArray[0]!=-129){
				redSlide.setValue(valArray[0]);
				greenSlide.setValue(valArray[1]);
				blueSlide.setValue(valArray[2]);
				brightSlide.setValue(valArray[3]);
			}
		}
		if(stack.size()!=0){
			redostack.push(stack.peek());
			alter.arrayToImg(stack.pop());
		}
	}
	
	public void redo(){
		if(redoslidestack.size()!=0){
			int[] valArray=redoslidestack.pop();
			slidestack.push(valArray);
			redSlide.setValue(valArray[0]);
			greenSlide.setValue(valArray[1]);
			blueSlide.setValue(valArray[2]);
			brightSlide.setValue(valArray[3]);
			
		}
		if(redostack.size()!=0){
			stack.push(redostack.peek());
			alter.arrayToImg(redostack.pop());
		}
	}
	
	public void update(){
		
		redostack.clear();
		redoslidestack.clear();
		int[] vals= {redSlide.getValue(), greenSlide.getValue(), blueSlide.getValue(), brightSlide.getValue()};
		slidestack.push(vals);
		stack.push(alter.imgToArray(alter.getImg()));
	}
	
	public void restore(){
		redostack.clear();
		redoslidestack.clear();
		redSlide.setValue(0);
		greenSlide.setValue(0);
		blueSlide.setValue(0);
		brightSlide.setValue(0);
		int[] vals= {redSlide.getValue(), greenSlide.getValue(), blueSlide.getValue(), brightSlide.getValue()};
		alter.updateSlideVals(vals);
		alter.arrayToImg(orig.imgToArray(orig.getImage()));
		alter.updatePrevImage(alter.getImage());
	}
	public void reset(){
		restore();
		stack.clear();
		slidestack.clear();
		int[] vals= {redSlide.getValue(), greenSlide.getValue(), blueSlide.getValue(), brightSlide.getValue()};
		alter.updateSlideVals(vals);
		alter.arrayToImg(orig.imgToArray(orig.getImage()));
		alter.updatePrevImage(alter.getImage());
	}
	
	public static void main(String[] args) {
		new ImageEditor();
	}

	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()!=undo && e.getSource()!=redo){ //it will automatically update the image unless undo was pressed 
			update();
		}
		else if(e.getSource()==undo){
			undo();
			alter.updatePrevImage(alter.getImage());
		}
		else{
			redo();
			alter.updatePrevImage(alter.getImage());
		}
		
		if(e.getSource()==open){
			jfcOpen.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int result;
			File f;
			do{
				result = jfcOpen.showOpenDialog(ImageEditor.this);
				if(result == JFileChooser.CANCEL_OPTION)
					return;
				f = jfcOpen.getSelectedFile();
				if(isValidSize(f)){
					orig.setImage(f);
					alter.setImage(f);
				}
				else{
					JOptionPane.showMessageDialog(null, "Sorry, the image you've selected is too large. Please select an image smaller than "+widthConstraint+"x"+heightConstraint+"."	);
				}
			}while(!isValidSize(f)&&result!=JFileChooser.CANCEL_OPTION&&result!=JFileChooser.ERROR_OPTION);
			reset();
			this.repaint();
		}
		else if(e.getSource()==save){
			jfcSave.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int result = jfcSave.showSaveDialog(ImageEditor.this);
			if(result == JFileChooser.CANCEL_OPTION)
				return;
			File f = jfcSave.getSelectedFile();
			String file_name = f.toString();
			if (!file_name.endsWith(".png")){
			    f = new File(f.toString()+".png");
			}
			BufferedImage bi = alter.getImg();
		    try {
		    	
				ImageIO.write(bi, "png", f);
				JOptionPane.showMessageDialog(null, "File saved successfully");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		else if(e.getSource()==red){ //asks for ratio of red and calls red function
			String b= JOptionPane.showInputDialog(null, "ratio:");
			try{
				alter.red(Integer.parseInt(b));
			}
			catch(NumberFormatException e1){}
		}
		else if(e.getSource()==green){ //asks for ratio of green and calls green function
			String b= JOptionPane.showInputDialog(null, "ratio:");
			try{
				alter.green(Integer.parseInt(b));
			}
			catch(NumberFormatException e1){}
			
		}
		else if(e.getSource()==blue){ //asks for ratio of blue and calls blue function
			String b= JOptionPane.showInputDialog(null, "ratio:");
			try{
				alter.blue(Integer.parseInt(b));
			}
			catch(NumberFormatException e1){}
		}
		else if(e.getSource()==neg){
			
			
			alter.negative(); // and make it negative
			redSlide.setValue(redSlide.getValue()*-1);
			greenSlide.setValue(greenSlide.getValue()*-1);
			blueSlide.setValue(blueSlide.getValue()*-1);
			brightSlide.setValue(brightSlide.getValue()*-1);
			int[] vals= new int[4];
			vals[0]=redSlide.getValue();
			vals[1]=greenSlide.getValue();
			vals[2]=blueSlide.getValue();
			vals[3]=brightSlide.getValue();
			alter.updateSlideVals(vals);
			
		}
		else if(e.getSource()==flipH){
			alter.mirrorHoriz();
			
		}
		else if(e.getSource()==flipV){
			alter.mirrorVert();
			
		}
		else if(e.getSource()==restore){
			restore();
		}
		else if(e.getSource()==blur){ //asks for the strength for the blur (preferred 1-3)
			//String b= JOptionPane.showInputDialog(null, "Strength of blur (Integer between 1 and 4):"); 
			//alter.blur(Integer.parseInt(b));
			alter.blur(2);
		}
		else if(e.getSource()==sharpen)
			alter.sharpen();
		else if(e.getSource()==rotate){ //asks for the angle for the rotate and rotates it
			String b= JOptionPane.showInputDialog(null, "Angle (degrees):");
			try{
				alter.rotate(Double.parseDouble(b));
			}
			catch(NumberFormatException e1){}
			
		}
		else if(e.getSource()==lengthen){ //asks for a ratio to lengthen the picture
			String b= JOptionPane.showInputDialog(null, "Lengthening Ratio:");
			try {
				alter.resizeY(Double.parseDouble(b));
			} catch (NumberFormatException e1) {}
		}
		else if(e.getSource()==widen){ //asks for a ratio to widen the picture
			String b= JOptionPane.showInputDialog(null, "Widening Ratio:");
			alter.resizeX(Double.parseDouble(b));
		}
		else if(e.getSource()==transform){ //asks for a matrix to tranform with and makes a double[2][2] with it
			String a= JOptionPane.showInputDialog(null, "MATRIX 0,0:");
			String b= JOptionPane.showInputDialog(null, "MATRIX 0,1:");
			String c= JOptionPane.showInputDialog(null, "MATRIX 1,0:");
			String d= JOptionPane.showInputDialog(null, "MATRIX 1,1:");
			try {
				double[][] mat= {{Double.parseDouble(a),Double.parseDouble(b)},{Double.parseDouble(c),Double.parseDouble(d)}};
				alter.transform(mat);
			} catch (NumberFormatException e1) {}
		}
		alter.updatePrevImage(alter.getImage());
	}

	private boolean isValidSize(File f) {

		try {
			BufferedImage bf= ImageIO.read((f)); 
			if(bf.getHeight()>heightConstraint){
				return false;
			}
			if(bf.getWidth()>widthConstraint){
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
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

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int[] vals= new int[4];
		vals[0]=redSlide.getValue();
		vals[1]=greenSlide.getValue();
		vals[2]=blueSlide.getValue();
		vals[3]=brightSlide.getValue();
		update();	
		alter.updateSlideVals(vals);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		alter.updatePrevImage(alter.getImage());
		int[] vals= new int[4];
		vals[0]=redSlide.getValue();
		vals[1]=greenSlide.getValue();
		vals[2]=blueSlide.getValue();
		vals[3]=brightSlide.getValue();
		alter.updateSlideVals(vals);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
