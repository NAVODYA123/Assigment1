package fireAlarms;

//Sensor interface for every available reading sensors;
interface  SensorInterface {

	public double getTemperature();
	public double getBatteryLevel();
	public int getSmokeLevel();
	public int getCo2Level();
}
