package pl.koder95.sbp.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.checkerframework.checker.index.qual.NonNegative;

@Schema(name = "Update request for abilities of a student", description = """
        Supplies information about limits to set.
        """)
public record UpdateStudentAbilitiesRequestDto(
        @Schema(name = "Number of remaining lessons available for booking", description = """
                Every student has got a number of lessons available for booking
                increased through buy actions. This value can be change by an admin anyway.
                If it is equal zero then a student cannot book any lesson.
                When this value is bigger than zero then a student can book some lesson and
                after every action this value decreased by one.
                """)
        @NonNegative int remainingLessons
) {
}
