package org.makson.tasktrackerscheduler.repositories;

import org.makson.tasktrackerscheduler.models.Report;
import org.makson.tasktrackerscheduler.models.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface ReportRepository extends JpaRepository<Report, Integer> {
    @Query("select r.recipientEmail from Report r where r.period = :period")
    Set<String> findProcessedRecipientsByPeriod(@Param("period") LocalDate period);

    List<Report> getAllByPeriodAndStatus(LocalDate period, ReportStatus status);

    Report getByRecipientEmailAndPeriod(String recipientEmail, LocalDate period);
}
