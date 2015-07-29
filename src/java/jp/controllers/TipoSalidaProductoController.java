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
import jp.entidades.TipoSalidaProducto;
import jp.facades.TipoSalidaProductoFacade;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;

@ManagedBean(name = "tipoSalidaProductoController")
@ViewScoped
public class TipoSalidaProductoController implements Serializable {

    @EJB
    private TipoSalidaProductoFacade ejbFacade;
    private List<TipoSalidaProducto> items = null;
    private TipoSalidaProducto selected;
    private String error;
    private final String uiError;

    public TipoSalidaProductoController() {
        uiError = "ui-state-error";
    }

    public TipoSalidaProducto getSelected() {
        return selected;
    }

    public void setSelected(TipoSalidaProducto selected) {
        this.selected = selected;
    }
    

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private TipoSalidaProductoFacade getFacade() {
        return ejbFacade;
    }

    public TipoSalidaProducto prepareCreate() {
        selected = new TipoSalidaProducto();
        setError("");
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        if (!getFacade().existeDetalle(selected)) {
            persist(PersistAction.CREATE, JsfUtil.getMessageBundle(new String[]{"TipoSalidaProductoMessage", "CreateSuccessM"}));
            if (!JsfUtil.isValidationFailed()) {
                items = null;    // Invalidate list of items to trigger re-query.
            }
        }else{
            setError(uiError);
            JsfUtil.addErrorMessage("El Nombre ya se encuentra en la base de datos.");
        }

    }

    public void update() {
        persist(PersistAction.UPDATE, JsfUtil.getMessageBundle(new String[]{"TipoSalidaProductoMessage", "UpdateSuccessM"}));
    }

    public void destroy() {
        persist(PersistAction.DELETE, JsfUtil.getMessageBundle(new String[]{"TipoSalidaProductoMessage", "DeleteSuccessM"}));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<TipoSalidaProducto> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
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

    public List<TipoSalidaProducto> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<TipoSalidaProducto> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = TipoSalidaProducto.class, value = "tiposalidaproductoconverter")
    public static class TipoSalidaProductoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TipoSalidaProductoController controller = (TipoSalidaProductoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "tipoSalidaProductoController");
            return controller.getFacade().find(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof TipoSalidaProducto) {
                TipoSalidaProducto o = (TipoSalidaProducto) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), TipoSalidaProducto.class.getName()});
                return null;
            }
        }

    }

}
