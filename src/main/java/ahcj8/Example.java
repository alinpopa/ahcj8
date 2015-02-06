package ahcj8;

import ahcj8.client.AhcCompletableClient;
import ahcj8.client.CompletableClient;
import com.ning.http.client.Response;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class Example {
    public static void main(String[] args) throws Exception{
        final CompletableClient client = new AhcCompletableClient("http://0.0.0.0:9991");
        final Function<Response, String> responseParser = response -> {
            try {
                return response.getResponseBody();
            } catch (Exception e) {
                return e.getMessage();
            }
        };
        final CompletableFuture<String> key = client.get("/", responseParser);
        final CompletableFuture<String> setValue = key.thenCompose(k ->
            client.put("/kv/" + k, "SOME DATA", r -> k)
        );
        final CompletableFuture<String> value = setValue.thenCompose(k ->
           client.get("/kv/" + k, responseParser)
        );
        System.out.println(value.get());
        client.close();
    }
}
