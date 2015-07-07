package jp.controllers;

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
import jp.entidades.ReciboCaja;
import jp.facades.ReciboCajaFacade;
import jp.seguridad.UsuarioActual;
import jp.util.EstadoPagoFactura;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;

@ManagedBean(name = "reciboCajaController")
@SessionScoped
public class ReciboCajaController implements Serializable {

    @EJB
    private ReciboCajaFacade ejbFacade;
    @EJB
    private UsuarioActual ejbUsuarioFacade;
    private List<ReciboCaja> items = null;
    private ReciboCaja selected;

    public ReciboCajaController() {
    }

    public ReciboCaja getSelected() {
        return selected;
    }

    public void setSelected(ReciboCaja selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ReciboCajaFacade getFacade() {
        return ejbFacade;
    }

    public UsuarioActual getEjbUsuarioFacade() {
        return ejbUsuarioFacade;
    }

    public ReciboCaja prepareCreate() {
        selected = new ReciboCaja();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        if (selected != null) {
            selected.setUsuario(getEjbUsuarioFacade().get());
            selected.setEstado(EstadoPagoFactura.REALIZADA.getValor());
            persist(PersistAction.CREATE, JsfUtil.getMessageBundle(new String[]{"MessageReciboCaja", "CreateSuccessM"}));
        }
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, JsfUtil.getMessageBundle(new String[]{"MessageReciboCaja", "UpdateSuccessM"}));
    }

    public void destroy() {
        persist(PersistAction.DELETE, JsfUtil.getMessageBundle(new String[]{"MessageReciboCaja", "DeleteSuccessM"}));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<ReciboCaja> getItems() {
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

    public List<ReciboCaja> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<ReciboCaja> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = ReciboCaja.class)
    public static class ReciboCajaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ReciboCajaController controller = (ReciboCajaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "reciboCajaController");
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
            if (object instanceof ReciboCaja) {
                ReciboCaja o = (ReciboCaja) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), ReciboCaja.class.getName()});
                return null;
            }
        }

    }

}
