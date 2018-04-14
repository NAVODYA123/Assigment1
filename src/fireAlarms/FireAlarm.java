package fireAlarms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;

public class FireAlarm implements SensorInterface {
	/* Begin Variable Area */

	Randoms random = new Randoms();
	private double temp = 25;
	private double battery = 100.0;
	private int smokeLevel = 0;
	private int co2 = 300;
	private String alarmID="23-13";
	private RequestParser parser =new RequestParser();
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
		System.out.println("rand : " + rand);
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

	private void connectToServer() {

		// Make connection

		Socket socket;
		try {
			socket = new Socket("localhost", 3001);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			System.out.println("Connected");
			
			//Authentication request
		
			out.println();

			while (true) {
				String cmd = in.readLine();

				if (!cmd.isEmpty()) {
					System.out.println(cmd);
				}

			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws Exception {

		FireAlarm client = new FireAlarm();
		// client. SendReadings();

		 for (int x = 0; x < 15; x++) {
			System.out.println( client.getCo2Level());
		// System.out.println(client.getTemperature());
		}
		Auth auth = new Auth();
		
		RequestParser parser=new RequestParser();
		parser.auhtInit("22-78");
		//System.out.println(parser.getAuthToken("\"header\":\"20\",\"authToken\":\"NDQ0NDQ0NDQ0\""));

	}

}
