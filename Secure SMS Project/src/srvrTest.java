import javax.swing.JFrame;

public class srvrTest {
	public static void main(String[] args) throws InterruptedException {
		Server srvr = new Server();
		srvr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		srvr.startRunning();
	}
}
