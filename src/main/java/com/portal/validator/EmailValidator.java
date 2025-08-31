package com.portal.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.util.regex.Pattern;

/**
 * Validador personalizado para emails
 */
public class EmailValidator implements Validator<Object> {
    
    private static final String EMAIL_PATTERN = 
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
        "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    @Override
    public void validate(FacesContext context, UIComponent component, Object value)
            throws ValidatorException {
        
        if (value == null || value.toString().trim().isEmpty()) {
            return; // Deixar a validação de required handle isso
        }
        
        String email = value.toString().trim();
        
        if (!pattern.matcher(email).matches()) {
            FacesMessage message = new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                "Email inválido",
                "Por favor, digite um email válido."
            );
            throw new ValidatorException(message);
        }
    }
}