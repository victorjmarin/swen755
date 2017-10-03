package controller;

public interface IBrake {
	public void applyBrake(long Time);
	public void releaseBrake();
	public void jamBrake();
}
