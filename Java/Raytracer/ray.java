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
	double[] rPos;
	double[] rDir;
	double[] N;
	double[] best_pt;
	material best_mat;
	globe    best_sph = null;
	Double   best_t   = Double.MAX_VALUE;

	ray(double[] rPos, double[] rDir) {
		this.rDir = math.unit(rDir);
		this.rPos = rPos;
		best_pt = new double[]{0.0, 0.0, 0.0};
	}

	static ray pixelRay(int x, int y, cam c) {
		double pX = (x / (c.width  - 1) * (c.umax - c.umin) + c.umin);
		double pY = (y / (c.height - 1) * (c.vmin - c.vmax) + c.vmax);

		// Naming is base point L and direction D
		double[] rPos = math.add((math.add(math.add(c.eye, math.mult(c.W, c.near)), math.mult(c.U, pX))), (math.mult(c.V, pY)));
		double[] rDir = (math.sub(rPos, c.eye));
		return new ray(rPos, rDir);
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

		for (double[][] verts : cur.polys) {
			double[] A = verts[0];
			double[] B = verts[1];
			double[] C = verts[2];

			double[] Y = {A[0] - self.rPos[0], A[1] - self.rPos[1], A[2] - self.rPos[2]};
			double[][] M = new double[][]{
					new double[]{A[0] - B[0], A[0] - C[0], self.rDir[0]},
					new double[]{A[1] - B[1], A[1] - C[1], self.rDir[1]},
					new double[]{A[2] - B[2], A[2] - C[2], self.rDir[2]}
			};

			RealMatrix a = new Array2DRowRealMatrix(M);
			DecompositionSolver solver = new LUDecomposition(a).getSolver();
			if (solver.isNonSingular()) {
				RealVector b = new ArrayRealVector(Y);
				RealVector x = solver.solve(b);
				double[] pt = x.toArray();
				double[] best = math.add(math.add(A, math.mult(math.sub(B, A), pt[0])), math.mult(math.sub(C, A), pt[1]));

				// SHADOW (checks if light source is being blocked by in between object)
				double d2l = Double.MAX_VALUE;
				if (shdw)
					d2l = Math.sqrt(Math.pow(self.best_pt[0] - ltp[0], 2)
						+ Math.pow(self.best_pt[1] - ltp[1], 2)
						+ Math.pow(self.best_pt[2] - ltp[2], 2));

				if ((pt[2] < self.best_t) && (pt[2] > 0.0000001) && (pt[0] < d2l) && pt[0] >= 0 && pt[1] >= 0 && pt[0] + pt[1] <= 1) {
					self.best_t = pt[2];
					self.best_pt = best;
					self.N = math.unit(math.cross(math.sub(A, B), math.sub(A, C)));
					if (math.dot(self.N, self.rDir) > 0) {
						self.N = math.unit(math.cross(math.sub(B, A), math.sub(A, C)));
					}

					for (material mat : cur.mats) {
						if (mat.name.equals(cur.matUsed.get(verts[3][0]))) {
							self.best_mat = mat.clone();
						}
					}
				}
			}
		}
	}

	private static void sphere_test(ray self, globe sph, boolean shdw, double[] ltp) {
		double[] Tv = math.sub(sph.center, self.rPos);
		double v    = math.dot(Tv, self.rDir);
		double csq  = math.dot(Tv, Tv);
		double disc = (sph.radius * sph.radius) - (csq - (v * v));
		if (disc > 0) {
			double t = v - Math.sqrt(disc);

			// SHADOW (checks if light source is being blocked by in between object)
			double d2l = Double.MAX_VALUE;
			if (shdw)
				d2l = Math.sqrt(Math.pow(self.best_pt[0] - ltp[0], 2)
					+ Math.pow(self.best_pt[1] - ltp[1], 2)
					+ Math.pow(self.best_pt[2] - ltp[2], 2));

			if ((t < self.best_t) && (t > 0.0000001) && t < d2l) {
				self.best_t = t;
				self.best_sph = sph;
				self.best_pt = math.add(self.rPos, math.mult(self.rDir, t));
				self.best_mat = self.best_sph.material;
				self.N = math.unit(math.sub(self.best_pt, self.best_sph.center));
			}
		}
	}
}
