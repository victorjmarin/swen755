package perception;

public interface AudioRecognition {

    /**
     * Recognizes sirens and horns in the environment.
     * 
     * @param audioStream
     *            an audio stream coming from the sensors.
     */
    void detectBlaring(byte[] audioStream);

}
