package ch.mabaka.geocaching;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.core.net.SyslogOutputStream;

/**
 * Automatic player for Geocaching lotto on
 * https://www.navikatzen.com/lotto/index.php?page=lotto
 *
 */
public class GCLotto {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GCLotto.class);
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public Map<Integer, String> solutions = new ConcurrentHashMap<Integer, String>();
	
	public GCLotto() {

	}

	public void play() {
		String time = "1556454731";
		while (true) {
			String[] numbers = { "3", "7", "21", "28", "37", "40" };
			String responseBody = null;
			responseBody = callServer(time, numbers, responseBody);
			if (responseBody != null) {
				time = findTime(responseBody);
				String treffer = findTreffer(responseBody).trim();
				if (treffer != null && Integer.parseInt(treffer) > 2) {
					String url = "https://www.navikatzen.com/lotto/winner.php?passwort=" + findPassword(responseBody);
					LOGGER.info(DATE_FORMAT.format(new Date()) + " " + treffer + " Treffer! " + url);
					solutions.put(Integer.parseInt(treffer), url);
					synchronized (this) {
						System.out.print("\033[H\033[2J");
						System.out.flush();
						System.out.println(DATE_FORMAT.format(new Date()));
						solutions.keySet().forEach((i) -> System.out.println(i + " Treffer " + solutions.get(i)));
						
					}
				} else {
					// System.out.println(treffer + " Treffer! ");
				}
			}
			if (solutions.containsKey(3) && solutions.containsKey(4) && solutions.containsKey(5) && solutions.containsKey(6)) {
				break;
			}

		}

	}

	private String callServer(String time, String[] numbers, String responseBody) {
		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

			List<NameValuePair> form = new ArrayList<>();
			form.add(new BasicNameValuePair("time", time));
			form.add(new BasicNameValuePair("action", "kukuk"));
			for (int i = 0; i < numbers.length; i++) {
				form.add(new BasicNameValuePair("n" + i, numbers[i]));
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);

			HttpPost httpPost = new HttpPost(
					"https://www.navikatzen.com/lotto/index.php?page=lotto&time=" + time + "&" + time);
			httpPost.setEntity(entity);
			// System.out.println("Executing request " + httpPost.getRequestLine());

			// Create a custom response handler
			ResponseHandler<String> responseHandler = response -> {
				int status = response.getStatusLine().getStatusCode();
				if (status >= 200 && status < 300) {
					HttpEntity responseEntity = response.getEntity();
					return responseEntity != null ? EntityUtils.toString(responseEntity) : null;
				} else {
					throw new ClientProtocolException("Unexpected response status: " + status);
				}
			};
			responseBody = httpclient.execute(httpPost, responseHandler);
		} catch (ClientProtocolException e) {
			LOGGER.error(e.getMessage());
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
		return responseBody;
	}

	private String findTime(String responseBody) {
		String searchString = "<INPUT TYPE=hidden NAME=time VALUE=\"";
		String terminalString = "\"";
		return extractValue(responseBody, searchString, terminalString);
	}

	private String findTreffer(String responseBody) {
		String searchString = "<B>Du hast";
		String terminalString = "Treffer gelandet!</B>";
		return extractValue(responseBody, searchString, terminalString);
	}

	private String findPassword(String responseBody) {
		String searchString = "<script type=\"text/javascript\">popup(\'";
		String terminalString = "');";
		return extractValue(responseBody, searchString, terminalString);
	}

	private String extractValue(String responseBody, String searchString, String terminalString) {
		String extractedString = null;
		int start = responseBody.indexOf(searchString);
		int end = responseBody.indexOf(terminalString, start + searchString.length());
		if (start > 0 && end > 0) {
			extractedString = responseBody.substring(start + searchString.length(), end);
		}
		return extractedString;
	}

	public static void main(String[] args) {
		System.out.println("Starting GCLotto...");
		System.out.println("Only hits will be printed out. This can take a while!");
		GCLotto gclotto = new GCLotto();

		ExecutorService executor = Executors.newFixedThreadPool(4);
		for (int i = 0; i < 4; i++) {
			executor.submit(() -> gclotto.play());
		}

		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// ignore
			}
			if (executor.isTerminated()) {
				executor.shutdown();
				System.exit(0);
			}
		}
	}
}
