package ma.WhiteLab.service.modules.notifications.impl;

import ma.WhiteLab.common.validation.Validator;
import ma.WhiteLab.service.modules.notifications.dto.NotificationDTO;
import java.util.HashMap;
import java.util.Map;

public class NotificationValidator implements Validator<NotificationDTO> {
    @Override
    public Map<String, String> validate(NotificationDTO dto) {
        Map<String, String> errors = new HashMap<>();
        if (dto == null) return errors;

        if (dto.getMessage() == null || dto.getMessage().isEmpty()) {
            errors.put("message", "Message vide");
        }
        if (dto.getDestinataire() == null || dto.getDestinataire().isEmpty()) {
            errors.put("destinataire", "Destinataire requis");
        }
        return errors;
    }
}
