import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class cam {
        cam() { }
        double[] eye, CLv, CUPv, W, U, V, bs, nf, rs, amb;
        double umax, umin, vmax, vmin, near, width, height;
        int depth = 3;
        List<object> objs  = new ArrayList<>();
        List<light> lights = new ArrayList<>();

        Map<String, double[]> cam = new HashMap<>();
}
