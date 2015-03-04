/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.util.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import jp.util.TipoPago;

/**
 *
 * @author gurzaf
 */
@FacesConverter(value = "formaPagoConverter")
public class FormaPagoConverter implements Converter{

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        try {
            return TipoPago.getFromValue(Integer.parseInt(value));
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        try {
            if(value instanceof TipoPago){
                return ((TipoPago)value).getValor()+"";
            }
        } catch (Exception e) {
        }
        return null;
    }
    
}
