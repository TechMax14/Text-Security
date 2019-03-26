import java.util.Random;

public class secretKey {
	public char getKey() {
		Random r = new Random();
		char rc = (char) (48 + r.nextInt(47));
		return rc;
	}
}
