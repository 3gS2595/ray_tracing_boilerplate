package Raytracer;

import Raytracer.objects.light;
import Raytracer.objects.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class cam {
        public cam() { }
        public double[] eye, CLv, CUPv, W, U, V, bs, nf, rs, amb;
        public double umax, umin, vmax, vmin, near, width, height;
        public int depth = 3;
        public List<object> objs  = new ArrayList<>();
        public List<light> lights = new ArrayList<>();

        public Map<String, double[]> cam = new HashMap<>();
}
