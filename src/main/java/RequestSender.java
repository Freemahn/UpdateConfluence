import com.google.gson.Gson;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 * @author Pavel Gordon
 */
public class RequestSender
{

    public static String createBody(List<String> permissions)
    {
        Collection collection = new ArrayList();
        collection.add(permissions);
        collection.add("confluence-users");
        collection.add("SPMJ");

        return new Gson().toJson(collection);

        //make request
    }


    public static String addPermissions(String urlText, String username, String password, List<String> permissions)
    {

        //            conn.setRequestProperty("Authorization", "Basic " + Base64.encode((username + ":" + password).getBytes()));
        //            conn.setRequestProperty("Content-Type", "application/json");
        //            conn.getOutputStream().write(createBody(permissions).getBytes());

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(urlText);

        // Create some NameValuePair for HttpPost parameters

        String payload = createBody(permissions);
        StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);
        post.setEntity(entity);
        post.setHeader("Authorization", "Basic " + Base64.encode((username + ":" + password).getBytes()));
        post.setHeader("Content-Type", "application/json");
        try
        {
            HttpResponse response = client.execute(post);

            // Print out the response message

            String result = "Request status " + response.getStatusLine() + "\n body" + EntityUtils.toString(response.getEntity());

            return result;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "ERROR";
        }

    }

}
