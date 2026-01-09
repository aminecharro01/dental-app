package ma.WhiteLab.service.modules.notifications.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class NotificationDTO {
    private Long id;
    private String type; // EMAIL, SMS
    private String destinataire;
    private String message;
    private String dateEnvoi;
    private boolean lu;
}
