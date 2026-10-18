package org.makson.tasktrackerscheduler.mappers;

import org.makson.tasktrackerscheduler.dto.DraftReportDto;
import org.makson.tasktrackerscheduler.models.Report;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RawReportMapper {
    Report toModel(DraftReportDto dto);

    List<Report> toListModel(List<DraftReportDto> dto);
}
