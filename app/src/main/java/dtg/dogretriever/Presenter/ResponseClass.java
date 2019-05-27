package dtg.dogretriever.Presenter;

import java.util.ArrayList;

import dtg.dogretriever.Presenter.LearningAlgoTemp.Cluster;

public class ResponseClass {

    ArrayList<Cluster> clustersList;

    public ArrayList<Cluster> getClustersList() {
        return clustersList;
    }

    public void setClustersList(ArrayList<Cluster> clustersList) {
        this.clustersList = clustersList;
    }

    public ResponseClass() {
    }

    public ResponseClass(ArrayList<Cluster> clustersList) {
        this.clustersList = clustersList;
    }

}
