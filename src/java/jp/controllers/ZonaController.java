package jp.controllers;

import jp.entidades.Zona;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;
import jp.facades.ZonaFacade;

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

@ManagedBean(name = "zonaController")
@SessionScoped
public class ZonaController implements Serializable {

    @EJB
    private jp.facades.ZonaFacade ejbFacade;
    private List<Zona> items = null;
    private Zona selected;

    public ZonaController() {
    }

    public Zona getSelected() {
        return selected;
    }

    public void setSelected(Zona selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ZonaFacade getFacade() {
        return ejbFacade;
    }

    public Zona prepareCreate() {
        selected = new Zona();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        if (!getFacade().getEntityByCodigoOrTipo(selected)) {
            persist(PersistAction.CREATE, JsfUtil.getMessageBundle(new String[]{"MessageZona", "CreateSuccessF"}));
            if (!JsfUtil.isValidationFailed()) {
                items = null;    // Invalidate list of items to trigger re-query.
            }
        } else {
            JsfUtil.addErrorMessage(JsfUtil.getMessageBundle("MessageEmpleadoCodigoExist").replaceAll("%cod%", selected.getCodigo()));
        }

    }

    public void update() {
        persist(PersistAction.UPDATE, JsfUtil.getMessageBundle(new String[]{"MessageZona", "UpdateSuccessF"}));
    }

    public void destroy() {
        persist(PersistAction.DELETE, JsfUtil.getMessageBundle(new String[]{"MessageZona", "DeleteSuccessF"}));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Zona> getItems() {
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

    public List<Zona> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Zona> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Zona.class, value = "zonaconverter")
    public static class ZonaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ZonaController controller = (ZonaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "zonaController");
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
            if (object instanceof Zona) {
                Zona o = (Zona) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Zona.class.getName()});
                return null;
            }
        }

    }

}
