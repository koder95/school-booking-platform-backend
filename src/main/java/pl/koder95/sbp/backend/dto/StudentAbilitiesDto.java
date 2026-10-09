package pl.koder95.sbp.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Abilities of a student", description = """
        Provides information about limits set for a specific student.
        """)
public record StudentAbilitiesDto(
        @Schema(name = "A student")
        StudentDto student,
        @Schema(name = "Number of remaining lessons available for booking", description = """
                Every student has got a number of lessons available for booking
                increased through buy actions. This value can be change by an admin anyway.
                If it is equal zero then a student cannot book any lesson.
                When this value is bigger than zero then a student can book some lesson and
                after every action this value decreased by one.
                """)
        int remainingLessons
) {
}
