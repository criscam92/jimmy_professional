package jp.validators;

import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import jp.facades.EmpleadoFacade;

@FacesValidator("validatorCodigo")
public class validatorCodigo implements Validator {

    @EJB
    EmpleadoFacade ejbFacade;

    public EmpleadoFacade getFacade() {
        return ejbFacade;
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) {
//        String codigo = value.toString();
//        if(){
//        }
    }
}
