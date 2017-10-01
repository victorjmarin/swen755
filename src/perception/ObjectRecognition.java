package perception;

import java.util.List;

import perception.object.EnvObject;

public interface ObjectRecognition {

    /**
     * Scans the environment creating representations of the objects around.
     * 
     * @param radius
     *            The scanning radius
     * @return A list of environment objects around the car
     */
    List<EnvObject> recognizeEnvironment(int radius);

    /**
     * Searches for free parking slots in the proximity of the car.
     * 
     * @return A list of available parking slots.
     */
    List<EnvObject> findFreeParkingSlots();

}
