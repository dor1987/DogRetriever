package dtg.dogretriever.Presenter.LearningAlgoTemp;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class Point {
    private int clusterId;
    private LatLng location;

    public Point(int clusterId, LatLng location) {
        this.clusterId = clusterId;
        this.location = location;
    }

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
