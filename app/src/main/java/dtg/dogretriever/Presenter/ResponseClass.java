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

    /*
    String greetings;

    public String getGreetings() {
        return greetings;
    }

    public void setGreetings(String greetings) {
        this.greetings = greetings;
    }

    public ResponseClass(String greetings) {
        this.greetings = greetings;
    }

    public ResponseClass() {
    }
    */
}
