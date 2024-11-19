package tc.tlouro_c;

import java.util.Properties;

import tc.tlouro_c.item.Item;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

    private static final Properties config = Config.getConfig();

    public static void sendEmail(String body) throws Exception {

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        final String email = config.getProperty("sender.email");
        final String password = config.getProperty("sender.password");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(email));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(config.getProperty("receiver.email")));
        message.setSubject(config.getProperty("email.subject"));
        message.setText(body);

        message.setContent(body, "text/html");

        Transport.send(message);

    }

    public static String formatItemAsHtml(Item item) {
        return "<html>" +
                "<head>" +
                "<style>" +
                "body { background-color: #121212; color: #e0e0e0; font-family: Arial, sans-serif; }" +
                "table { border-collapse: collapse; width: 100%; margin: 20px 0; font-size: 18px; text-align: left; border: 1px solid #333; }" +
                "th, td { padding: 12px; border: 1px solid #444; }" +
                "th { background-color: #333; color: #fff; }" +
                "tr:nth-child(even) { background-color: #2c2c2c; }" +
                "tr:nth-child(odd) { background-color: #1f1f1f; }" +
                "a { color: #bb86fc; text-decoration: none; }" +
                "a:hover { text-decoration: underline; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h2>Item Details</h2>" +
                "<table>" +
                String.format("<tr><th>ID</th><td>%d</td></tr>", item.getId()) +
                String.format("<tr><th>Title</th><td>%s</td></tr>", item.getTitle()) +
                String.format("<tr><th>Price</th><td>%s</td></tr>", item.getPrice().getAmount()) +
                String.format("<tr><th>Brand</th><td>%s</td></tr>", item.getBrandTitle()) +
                String.format("<tr><th>Size</th><td>%s</td></tr>", item.getSizeTitle()) +
                String.format("<tr><th>URL</th><td><a href=\"%s\">View Item</a></td></tr>", item.getUrl()) +
                String.format("<tr><th>Seller</th><td>%s</td></tr>", item.getUser().getLogin()) +
                "</table>" +
                "</body>" +
                "</html>";
    }
}
