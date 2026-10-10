package org.makson.tasktrackerscheduler.services;

import lombok.RequiredArgsConstructor;
import org.makson.tasktrackerscheduler.dto.ReportDto;
import org.makson.tasktrackerscheduler.mappers.ReportMapper;
import org.makson.tasktrackerscheduler.models.Report;
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
    private final ReportMapper mapper;

    @Transactional
    public void save(ReportDto dto, LocalDate period) {
        Report report = mapper.toModel(dto);
        report.setPeriod(period);
        reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public List<ReportDto> getAllReport(LocalDate period) {
        List<Report> reports = reportRepository.getAllByPeriod(period);
        return mapper.toListDto(reports);
    }

    @Transactional(readOnly = true)
    public Set<String> getProcessedRecipients() {
        return reportRepository.findProcessedRecipientsByPeriod(LocalDate.now().minusDays(1));
    }
}
