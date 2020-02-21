package Raytracer.objects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class model implements object {

	int id;
	String OBJname;
	String MTLname;
	public List<double[]>   verts = new ArrayList<>();
	public List<double[][]> polys = new ArrayList<>();
	public List<material>    mats = new ArrayList<>();
	public HashMap<Double, String> matUsed = new HashMap<>();
	public static List<double[][]> comp = new ArrayList<>();
	public static HashMap<double[], ArrayList<double[][]>> map = new HashMap<>();



	// Handles .obj first and then .mtl
	public static void intake(model model){
		try {
			BufferedReader reader;
			String line;

		// OBJ
			if (model.OBJname != null) {

				//adds oen to offset from 0 as to keep indices correct
				model.verts.add(new double[]{0.0, 0.0, 0.0});

				reader = new BufferedReader(new FileReader(model.id + "_" + model.OBJname));
				double[] matID = new double[]{0.0, 0.0};
				while ((line = reader.readLine()) != null) {
					String[] raw = line.split(" ");
					double[] num = new double[raw.length];

					// Skips comments
					if (!raw[0].equals("#")) {

						// Converts raw string into doubles
						for (int i = 1; i < raw.length; i++) {
							try { num[i] = Double.parseDouble(raw[i]);
							} catch (Exception ignored) { }
						}

						switch (raw[0]) {
							case "mtllib":
								model.MTLname = raw[1];
								break;

							case "v":
								model.verts.add(new double[]{num[1], num[2], num[3], matID[0]});
								map.put(new double[]{num[1], num[2], num[3], matID[0]}, null);
								break;

							case "f":
								int[] one = new int[]{
										Integer.parseInt(raw[1].split("//")[0]),
										Integer.parseInt(raw[2].split("//")[0]),
										Integer.parseInt(raw[3].split("//")[0])
								};
								double[][] temps = new double[][]{
										model.verts.get(one[0]).clone(),
										model.verts.get(one[1]).clone(),
										model.verts.get(one[2]).clone(),
										matID.clone()
								};
								comp.add(temps);
								model.polys.add(temps);
								break;

							case "usemtl":
								matID[0] = matID[0] + 1;
								model.matUsed.put(matID[0], raw[1]);
								break;
						}
					}
				}
			}

		// MTL
			if(model.MTLname != null) {
				reader = new BufferedReader(new FileReader(model.MTLname));
				material m = new material();
				while ((line = reader.readLine()) != null) {
					String[] raw = line.split(" ");
					double[] num = new double[raw.length];

					// Skips comments
					if (!raw[0].equals("#")) {

						// Converts raw string into doubles
						for (int i = 1; i < raw.length; i++) {
							try { num[i] = Double.parseDouble(raw[i]);
							} catch (Exception ignored) {}
						}

						// Intake
						switch (raw[0]) {
							case "newmtl":
								// .mtl data intake entrance
								m = new material();
								m.name = raw[1];
								break;

							case "Ns": m.alpha = num[1];break;
							case "Ka": m.ka = new double[]{num[1], num[2], num[3]};break;
							case "Kd": m.kd = new double[]{num[1], num[2], num[3]};break;
							case "Ks": m.ks = new double[]{num[1], num[2], num[3]};break;
							case "Tr": m.tr = new double[]{num[1], num[2], num[3]};break;
							case "Ni": m.ni = num[1];break;

							case "illum":
								if (num[1] == 3){ m.kr = m.ks;}
								else{ m.kr = new double[]{0.0, 0.0, 0.0};}

								// LOAD COMPLETE CHECK
								if(m.name != null && m.ka != null && m.kd != null && m.ks != null) {
									model.mats.add(m);
								} else System.out.println("MAT LOAD FAILURE");
								break;
						}
					}
				}
			}
		} catch (IOException e) { e.printStackTrace(); }

//		clean up
		File file = new File(model.id + "_" + model.OBJname);
		if( !(file.delete())){
			System.out.println("FAILED TO DELETE OBJ TRANSFORM FILE");
		}
	}

}