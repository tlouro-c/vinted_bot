package tc.tlouro_c;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import tc.tlouro_c.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class App {

    private String accessToken;
    private Item lastItem;
    private final Properties config = Config.getConfig();

    private void run() {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate( () -> {

            var cookieStore = new BasicCookieStore();

            try (var httpClient = HttpClients.custom()
                    .setDefaultCookieStore(cookieStore)
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setCookieSpec(CookieSpecs.STANDARD)
                            .build())
                    .build()) {

                var request = new HttpGet("https://www.vinted.pt/");
                try (var ignored = httpClient.execute(request)) {

                    List<Cookie> cookies = cookieStore.getCookies();
                    for (var cookie : cookies) {
                        if (cookie.getName().equals("access_token_web")) {
                            accessToken = cookie.getValue();
                            break;
                        }
                    }
                }

                request = new HttpGet(config.getProperty("vinted.api.url"));
                request.setHeader("Authorization", "Bearer " + accessToken);

                try (var response = httpClient.execute(request)) {

                    String jsonResponse = EntityUtils.toString(response.getEntity());
                    var objectMapper = new ObjectMapper();
                    RootWrapper root = objectMapper.readValue(jsonResponse, RootWrapper.class);

                    var items = root.getItems();
                    if (lastItem == null || (!lastItem.equals(items.get(0)))) {
                        lastItem = items.get(0);
                        EmailSender.sendEmail(EmailSender.formatItemAsHtml(lastItem));
                    }
                }

            } catch (Exception e) {
                System.err.println(e.getMessage());
                scheduler.shutdownNow();
            }
        }, 0, Integer.parseInt(config.getProperty("interval")), TimeUnit.MINUTES);
    }

    public static void main(String[] args) { new App().run(); }
}
