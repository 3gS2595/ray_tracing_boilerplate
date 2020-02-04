//DYLAN TRINKNER
//dbtrinkner@gmail.com

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;


class transform {
	// Contains all models requested by driver
	private ArrayList<model> models = new ArrayList<>();
	private DecimalFormat df = new DecimalFormat("####0.000000");

	static void trans(String driverName, String ID) {
		transform main = new transform();
		try {
			main.intake(driverName);
			main.execute(ID);
		} catch (Exception e) {  e.printStackTrace(); System.out.println("method has failed."); }
	}

	// APPLIES TRANSFORM TO MODEL
	private void solve(model cur) {
		ArrayList<String> newObj = new ArrayList<>();
		for (String line : cur.getObj()) {
			String[] parsed = line.split(" ");
			if (parsed[0].equals("v")) {
				double[] prime = new double[4];
				double x = Double.parseDouble(parsed[1]);
				double y = Double.parseDouble(parsed[2]);
				double z = Double.parseDouble(parsed[3]);
				prime[1] = (x * cur.tranM[0][0]) + (y * cur.tranM[0][1]) + (z * cur.tranM[0][2]) + (1 * cur.tranM[0][3]);
				prime[2] = (x * cur.tranM[1][0]) + (y * cur.tranM[1][1]) + (z * cur.tranM[1][2]) + (1 * cur.tranM[1][3]);
				prime[3] = (x * cur.tranM[2][0]) + (y * cur.tranM[2][1]) + (z * cur.tranM[2][2]) + (1 * cur.tranM[2][3]);

				for (int i = 0; i < 3; i++){
					if (prime[i] < 0.000001 && prime[i] > 0.000001){
						prime[i] = 0;
					}
				}
				line = "v " + df.format(prime[1]) + " " + df.format(prime[2]) + " " + df.format(prime[3]); 
				cur.sum += Math.abs(x - prime[1]) + Math.abs(y - prime[2]) + Math.abs(z - prime[3]);
			}
			newObj.add(line);
		}
		cur.setMod(newObj);
	}

	// SCALE
	private void scale(model cur) {
		String[] driver = cur.getDriver();
		double scale = Double.parseDouble(driver[5]);
		for(int i = 0; i<3; i++)
    			for(int j = 0; j<3; j++)
        			cur.tranM[i][j] = cur.tranM[i][j] * scale;
	}
	
	// TRANSLATION
	private void translation(model cur) {
		String[] driver = cur.getDriver();
		double xT = Double.parseDouble(driver[6]);
		double yT = Double.parseDouble(driver[7]);
		double zT = Double.parseDouble(driver[8]);
		cur.tranM[0][3] = xT;
		cur.tranM[1][3] = yT;
		cur.tranM[2][3] = zT;
	}
	
	// ROTATE
	private void rotate(model cur) {
		String[] driver = cur.getDriver();
		double rX = Double.parseDouble(driver[1]);
		double rY = Double.parseDouble(driver[2]);
		double rZ = Double.parseDouble(driver[3]);
		double theta = Math.toRadians(Double.parseDouble(driver[4]));
		double cos = Math.cos(theta);
		double sin = Math.sin(theta);				
		if (rX > 0) {
			cur.tranM[1][1] = cos;
			cur.tranM[1][2] = -sin;
			cur.tranM[2][1] = sin;
			cur.tranM[2][2] = cos;
		}
		if (rY > 0) {
		    cur.tranM[0][0] = cos;
			cur.tranM[0][2] = sin;
			cur.tranM[2][0] = -sin;
			cur.tranM[2][2] = cos;
		}
		if (rZ > 0) {
			cur.tranM[0][1] = cos;
			cur.tranM[0][1] = -sin;
			cur.tranM[1][0] = sin;
			cur.tranM[1][1] = cos;			
		}
	}

	// INVERSE AND INVERSE SUM
	private static void GetInverse(model cur) {
		double[][] m = cur.tranM;
	    double s0 = m[0][0] * m[1][1] - m[1][0] * m[0][1];
	    double s1 = m[0][0] * m[1][2] - m[1][0] * m[0][2];
 	    double s2 = m[0][0] * m[1][3] - m[1][0] * m[0][3];
 	    double s3 = m[0][1] * m[1][2] - m[1][1] * m[0][2];
 		double s4 = m[0][1] * m[1][3] - m[1][1] * m[0][3];
  		double s5 = m[0][2] * m[1][3] - m[1][2] * m[0][3];

    	double c5 = m[2][2] * m[3][3] - m[3][2] * m[2][3];
    	double c4 = m[2][1] * m[3][3] - m[3][1] * m[2][3];
    	double c3 = m[2][1] * m[3][2] - m[3][1] * m[2][2];
    	double c2 = m[2][0] * m[3][3] - m[3][0] * m[2][3];
    	double c1 = m[2][0] * m[3][2] - m[3][0] * m[2][2];
    	double c0 = m[2][0] * m[3][1] - m[3][0] * m[2][1];

    	double invD = 1.0 / (s0 * c5 - s1 * c4 + s2 * c3 + s3 * c2 - s4 * c1 + s5 * c0);

    	double[][] mI = new double[4][4];
    	// pls no 0 determinants pls pls

    	mI[0][0] = ( m[1][1] * c5 - m[1][2] * c4 + m[1][3] * c3) * invD;
    	mI[0][1] = (-m[0][1] * c5 + m[0][2] * c4 - m[0][3] * c3) * invD;
    	mI[0][2] = ( m[3][1] * s5 - m[3][2] * s4 + m[3][3] * s3) * invD;
    	mI[0][3] = (-m[2][1] * s5 + m[2][2] * s4 - m[2][3] * s3) * invD;

    	mI[1][0] = (-m[1][0] * c5 + m[1][2] * c2 - m[1][3] * c1) * invD;
    	mI[1][1] = ( m[0][0] * c5 - m[0][2] * c2 + m[0][3] * c1) * invD;
    	mI[1][2] = (-m[3][0] * s5 + m[3][2] * s2 - m[3][3] * s1) * invD;
    	mI[1][3] = ( m[2][0] * s5 - m[2][2] * s2 + m[2][3] * s1) * invD;

    	mI[2][0] = ( m[1][0] * c4 - m[1][1] * c2 + m[1][3] * c0) * invD;
    	mI[2][1] = (-m[0][0] * c4 + m[0][1] * c2 - m[0][3] * c0) * invD;
    	mI[2][2] = ( m[3][0] * s4 - m[3][1] * s2 + m[3][3] * s0) * invD;
    	mI[2][3] = (-m[2][0] * s4 + m[2][1] * s2 - m[2][3] * s0) * invD;

    	mI[3][0] = (-m[1][0] * c3 + m[1][1] * c1 - m[1][2] * c0) * invD;
    	mI[3][1] = ( m[0][0] * c3 - m[0][1] * c1 + m[0][2] * c0) * invD;
    	mI[3][2] = (-m[3][0] * s3 + m[3][1] * s1 - m[3][2] * s0) * invD;
    	mI[3][3] = ( m[2][0] * s3 - m[2][1] * s1 + m[2][2] * s0) * invD;
    	cur.setInv(mI);

    	cur.sumI= cur.sum;
		for (String line : cur.getObj()) {
			String[] parsed = line.split(" ");
			if (parsed[0].equals("v")) {
				double[] prime = new double[4];
				double x = Double.parseDouble(parsed[1]);
				double y = Double.parseDouble(parsed[2]);
				double z = Double.parseDouble(parsed[3]);
				prime[1] = (x * cur.tranMI[0][0]) + (y * cur.tranMI[0][1]) + (z * cur.tranMI[0][2]) + (1 * cur.tranMI[0][3]);
				prime[2] = (x * cur.tranMI[1][0]) + (y * cur.tranMI[1][1]) + (z * cur.tranMI[1][2]) + (1 * cur.tranMI[1][3]);
				prime[3] = (x * cur.tranMI[2][0]) + (y * cur.tranMI[2][1]) + (z * cur.tranMI[2][2]) + (1 * cur.tranMI[2][3]);

				cur.sumI -= Math.abs(x - prime[1]) + Math.abs(y - prime[2]) + Math.abs(z - prime[3]);
			}
		}
	}

	private void intake(String line) throws IOException {
    	BufferedReader reader;

		String[] data = line.split(" ");
		model n = new model(data);
		models.add(n);


		// Intakes the model files
    	for (model x : models) {
    	   	reader = new BufferedReader(new FileReader(x.getFileName()));

    	   	// Creates and populates ArrayList with a components.model
			ArrayList<String> obj = new ArrayList<>();
    	   	while ((line = reader.readLine()) != null)
    	   		if (!((line.charAt(0) == 'v') && (line.charAt(1) == 'n'))) 
    				obj.add(line);
    		x.setMod(obj);
    		if(data.length == 11) x.args = data[10];
    		else x.args = data[9];
    		reader.close();
    	}
	}
	
	private void execute(String ID) throws IOException {
		for (model n : models) {
			rotate(n);
    		scale(n);
   			translation(n);
   			solve(n);
   			GetInverse(n);
   			output(n, ID);
    	}
	}
	
	private void output(model cur, String ID) throws IOException {
		// Creates folder
		String dirPath = new File(".").getCanonicalPath() + "/" + ID + "_" + cur.args.substring(0, cur.args.length() - 4);
		File file = new File(dirPath  + ".obj");

		// Prints .obj
        FileWriter writer = new FileWriter(file);
        for(String str: cur.model) {
  			writer.write(str + "\n");
		}
		writer.close();
	}

	// Each instance represents a single components.model
	private static class model {
		//Driver data, memory components.object data, Transform matrices
		private String[] driver;
		private ArrayList<String> model = new ArrayList<>();
		double[][] tranM = new double[4][4];
		double[][] tranMI = new double[4][4];
		double sum = 0;
		double sumI = 0;
		String args;
		
		// Constructs
		private model(String[] driver){
		 	this.driver = driver; 
		 	for(int i = 0; i<4; i++)
    			for(int j = 0; j<4; j++)
        			tranM[i][j] = 0;
        			
        	tranM[0][0] = 1;
        	tranM[1][1] = 1;
        	tranM[2][2] = 1;
        	tranM[3][3] = 1;
		}
		
		// Getters
		private String getFileName() {
			if(driver.length == 11) return driver[10];
			else return driver[9];
		}
		private ArrayList<String> getObj() { 
			return this.model; 
		}
		private String[] getDriver() { 
			return this.driver; 
		}
		
		// Setters
		private void setMod(ArrayList<String> model){
			this.model = model;
		}
		private void setInv(double[][] mI){
			this.tranMI = mI;		
		}
	}
}
