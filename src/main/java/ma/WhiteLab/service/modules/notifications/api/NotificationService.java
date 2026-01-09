package ma.WhiteLab.service.modules.notifications.api;

import ma.WhiteLab.service.modules.notifications.dto.NotificationDTO;
import ma.WhiteLab.common.exceptions.ValidationException;

public interface NotificationService {
    void send(NotificationDTO dto) throws ValidationException;
}
