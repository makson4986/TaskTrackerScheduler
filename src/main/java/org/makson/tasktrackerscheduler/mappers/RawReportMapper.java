package org.makson.tasktrackerscheduler.mappers;

import org.makson.tasktrackerscheduler.dto.RawReportDto;
import org.makson.tasktrackerscheduler.models.Report;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RawReportMapper {
    Report toModel(RawReportDto dto);

    List<Report> toListModel(List<RawReportDto> dto);
}
