package pl.koder95.sbp.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "teachers_colors")
public class TeacherColor {
    @Id
    private UUID uuid;
    @MapsId
    @OneToOne
    @JoinColumn(name = "teacher_uuid", referencedColumnName = "uuid")
    private Teacher teacher;
    @Column(nullable = false)
    private int color = random();

    public String getColorHex() {
        return "#%06x".formatted(color);
    }

    public void setColorHex(String colorHex) {
        if (colorHex.startsWith("#")) {
            colorHex = colorHex.substring(1);
            if (colorHex.length() == 3) {
                colorHex = ""
                        + colorHex.charAt(0) + colorHex.charAt(0)
                        + colorHex.charAt(1) + colorHex.charAt(1)
                        + colorHex.charAt(2) + colorHex.charAt(2);
            }
            if (colorHex.length() != 6) {
                throw new IllegalArgumentException("Invalid color hex: " + colorHex);
            }
        }
        this.color = Integer.parseInt(colorHex, 16);
    }

    public static int random() {
        return (int) Math.round(Math.random() * 0xffffff);
    }
}
