package perception.object;

public interface Light extends EnvObject {

    public enum Color {
	GREEN, RED, AMBER
    }

    /**
     * Current color of the traffic light.
     * 
     * @return The current color of the traffic light.
     */
    Color getColor();

}
