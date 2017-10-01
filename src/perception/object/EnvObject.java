package perception.object;

/**
 * An EnvObject represents an environment object around the car: other cars, pedestrians,
 * bikers, signs, ...
 */
public interface EnvObject {

    /**
     * Distance from the car to the environment object.
     * 
     * @return The distance to the object in meters.
     */
    int distanceTo();

    /**
     * Indicates whether the environment object is currently moving or not.
     * 
     * @return A boolean indicating whether the environment object is still or in
     *         movement.
     */
    boolean isMoving();

}
