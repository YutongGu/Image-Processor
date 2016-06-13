import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;
public class ImageCanvas extends JPanel{
	private BufferedImage img; //the image that appears on this canvas
	private BufferedImage original; //this will be the original image
	private BufferedImage prev;
	private int[] slidevals=new int[4];
	static final double radify= Math.PI/180; //this turns degrees into radians
	private int w, h;
	private static final int TYPE = BufferedImage.TYPE_INT_ARGB_PRE;
	public int getW(){return w;}
	public int getH(){return h;}
	public BufferedImage getImg(){return img;}
	public BufferedImage getOrg(){return original;}
	public void updateSlideVals(int[] vals){slidevals=vals;}
	
	/** ***************** PIXEL FUNCTIONS ****************** **/
	 public static final int A=0, R=1, G=2, B=3;

	 //returns only the red value of the pixel
	 //   EX: pixel = 0x004f2ca5 --> returns 4f
	 public int howRed(int pixel){return (pixel&0xff0000)>>16;}

	 //returns only the green value of the pixel
	 //   EX: pixel = 0x004f2ca5 --> returns 2c 
	 public int howGreen(int pixel){return (pixel&0x00ff00)>>8;}
	 
	 //returns only the blue value of the pixel
	 //   EX: pixel = 0x004f2ca5 --> returns a5
	 public int howBlue(int pixel){return pixel&0x0000ff;}
	 
	 public int combine(int a, int r, int g, int b){
		 //this long line of if statements makes sure that the r, g, & b values are legit
		 if(r>255)
			 r=255;
		 if(g>255)
			 g=255;
		 if(b>255)
			 b=255;
		 if(r<0)
			 r=0;
		 if(g<0)
			 g=0;
		 if(b<0)
			 b=0;
		 return a<<24|r<<16|g<<8|b; //moves over the rgb values and returns the combined #
	 }
    /** ***************************************************   **/
	public ImageCanvas(){
		super();
		this.setBackground(Color.gray);
		this.setPreferredSize(new Dimension(400,400));
		img = new BufferedImage(100,100,TYPE);
		w = img.getWidth();
		h = img.getHeight();
	}
	static BufferedImage deepCopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	public BufferedImage getImage(){return img;}
	
	public void updatePrevImage(BufferedImage prevImg){
		prev=deepCopy(prevImg);
	}
	
	public void setImage(File file){
		try{ 
			original= ImageIO.read((file)); 
			img = ImageIO.read((file)); 
			MediaTracker mt = new MediaTracker(new Component(){});
			mt.addImage(img, 0);
			mt.waitForAll();
			prev=deepCopy(img);
		}
		catch(Exception ex){ex.printStackTrace();}
		w = img.getWidth();
		h = img.getHeight();
		//pix = imgToArray();
		this.setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
	}
	

	// *********************Easy pixel manips************************
	//precondition: enter in a int 0-255
	//postcondition: the red values in img are set to be a factor of (red/255) of its original
	public void red(int red){
		int R;
		int G; 
		int	B; 
		int[][] image = imgToArray(prev); //this will be where things are changed
		for(int r=0; r<h; r++) 
			for(int c=0; c<w; c++){
				R = howRed(image[r][c]); //r= red value of original 
				G = howGreen(image[r][c]); //g= green value of the current image
				B = howBlue(image[r][c]); //b= blue value of current image
				R +=(red-slidevals[0]); //r is factored down by (red/255.0)
				img.setRGB(c, r, combine(0,R,G,B)); //combines the values
			}
				
		repaint();
	}
	//precondition: enter in a int 0-255
	//postcondition: the green values in img are set to be a factor of (green/255) of its original
	public void green(int green){
		int R;
		int G; 
		int	B; 
		int[][] image = imgToArray(prev); //this will be where things are changed
		for(int r=0; r<h; r++)
			for(int c=0; c<w; c++){
				R = howRed(image[r][c]); //r= red value of current image
				G = howGreen(image[r][c]); //g= green value of the original
				B = howBlue(image[r][c]); //b= blue value of current image
				G +=(green-slidevals[1]); //g is factored down by (red/255.0)
				img.setRGB(c, r, combine(0,R,G,B)); //combines the values
			}
				
		repaint();
	}
	//precondition: enter in a int 0-255
	//postcondition: the blue values in img are set to be a factor of (blue/255) of its original
	public void blue(int blue){
		int R;
		int G; 
		int	B; 
		int[][] image = imgToArray(prev); //this will be where things are changed
		for(int r=0; r<h; r++)
			for(int c=0; c<w; c++){
				R = howRed(image[r][c]); //r= red value of current image
				G = howGreen(image[r][c]); //g= green value of  current image
				B = howBlue(image[r][c]); //b= blue value of the original
				B +=(blue-slidevals[2]); //b is factored down by (red/255.0)
				img.setRGB(c, r, combine(0,R,G,B)); //combines the values
			}
				
		repaint();
	}
	
	public void negative(){
		
		int newPix;
		int red;
		int green;
		int blue;
		int[][] orig = imgToArray(img); //uses the values in the current img
		for(int r=0; r<h; r++)
			for(int c=0; c<w; c++){

				red= 255-howRed(orig[r][c])+2*slidevals[0]+2*slidevals[3]; // red values are flipped
				green= 255-howGreen(orig[r][c])+2*slidevals[1]+2*slidevals[3]; //green values are flipped
				blue= 255-howBlue(orig[r][c])+2*slidevals[2]+2*slidevals[3]; //blue values are flipped

				img.setRGB(c, r, combine(0,red,green,blue)); //combines the values
				prev.setRGB(c, r, combine(0,red,green,blue));
			}
		

		repaint();
	}
	
	public void brightness(int light){
		int red, green, blue;
		int[][] image;
		if(prev!=null)
			 image = imgToArray(prev);
		else
			image= imgToArray(original);
		light-=slidevals[3];
		for(int r=0; r<h; r++)
			for(int c=0; c<w; c++){
				red= howRed(image[r][c])+light; // red values are increased/decreased
				green= howGreen(image[r][c])+light; //green values are increased/decreased
				blue= howBlue(image[r][c])+light; //blue values are increased/decreased
				 //this long line of if statements makes sure that the red, green, & blue values are legit
				 if(red>255)
					 red=255;
				 if(green>255)
					 green=255;
				 if(blue>255)
					 blue=255;
				 if(red<0)
					 red=0;
				 if(green<0)
					 green=0;
				 if(blue<0)
					 blue=0;
				img.setRGB(c, r, combine(0,red,green,blue)); //combines the values
				
			}
		
		repaint();
	}
	
	public void mirrorHoriz(){
		int[][] orig = imgToArray(img);
		for(int r=0; r<h; r++)
			for(int c=0; c<w; c++){
				img.setRGB(w-c-1, r, orig[r][c]); //places everything in orig in img, but flipped
				original.setRGB(w-c-1, r, orig[r][c]);
			}
		this.repaint();
	}
	
	public void mirrorVert(){
		int[][] orig = imgToArray(img);
		for(int r=0; r<h; r++)
			for(int c=0; c<w; c++){
				img.setRGB(c, h-r-1, orig[r][c]); //places everything in orig in img, but upside down
				original.setRGB(c, h-r-1, orig[r][c]);
			}
		this.repaint();
	}
	
	// *********************END Easy pixel manips************************
	
	// ********************KERNEL STUFF *********************************
	public ArrayList<Integer> getKernel(int[][] image, int r, int c){ 
		ArrayList<Integer> ans= new ArrayList<Integer>();
		for(int a=-1; a<=1; a++) //this will be the up and down of the kernal
			for(int b=-1; b<=1; b++) //this will be the left and right of the kernal
				if(isValid(r+a,c+b)&&!(a==0&&b==0)) //basically it checks if the values around it is valid and if it is not on the center of the kernal
					ans.add(image[r+a][c+b]); //adds the valid value around the center
		return ans;
	}
	public ArrayList<Integer> getKernel(int[][] image, int r, int c, int size){
		ArrayList<Integer> ans= new ArrayList<Integer>();
		for(int a=-size; a<=size; a++) //this will be the up and down of the kernal with radius size
			for(int b=-size; b<=size; b++) //this will be the left and right of the kernal with radius size
				if(isValid(r+a,c+b)&&!(a==0&&b==0)) //basically it checks if the values around it is valid and if it is not on the center of the kernal
					ans.add(image[r+a][c+b]); //adds the valid value around the center
		return ans;
	}
	
	public boolean isValid(int r, int c){
		if( r<h && r>=0 && c<w && c>=0)
			return true;
		else
			return false;
	}
	
	
	public void blur(int b){
		int[][] orig = imgToArray(img); //the current img
		int[][] newImg= new int[orig.length][orig[0].length]; //the new image to be made
		double newRVal=0; 
		double newGVal=0;
		double newBVal=0;
		double denom = (2*b+1)*(2*b+1); //this will be how much each value will be weighed, given its size b
		ArrayList<Integer> kern; 
		for(int r=0; r<h; r++){
			for(int c=0; c<w; c++){
				kern=getKernel(orig, r, c, b); //makes a kernal with the radius size and the location
				for(int i: kern){ //for each value in the kernal 
					newRVal+=howRed(i)/denom; //the surrounding red values are divided by denom and added to red
					newGVal+=howGreen(i)/denom; //the surrounding green values are divided by denom and added to green
					newBVal+=howBlue(i)/denom;//the surrounding blue values are divided by denom and added to blue
				}
				newRVal+=howRed(orig[r][c])/denom; //the current red value is divided by denom and added to red
				newGVal+=howGreen(orig[r][c])/denom; //the current green values is divided by denom and added to green
				newBVal+=howBlue(orig[r][c])/denom; //the current blue values is divided by denom and added to blue
				newImg[r][c]= combine(0, (int)newRVal, (int)newGVal, (int)newBVal); //combines the values 
				newRVal=0; //resets the values
				newGVal=0;
				newBVal=0;
			}
			
		} 
		arrayToImg(newImg); //forms the img
		
		
	}
	public void blur(){
		int[][] orig = imgToArray(img); //the current img
		int[][] newImg= new int[orig.length][orig[0].length]; //the new image to be made
		ArrayList<Integer> kern; //this will be the kernal 
		int newRVal=0;
		int newGVal=0;
		int newBVal=0;
		for(int r=0; r<h; r++){
			for(int c=0; c<w; c++){
				kern= getKernel(orig, r, c); //finds the surrounding values
				for(int i: kern){ //for each surrounding value
					newRVal+=howRed(i)/9; //the surrounding red values are divided by 9 and added to red
					newGVal+=howGreen(i)/9; //the surrounding green values are divided by 9 and added to green
					newBVal+=howBlue(i)/9;//the surrounding blue values are divided by 9 and added to blue
				}
				newRVal+=howRed(orig[r][c])/9; //the current red value is divided by 9 and added to red
				newGVal+=howGreen(orig[r][c])/9; //the current green values is divided by 9 and added to green
				newBVal+=howBlue(orig[r][c])/9; //the current blue values is divided by 9 and added to blue
				newImg[r][c]= combine(0, newRVal, newGVal, newBVal); //combines the values
				newRVal=0; //resets the values
				newGVal=0;
				newBVal=0;
			}
			
		}
		arrayToImg(newImg); //forms the img
		
	}
		
	public void sharpen(){
		int[][] orig = imgToArray(img); //original
		int[][] newImg= new int[orig.length][orig[0].length]; //the new image to be made
		int newRVal=0;
		int newGVal=0;
		int newBVal=0;
		for(int r=0; r<h; r++){
			for(int c=0; c<w; c++){
				for(int i: getKernel(orig, r, c)){ //for each value in the surroundings
					newRVal-=howRed(i)/9; //red values will be divided by 9 and subtracted from red
					newGVal-=howGreen(i)/9; //green values will be divided by 9 and subtracted from green
					newBVal-=howBlue(i)/9; //blue values will be divided by 9 and subtracted from blue
				}
				newRVal+=17*howRed(orig[r][c])/9; //current red value will by multiplied by 17/9 and added to red
				newGVal+=17*howGreen(orig[r][c])/9; //current green value will by multiplied by 17/9 and added to green
				newBVal+=17*howBlue(orig[r][c])/9; //current blue value will by multiplied by 17/9 and added to blue
				newImg[r][c]= combine(0, newRVal, newGVal, newBVal); //combines the values
				newRVal=0; // resets the values
				newGVal=0;
				newBVal=0;
			}
			
		}
		arrayToImg(newImg); //forms the img
		
	}

	/* **************MATRIX STUFF ********************************* */
	public void resizeY(double ratio){
		double[][] mat = {{1,0},{0,ratio}}; //this is the matrix to resizeY
		transform(mat); //transforms with the matrix
	}
	public void resizeX(double ratio){
		double[][] mat = {{ratio,0},{0,1}}; //this is the matrix to resizeX
		transform(mat); //transforms with the matrix
	}
	
	public void rotate(double angle){
		double[][] mat = {{Math.cos(angle*radify),-Math.sin(angle*radify)},{Math.sin(angle*radify),Math.cos(angle*radify)}}; //this is the matrix to rotate
		transform(mat); //transforms with the matrix
	}
	
	public void transform(double[][] matrix){
		int[] coor= new int[2];
		int[] newCoor= new int[2];
		int[][] orig= imgToArray(img);
		
		int [] TL = {0,0}; //this is top left corner
		int [] TR = {w-1,0}; //this is top right corner
		int [] BL = {0,h-1}; //this is the bottom left corner
		int [] BR = {w-1,h-1}; //this is the bottom right corner
		
		TL= transHelp(TL, matrix); //finds where top left would be after transform
		TR= transHelp(TR, matrix); //finds where top right would be after transform
		BL= transHelp(BL, matrix); //finds where bottom left would be after transform
		BR= transHelp(BR, matrix); //finds where bottom right would be after transform
		
		int maxX= Math.max(Math.max(TL[0], TR[0]),Math.max(BL[0], BR[0])); //finds the maxX value of the corners
		int minX= Math.min(Math.min(TL[0], TR[0]),Math.min(BL[0], BR[0])); //finds the minX value of the corners
		int maxY= Math.max(Math.max(TL[1], TR[1]),Math.max(BL[1], BR[1])); //finds the maxY value of the corners
		int minY= Math.min(Math.min(TL[1], TR[1]),Math.min(BL[1], BR[1])); //finds the minY value of the corners
		
		int yShift=0, xShift=0; //yShift & xShift determines how much you shift x & y
		
		if(minX!=0) //if the minX is not 0, it will shift in the opposite direction so that its 0
			xShift=-(minX); 
		if(minY!=0) //if the minY is not 0, it will shift in the opposite direction so that its 0
			yShift=-(minY);
		
		
		int[][] newImg= new int[maxY-minY+1][maxX-minX+1]; //the new image will have the size of the x and y range
		
		for(int r=0; r<h; r++){
			for(int c=0; c<w; c++){
				coor[0]=c; //stores the x in coor[0]
				coor[1]=r; //stores the y in coor[1]
				newCoor= transHelp(coor, matrix); //transforms the coor with the given matrix
				newImg[newCoor[1]+yShift][newCoor[0]+xShift]= orig[r][c]; //the new img will be placed in a shifted location that fits in the new img
			}
		}
		
		arrayToImg(newImg); //forms the img
	}
	public int[] transHelp(int[] coor, double [][] matrix){ //finds the translated loction by multiplying matrices
		int[] newCoor= new int[2];
		newCoor[0]= (int) (matrix[0][0]*coor[0]+matrix[0][1]*coor[1]);
		newCoor[1]= (int) (matrix[1][0]*coor[0]+matrix[1][1]*coor[1]);
		return newCoor;
	}
	
	public void paint(Graphics g){
		super.paint(g);
		((Graphics2D)g).drawImage(img,null,0,0);
		//g.drawImage(img, 0, 0, null);
	}
	
	
/**  **************** START WITH THESE!  ************** **/
	//have kids do this first!  just take the pixels and replace them
	public void tester(){
		int[][] blah = imgToArray(img);		
		arrayToImg(blah);
		
	}
	

	//Postconditions:  all of the pixels from the original image have been stored
	//  into a 2d array and that 2d array has been returned
	public int[][] imgToArray(BufferedImage b){
		//this puts the pixels into a 1d array.  You want to move them into a 2d array
		int[] pix = b.getRGB(0, 0, w, h, null, 0, w);
		int[][] pixArray= new int[h][w];
		for(int i=0; i<pix.length; i++){
			pixArray[i/w][i%w]= pix[i]; 
		}
		return pixArray;
	}
	
	//Postconditions:  the pixel values from the given 2d array have been loaded onto
	//  the image
	//HINT:  use this function--> img.setRGB(x,y,val);
	public void arrayToImg(int[][] pix){
		w = pix[0].length;
		h = pix.length;
		this.setPreferredSize(new Dimension(w,h));
		img = new BufferedImage(w,h,img.getType());
		original = new BufferedImage(w,h,img.getType());
		for(int r=0; r<h; r++)
			for(int c=0; c<w; c++){
				img.setRGB(c, r, pix[r][c]);
				original.setRGB(c, r, pix[r][c]);
			}
		this.repaint();
	}
	
/**  ***************************************************  **/
}
