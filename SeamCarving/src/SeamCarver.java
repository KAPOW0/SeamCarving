import java.awt.Color;
import java.util.Arrays;



public class SeamCarver
{
	private double[][] energy2D;
	private SmC_Picture picture;
	private Color[][] color2D;
	int width;
	int height;
	boolean horiz = false;
	
	public SeamCarver(SmC_Picture pictureP)
	{
	
		picture = pictureP;
		height = picture.height();
		width = picture.width();
		
		color2D = new Color[this.height][this.width];
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				
				color2D[i][j] = picture.get(j, i);
			}
		}
		energy2D = new double[this.height][this.width];
		
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				
				energy2D[i][j] = this.energy(j, i);
			}
		}
	}

	public SmC_Picture picture()
	{
		if (horiz) {
			transpose();
			horiz = false;
		}
		
		picture = new SmC_Picture(this.width, this.height);
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				picture.set(j, i, color2D[i][j]);
			}
		}
		return picture;
	}

	public int width()
	{
		if (horiz) {
			return height;
		}
		else {
			return width;
		}
	}

	public int height()
	{
		if (horiz) {
			return width;
		}
		else {
			return height;
		}
	}

	private double energyP(int x, int y)
	{
		
		
		if (x <= 0 || y <= 0 || x >= color2D[0].length - 1 || y >= color2D.length - 1) {
			return 1000.0;
		}
		else {
			Color left = color2D[y - 1][x];
			Color right = color2D[y + 1][x];
			Color top = color2D[y][x + 1];
			Color bottom = color2D[y][x - 1];
			return Math.sqrt(cDistance(left, right) * cDistance(left, right) + cDistance(top, bottom) * cDistance(top, bottom));
			
		}
	}
	public double energy(int x, int y) {
		if (horiz) {
			int temp = x;
			x = y;
			y = temp;
		}
		if (x >= this.width || x < 0 || y >= this.height || y < 0) { throw new IndexOutOfBoundsException(); }
		return energyP(x, y);	
	}

	public int[] findHorizontalSeam()
	{
		
		if (!horiz) {
			transpose();
			horiz = true;
		}
		
		return findSeam(); 
	
		
		
	}
	
	public int[] findVerticalSeam() {
		if (horiz) {
			transpose();
			horiz = false;
		}
		
		return findSeam();
	}
	
	private int[] findSeam()
	{
		
		double[][] distTo = new double[this.height][this.width];
		int[][] prev = new int[this.height][this.width];
		for (double[] row: distTo) {
			Arrays.fill(row, Double.POSITIVE_INFINITY);
		}
		for (int j = 0; j < this.width; j++) {
			distTo[0][j] = energy2D[1][j];
			prev[0][j] = j;
		}
		int i = 0;
		int j = 1;
		while (i < this.height - 1) {
			
			for (int k = 0; k < 3; k++) {
				if(distTo[i][j] + energy2D[i + 1][j - 1 + k] < distTo[i + 1][j - 1 + k] ) {
					distTo[i + 1][j - 1 + k] = distTo[i][j] + energy2D[i + 1][j - 1 + k];
					
					prev[i + 1][j - 1 + k] = j;
					
				}
			}
			
			
			
			if (j < this.width - 2) {
				j++;
			}
			else {
				i++;
				j = 1;
			}
		}
		double minDist = Double.POSITIVE_INFINITY;
		int minV = 1;
		
		for (int k = 1; k < this.width - 1; k++) {
			if(distTo[this.height - 1][k] < minDist) {
				minDist = distTo[this.height - 1][k];
				minV = k;
			}
		}
		
		int[] seam = new int[this.height];
	
		int curr = minV;
		for (int z = this.height - 1; z >= 0; z--) {
			
			seam[z] = curr;
			curr = prev[z][curr];
		}
		return seam;
	}

	public void removeHorizontalSeam(int[] a)
	{
		if (!horiz) {
			transpose();
			horiz = true;
		}
		removeSeam(a);
	
	}
	
	public void removeVerticalSeam(int[] a)
	{
		if (horiz) {
			transpose();
			horiz = false;
		}
		removeSeam(a);
	}
	

	private void removeSeam(int[] a)
	{
		if (a.length != this.height) { throw new IllegalArgumentException(); }
	
		for(int i = 0; i < this.height; i++) {
			
			if (a[i] < 0 || a[i] >= this.width) { throw new IllegalArgumentException(); } 
			
			if (i < this.height - 1) {
				int diff = a[i] - a[i + 1];
				if (!(diff == 1 || diff == -1 || diff == 0)) { throw new IllegalArgumentException(); } 
			}
			if(i < this.height  - 2) {
				int diff = a[i] - a[i + 1];
				if (!(diff == 1 || diff == -1 || diff == 0)) { throw new IllegalArgumentException(); } 
			}
			
			for (int j = a[i]; j < this.width - 1; j++) {
				
				color2D[i][j] = color2D[i][j + 1];
			}
			color2D[i][this.width - 1] = new Color(0, 0, 0);
		}
		
		
		
		for(int i = 0; i < this.height; i++) {
			
			
			for(int j = a[i] + 1; j < this.width - 1; j++) {
				energy2D[i][j] = energy2D[i][j + 1];
			}
			if (a[i] > 0) {
				energy2D[i][a[i] - 1] = this.energyP(a[i] - 1, i);
			}
			
			energy2D[i][a[i]] = this.energyP(a[i], i);
		
			
		}
		width--;
		
		
	}
	
	private double cDistance(Color one, Color two) {
		return Math.sqrt( (one.getBlue() - two.getBlue()) * (one.getBlue() - two.getBlue()) + (one.getGreen() - two.getGreen()) * (one.getGreen() - two.getGreen()) + (one.getRed() - two.getRed()) * (one.getRed() - two.getRed()));
	}
	
	private void transpose() {
		
		Color[][] newC = new Color[color2D[0].length][color2D.length];
		double[][] newE = new double[energy2D[0].length][energy2D.length];
		for (int i = 0; i < color2D[0].length; i++) {
			for (int j = 0; j < color2D.length; j++) {
				newC[i][j] = color2D[j][i];
			
			}
		}
		for (int i = 0; i < energy2D[0].length; i++) {
			for (int j = 0; j < energy2D.length; j++) {
				
				newE[i][j] = energy2D[j][i];
			}
		}
		color2D = newC;
		energy2D = newE;
		
		int temp = height;
		height = width;
		width = temp;
	}
	
	
}