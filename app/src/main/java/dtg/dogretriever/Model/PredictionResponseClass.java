package dtg.dogretriever.Model;


public class PredictionResponseClass {

    double x;
    double y;


    public PredictionResponseClass() {
    }
    public PredictionResponseClass(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.y = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }


}
