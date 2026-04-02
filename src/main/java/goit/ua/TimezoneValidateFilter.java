package goit.ua;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.TimeZone;

@WebFilter("/time")
public class TimezoneValidateFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        String timezoneParam = req.getParameter("timezone");

        if (timezoneParam == null || timezoneParam.isEmpty()) {
            chain.doFilter(req, resp);
            return;
        }

        String processed = timezoneParam.replace(" ", "+");

        String checkId = processed;
        if (processed.startsWith("UTC")) {
            checkId = processed.replace("UTC", "GMT");
        }

        TimeZone tz = TimeZone.getTimeZone(checkId);

        boolean isValid = true;
        if (tz.getID().equals("GMT")) {
            if (!(processed.equalsIgnoreCase("GMT") || processed.equalsIgnoreCase("UTC"))) {
                isValid = false;
            }
        }

        if (isValid) {
            req.setAttribute("validatedTimezone", processed);
            chain.doFilter(req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("text/html; charset=utf-8");
            resp.getWriter().write("<html><body><h1>Invalid timezone</h1></body></html>");
        }
    }
}