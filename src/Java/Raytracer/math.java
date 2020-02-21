package Raytracer;

class math {

    static double[] unit(double[] a){
        double[] temp = new double[a.length];
        double norm = 0;
        for (int i = 0; i < 3; i++) {
            temp[i] = a[i] * a[i];
            norm += temp[i];
        }
        norm = Math.sqrt(norm);
        for (int i = 0; i < 3; i++) {
            temp[i] = a[i] / norm;
        }
        return temp;
    }

    static double[] cross(double[] a, double[] b){
        double[] temp = new double[a.length];
        temp[0] = a[1] * b[2] - a[2] * b[1];
        temp[1] = a[2] * b[0] - a[0] * b[2];
        temp[2] = a[0] * b[1] - a[1] * b[0];
        return temp;
    }

    static double dot(double[] a, double[] b){
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    static double[] mult(double[] a, double b){
        double[] temp = new double[a.length];
        temp[0] = a[0] * b;
        temp[1] = a[1] * b;
        temp[2] = a[2] * b;
        return temp;
    }

    static double[] pair(double[] a, double[] b){
        double[] temp = new double[a.length];
        temp[0] = a[0] * b[0];
        temp[1] = a[1] * b[1];
        temp[2] = a[2] * b[2];
        return temp;
    }

    static double[] sub(double[] a, double[] b){
        double[] returnMatrix = new double[a.length];
        for (int i = 0; i < 3; i++) {
            returnMatrix[i] = a[i] - b[i];
        }
        return returnMatrix;
    }

    static double[] add(double[] a, double[] b){
        double[] returnMatrix = new double[a.length];
        for (int i = 0; i < 3; i++) {
            returnMatrix[i] = a[i] + b[i];
        }
        return returnMatrix;
    }
}