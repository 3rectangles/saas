package com.barraiser.communication.automation.template;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
@RequiredArgsConstructor
public class TemplatePopulator {
    private final TemplateLoader loader = new ClassPathTemplateLoader();

    // TODO: what to do if it fails?
    public String populateTemplate(final String templateString, final Object queryData) {
        try {
            final Handlebars handlebars = new Handlebars();
            handlebars.registerHelpers(new HandlebarsHelperSource());
            return handlebars.compileInline(templateString).apply(queryData);
        }
        catch (final IOException e) {
            log.error(e, e);
            return null;
        }
    }

    public String getTemplateStringFromBaseTemplate(final String templateString, final String baseTemplate) {
        try {
            final Handlebars handlebars = new Handlebars(this.loader);
            final String baseTemplateString = handlebars.compile(baseTemplate).text();
            return templateString + baseTemplateString;
        }
        catch (final IOException e) {
            log.error(e, e);
            return null;
        }
    }
}
