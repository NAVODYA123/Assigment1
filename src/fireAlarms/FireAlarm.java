package fireAlarms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class FireAlarm implements SensorInterface {
	Randoms random = new Randoms();
	double temp = 25;
	double battery=100.0;
	@Override
	public double getTemperature() {
		Randoms random = new Randoms();

		// random incident of fire
		// its only 8% possible to have a fire
		if (random.getRandomDouble(1, 100, 2) < 0.08) {
			temp = 300;
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
		return (battery-=random.getRandomDouble(10, 30));
	}

	@Override
	public int getSmokeLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCo2Level() {
		// TODO Auto-generated method stub
		return 0;
	}

	private void SendReadings() throws IOException {

		// Make connection

		Socket socket = new Socket("localhost", 3001);
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		System.out.println("Connected");

		while (true) {
			String cmd = in.readLine();

			if (!cmd.isEmpty()) {
				System.out.println(cmd);
			}

		}

	}

	public static void main(String[] args) throws Exception {

		FireAlarm client = new FireAlarm();
		// client. SendReadings();

		for (int x = 0; x < 15; x++) {

			System.out.println(client.getTemperature());
		}

	}

}
