public class material {
    public double[] ka; // ambient
    public double[] kd; // diffuse
    public double[] ks; // specular
    public double[] kr; // reflection constant
    public double[] tr; // reflection constant

    public String name;
    public double ni;
    public double eta;
    public double alpha = 100;

    public material clone(){
        material n = new material();
        n.ka = this.ka;
        n.kd = this.kd;
        n.ks = this.ks;
        n.kr = this.kr;
        n.name = this.name;
        n.alpha = this.alpha;
        n.tr = this.tr;

        return n;
    }
    public double sum(){
        return this.tr[0] + this.tr[1] + this.tr[2];
    }
}
