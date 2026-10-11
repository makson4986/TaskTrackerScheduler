package org.makson.tasktrackerscheduler.dto;

import org.makson.tasktrackerscheduler.models.ReportStatus;

import java.time.LocalDate;

public record RawReportDto(String recipientEmail, LocalDate period, ReportStatus status) {
}
