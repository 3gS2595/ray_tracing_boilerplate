package Raytracer;//DYLAN TRINKNER
//dbtrinkner@gmail.com

import Raytracer.objects.intake;
import Raytracer.objects.light;
import Raytracer.objects.material;

import java.io.FileWriter;
import java.io.IOException;

public class Raytracer {
	public static void main(String[] args) {
		Raytracer main = new Raytracer();
		if (args.length == 2) {
			try { main.execute(args[0], args[1]); } catch (Exception e) { e.printStackTrace(); }
		} else System.out.println("INCORRECT ARGUMENTS");
	}

	private void execute(String driverName, String outputName) throws IOException {
		cam c = new cam();
		intake.init(driverName, c);
		setup.init(c);
		render(c, outputName);
	}

	private void render(cam c, String outputName) {
		try {
			FileWriter writer = new FileWriter(outputName);
			writer.write("P3\n");
			writer.write((int) c.height + " " + (int) c.width + " " + "255\n");
			for (int y = 0; y < c.height; y++) {
				for (int x = 0; x < c.width; x++) {
					ray r = ray.pixelRay(x, y, c);
					double[] rgb = new double[]{0.0, 0.0, 0.0};
					ray_trace(c, r, rgb, new double[]{1.0, 1.0, 1.0}, c.depth);
					for (int i = 0; i < rgb.length; i++) {
						if (rgb[i] > 1)
							rgb[i] = 1;
						rgb[i] = rgb[i] * 255;
					}
					writer.write((int) rgb[0] + " " + (int) rgb[1] + " " + (int) rgb[2] + " ");
				}
				writer.write("\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// this method is oriented towards greatness
	private static void ray_trace(cam c, ray r, double[] accum, double[] refatt, int level) {
		if (ray.ray_find(r, c)) {
			material mat = r.best_mat;
			double[] N = r.N;
			double[] color = math.pair(c.amb, mat.ka);
			for (light lt : c.lights) {
				// to the light
				double[] toL = math.unit(math.sub(lt.p, r.best_pt));
				double NdotL = math.dot(N, toL);

				// "Do not eat the light"
				// "you will start subtracting the light"
				// "this will make terrance dimmer"
				boolean shadow = ray.shdw_find(new ray(r.best_pt, math.unit(math.sub(lt.p, r.best_pt))), c, lt.p);
				if (NdotL > 0.0) {
					if (!shadow) {
						color = math.add(color, math.mult(math.pair(mat.kd, lt.e), NdotL));

						double[] toC = math.unit(math.sub(r.L, r.best_pt));
						double[] spR = math.unit(math.sub(math.mult(N, 2 * NdotL), toL));
						double CdR = math.dot(toC, spR);
						if (CdR > 0.0) {
							color = math.add(color, math.mult(math.pair(mat.ks, lt.e), Math.pow(math.dot(toC, spR), mat.alpha)));
						}
					}
				}
			}
			for (int i = 0; i < 3; i++) { accum[i] = accum[i] + (refatt[i] * color[i]); }
			if (level > 0) {
				double[] flec = new double[]{0.0, 0.0, 0.0};
				double[] Uinv = math.mult(r.D, -1);
				double[] refR = math.unit(math.sub(math.mult(N, 2 * math.dot(N, Uinv)), Uinv));
				ray_trace(c, new ray(r.best_pt, refR), flec, math.pair(mat.kr, refatt), (level - 1));
				for (int i = 0; i < 3; i++) { accum[i] = accum[i] + refatt[i] * flec[i]; }
			}
		}
	}
}



