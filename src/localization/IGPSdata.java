package localization;

public interface IGPSdata {
	 /** 
	  * Maintains connection with GPS and tracks data(i.e.,distance)
	  * To get possible routes the data about origin and destination locations 
	  * will be extracted from the GPS
	  * @param origin: co-ordinates for the origin location
	  * @param destination = co-ordinates for the 
	  */
    void getNavData(double[] origin,double[] destination);
}
