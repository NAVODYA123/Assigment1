package fireAlarms;

import java.util.Random;

public class Randoms {
	Random random;
	
	public Randoms() {
		random = new Random();
	}
	
	public int getRandom(int min,int max) {
		
		return random.nextInt(max-min) + min;
		
	}
	
	public double getRandomDouble(int min,int max,int precision) {
		
		return ((random.nextInt(max-min) + min)/(10.0*precision));
		
	}
	
	public double getRandomDouble(int min,int max) {
		
		return ((random.nextInt(max-min) + min)/(10.0));
		
	}
}
