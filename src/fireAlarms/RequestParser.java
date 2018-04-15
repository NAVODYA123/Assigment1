package fireAlarms;

import java.io.Console;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.*;

public class RequestParser implements RequestParserInterface {
	private int responseOk = 20;
	private int authOk = 21;
	
	private int readingRequest = 30;
	private int alarmExists = 40;
	private int invalidResponse = -1;

	@Override
	public String auhtInit(String alarmId) {
		JSONObject json=new JSONObject();
		json.put("type", "authInit");
		json.put("id", alarmId);
		System.out.println(json.toJSONString());
		return Base64.encodeBase64String((json.toJSONString().getBytes()));
	}

	@Override
	public String getResponseType(String response) {
		
		
		try {
			JSONObject json=(JSONObject)JSONValue.parse(new String(Base64.decodeBase64(response)));
			if (Response(response) == this.responseOk) {
				
				if(json.containsKey("type") ) {
					//return the token by decoding base64 string
					return json.get("type").toString();
				}
				
				return "-1";
				
			}
			
		} catch (Exception e) {
			
		}
		
		return "-1";
	}

	
	@Override
	public int Response(String response) {
		
		// return the response code in the response message send by the server
		// if their is an error in the response message this will return -1 as an error
		// code
		try {
			//create a JSON object from the response parameter by decoding it and parsing
			JSONObject json=(JSONObject)JSONValue.parse(new String(Base64.decodeBase64(response)));
			
			if (json.containsKey("header")) {
				//extract the response code and return it
				return Integer.parseInt(json.get("header").toString());
			} 
			//when a header section not found return -1 as an error code
			return -1;
		} catch (Exception e) {
			//return error code -1 if error occur while parsing the response
			return -1;
		}

	}

	@Override
	public String exchangeCypher(String encrypted,String pass) {
		try {
			Auth auth=new Auth();
			String decrypt= auth.decrypt(pass, encrypted);
			int authNext=Integer.parseInt(decrypt)+1;
			return String.valueOf(auth.encrypt(pass,String.valueOf(authNext)));
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error in server auth reply...\n");
			return "-1";
		}
		
	
	}
	

	@Override
	public String sensorReadingMessage(String temp,String battery,String smoke,String co2,String token,String alarmId) {
		JSONObject json=new JSONObject();
		json.put("id", alarmId);
		json.put("type", "sensorReading");
		json.put("token", token);
		json.put("temp", temp);
		json.put("battery", battery);
		json.put("smoke", smoke);
		json.put("co2", co2);
		
		
		//System.out.println(json.toJSONString());
		return Base64.encodeBase64String((json.toJSONString().getBytes()));
	}
	
	@Override
	public String sensorReadingsMessage(JSONArray readings,String token,String alarmId) {
		JSONObject json=new JSONObject();
		
		json.put("id", alarmId);
		json.put("type", "sensorReading[]");
		json.put("token", token);
		json.put("readings", readings);
		
		//System.out.println(json.toJSONString());
		return Base64.encodeBase64String((json.toJSONString().getBytes()));
	}

	@Override
	public JSONObject sensorReadings(String temp, String battery, String smoke, String co2) {
		JSONObject json=new JSONObject();
		
		json.put("temp", temp);
		json.put("battery", battery);
		json.put("smoke", smoke);
		json.put("co2", co2);
		json.put("battery", battery);
		
		//System.out.println(json.toJSONString());
		return json;
	}
	@Override
	public String getAuthChallangeToken(String response) throws ResponseException {
		// this will extract the token send by the server to complete the authentication
		try {
			//create a json object from the response parameter
			JSONObject json=(JSONObject)JSONValue.parse(new String(Base64.decodeBase64(response)));
			if (Response(response) == this.responseOk) {
				//check if there is token field in the json and it is a base64 encoded one
				if(json.containsKey("authToken")) {
					//return the token by decoding base64 string
					return new String (json.get("authToken").toString());
				}
				
				//if no tokens found throw a custom exception
				throw new  ResponseException("No Auth Tokens Found");
			}
			
			
		} catch (Exception e) {
			throw new  ResponseException("Error Parsing Response");
		}
		//if error occur parsing throw an exception
		throw new  ResponseException("Error Parsing Response");
		
	}
	
	@Override
	public String authChallangeReply(String alarmId,String authNext) throws Exception{
		JSONObject json=new JSONObject();
		json.put("header",ResponseOk());
		json.put("id", alarmId);
		json.put("type", "authReply");
		json.put("authRepToken",authNext);
		
		
		return Base64.encodeBase64String((json.toJSONString().getBytes()));
	}
	
	@Override
	public String getSessionToken(String response) {
		try {
			//create a json object from the response parameter
			JSONObject json=(JSONObject)JSONValue.parse(new String(Base64.decodeBase64(response)));
			if (Response(response) == this.responseOk) {
				//check if there is token field in the json
				if(json.containsKey("token")) {
					//return the token 
					return new String (json.get("token").toString());
				}
				
				
			}
			//if no tokens found throw -1
			return "-1";
			
		} catch (Exception e) {
			return "-1";
		}
		
	}
	
	

	/*
	 * Request response states getter methods
	 */

	/**
	 * @return the responseOk
	 */
	public int ResponseOk() {
		return responseOk;
	}

	/**
	 * @return the authOk
	 */
	public int AuthOk() {
		return authOk;
	}

	/**
	 * @return the readingRequest
	 */
	public int ReadingRequest() {
		return readingRequest;
	}

	/**
	 * @return the alarmExists
	 */
	public int AlarmExists() {
		return alarmExists;
	}

	/**
	 * @return the invalidResponse
	 */
	public int InvalidResponse() {
		return invalidResponse;
	}



	


	
	

	

}
