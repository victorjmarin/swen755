package controller;

public interface IThrottle {
	public void applyAccelerator(int speed);
	public void floorAccelerator();
	public void releaseAccelerator();
}
