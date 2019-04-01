package dtg.dogretriever.Presenter;

import java.util.ArrayList;

import dtg.dogretriever.Presenter.LearningAlgoTemp.Point;

public class RequestClass {
    ArrayList<Point> pointsList;

    public RequestClass() {
    }

    public RequestClass(ArrayList<Point> pointsList) {
        this.pointsList = pointsList;
    }

    public ArrayList<Point> getPointsList() {
        return pointsList;
    }

    public void setPointsList(ArrayList<Point> pointsList) {
        this.pointsList = pointsList;
    }

    /*
    String firstName;
    String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public RequestClass(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public RequestClass() {
    }

    */
}
