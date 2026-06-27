package pl.koder95.sbp.backend.mapper;

import java.util.Optional;
import java.util.UUID;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import pl.koder95.sbp.backend.config.MapperConfig;
import pl.koder95.sbp.backend.dto.AvailabilityDto;
import pl.koder95.sbp.backend.dto.TimeRangeDto;
import pl.koder95.sbp.backend.dto.UpdateAvailabilityRequestDto;
import pl.koder95.sbp.backend.model.Availability;
import pl.koder95.sbp.backend.model.Teacher;
import pl.koder95.sbp.backend.model.TimeRange;
import pl.koder95.sbp.backend.repository.TeacherRepository;
import pl.koder95.sbp.backend.repository.TimeRangeRepository;

@Mapper(config = MapperConfig.class)
public interface AvailabilityMapper {
    @Mapping(target = "teacherUuid", source = "uuid")
    @Mapping(target = "monday", source = "mondayRange")
    @Mapping(target = "tuesday", source = "tuesdayRange")
    @Mapping(target = "wednesday", source = "wednesdayRange")
    @Mapping(target = "thursday", source = "thursdayRange")
    @Mapping(target = "friday", source = "fridayRange")
    @Mapping(target = "lunchBreak", source = "lunchBreakRange")
    AvailabilityDto toDto(Availability model);

    @Mapping(target = "teacher", source = "teacherUuid")
    @Mapping(target = "mondayRange", source = "monday")
    @Mapping(target = "tuesdayRange", source = "tuesday")
    @Mapping(target = "wednesdayRange", source = "wednesday")
    @Mapping(target = "thursdayRange", source = "thursday")
    @Mapping(target = "fridayRange", source = "friday")
    @Mapping(target = "lunchBreakRange", source = "lunchBreak")
    Availability toModel(AvailabilityDto dto, @Context TeacherRepository teacherRepository,
                         @Context TimeRangeRepository timeRangeRepository);

    @Mapping(target = "mondayRange", source = "monday")
    @Mapping(target = "tuesdayRange", source = "tuesday")
    @Mapping(target = "wednesdayRange", source = "wednesday")
    @Mapping(target = "thursdayRange", source = "thursday")
    @Mapping(target = "fridayRange", source = "friday")
    @Mapping(target = "lunchBreakRange", source = "lunchBreak")
    void updateModel(@MappingTarget Availability model, UpdateAvailabilityRequestDto dto,
                     @Context TimeRangeRepository timeRangeRepository);

    @Mapping(target = "mondayRange", source = "monday",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "tuesdayRange", source = "tuesday",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "wednesdayRange", source = "wednesday",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "thursdayRange", source = "thursday",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "fridayRange", source = "friday",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "lunchBreakRange", source = "lunchBreak",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void addToModel(@MappingTarget Availability model, UpdateAvailabilityRequestDto dto,
                     @Context TimeRangeRepository timeRangeRepository);

    default Teacher mapTeacher(UUID teacherUuid, @Context TeacherRepository teacherRepository) {
        return teacherRepository.findById(teacherUuid).orElse(null);
    }

    default TimeRange mapTimeRange(TimeRangeDto dto,
                                   @Context TimeRangeRepository timeRangeRepository) {
        return timeRangeRepository.findFirstByStartTimeAndEndTime(dto.startTime(), dto.endTime())
                .or(() -> createTimeRange(dto, timeRangeRepository))
                .orElse(null);
    }

    private Optional<TimeRange> createTimeRange(TimeRangeDto dto, TimeRangeRepository repository) {
        TimeRange entity = new TimeRange();
        entity.setStartTime(dto.startTime());
        entity.setEndTime(dto.endTime());
        return Optional.of(repository.save(entity));
    }
}
