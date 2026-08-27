package pl.koder95.sbp.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.koder95.sbp.backend.config.MapperConfig;
import pl.koder95.sbp.backend.dto.BookingDto;
import pl.koder95.sbp.backend.dto.BookingType;
import pl.koder95.sbp.backend.model.Booking;

@Mapper(config = MapperConfig.class)
public interface BookingMapper {
    @Mapping(target = "studentUuid", source = "student.uuid")
    @Mapping(target = "lessonUuid", source = "lesson.uuid")
    @Mapping(target = "type", expression = "java(mapAccepted(model))")
    BookingDto toDto(Booking model);

    default BookingType mapAccepted(Booking model) {
        return model.isAccepted() ? BookingType.ACCEPTED : BookingType.REQUESTED;
    }
}
