package dtg.dogretriever.Controller;

import dtg.dogretriever.Model.Dog;

public interface DogScanListFunctionalityInterface {
    void showOwnerInformation(String ownerId);
    void scanDog(Dog dog);
}
