package goit.ua;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {

    private TemplateEngine templateEngine;
    private JakartaServletWebApplication application;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        this.application = JakartaServletWebApplication.buildApplication(getServletContext());
        WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(application);

        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String time = (String) req.getAttribute("validatedTimezone");

        if (time == null) {
            time = req.getParameter("timezone");
        }

        ZonedDateTime zonedDateTime;
        String displayZone = "UTC";

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
                displayZone = processedTime;
            } catch (DateTimeException e) {
                zonedDateTime = ZonedDateTime.now(ZoneId.of("UTC"));
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = formatter.format(zonedDateTime) + " " + displayZone;

        IWebExchange exchange = this.application.buildExchange(req, resp);
        WebContext context = new WebContext(exchange, exchange.getLocale());

        context.setVariable("formattedTime", formattedTime);

        resp.setContentType("text/html; charset=utf-8");

        templateEngine.process("index", context, resp.getWriter());
    }
}
