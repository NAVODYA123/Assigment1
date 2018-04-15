package fireAlarms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
			temp = random.getRandom(51, 300);
			;
		}

		int rand = random.getRandom(-10, 10);
		// System.out.println("rand : " + rand);
		if (rand < 0) {
			temp += random.getRandomDouble(20, 80);

		} else {
			temp -= random.getRandomDouble(20, 80);

		}

		return temp;
	}

	@Override
	public double getBatteryLevel() {
		return (battery -= random.getRandomDouble(10, 30));
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

	private void readingWriteToFile() {

	}

	private boolean authenticate(PrintWriter out, BufferedReader in) {
		try {
			// Authentication request

			out.println(parser.auhtInit(alarmId));
			// waiting for response
			while (true) {
				String response = in.readLine();
				System.out.println(response);
				System.out.println(parser.getResponseType(response));
				// check the server response to check whether the alarm it already exists in the
				// server
				if (!response.isEmpty() && parser.Response(response) == parser.AlarmExists()) {
					// if the alarm id already exists regenerate an new one and send it back
					System.out.println("Alarm id exists -sending a new one");

					alarmId = "23-" + String.valueOf(random.getRandom(1, 80));
					out.println(parser.auhtInit(alarmId));
				} else if (!response.isEmpty() && parser.getResponseType(response).equals("authToken")) {
					try {
						System.out.println(Auth.decrypt(key, parser.getAuthChallangeToken(response)));
						String reply = parser.authChallangeReply(alarmId, (Auth.encrypt(key, String.valueOf(
								(Integer.parseInt(Auth.decrypt(key, parser.getAuthChallangeToken(response))) + 1)))));
						System.out.println(reply);
						out.println(reply);
						// break;
					} catch (Exception e) {
						// TODO: handle exception
					}

					// System.out.println(cmd);
				} else if (!response.isEmpty() && parser.getResponseType(response).equals("authOk")) {
					// System.out.println("Here");
					sessionToken = parser.getSessionToken(response);
					return true;

				} else if (!response.isEmpty() && parser.getResponseType(response).equals("authFail")) {
					System.out.println("auth Failed");
					System.out.println("Program Exist");
					return false;
				}
			}

		} catch (Exception e) {
			System.out.println(e);
			return false;

		}

	}

	private void connectToServer() {

		// Make connection

		try {
			socket = new Socket("localhost", 3001);
			writer = new PrintWriter(socket.getOutputStream(), true);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			System.out.println("Connected");

			if (!authenticate(writer, reader)) {
				socket.close();

			}

			authState = true;
			System.out.println("\nAuthentication Successful");
			// return true;

			System.out.println("\nListening to messages");

			while (true) {
				String response = reader.readLine();
				System.out.println(response);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

	public static void main(String[] args) throws Exception {

		FireAlarm client = new FireAlarm();

		System.out.println("Initiating Socket Thread...");
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

		Runnable readerRunnable = new Runnable() {

			@Override
			public void run() {

				RequestParser parser = new RequestParser();

				// synchronized (readings) {

				System.out.println("Reading sensors " + authState);

				if (authState == true) {
					synchronized (readings) {
						try {
							JSONObject values = parser.sensorReadings(String.valueOf(client.getTemperature()),
									String.valueOf(client.getBatteryLevel()), String.valueOf(client.getSmokeLevel()),
									String.valueOf(client.getCo2Level()));

							System.out.println(values.toJSONString());

							readings.add(values);
						} catch (Exception e) {
							System.out.println(e);
							// TODO: handle exception
						}
					}
				}

				// }

			}
		};

		Runnable senderRunnable = new Runnable() {

			@Override
			public void run() {
				// System.out.println("sending Reading sensors " +authState);
				RequestParser parser = new RequestParser();

				if (authState == true) {
					synchronized (readings) {
						System.out.println("sending Reading sensors");
						writer.println(parser.sensorReadingsMessage(readings, sessionToken, alarmId));
						readings.clear();
					}
				}

			}
		};

		System.out.println("\nServices\n");
		ScheduledExecutorService readerService = Executors.newSingleThreadScheduledExecutor();
		readerService.scheduleWithFixedDelay(readerRunnable, 0, 10, TimeUnit.SECONDS);

		ScheduledExecutorService senderService = Executors.newScheduledThreadPool(2);
		senderService.scheduleWithFixedDelay(senderRunnable, 60, 60, TimeUnit.SECONDS);

	}

}
