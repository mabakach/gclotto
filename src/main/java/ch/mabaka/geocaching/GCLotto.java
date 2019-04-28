package ch.mabaka.geocaching;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

/**
 * Hello world!
 *
 */
public class GCLotto 
{
    public GCLotto() {
    	
    }
    
    public void play() {
    	try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

            List<NameValuePair> form = new ArrayList<>();
            form.add(new BasicNameValuePair("time", "1556454731"));
            form.add(new BasicNameValuePair("action", "kukuk"));
            form.add(new BasicNameValuePair("n1", "3"));
            form.add(new BasicNameValuePair("n2", "17"));
            form.add(new BasicNameValuePair("n3", "21"));
            form.add(new BasicNameValuePair("n4", "24"));
            form.add(new BasicNameValuePair("n5", "36"));
            form.add(new BasicNameValuePair("n6", "43"));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);

            HttpPost httpPost = new HttpPost("https://www.navikatzen.com/lotto/index.php?page=lotto&time=1556454731&1556454731");
            httpPost.setEntity(entity);
            System.out.println("Executing request " + httpPost.getRequestLine());

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
            String responseBody = httpclient.execute(httpPost, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
        } catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	
	public static void main( String[] args )
    {
        System.out.println( "Starting GCLotto..." );
        
        GCLotto gclotto = new GCLotto();
        gclotto.play();

    }
}
