package org.makson.tasktrackerscheduler.services;

import lombok.RequiredArgsConstructor;
import org.makson.tasktrackerscheduler.exceptions.TemplateRetrievalException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportTemplateService {
    private final ResourceLoader resourceLoader;

    public String getUnCompletedTaskTemplate() {
        return get("uncompleted-tasks");
    }

    public String getCompletedTaskTemplate() {
        return get("completed-tasks");
    }

    public String getAllTaskTemplate() {
        return get("all-tasks");
    }

    public String fillTemplate(String template, Map<TemplatePlaceholder, String> arg) {
        String filledTemplate = template;

        for (var entrySet : arg.entrySet()) {
            filledTemplate = filledTemplate.replace(entrySet.getKey().getPlaceholder(), entrySet.getValue());
        }

        return filledTemplate;
    }

    private String get(String templateName) {
        String path = "classpath:/templates/report/";
        String fileFormat = ".txt";

        Resource template = resourceLoader.getResource(path + templateName + fileFormat);

        try {
            return template.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new TemplateRetrievalException(e);
        }
    }
}
