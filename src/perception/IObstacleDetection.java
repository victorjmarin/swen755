package perception;

import java.util.Set;
import perception.object.EnvObject;

public interface IObstacleDetection {

    /**
     * Given a set of environment objects, determines the subset of them that are
     * obstacles and the car should circumvent.
     * 
     * @param objects
     *            Set of environment objects to be classified as obstacles.
     * @return The subset of the environment objects that are obstacles.
     */
    Set<EnvObject> detectObstacles(Set<EnvObject> objects);
    
}
