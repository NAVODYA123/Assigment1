package fireAlarms;



public interface RequestParserInterface {
	public String auhtInit(String alarmId);
	public int Response(String response);
	public String exchangeCypher(String encrypted);
	public String sensorReadings(String temp,String battery,String smoke,String co2);
	public String getAuthToken(String response)throws ResponseException;
	
}
