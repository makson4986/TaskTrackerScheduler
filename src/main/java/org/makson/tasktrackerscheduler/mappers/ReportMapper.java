package org.makson.tasktrackerscheduler.mappers;

import org.makson.tasktrackerscheduler.dto.ReportDto;
import org.makson.tasktrackerscheduler.models.Report;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReportMapper {
    ReportDto toDto(Report model);

    Report toModel(ReportDto dto);

    List<ReportDto> toListDto(List<Report> models);

    List<Report> toListModel(List<ReportDto> dto);
}
