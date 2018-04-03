package fireAlarms;

import org.apache.commons.codec.binary.Base64;

public class RequestParser implements RequestParserInterface {
	private int responseOk = 20;
	private int authOk = 21;
	private int readingRequest = 30;
	private int alarmExists = 40;
	private int invalidResponse = -1;

	@Override
	public String auhtInit(String alarmId) {

		return Base64.encodeBase64String(("{\"id\":" + alarmId + ",\"requestType\":\"authInit\"}").getBytes());
	}

	@Override
	public int Response(String response) {

		// return the response code in the response message send by the server
		// if their is an error in the response message this will return -1 as an error
		// code
		try {
			String[] code = response.replaceAll("\\{", "").replaceAll("\\}", "").split(",");
			//check if the response contains a value of header in its first value pair
			
			if (code[0].split(":")[0].equals("\"header\"")) {
				//extract the response code and return it
				return Integer.parseInt(code[0].split(":")[1].replaceAll("\"", ""));
			}
			//when a header section not found return -1 as an error code
			return -1;
		} catch (Exception e) {
			//return error code -1 if error occur while parsing the response
			return -1;
		}

	}

	@Override
	public String exchangeCypher(String encrypted) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sensorReadings(String temp, String battery, String smoke, String co2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAuthToken(String response) throws ResponseException {
		// this will extract the token send by the server to complete the authentication
		try {
			
			if (Response(response) == this.responseOk) {
				String[] items = response.replaceAll("\\{", "").replaceAll("\\}", "").split(",");
				for (String code : items) {
					String[] item = code.split(":");
					// check if it contains authToken header and its value is base64 if so return token
				
					if (item[0].contains("authToken") & Base64.isBase64(item[1].replaceAll("\"", ""))) {
						return item[1].replaceAll("\"", "");
					}
				}
				//if no tokens found throw a custom exception
				throw new  ResponseException("No Auth Tokens Found");
			}
			
			return "";
		} catch (Exception e) {
			
		}
		//if error occur parsing throw an exception
		throw new  ResponseException("Error Parsing Response");
		
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
