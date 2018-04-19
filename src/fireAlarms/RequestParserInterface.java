package fireAlarms;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public interface RequestParserInterface {
	public String auhtInit(String alarmId);
	public String getResponseType(String response);
	public int Response(String response);
	public String exchangeCypher(String encrypted,String pass);
	public String sensorReadingMessage(String temp,String battery,String smoke,String co2,String token,String alarmId,String requestId);
	public JSONObject sensorReadings(String temp,String battery,String smoke,String co2);
	public String sensorReadingsMessage(JSONArray readings,String token,String alarmId);
	public String getAuthChallangeToken(String response)throws ResponseException;
	public String authChallangeReply(String alarmId,String authNext) throws Exception ;
	public String getSessionToken(String response);
	public String getClientId(String response);
	public String alertMessage(String msg,String alarmId,String token);
}
