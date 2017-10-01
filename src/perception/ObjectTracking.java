package perception;

import perception.object.EnvObject;
import perception.object.Light;

public interface ObjectTracking {

    /**
     * Tracks changes in the color of a traffic light
     * 
     * @param light
     *            The traffic light whose color is to be tracked.
     */
    void trackLightColor(Light light);

    /**
     * Tracks an object in the environment to keep its state updated.
     * 
     * @param object
     *            The environment object whose status we want to keep updated.
     */
    void trackEnvObject(EnvObject object);

}
