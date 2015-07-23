package jp.controllers;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import jp.entidades.Salida;
import jp.entidades.SalidaProducto;
import jp.facades.SalidaFacade;
import jp.facades.SalidaProductoFacade;
import jp.facades.TransactionFacade;
import jp.seguridad.UsuarioActual;
import jp.util.EstadoPagoFactura;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;

@ManagedBean(name = "salidaProductoController")
@ViewScoped
public class SalidaProductoController implements Serializable {

    @EJB
    private SalidaProductoFacade ejbFacade;
    @EJB
    private SalidaFacade ejbSalidaFacade;
    @EJB
    private TransactionFacade ejbTransactionFacade;
    @EJB
    private UsuarioActual ejbUsuarioActual;
    private List<SalidaProducto> items = null;
    private List<SalidaProducto> itemsSalidasProducto = null;
    private List<Salida> itemsSalida = null;
    private SalidaProducto selected;
    private Salida salida;

    public SalidaProductoController() {
    }

    public SalidaProducto getSelected() {
        return selected;
    }

    public void setSelected(SalidaProducto selected) {
        this.selected = selected;
    }

    public Salida getSalida() {
        return salida;
    }

    public void setSalida(Salida salida) {
        this.salida = salida;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private SalidaProductoFacade getFacade() {
        return ejbFacade;
    }
    
    private SalidaFacade getSalidaFacade() {
        return ejbSalidaFacade;
    }

    public TransactionFacade getEjbTransactionFacade() {
        return ejbTransactionFacade;
    }

    public UsuarioActual getEjbUsuarioActual() {
        return ejbUsuarioActual;
    }

    public SalidaProducto prepareCreate() {
        selected = new SalidaProducto();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, JsfUtil.getMessageBundle(new String[]{"SalidaProductoMessage", "CreateSuccessF"}));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, JsfUtil.getMessageBundle(new String[]{"SalidaProductoMessage", "UpdateSuccessF"}));
    }

    public void destroy() {
        persist(PersistAction.DELETE, JsfUtil.getMessageBundle(new String[]{"SalidaProductoMessage", "DeleteSuccessF"}));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<SalidaProducto> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }
    
    public List<SalidaProducto> getItemsSalidaProductos() {
        if (itemsSalidasProducto == null) {
            itemsSalidasProducto = getSalidaFacade().getSalidasProductoBySalida(null);
        }
        return itemsSalidasProducto;
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public List<SalidaProducto> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<SalidaProducto> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = SalidaProducto.class, value = "salidaproductoconverter")
    public static class SalidaProductoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SalidaProductoController controller = (SalidaProductoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "salidaProductoController");
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
            if (object instanceof SalidaProducto) {
                SalidaProducto o = (SalidaProducto) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), SalidaProducto.class.getName()});
                return null;
            }
        }

    }
    
    public void updateListSalidaProductos(Salida s){
        itemsSalidasProducto = getSalidaFacade().getSalidasProductoBySalida(s);
    }
    
    public String getEstadoSalida(int estado){
        return EstadoPagoFactura.getFromValue(estado).getDetalle();
    }
    
    public int getEstadoAnulado() {
        return EstadoPagoFactura.ANULADO.getValor();
    }
    public boolean disableAnular() {
        boolean result = !(ejbUsuarioActual.getUsuario().isAdmin() && salida != null && (salida.getEstado() == EstadoPagoFactura.REALIZADA.getValor()));
        return result;
    }
    
    public void anularSalida() {
        if (getEjbTransactionFacade().anularSalida(salida)) {
            if (!JsfUtil.isValidationFailed()) {
                JsfUtil.addSuccessMessage(JsfUtil.getMessageBundle(new String[]{"MessageSalida", "AnullSuccessF"}));
                salida = null; // Remove selection
                itemsSalida = null;
            }
        } else {
            JsfUtil.addErrorMessage("Ocurrió un error anulando la Salida de Producto(s)");
        }
    }
    
    public List<Salida> getSalidaItems() {
        if (itemsSalida == null) {
            itemsSalida = getSalidaFacade().findAll();
        }
        return itemsSalida;
    }


}
