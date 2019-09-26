package org.saeon.mims.accession.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class AppUtils {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    public static String getAuthTokenFromRequest(HttpServletRequest request) {
        if (request.getCookies() != null) {

            List<Cookie> cookies = Arrays.asList(request.getCookies());
            if (cookies != null && !cookies.isEmpty()) {
                Cookie c = cookies.stream().filter(cookie -> cookie.getName().equalsIgnoreCase("sanctuarytycoon")).findFirst().orElse(null);
                if (c != null) {
                    return c.getValue();
                }
            }
        }
        return null;
    }


    public static LocalDate convertStringToDate(String date) {
        return LocalDate.parse(date, formatter);
    }

    public static String convertDateToString(LocalDate date) {
        return date.format(formatter);
    }


    public static void sendMail(String email, String title, String body) {
//        log.info("Sending mail to {} about {}", email, title);
//        Email from = new Email(Constants.Email.FROM_EMAIL);
//        String subject = title;
//        Email to = new Email(email);
//        Content content = new Content("text/html", body);
//        Mail mail = new Mail(from, subject, to, content);
//
//        SendGrid sg = new SendGrid(Constants.Email.SENDGRID_KEY);
//        Request request = new Request();
//        try {
//            request.method = Method.POST;
//            request.endpoint = "mail/send";
//            request.body = mail.build();
//            Response response = sg.api(request);
//            log.info("Mail sent. Response code: {}", response.statusCode);
//
//        } catch (IOException ex) {
//            log.error("Error sending mail", ex);
//        }
    }

}
