package utils;

import spark.ModelAndView;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import java.util.Map;

public class ViewUtils {
    /**
     * provided in document: https://sparkjava.com/documentation#views-and-templates
     * @param model        parameters passed into template
     * @param templatePath name of the template
     * @return             rendered HTML text
     */
    public static String render(Map<String, Object> model, String templatePath) {
        return new ThymeleafTemplateEngine().render(new ModelAndView(model, templatePath));
    }
}
