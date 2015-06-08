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
					new Client("/dropbox/");
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();

		try {
			Thread.sleep(3000);
		}
		catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		try {
			new Client("/dropbox1/");
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}
}