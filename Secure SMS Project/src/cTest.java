import javax.swing.JFrame;

public class cTest {
	public static void main(String[] args) throws InterruptedException {
		Client c;
		c = new Client("127.0.0.1");
		c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		c.startRunning();
	}
}  
