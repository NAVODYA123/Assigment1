package fireAlarms;

//Sensor interface for every available reading sensors;
interface  SensorInterface {

	public int getTemperature();
	public int getBatteryLevel();
	public int getSmokeLevel();
	public int getCo2Level();
}
