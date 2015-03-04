package jp.controllers;

import jp.entidades.Pago;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import jp.entidades.Factura;
import jp.facades.FacturaFacade;
import jp.facades.PagoFacade;
import jp.util.TipoPago;

@ManagedBean(name = "pagoController")
@SessionScoped
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

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private PagoFacade getFacade() {
        return ejbFacade;
    }
    
    private FacturaFacade getFacturaFacade(){
        return facturaFacade;
    }

    public Pago prepareCreate() {
        selected = new Pago();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("languages/Bundle").getString("PagoCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("languages/Bundle").getString("PagoUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("languages/Bundle").getString("PagoDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }
    
    public void buscar(){
        
        if(numeroFactura==null || numeroFactura.trim().isEmpty()){
            JsfUtil.addWarnMessage("No indicó un número de factura");
        }else{
            try {
                Factura factura = getFacturaFacade().findFacturaByOrdenPedido(numeroFactura);
                if(factura!=null){
                    
                    double valorPendienteTMP = getFacturaFacade().getValorPendientePagoFactura(factura);
                    if(valorPendienteTMP==-1){
                        valorPendiente = 0d;
                    }else{
                        valorPendiente = valorPendienteTMP;
                    }
                    
                    JsfUtil.addSuccessMessage("Se cargó la factura indicada");
                    selected = new Pago();
                    selected.setFactura(factura);
                }else{
                    JsfUtil.addWarnMessage("No se encontró la Factura indicada.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                JsfUtil.addErrorMessage("No se encontró la Factura indicada.");
            }
            
        }
    }
    
    public TipoPago[] getTiposPago(){
        return new TipoPago[]{TipoPago.EFECTIVO,TipoPago.CHEQUE};
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

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("languages/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("languages/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public List<Pago> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Pago> getItemsAvailableSelectOne() {
        return getFacade().findAll();
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

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
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
