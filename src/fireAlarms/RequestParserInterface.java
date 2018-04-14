package fireAlarms;



public interface RequestParserInterface {
	public String auhtInit(String alarmId);
	public int Response(String response);
	public String exchangeCypher(String encrypted,String pass);
	public String sensorReadings(String temp,String battery,String smoke,String co2,String token);
	public String getAuthChallangeToken(String response)throws ResponseException;
	public String authChallangeReply(String alarmId,String authNext) throws Exception ;
	public String getSessionToken(String response);
	
}
