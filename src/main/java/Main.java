import static spark.Spark.*;
import spark.template.velocity.VelocityTemplateEngine;
import java.util.*;
import spark.ModelAndView;
public class Main {

    private static final int PORT = 8000;

    public static void main(String[] args) {

        port(PORT);
        get("/", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            return render(model, "index.html");
        });

    }

    public static String render(Map<String, Object> model, String templatePath) {
        return new VelocityTemplateEngine().render(new ModelAndView(model, templatePath));
    }
}
