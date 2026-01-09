package ma.WhiteLab.service.modules.notifications.test;

import junit.framework.TestCase;
import ma.WhiteLab.service.modules.notifications.dto.NotificationDTO;
import ma.WhiteLab.service.modules.notifications.impl.NotificationValidator;

public class NotificationServiceImplTest extends TestCase {
    public void testValidate() {
        NotificationValidator val = new NotificationValidator();
        NotificationDTO dto = new NotificationDTO();
        assertFalse(val.validate(dto).isEmpty());
    }
}
