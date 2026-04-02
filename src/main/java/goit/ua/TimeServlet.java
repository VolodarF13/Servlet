package goit.ua;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String time = (String) req.getAttribute("validatedTimezone");

        if (time == null) {
            time = req.getParameter("timezone");
        }

        ZonedDateTime zonedDateTime;

        if (time == null || time.isEmpty()) {
            zonedDateTime = ZonedDateTime.now(ZoneId.of("UTC"));
        } else {
            try {
                String processedTime = time.replace(" ", "+");

                if (processedTime.contains("+") || processedTime.contains("-")) {
                    processedTime = processedTime.replaceFirst("([+-])(\\d)$", "$10$2:00");
                    processedTime = processedTime.replaceFirst("([+-])(\\d):", "$10$2:");
                    if (!processedTime.contains(":")) processedTime += ":00";
                }

                zonedDateTime = ZonedDateTime.now(ZoneId.of(processedTime));
            } catch (DateTimeException e) {
                zonedDateTime = ZonedDateTime.now(ZoneId.of("UTC"));
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

        resp.setContentType("text/html; charset=utf-8");
        resp.getWriter().write("<html>" +
                "<body>" +
                "<h1>Поточний час</h1>" +
                "<p>" + formatter.format(zonedDateTime) + "</p>" +
                "</body>" +
                "</html>");
    }
}
