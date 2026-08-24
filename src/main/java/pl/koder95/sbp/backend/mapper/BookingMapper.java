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
    @Mapping(target = "type", expression = "java(type)")
    BookingDto toDto(Booking model, BookingType type);

    default BookingDto toDto(Booking model) {
        return toDto(model, BookingType.ACCEPTED);
    }
}
