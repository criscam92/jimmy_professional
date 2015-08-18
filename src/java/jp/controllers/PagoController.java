package jp.controllers;

import jp.entidades.Pago;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import jp.facades.FacturaFacade;
import jp.facades.PagoFacade;
import jp.util.TipoPago;

@ManagedBean(name = "pagoController")
@ViewScoped
public class PagoController implements Serializable {

    @EJB
    private jp.facades.PagoFacade ejbFacade;    
    @EJB
    private FacturaFacade facturaFacade;
    
    private List<Pago> items = null;
    private Pago selected;
    
    private String numeroFactura;
    private Double valorPendiente;

    public PagoController() {
    }

    public Pago getSelected() {
        return selected;
    }

    public void setSelected(Pago selected) {
        this.selected = selected;
    }

    private PagoFacade getFacade() {
        return ejbFacade;
    }
    
    private FacturaFacade getFacturaFacade(){
        return facturaFacade;
    }
    
    public void prepareEdit(){
    }

    public void update() {
    }

    public void anular() {
    }
    
    public TipoPago[] getTiposPago(){
        return new TipoPago[]{TipoPago.CONTADO,TipoPago.CHEQUE};
    }

    public List<Pago> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public Double getValorPendiente() {
        return valorPendiente;
    }

    public void setValorPendiente(Double valorPendiente) {
        this.valorPendiente = valorPendiente;
    }

    @FacesConverter(forClass = Pago.class)
    public static class PagoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PagoController controller = (PagoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "pagoController");
            return controller.getFacade().find(getKey(value));
        }

        Long getKey(String value) {
            Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Pago) {
                Pago o = (Pago) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Pago.class.getName()});
                return null;
            }
        }

    }

}
