package project.parse.mavenlogback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App {
	private static final Logger log = LoggerFactory.getLogger(App.class);
	private static int S;
	private static int a = 0;
	private static int t = 0;
	private String name;
	static LocalTime time0 = LocalTime.now();
	public static final String url = "http://news.admicro.vn:10002/api/realtime?domain=kenh14.vn";

	public App(String name) {
		this.name = name;
	}

	public static JSONObject readJsonFromURL(String url) throws MalformedURLException, IOException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("utf-8")));
			String jsontxt = br.readLine();
			JSONObject json = new JSONObject(jsontxt);
			return json;
		} finally {
			is.close();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		Runnable runnable1 = new Runnable() {

			@Override
			public void run() {
				JSONObject json;
				try {
					// System.out.println(thread.get);
					json = readJsonFromURL(url);
					JSONArray js = json.optJSONArray("source");
					S = 0;
					for (int i = 0; i < js.length(); i++) {
						JSONObject li = js.optJSONObject(i);
						// System.out.println(li.getInt("number"));
						S = S + li.getInt("number");
					}
					System.out.println("Thread 1: S = " + S);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Runnable runnable2 = new Runnable() {

			@Override
			public void run() {
				JSONObject json;
				try {
					json = readJsonFromURL(url);
					JSONArray js = json.optJSONArray("source");
					S = 0;
					for (int i = 0; i < js.length(); i++) {
						JSONObject li = js.optJSONObject(i);
						S = S + li.getInt("number");
					}
					LocalTime time1 = LocalTime.now();
					if ((float) a / S == 0) {
						log.info("Thread 2: S(t) = " + S + " ,S(t-1)= " + a + "  Giây thứ:"
								+ time0.until(time1, ChronoUnit.SECONDS));
					} else if ((float) ((S - a) / a) > 0.015) {
						log.info("Thread 2: S(t) = " + S + " ,S(t-1)= " + a + "  Giây thứ:"
								+ time0.until(time1, ChronoUnit.SECONDS));
						t++;
					} else {
						t++;
						if (t % 6 == 0) {
							log.debug("Thread 2: S(t) = " + S + " ,S(t-1)= " + a + "  Giây thứ:"
									+ time0.until(time1, ChronoUnit.SECONDS));
						}
					}
					a = S;
					Thread.sleep(1500);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
		ses.scheduleAtFixedRate(runnable1, 0, 1, TimeUnit.SECONDS);
		ses.scheduleAtFixedRate(runnable2, 0, 2, TimeUnit.SECONDS);
		TimeUnit.SECONDS.sleep(30);
		ses.shutdown();

	}

}
