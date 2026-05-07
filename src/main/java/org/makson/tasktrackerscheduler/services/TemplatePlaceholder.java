package org.makson.tasktrackerscheduler.services;

import lombok.Getter;

@Getter
public enum TemplatePlaceholder {
    NUMBER_UNCOMPLETED_TASK("{number_uncompleted_task}"),
    NUMBER_COMPLETED_TASK("{number_completed_task}"),
    LIST_COMPLETED_TASK("{list_completed_task}"),
    LIST_UNCOMPLETED_TASK("{list_uncompleted_task}");

    private final String placeholder;

    TemplatePlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }
}
