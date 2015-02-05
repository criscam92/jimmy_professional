package jp.controllers;

import jp.entidades.CodigoDevolucion;
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
import jp.facades.CodigoDevolucionFacade;

@ManagedBean(name = "codigoDevolucionController")
@SessionScoped
public class CodigoDevolucionController implements Serializable {

    @EJB
    private jp.facades.CodigoDevolucionFacade ejbFacade;
    private List<CodigoDevolucion> items = null;
    private CodigoDevolucion selected;

    public CodigoDevolucionController() {
    }

    public CodigoDevolucion getSelected() {
        return selected;
    }

    public void setSelected(CodigoDevolucion selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private CodigoDevolucionFacade getFacade() {
        return ejbFacade;
    }

    public CodigoDevolucion prepareCreate() {
        selected = new CodigoDevolucion();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        if (!getFacade().getEntityByCodigo(selected)) {
            persist(PersistAction.CREATE, JsfUtil.getMessageBundle(new String[]{"MessageCodigoDevolucion", "CreateSuccessM"}));
            if (!JsfUtil.isValidationFailed()) {
                items = null;    // Invalidate list of items to trigger re-query.
            }
        }else{
            JsfUtil.addErrorMessage(JsfUtil.getMessageBundle("MessageEmpleadoCodigoExist").replaceAll("%cod%", selected.getCodigo()));
        }

    }

    public void update() {
        if (!getFacade().getEntityByCodigo(selected)) {
            persist(PersistAction.UPDATE, JsfUtil.getMessageBundle(new String[]{"MessageCodigoDevolucion", "UpdateSuccessM"}));
        }else{
            JsfUtil.addErrorMessage(JsfUtil.getMessageBundle("MessageEmpleadoCodigoExist").replaceAll("%cod%", selected.getCodigo()));
        }
        
    }

    public void destroy() {
        persist(PersistAction.DELETE, JsfUtil.getMessageBundle(new String[]{"MessageCodigoDevolucion", "DeleteSuccessM"}));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<CodigoDevolucion> getItems() {
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("languages/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("languages/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public List<CodigoDevolucion> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<CodigoDevolucion> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = CodigoDevolucion.class)
    public static class CodigoDevolucionControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CodigoDevolucionController controller = (CodigoDevolucionController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "codigoDevolucionController");
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
            if (object instanceof CodigoDevolucion) {
                CodigoDevolucion o = (CodigoDevolucion) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), CodigoDevolucion.class.getName()});
                return null;
            }
        }

    }

}
