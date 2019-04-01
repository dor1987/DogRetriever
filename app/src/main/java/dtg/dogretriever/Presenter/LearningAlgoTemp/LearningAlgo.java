package dtg.dogretriever.Presenter.LearningAlgoTemp;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Map;

import dtg.dogretriever.Model.Coordinate;
import dtg.dogretriever.Model.Scan;

public class LearningAlgo {
//
//    ArrayList<Cluster> clustersList;
//    ArrayList<Point> pointsList;
//    double wantedQuality,currentQuality;
//    int maxNumberOfKmeansIterartion;
//    boolean pointsMoved,qualityFlag;
//    int limitCounter;
//
//    public LearningAlgo() {
//        clustersList = new ArrayList<Cluster>();
//        pointsList = new ArrayList<Point>();
//        wantedQuality = 1;
//        maxNumberOfKmeansIterartion = 100;
//
//       // learningAlgo();
//    }
//
//    public ArrayList<Coordinate> learningAlgo(Map<String,Scan> scansMap){
//        //Current location should be taken from Gps sensor and radius should be taken from the user
//        // pointsList = gettingPointsFromFireBase(2000,new LatLng(32.3054855,35.0048861));
//        ArrayList<Coordinate> coordinatesResultOfAlgo = new ArrayList<>();
//
//        pointsList = convertScanMapToPointsArrayList(scansMap);
//        int numOfClusters = 3; //Starting with 1 if cluster is good enough ok else increase k by 1;
//        currentQuality = 0;
//        qualityFlag = false;
//
//
//
//        if(pointsList.size()<20){
//            //if not enough points return the list of points
//            for(Scan scan: scansMap.values()){
//                coordinatesResultOfAlgo.add(scan.getCoordinate());
//            }
//            return coordinatesResultOfAlgo;
//        }
//
//
//        initAllClusters(pointsList,clustersList,numOfClusters);
//
//        while(qualityFlag == false) {
//            pointsMoved = true;
//            limitCounter = 0;
//
//
//            while (pointsMoved && (limitCounter < maxNumberOfKmeansIterartion)) {
//                pointsMoved = assignPointsListToClusters(pointsList, clustersList);
//                AssignNewCentersToAllClusters(clustersList);
//                limitCounter++;
//            }
//
//
//            calClustersListDiameters(clustersList, numOfClusters, pointsList);
//
//            currentQuality = evaluateClustersQuality(clustersList, numOfClusters);
//
//            if (wantedQuality < currentQuality) {
//                Log.d("low Quality: "+currentQuality," Number Of Clusters "+numOfClusters);
//                qualityFlag = false;
//                numOfClusters++;
//            } else {
//                qualityFlag = true;
//                Log.d("Success Clustering numOfClusters: "+numOfClusters," Clusters Quality "+currentQuality);
//
//                for (Cluster cluster : clustersList){
//                   // Log.d("Cluster Id: "+cluster.getClusterId()," Cluster Center: "+ cluster.getCenter()+" Size "+ cluster.getNumOfPoints());
//                    coordinatesResultOfAlgo.add(new Coordinate(cluster.getCenter().latitude,cluster.getCenter().longitude));
//                }
//            }
//
//            resetClustersArrAndPoints(clustersList, numOfClusters, pointsList);
//        }
//        return coordinatesResultOfAlgo;
//    }
//
//
//
//
//    public boolean assignPointsListToClusters(ArrayList<Point> pointsList, ArrayList<Cluster> clustersList){
//        //assign all points to clusters
//        //if point moved clusters return true else false
//
//        boolean pointMoved = false;
//        for(int i = 0; i<pointsList.size();i++){
//            if(addPointToClosestCluster(pointsList.get(i),clustersList)){
//                pointMoved = true;
//            }
//        }
//
//        return pointMoved;
//    }
//
//    public void addPointToCluster(Cluster cluster, Point point){
//        //assign a single point to cluster
//        cluster.addPointToCluster(point);
//
//    }
//
//    public void removePointFromCluster(Cluster cluster, Point point){
//        //remove a single point from cluster
//        cluster.removePointFromCluster(point);
//    }
//
//    public boolean movePointToOtherCluster(Cluster srcCluster, Cluster destCluster, Point point){
//        //check if point need to move to other cluster
//        //if it need to move , move it and return true else do nothing and return false
//        if(srcCluster.getClusterId() != destCluster.getClusterId()){
//            removePointFromCluster(srcCluster,point);
//            addPointToCluster(destCluster,point);
//            return true;
//        }
//
//        return false;
//    }
//    public double calcDistanceBetweenLocations(LatLng firstLocation, LatLng secondLocation){
//        //calc  distance between two latLngs
//        //https://readyandroid.wordpress.com/calculate-distance-between-two-latlng-points-using-google-api-or-math-function-android/
//        Location location1 = new Location("");
//        location1.setLatitude(firstLocation.latitude);
//        location1.setLongitude(firstLocation.longitude);
//
//        Location location2 = new Location("");
//        location2.setLatitude(secondLocation.latitude);
//        location2.setLongitude(secondLocation.longitude);
//
///*
//        double numOfMinutesInADegree = 60;
//        double NumOfStatuteMilesInANauticalMile = 1.1515;
//        //One nautical mile is the length of one minute of latitude at the equator.
//
//        double firstLat = firstLocation.latitude;
//        double firstLng = firstLocation.longitude;
//
//        double secondLat = secondLocation.latitude;
//        double secondLng = secondLocation.longitude;
//
//        double theta = firstLat - secondLat;
//
//        double distance = Math.sin(deg2rad(firstLat))
//                * Math.sin(deg2rad(secondLng))
//                + Math.cos(deg2rad(firstLat))
//                * Math.cos(deg2rad(secondLng))
//                * Math.cos(deg2rad(theta));
//
//        distance = Math.acos(distance);
//        distance = rad2deg(distance);
//        distance = distance * numOfMinutesInADegree * NumOfStatuteMilesInANauticalMile;
//        //distance is in miles
//
//        distance *= 1.609344; // 1 mile = 1.609344 Km
//*/
//
//
//        return location1.distanceTo(location2);
//    }
//
//    private double deg2rad(double deg) {
//        return (deg * Math.PI / 180.0);
//    }
//
//    private double rad2deg(double rad) {
//        return (rad * 180.0 / Math.PI);
//    }
//
//
//    public boolean addPointToClosestCluster(Point point, ArrayList<Cluster> clustersList){
//        //find the closest cluster to point
//        double closestDistance = calcDistanceBetweenLocations(point.getLocation(),clustersList.get(0).getCenter());
//        double distanceFromCenter = 0;
//        Cluster closestCluster = clustersList.get(0);
//        Cluster originalCluster;
//
//        for (int i=1 ; i < clustersList.size();i++){
//            distanceFromCenter = calcDistanceBetweenLocations(point.getLocation(),clustersList.get(i).getCenter());
//
//            if(distanceFromCenter < closestDistance) {
//                closestDistance = distanceFromCenter;
//                closestCluster = clustersList.get(i);
//            }
//        }
//
//        if(point.getClusterId() == -1){
//            addPointToCluster(closestCluster,point);
//            return true;
//        }
//
//        else {
//            originalCluster = clustersList.get(point.getClusterId());
//            return movePointToOtherCluster(originalCluster,closestCluster,point);
//        }
//    }
//
//    public void initAllClusters(ArrayList<Point> pointsList, ArrayList<Cluster> clustersList, int numOfCluster){
//        //create new cluster
//        //set center as the first i points location
//        //set cluster id
//        //add it to list
//
//        for(int i = 0; i< numOfCluster;i++){
//            clustersList.add(new Cluster(i,pointsList.get(i).getLocation()));
//        }
//    }
//
//    public void calClustersListDiameters(ArrayList<Cluster> clustersList, int numOfClusters, ArrayList<Point> pointsList){
//        //calculte the maximum distance between 2 points in the cluster and set it as diameter
//        double tempDistance, currentDiameter;
//        int numOfPoints = pointsList.size();
//
//        for(int i = 0; i <pointsList.size();i++){
//            for(int j = i+1; j<numOfPoints;j++){
//                if(pointsList.get(i).getClusterId() == pointsList.get(j).getClusterId()){
//                    tempDistance = calcDistanceBetweenLocations(pointsList.get(i).getLocation(),pointsList.get(j).getLocation());
//                    currentDiameter = clustersList.get(pointsList.get(i).getClusterId()).getDiameter();
//                    if(tempDistance  > currentDiameter){
//                        clustersList.get(pointsList.get(i).getClusterId()).setDiameter(tempDistance);
//                    }
//                }
//            }
//        }
//
//    }
//
//    void resetClustersArrAndPoints(ArrayList<Cluster> clustersList, int numOfClusters,ArrayList<Point> pointsList){
//        //Reset all clusters back to empty mode and set default centers
//        clustersList.clear();
//        initAllClusters(pointsList,clustersList,numOfClusters);
//        for (Point point : pointsList){
//            point.setClusterId(-1);
//        }
//    }
//
//    public ArrayList<Point> gettingPointsFromFireBase(int radiusInMeters ,LatLng userCurrentLocation){
//        //Get relevant points from Firebase database filtering them by radius from current user location
//        //put them in a list and return it
//        //ToDo - Need to be implemented
//
//        ArrayList<Point> listOfPointsNearBy = new ArrayList<>();
//
//
//        return listOfPointsNearBy;
//    }
//    public ArrayList<Point> convertScanMapToPointsArrayList(Map<String,Scan> scansMap){
//        //getting a scansArrayList and converting them to points
//        ArrayList<Point> listOfPointsNearBy = new ArrayList<>();
//
//
//
//        for(Scan scan : scansMap.values()){
//            Coordinate tempCoord = scan.getCoordinate();
//            listOfPointsNearBy.add(new Point(-1,new LatLng(tempCoord.getLatitude(),tempCoord.getLongitude())));
//        }
//
//        return listOfPointsNearBy;
//    }
//    public ArrayList<Point> gettingPointsFromLocalDataBase(){
//        //Used only for testing and debugging
//        ArrayList<Point> listOfPointsNearBy = new ArrayList<>();
//
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.08214923363315,34.80973559087552)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.30965915534621,35.006943696630394)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.122594782521034,34.80994942808484)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.0821234983586,34.816128063089735)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.309201756866045,35.00790375055067)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.301540364314825,35.003616874787326)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.30297116791951,35.00883232191095)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.301853861395784,34.999974026064486)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.307957678403675,35.00205847134719)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.125511073888845,34.81258408487187)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.12528177274484,34.81332506319516)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.123645270277706,34.81393239266712)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.08234840489815,34.81119792221434)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.07811485430044,34.81018849244735)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.31308175251784,35.003511862267445)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.30161574391205,35.00124216323976)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.12913059187641,34.80537750370099)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.12157308587709,34.80429037031791)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.128402993488706,34.80228116177379)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.30742858225217,35.00030379425451)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.0819812469211,34.80298510886702)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.07761317390051,34.811480581835966)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.308372502253164,35.0031738785443)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.30937101025047,35.00621857477045)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.127686973093496,34.79913440003735)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.30808746975593,35.003672518193355)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.12611975611925,34.80825883963966)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.119167042488606,34.81779009787967)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.07972762650779,34.81489980384389)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.080824278772,34.80693192482819)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.07786351024917,34.81254461502623)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.117271318214364,34.80509003780197)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.11795588021349,34.80502609826862)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.07681431389271,34.809295244000225)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.304072798835364,34.99933955659375)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.31273943626436,35.00241022771109)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.125782330008555,34.8092459022293)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.12071912805489,34.813087196489924)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.1225855030416,34.80684804672792)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.12486591252194,34.80685846703949)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.127293722326485,34.80548486380975)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.088521878139645,34.80664209886034)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.09226664876409,34.80722027040843)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.11793664521571,34.81105551132901)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.11702412133692,34.80348298054272)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.30473755705877,35.005362845569316)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.305835528089546,35.01085897543265)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.12215359171045,34.807486337850634)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.08358199225728,34.808438882182266)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.0828232078412,34.814821612482845)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.30420997044915,35.008693959886195)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.12339916373115,34.80598699271983)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.12265149053522,34.80182889161968)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.08264568103754,34.80966130961121)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.079514569603234,34.815169762820176)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.087958176876654,34.80324789751281)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.08212833675388,34.808852234092726)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.12306824012199,34.79815297650883)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.11864499804148,34.810123230136014)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.077953257816574,34.80569725413793)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.11928870418418,34.8023828203432)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.11252855125048,34.809800070974845)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.11642287920026,34.80379987763691)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.12617817620688,34.81125965968172)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.311036271604245,35.004688638062056)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.30577656295863,35.00339558951406)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.31015462571517,35.00697572474253)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.08093143081311,34.813131102092065)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.085289678429454,34.80255675134506)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.305205857325284,35.003482289782234)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.3076218762915,35.00423706198886)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.07649226494996,34.811269796956864)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.08582769946793,34.81964923399657)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.08487467320897,34.81286095969649)));
//        listOfPointsNearBy.add(new Point(-1,new LatLng(32.08211257471592,34.819152798747396)));
//
//        return listOfPointsNearBy;
//    }
//
//    public void AssignNewCentersToAllClusters(ArrayList<Cluster> clustersList){
//
//        for (int i = 0 ; i<clustersList.size();i++){
//            calcAndAssignNewCenterToSingleCluster(clustersList.get(i));
//        }
//    }
//
//    public void calcAndAssignNewCenterToSingleCluster(Cluster cluster){
//        //Calculate the new and updated center based of the points that belong to it
//        cluster.calcAndSetUpdatedCenter();
//    }
//
//    public double evaluateClustersQuality(ArrayList<Cluster> clustersList, int numOfClusters){
//        //evluate the quality of the clusters
//        //ToDo need to add more elements to determine the quality , right now it's only based to centers distance / diameters
//        double quality = 0;
//        double distanceBetweenCenters;
//
//        for(int i = 0; i < numOfClusters; i++){
//            for (int j = i+1; j< numOfClusters;j++){
//                distanceBetweenCenters = calcDistanceBetweenLocations(clustersList.get(i).getCenter(),clustersList.get(j).getCenter());
//                if(distanceBetweenCenters != 0){
//                    quality += (clustersList.get(i).getDiameter() / distanceBetweenCenters) + (clustersList.get(j).getDiameter() / distanceBetweenCenters);
//                }
//            }
//        }
//        quality = quality;
//        return (quality / (numOfClusters * (numOfClusters - 1)));
//
//    }

}
