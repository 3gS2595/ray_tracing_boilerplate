package Raytracer;

import Raytracer.objects.globe;
import Raytracer.objects.material;
import Raytracer.objects.model;
import Raytracer.objects.object;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.ArrayRealVector;



class ray {
	double[] L;
	double[] N;
	double[] D;
	double[] best_pt;
	material best_mat;
	private Double best_t = Double.MAX_VALUE;
	globe best_sph = null;
	int flag = 0;

	ray(double[] L, double[] D) {
		this.D = math.unit(D);
		this.L = L;
		best_pt = new double[]{0.0, 0.0, 0.0};
	}

	static ray pixelRay(int x, int y, cam c) {
		double px = (x / (c.width - 1) * (c.umax - c.umin) + c.umin);
		double py = (y / (c.height - 1) * (c.vmin - c.vmax) + c.vmax);

		// Naming is base point L and direction D
		double[] L = math.add((math.add(math.add(c.eye, math.mult(c.W, c.near)), math.mult(c.U, px))), (math.mult(c.V, py)));
		double[] D = (math.sub(L, c.eye));
		return new ray(L, D);
	}

	// RETURNS OBJECT FOR DESIRED PIXEL
	static boolean ray_find(ray r, cam c) {
		for (object cur : c.objs) {
			if (cur instanceof model)
				model_test(r, (model) cur, false, null);

			if (cur instanceof globe)
				sphere_test(r, (globe) cur, false, null);
		}
		return r.best_mat != null;
	}

	// RETURNS OBJECT FOR DESIRED PIXEL
	static boolean shdw_find(ray r, cam c, double[] ltp) {
		for (object cur : c.objs) {
			if (cur instanceof model)
				model_test(r, (model) cur, true, ltp);

			if (cur instanceof globe)
				sphere_test(r, (globe) cur, true, ltp);
		}
		return r.best_mat != null;
	}

	private static void model_test(ray self, model cur, boolean shdw, double[] ltp) {

		for (double[][] vert : cur.comp) {
			double[] A = vert[0];
			double[] B = vert[1];
			double[] C = vert[2];

			double[] Y = {A[0] - self.L[0], A[1] - self.L[1], A[2] - self.L[2]};
			double[][] M = new double[][]{new double[]{A[0] - B[0], A[0] - C[0], self.D[0]},
				new double[]{A[1] - B[1], A[1] - C[1], self.D[1]},
				new double[]{A[2] - B[2], A[2] - C[2], self.D[2]}};

			RealMatrix a = new Array2DRowRealMatrix(M);
			DecompositionSolver solver = new LUDecomposition(a).getSolver();
			if (solver.isNonSingular()) {
				RealVector b = new ArrayRealVector(Y);
				RealVector x = solver.solve(b);
				double[] pt = x.toArray();
				double[] best = math.add(math.add(A, math.mult(math.sub(B, A), pt[0])), math.mult(math.sub(C, A), pt[1]));


				// SHADOW (checks if light source is between Raytracer.objects)
				double d2l = Double.MAX_VALUE;
				if (shdw)
					d2l = Math.sqrt(Math.pow(self.best_pt[0] - ltp[0], 2)
						+ Math.pow(self.best_pt[1] - ltp[1], 2)
						+ Math.pow(self.best_pt[2] - ltp[2], 2));

				if ((pt[2] < self.best_t) && (pt[2] > 0.0000001) && (pt[0] < d2l) && pt[0] >= 0 && pt[1] >= 0 && pt[0] + pt[1] <= 1) {
					self.best_t = pt[2];
					self.best_pt = best;
//					self.N = math.unit(math.add(C, math.add(B, math.mult(A, 1-pt[0] -pt[1]))));
					self.N = math.unit(math.cross(math.sub(A, B), math.sub(A, C)));
					if (math.dot(self.N, self.D) > 0) {
						self.N = math.unit(math.cross(math.sub(B, A), math.sub(A, C)));
					}

					for (material test : cur.mats) {
						if (test.name.equals(cur.matUsed.get(vert[3][0]))) {
							self.best_mat = test.clone();
						}
					}
				}
			}
		}
	}

	private static void sphere_test(ray self, globe sph, boolean shdw, double[] ltp) {
		double[] Tv = math.sub(sph.center, self.L);
		double v    = math.dot(Tv, self.D);
		double csq  = math.dot(Tv, Tv);
		double disc = (sph.radius * sph.radius) - (csq - (v * v));
		if (disc > 0) {
			double t = v - Math.sqrt(disc);

			// SHADOW (checks if light source is between Raytracer.objects)
			double d2l = Double.MAX_VALUE;
			if (shdw)
				d2l = Math.sqrt(Math.pow(self.best_pt[0] - ltp[0], 2)
					+ Math.pow(self.best_pt[1] - ltp[1], 2)
					+ Math.pow(self.best_pt[2] - ltp[2], 2));

			if ((t < self.best_t) && (t > 0.0000001) && t < d2l) {
				self.best_t = t;
				self.best_sph = sph;
				self.best_pt = math.add(self.L, math.mult(self.D, t));
				self.best_mat = self.best_sph.material;
				self.N = math.unit(math.sub(self.best_pt, self.best_sph.center));
			}
		}
	}


	//REFRACTION
	private static double[] refract_tray(ray self, double[] W, double[] pt, double[] N, double eta1, double eta2) {
		double etar = eta1 / eta2;
		double a = -1 * etar;
		double wn = math.dot(W, N);
		double radsq = Math.pow(etar, 2) * (Math.pow(wn, 2) -1) + 1;
		double[] T;
		double b;

		if (radsq < 0.0) {
			T = new double[]{0, 0, 0};
		} else {
			b = (etar * wn) - Math.sqrt(radsq);
			T = math.add(math.mult(W, a), math.mult(N, b));
		}
		return T;
	}

	static ray refract_exit(ray self, double[] W, double[] pt, double eta_in, double eta_out) {
		double[] T1 = refract_tray(self, W, pt, math.unit(math.sub(pt, self.best_sph.center)), eta_out, eta_in);
		if (T1[0] + T1[1] + T1[2] == 0.0) {
			return null;
		} else {
			double[] exit = math.add(pt, (math.mult(T1, (math.dot(math.sub(self.best_sph.center, pt), T1) * 2))));
			double[] Nin = math.unit(math.sub(self.best_sph.center, exit));
			if (math.dot(Nin, exit) < 0) {

			}

			double[] T2 = refract_tray(self, math.mult(T1, -1), exit, Nin, eta_in, eta_out);
			ray refR = new ray(exit, T2);
			System.out.println("hey hey ey");
			return refR;
		}
	}
}
