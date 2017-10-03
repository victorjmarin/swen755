package perception;

public interface IAudioRecognition {

    /**
     * Recognizes sirens and horns in the environment.
     * 
     * @param audioStream
     *            an audio stream coming from the sensors.
     */
    void detectBlaring(byte[] audioStream);

}
