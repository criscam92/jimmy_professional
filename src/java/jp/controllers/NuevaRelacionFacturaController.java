package jp.controllers;

import jp.entidades.Pago;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import jp.entidades.Empleado;
import jp.entidades.RelacionFactura;
import jp.entidades.Talonario;
import jp.facades.RelacionFacturaFacade;
import jp.facades.TalonarioFacade;
import jp.util.TipoTalonario;
import org.primefaces.event.SelectEvent;

@ManagedBean(name = "nuevaRelacionFacturaController")
@ViewScoped
public class NuevaRelacionFacturaController implements Serializable {

    @EJB
    private jp.facades.RelacionFacturaFacade ejbFacade;
    @EJB
    private jp.facades.TalonarioFacade talonarioFacade;
    
    private RelacionFactura relacionFactura;
    
    private List<Pago> items = null;
    private Pago selected;
    
    private Talonario talonario;
    

    public NuevaRelacionFacturaController() {
    }
    
    @PostConstruct
    private void init(){
        relacionFactura = prepareCreate();
    }

    public Pago getSelected() {
        return selected;
    }

    public void setSelected(Pago selected) {
        this.selected = selected;
    }

    public RelacionFactura getRelacionFactura() {
        return relacionFactura;
    }

    public void setRelacionFactura(RelacionFactura relacionFactura) {
        this.relacionFactura = relacionFactura;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private RelacionFacturaFacade getFacade() {
        return ejbFacade;
    }
    
    public TalonarioFacade getTalonarioFacade() {
        return talonarioFacade;
    }

    public RelacionFactura prepareCreate() {
        relacionFactura = new RelacionFactura();
        relacionFactura.setFecha(Calendar.getInstance().getTime());
        initializeEmbeddableKey();
        System.out.println("Se crea una nueva instancia de relación factura");
        return relacionFactura;
    }
    
    public Pago prepareCreatePago() {
        selected = new Pago();
        selected.setFecha(relacionFactura.getFecha());
        selected.setRelacionFactura(relacionFactura);
        if(talonario!=null){
            selected.setOrdenPago(talonario.getActual()+"");
            System.out.println("Se crea una nueva instancia de pago: "+selected.getOrdenPago());
        }
        
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("languages/BundleRelacionFactura").getString("RelacionCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("languages/BundleRelacionFactura").getString("RelacionUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("languages/BundleRelacionFactura").getString("RelacionDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }
    
//    public void buscar(){
//        
//        if(numeroFactura==null || numeroFactura.trim().isEmpty()){
//            JsfUtil.addWarnMessage("No indicó un número de factura");
//        }else{
//            try {
//                Factura factura = getFacturaFacade().findFacturaByOrdenPedido(numeroFactura);
//                if(factura!=null){
//                    
//                    double valorPendienteTMP = getFacturaFacade().getValorPendientePagoFactura(factura);
//                    if(valorPendienteTMP==-1){
//                        valorPendiente = 0d;
//                    }else{
//                        valorPendiente = valorPendienteTMP;
//                    }
//                    
//                    JsfUtil.addSuccessMessage("Se cargó la factura indicada");
//                    selected = new Pago();
//                    selected.setFactura(factura);
//                }else{
//                    JsfUtil.addWarnMessage("No se encontró la Factura indicada.");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                JsfUtil.addErrorMessage("No se encontró la Factura indicada.");
//            }
//            
//        }
//    }
    
//    public TipoPago[] getTiposPago(){
//        return new TipoPago[]{TipoPago.CONTADO,TipoPago.CHEQUE};
//    }
//
    public List<Pago> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

//    public String getNumeroFactura() {
//        return numeroFactura;
//    }
//
//    public void setNumeroFactura(String numeroFactura) {
//        this.numeroFactura = numeroFactura;
//    }
//
//    public Double getValorPendiente() {
//        return valorPendiente;
//    }
//
//    public void setValorPendiente(Double valorPendiente) {
//        this.valorPendiente = valorPendiente;
//    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(relacionFactura);
                } else {
                    getFacade().remove(relacionFactura);
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

//    public List<Pago> getItemsAvailableSelectMany() {
//        return getFacade().findAll();
//    }
//
//    public List<Pago> getItemsAvailableSelectOne() {
//        return getFacade().findAll();
//    }

    @FacesConverter(forClass = RelacionFactura.class)
    public static class RelacionFacturaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            NuevaRelacionFacturaController controller = (NuevaRelacionFacturaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "nuevaRelacionPagoController");
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
            if (object instanceof RelacionFactura) {
                RelacionFactura o = (RelacionFactura) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), RelacionFactura.class.getName()});
                return null;
            }
        }

    }
    
    public void changedEmpleado(SelectEvent e){
        System.out.println("Se seleccionó el empleado...");
        if(e!=null && e.getObject()!=null){
            talonario = getTalonarioFacade().getActiveTalonario(TipoTalonario.RECIBO_CAJA, (Empleado) e.getObject());
            if(talonario==null){
                JsfUtil.addWarnMessage("El empleado seleccionado no cuenta con un talonario de pagos actualmente");
            }else{
                return;
            }
        }
        talonario = null;
        relacionFactura.setVendedor(null);
    }

}
