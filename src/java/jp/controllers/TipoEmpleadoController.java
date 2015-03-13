package jp.controllers;

import jp.entidades.TipoEmpleado;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;

import java.io.Serializable;
import java.util.List;
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
import jp.facades.TipoEmpleadoFacade;
import jp.util.Error;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "tipoEmpleadoController")
@SessionScoped
public class TipoEmpleadoController implements Serializable {

    @EJB
    private jp.facades.TipoEmpleadoFacade ejbFacade;
    private List<TipoEmpleado> items = null;
    private TipoEmpleado selected;
    private final Error error;

    public TipoEmpleadoController() {
        error = new Error();
    }

    public TipoEmpleado getSelected() {
        return selected;
    }

    public void setSelected(TipoEmpleado selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private TipoEmpleadoFacade getFacade() {
        return ejbFacade;
    }

    public TipoEmpleado prepareCreate() {
        selected = new TipoEmpleado();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        if (!getFacade().getEntityByCodigoOrTipo(selected)) {
            persist(PersistAction.CREATE, JsfUtil.getMessageBundle(new String[]{"MessageTipoEmpleado", "CreateSuccessM"}));
            if (!JsfUtil.isValidationFailed()) {
                selected = null; // Remove selection
                items = null;    // Invalidate list of items to trigger re-query.
                error.cleanError();
                RequestContext.getCurrentInstance().execute("PF('TipoEmpleadoCreateDialog').hide()");
            }
        } else {
            JsfUtil.addErrorMessage("YA EXISTE EL TIPO");
            error.addError();
        }
    }

    public void update() {
        if (!getFacade().getEntityByCodigoOrTipo(selected)) {
            persist(PersistAction.UPDATE, JsfUtil.getMessageBundle(new String[]{"MessageTipoEmpleado", "UpdateSuccessM"}));
            error.cleanError();
            RequestContext.getCurrentInstance().execute("PF('TipoEmpleadoEditDialog').hide()");
        } else {
            JsfUtil.addErrorMessage("YA EXISTE EL TIPO");
            error.addError();
        }
    }

    public void destroy() {
        persist(PersistAction.DELETE, JsfUtil.getMessageBundle(new String[]{"MessageTipoEmpleado", "DeleteSuccessM"}));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
            error.cleanError();
        }
    }

    public List<TipoEmpleado> getItems() {
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
                    JsfUtil.addErrorMessage(ex, JsfUtil.getMessageBundle("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, JsfUtil.getMessageBundle("PersistenceErrorOccured"));
            }
        }
    }

    public List<TipoEmpleado> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<TipoEmpleado> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = TipoEmpleado.class)
    public static class TipoEmpleadoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TipoEmpleadoController controller = (TipoEmpleadoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "tipoEmpleadoController");
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
            if (object instanceof TipoEmpleado) {
                TipoEmpleado o = (TipoEmpleado) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), TipoEmpleado.class.getName()});
                return null;
            }
        }
    }

    public String classError() {
        return error.getClassError();
    }

}
