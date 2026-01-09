package ma.WhiteLab.service.modules.notifications.impl;

import ma.WhiteLab.service.modules.notifications.api.NotificationService;
import ma.WhiteLab.service.modules.notifications.dto.NotificationDTO;
import ma.WhiteLab.common.exceptions.ValidationException;
import ma.WhiteLab.common.consoleLog.ConsoleLogger;

import java.util.Map;

public class NotificationServiceImpl implements NotificationService {

    private final NotificationValidator validator = new NotificationValidator();

    @Override
    public void send(NotificationDTO dto) throws ValidationException {
        // Validation
        Map<String, String> errors = validator.validate(dto);
        if (!errors.isEmpty()) {
            throw new ValidationException("Notification invalide");
        }

        // Simulation d'envoi (Faute de serveur SMTP configuré)
        ConsoleLogger.info(">>> ENVOI NOTIFICATION [" + dto.getType() + "] à " + dto.getDestinataire() + " : " + dto.getMessage());
        
        // Logique de persistance possible ici si NotificationRepository existe
    }
}
