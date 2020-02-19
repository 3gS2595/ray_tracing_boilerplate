package Raytracer.objects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class model implements object {

	public int id;
	public double d;
	public String str;
	public String fileOBJ;
	private String fileMTL;
	public List<material> mats = new ArrayList<>();
	public HashMap<Double, String> matUsed = new HashMap<>();
	public List<double[]> v = new ArrayList<>();
	private List<double[]> f = new ArrayList<>();
	public static HashMap<double[], ArrayList<double[][]>> map = new HashMap<>();
	public static List<double[][]> comp = new ArrayList<>();

	public static void intake(model model){
		BufferedReader reader;
		String line;
		try {

		// OBJ
			if (model.fileOBJ != null) {

				//adds oen to offset from 0 as to keep indices correct
				model.v.add(new double[]{0.0, 0.0, 0.0});

				reader = new BufferedReader(new FileReader(model.id + "_" + model.fileOBJ));
				double[] matkey = new double[]{0.0, 0.0};
				while ((line = reader.readLine()) != null) {
					String[] raw = line.split(" ");
					double[] num = new double[raw.length];

					// Skips comments
					if (!raw[0].equals("#")) {

						// Converts raw string into doubles
						for (int i = 1; i < raw.length; i++) {
							try {
								num[i] = Double.parseDouble(raw[i]);
							} catch (Exception ignored) {
							}
						}

						switch (raw[0]) {
							case "mtllib":
								model.fileMTL = raw[1];
								break;
							case "v":
								model.v.add(new double[]{num[1], num[2], num[3], matkey[0]});
								map.put(new double[]{num[1], num[2], num[3], matkey[0]}, null);
								break;
							case "f":
								int[] one = new int[]{Integer.parseInt(raw[1].split("//")[0]),
													  Integer.parseInt(raw[2].split("//")[0]),
									 				  Integer.parseInt(raw[3].split("//")[0])};


								double[][] temps = new double[][]{model.v.get(one[0]).clone(),
									model.v.get(one[1]).clone(),
									model.v.get(one[2]).clone(),
									matkey.clone()};
								comp.add(temps);

//								ArrayList<double[][]> te = model.map.get(model.v.get(one[0]).clone());
//								te.add(temps);
//								model.map.replace(model.v.get(one[0]).clone(), te);
								break;
							case "usemtl":
								matkey[0] = matkey[0] + 1;
								model.matUsed.put(matkey[0], raw[1]);
								break;




//								ArrayList<double[][]> temp;
//								matkey[1] = one[0];
//								if(!model.map.containsKey(one[0])){
//									temp = new ArrayList<>();
//									temp.add(new double[][]{math.unit(model.v.get(one[0]).clone()),
//										math.unit(model.v.get(one[1]).clone()),
//										math.unit(model.v.get(one[2]).clone()),
//										matkey.clone()});
//									model.map.put(one[0], temp);
//								} else {
//									temp = model.map.get(one[0]);
//									temp.add(new double[][]{math.unit(model.v.get(one[0]).clone()),
//										math.unit(model.v.get(one[1]).clone()),
//										math.unit(model.v.get(one[2]).clone()),
//										matkey.clone()});
//									model.map.replace(one[0], temp);
//								}

//								for (Double i : one) {
//									System.out.println(i);
//									if (!model.map.containsKey(i)) {
//										ArrayList<double[]> temp = new ArrayList<>();
//										temp.add(one);
//										model.map.put(i.intValue(), temp);
//									} else {
//										ArrayList<double[]> temp = model.map.get(i);
//										temp.add(one);
//										model.map.replace(i.intValue(), temp);
//									}
//								}
//								System.out.println();


						}
					}
				}

//				double a = 0;
//				double b = 0;
//				double c = 0;
//
//				for (HashMap.Entry mapElement : model.map.entrySet()) {
//					double[] key = (double[]) mapElement.getKey();
//					ArrayList<double[][]> value = (ArrayList<double[][]>) mapElement.getValue();
//					for(double[][] h : value){
//						double[]A = h[0];
//						double[]B = h[0];
//						double[]C = h[0];
//						a += math.unit(math.cross(math.sub(A, B), math.sub(A, C)))[0];
//						b += math.unit(math.cross(math.sub(A, B), math.sub(A, C)))[1];
//						c += math.unit(math.cross(math.sub(A, B), math.sub(A, C)))[2];
//					}
//					value = new ArrayList<>();
//					value.add(new double[][]{new double[]{a,b,c}});
//					model.map.replace(key, value);
//				}

//						double[] A = new double[]{0.0, 0.0, 0.0};
//						double[] B = new double[]{0.0, 0.0, 0.0};
//						double[] C = new double[]{0.0, 0.0, 0.0};
//						double[] N = new double[]{0.0};
//						int    cnt = 0;
//
//						for(double[] i : value){
//							A[0] += model.v.get((int)i[0])[1];
//							A[1] += model.v.get((int)i[0])[2];
//							A[2] += model.v.get((int)i[0])[2];
//
//							B[0] += model.v.get((int)i[1])[0];
//							B[1] += model.v.get((int)i[1])[1];
//							B[2] += model.v.get((int)i[1])[2];
//
//							C[0] += model.v.get((int)i[2])[0];
//							C[1] += model.v.get((int)i[2])[1];
//							C[2] += model.v.get((int)i[2])[2];
//							cnt++;
//						}
//
//						for (int i = 0; i < 3; i++){
//							A[i] = A[i]/cnt;
//							B[i] = B[i]/cnt;
//							C[i] = C[i]/cnt;
//						}
//						N = math.unit(math.cross(math.sub(A, B), math.sub(A, C)));
//
//						ArrayList<double[]> i = new ArrayList<>();
//						i.add(0,A);
//						i.add(1,B);
//						i.add(2,C);
//						i.add(3,N);
//						i.add(4, matkey);
//						model.map.replace(key, i);
//					}
			}

		// MTL
			if(model.fileMTL != null) {
				reader = new BufferedReader(new FileReader(model.fileMTL));
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
								// (Theoretical) entrance
								m = new material();
								m.name = raw[1];
								break;
							case "Ns":
								m.alpha = num[1];
								break;
							case "Ka":
								m.ka = new double[]{num[1], num[2], num[3]};
								break;
							case "Kd":
								m.kd = new double[]{num[1], num[2], num[3]};
								break;
							case "Ks":
								m.ks = new double[]{num[1], num[2], num[3]};
								break;
							case "Tr":
								m.tr = new double[]{num[1], num[2], num[3]};
								break;
							case "Ni":
								m.ni = num[1];
								break;
							case "illum":
								if (num[1] == 3) {
									m.kr = m.ks;
								} else m.kr = new double[]{0.0, 0.0, 0.0};
								if(m.name != null && m.ka != null && m.kd != null && m.ks != null && m.kr != null) {
									// (Theoretical) exit
									model.mats.add(m);
								}else System.out.println("MAT LOAD FAILURE");
								break;
						}
					}
				}
			}
		} catch (IOException e) { e.printStackTrace(); }

//		clean up
		File file = new File(model.id + "_" + model.fileOBJ);
		if( !(file.delete())){ System.out.println("FAILED DELETING TRANSFORM OBJ FILE"); }
	}

}