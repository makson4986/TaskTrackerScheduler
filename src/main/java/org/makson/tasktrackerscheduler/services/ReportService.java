package org.makson.tasktrackerscheduler.services;

import lombok.RequiredArgsConstructor;
import org.makson.tasktrackerscheduler.dto.RawReportDto;
import org.makson.tasktrackerscheduler.dto.ReportDto;
import org.makson.tasktrackerscheduler.exceptions.DatabaseException;
import org.makson.tasktrackerscheduler.mappers.RawReportMapper;
import org.makson.tasktrackerscheduler.mappers.ReportMapper;
import org.makson.tasktrackerscheduler.models.Report;
import org.makson.tasktrackerscheduler.models.ReportStatus;
import org.makson.tasktrackerscheduler.repositories.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;
    private final RawReportMapper rawReportMapper;

    @Transactional
    public void saveAsGenerated(ReportDto dto, LocalDate period) {
        try {
            Report report = reportRepository.getByRecipientEmailAndPeriod(dto.recipientEmail(), period);
            report.setTitle(dto.title());
            report.setContent(dto.content());
            report.setPeriod(period);
            report.setStatus(ReportStatus.GENERATED);
            reportRepository.save(report);
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    @Transactional
    public void saveAsFailed(String recipientEmail, LocalDate period) {
        try {
            Report rawReport = reportRepository.getByRecipientEmailAndPeriod(recipientEmail, period);
            rawReport.setStatus(ReportStatus.FAILED);
            reportRepository.save(rawReport);
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    @Transactional
    public void prepareReports(List<RawReportDto> rawReports) {
        try {
            reportRepository.saveAll(rawReportMapper.toListModel(rawReports));
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    @Transactional(readOnly = true)
    public List<ReportDto> getAllReport(LocalDate period) {
        List<Report> reports = reportRepository.getAllByPeriod(period);
        return reportMapper.toListDto(reports);
    }

    @Transactional(readOnly = true)
    public Set<String> getProcessedRecipients(LocalDate period) {
        return reportRepository.findProcessedRecipientsByPeriod(period);
    }
}
