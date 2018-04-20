package fireAlarms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class FireAlarm extends Timer implements SensorInterface {
	/* Begin Variable Area */

	Randoms random = new Randoms();
	private double temp = 25;
	private double battery = 100.0;
	private int smokeLevel = 0;
	private int co2 = 300;
	private static String alarmId = "23-13";
	private RequestParser parser = new RequestParser();
	private String key = "IT16107274";
	private static String sessionToken;
	private static Socket socket;
	private static PrintWriter writer;
	private static BufferedReader reader;
	private static boolean authState = false;
	private static JSONArray readings = new JSONArray();
	/* End Variable Area */

	@Override
	public double getTemperature() {
		Randoms random = new Randoms();

		// random incident of fire
		// its only 8% possible to have a fire
		if (random.getRandomDouble(1, 100, 2) < 0.08) {
			// fire temperature
			temp = random.getRandom(100, 300);
			;
		}

		int rand = random.getRandom(-10, 10);
		
		if (rand > 0) {
			temp += random.getRandomDouble(20, 80);

		} else {
			temp -= random.getRandomDouble(20, 80);

		}

		if (temp < 0) {
			return -temp;
		}

		return temp;
	}

	@Override
	public double getBatteryLevel() {
		battery -= random.getRandomDouble(10, 30);
		if (battery < 0) {
			battery = 100;
			battery -= random.getRandomDouble(10, 30);
		}
		return battery;
	}

	@Override
	public int getSmokeLevel() {

		// random smoke levels with 20% probability for of high smoke levels
		if (random.getRandomDouble(1, 100, 2) < 0.2) {
			smokeLevel = random.getRandom(7, 10);
		} else {
			smokeLevel = random.getRandom(0, 6);
		}
		return smokeLevel;
	}

	@Override
	public int getCo2Level() {
		// random co2 levels with 20% probability for high co2 levels
		if (random.getRandomDouble(1, 100, 2) < 0.2) {
			co2 = random.getRandom(400, 500);
		} else {
			co2 = random.getRandom(280, 340);
		}
		return co2;

	}

	private boolean authenticate(PrintWriter out, BufferedReader in) {
		try {
			// Authentication request

			out.println(parser.auhtInit(alarmId));
			// waiting for response
			while (true) {
				String response = in.readLine();
				
				// check the server response to check whether the alarm it already exists in the
				// server
				if (!response.isEmpty() && parser.Response(response) == parser.AlarmExists()) {
					// if the alarm id already exists regenerate an new one and send it back
					System.out.println("Alarm id exists -sending a new one");
					//System.out.println(parser);
					alarmId = "23-" + String.valueOf(random.getRandom(1, 80));
					out.println(parser.auhtInit(alarmId));
					
				} else if (!response.isEmpty() && parser.getResponseType(response).equals("authToken")) {
					//check if the server response has "authToken" field
					try {
						//this generate the reply string by decrypting the authToken and adding it one and making the JSON request
						String reply = parser.authChallangeReply(alarmId, (Auth.encrypt(key, String.valueOf(
								(Integer.parseInt(Auth.decrypt(key, parser.getAuthChallangeToken(response))) + 1)))));
						//send the message to the server
						out.println(reply);
						// break;
					} catch (Exception e) {
					
					}

					
				} else if (!response.isEmpty() && parser.getResponseType(response).equals("authOk")) {
					// here it check if the server accepted the response with authToken and granted permission
					//if so the get the token send by the server
					//and store it in relevant alarm
					sessionToken = parser.getSessionToken(response);
					return true;

				} else if (!response.isEmpty() && parser.getResponseType(response).equals("authFail")) {
					//this is when server declined authorization
					System.out.println("auth Failed");
					System.out.println("Program Exist");
					return false;
				}
			}

		} catch (Exception e) {
			//System.out.println(e);
			return false;

		}

	}

	private void connectToServer() {

		// Make connection

		try {
			//Make a socket connection on port 3001
			socket = new Socket("localhost", 3001);
			writer = new PrintWriter(socket.getOutputStream(), true);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			System.out.println("Connected");
			//check if the authentication is successfull
			//if not exit the by closing the socket
			if (!authenticate(writer, reader)) {
				socket.close();
			}

			authState = true;
			System.out.println("\nAuthentication Successful");
			// return true;

			System.out.println("\nListening to messages");

			//after auth listen for server request messages
			while (true) {
				String response = reader.readLine();
				
				if (!response.isEmpty() && parser.getResponseType(response).equals("sensorReading")) {
					//if server request a sensor reading
					//send back the JSON message to the server with readings
					writer.println(parser.sensorReadingMessage(String.valueOf(getTemperature()),
							String.valueOf(getBatteryLevel()), String.valueOf(getSmokeLevel()),
							String.valueOf(getCo2Level()), sessionToken, alarmId, parser.getClientId(response)));

				}
			}

		} catch (IOException e) {
			
		}
	}

	public static void main(String[] args) throws Exception {

		FireAlarm client = new FireAlarm();

		System.out.println("Initiating Socket Thread...");
		//this runnable seperate the socket connetion from main thread
		Runnable runnable = new Runnable() {
			@Override
			public void run() {

				try {
					client.connectToServer();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};

		System.out.println("Creating Socket Thread...");
		Thread thread = new Thread(runnable);

		System.out.println("Starting Thread...");
		thread.start();
		System.out.println("Socket connetions running seperatley...");

		
		//this  runnable handle the sensor reading at interval of 5 mins and this will repeatedly respawn every 5 mins
		Runnable readerRunnable = new Runnable() {

			@Override
			public void run() {

				RequestParser parser = new RequestParser();

				// synchronized (readings) {

				System.out.println("Reading sensors " + authState);

				if (authState == true) {
					synchronized (readings) {
						try {
							double temp=client.getTemperature();
							int smoke=client.getSmokeLevel();
							JSONObject values = parser.sensorReadings(String.valueOf(temp),
									String.valueOf(client.getBatteryLevel()), String.valueOf(smoke),
									String.valueOf(client.getCo2Level()));

							System.out.println(values.toJSONString());
							//add the readings to the array for storing until they are send
							readings.add(values);
							
							//check for sensor anomalies .if there are any send message to the server
							if(temp>50) {
								writer.println(parser.alertMessage(alarmId+": High Temperature levels detected: "+temp+" c", alarmId, sessionToken));
							}
							if(smoke>7){
								writer.println(parser.alertMessage(alarmId+": High Smoke levels detected: "+smoke, alarmId, sessionToken));
							}
							
						} catch (Exception e) {
							
						}
					}
				}

				

			}
		};
		
		
		//this runnable runs every 60 minutes.this is responsible for sending the readings of the hour
		Runnable senderRunnable = new Runnable() {

			@Override
			public void run() {
				
				RequestParser parser = new RequestParser();
				// send only if the sensor is authenticated
				if (authState == true) {
					synchronized (readings) {
						System.out.println("sending Reading sensors");
						writer.println(parser.sensorReadingsMessage(readings, sessionToken, alarmId));
						readings.clear();
					}
				}

			}
		};

		
		Calendar rightNow = Calendar.getInstance();
		ScheduledExecutorService readerService = Executors.newSingleThreadScheduledExecutor();
		//Schedule the runnable to run every 5 mins
		readerService.scheduleWithFixedDelay(readerRunnable, 5 - (rightNow.get(Calendar.MINUTE) % 10), 5, TimeUnit.MINUTES);

		ScheduledExecutorService senderService = Executors.newScheduledThreadPool(2);
		//Schedule the runnable to run every 61 mins
		senderService.scheduleWithFixedDelay(senderRunnable, 61- rightNow.get(Calendar.MINUTE) , 61,TimeUnit.MINUTES);
		System.out.println("\nShedule Services running\n");
	}

}
