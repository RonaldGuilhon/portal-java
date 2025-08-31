package com.portal.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * Validador personalizado para comprimento de texto
 */
public class LengthValidator implements Validator<Object> {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value)
            throws ValidatorException {
        
        if (value == null) {
            return;
        }
        
        String text = value.toString();
        
        // Obter parâmetros do componente
        Integer minimum = (Integer) component.getAttributes().get("minimum");
        Integer maximum = (Integer) component.getAttributes().get("maximum");
        
        if (minimum != null && text.length() < minimum) {
            FacesMessage message = new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                "Texto muito curto",
                "O texto deve ter pelo menos " + minimum + " caracteres."
            );
            throw new ValidatorException(message);
        }
        
        if (maximum != null && text.length() > maximum) {
            FacesMessage message = new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                "Texto muito longo",
                "O texto deve ter no máximo " + maximum + " caracteres."
            );
            throw new ValidatorException(message);
        }
    }
}