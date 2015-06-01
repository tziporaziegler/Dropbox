package dropbox;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {

		Thread thread1 = new Thread() {
			@Override
			public void run() {
				try {
					new Server();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		thread1.start();

		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					new Client();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}
}