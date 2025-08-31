package com.portal.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Validador personalizado para URLs
 */
public class UrlValidator implements Validator<Object> {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value)
            throws ValidatorException {
        
        if (value == null || value.toString().trim().isEmpty()) {
            return; // URL é opcional
        }
        
        String url = value.toString().trim();
        
        try {
            URL urlObject = new URL(url);
        } catch (MalformedURLException e) {
            FacesMessage message = new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                "URL inválida",
                "Por favor, digite uma URL válida (ex: https://exemplo.com/imagem.jpg)."
            );
            throw new ValidatorException(message);
        }
    }
}