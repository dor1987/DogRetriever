package dtg.dogretriever.Presenter.LearningAlgoTemp;

import com.google.android.gms.maps.model.LatLng;

public class Cluster {
    private int clusterId;
    private int numOfPoints;
    private LatLng center;
    private double diameter;
    private double sumOfPointsX;
    private double sumOfPointsY;
    private double sumOfPointsZ;


    public Cluster(int clusterId, LatLng center) {
        this.clusterId = clusterId;
        this.numOfPoints = 0;
        this.center = center;
        this.diameter = 0;
        this.sumOfPointsX = 0;
        this.sumOfPointsY = 0;
        this.sumOfPointsZ = 0;
    }


    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public int getNumOfPoints() {
        return numOfPoints;
    }

    public void setNumOfPoints(int numOfPoints) {
        this.numOfPoints = numOfPoints;
    }

    public LatLng getCenter() {
        return center;
    }

    public void setCenter(LatLng center) {
        this.center = center;
    }

    public double getDiameter() {
        return diameter;
    }

    public void setDiameter(double diameter) {
        this.diameter = diameter;
    }

    public void addPointToCluster(Point point){
        //Convret Latlng to double on 3 Axis (xyz) and add to sum Of Points
        //Assign the point to this cluster and increase num of points

        double convertedLat = point.getLocation().latitude * Math.PI / 180;
        double convertedLong = point.getLocation().longitude * Math.PI / 180;

        sumOfPointsX += Math.cos(convertedLat) * Math.cos(convertedLong);
        sumOfPointsY += Math.cos(convertedLat) * Math.sin(convertedLong);
        sumOfPointsZ += Math.sin(convertedLat);

        point.setClusterId(clusterId);
        numOfPoints+=1;
    }


    public void removePointFromCluster(Point point){
        double convertedLat = point.getLocation().latitude * Math.PI / 180;
        double convertedLong = point.getLocation().longitude * Math.PI / 180;

        sumOfPointsX -= Math.cos(convertedLat) * Math.cos(convertedLong);
        sumOfPointsY -= Math.cos(convertedLat) * Math.sin(convertedLong);
        sumOfPointsZ -= Math.sin(convertedLat);

        numOfPoints-=1;

    }

    public void calcAndSetUpdatedCenter(){
        //https://stackoverflow.com/questions/6671183/calculate-the-center-point-of-multiple-latitude-longitude-coordinate-pairs

        //calculating the average sum of points
        //convreting average from 3d vector to latlng(spherical coords)
        //setting it to be new cluster center
        //ToDo when numOfPoints = 1 doesnt have to calc center, u can just set center as the point cords. need to think of a way to implement
        double x = sumOfPointsX / numOfPoints;
        double y = sumOfPointsY / numOfPoints;
        double z = sumOfPointsZ / numOfPoints;

        double preConvertNewCenterLongitude = Math.atan2(y,x);
        double cenetralSquareRoot = Math.sqrt(x*x + y*y);
        double preConvertNewCenterLatitude = Math.atan2(z,cenetralSquareRoot);

        this.center = new LatLng(preConvertNewCenterLatitude * 180 / Math.PI, preConvertNewCenterLongitude* 180 / Math.PI);
        center=center;
    }

}
