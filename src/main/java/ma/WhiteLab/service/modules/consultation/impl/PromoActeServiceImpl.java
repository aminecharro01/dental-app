package ma.WhiteLab.service.modules.consultation.impl;

import ma.WhiteLab.service.modules.consultation.api.PromoActeService;
import ma.WhiteLab.service.modules.consultation.dto.ActeDTO;

public class PromoActeServiceImpl implements PromoActeService {

    @Override
    public double calculateDiscount(ActeDTO acte, String promoCode) {
        if (acte == null) return 0.0;
        
        double discount = 0.0;
        
        if ("PROMO_TEETH".equals(promoCode) && "BLANCHIMENT".equals(acte.getCategorie())) {
            discount = acte.getPrixBase() * 0.15; // 15%
        } else if ("FAMILY".equals(promoCode)) {
            discount = acte.getPrixBase() * 0.05; // 5% global
        }
        
        return discount;
    }
}
