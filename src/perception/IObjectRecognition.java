package perception;

import java.util.Set;

import perception.object.EnvObject;

public interface IObjectRecognition {

    /**
     * Scans the environment creating representations of the objects around.
     * 
     * @param radius
     *            The scanning radius
     * @return A set of environment objects around the car
     */
	Set<EnvObject> recognizeObject(Set<EnvObject> objects);

}
