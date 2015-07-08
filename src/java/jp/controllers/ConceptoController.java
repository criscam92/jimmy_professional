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
import jp.entidades.Concepto;
import jp.facades.ConceptoFacade;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "conceptoController")
@SessionScoped
public class ConceptoController implements Serializable {

    @EJB
    private ConceptoFacade ejbFacade;
    private List<Concepto> items = null;
    private Concepto selected;
    private final String uiError;
    private String error;

    public ConceptoController() {
        uiError = "ui-state-error";
    }

    public Concepto getSelected() {
        return selected;
    }

    public void setSelected(Concepto selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ConceptoFacade getFacade() {
        return ejbFacade;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Concepto prepareCreate() {
        selected = new Concepto();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        if (!getFacade().getEntityByCodigoOrTipo(selected)) {
            persist(PersistAction.CREATE, JsfUtil.getMessageBundle(new String[]{"MessageConcepto", "CreateSuccessM"}));
            if (!JsfUtil.isValidationFailed()) {
                items = null;    // Invalidate list of items to trigger re-query.
                setError("");
                RequestContext.getCurrentInstance().execute("PF('ConceptoCreateDialog').hide()");
            }
        } else {
            setError(uiError);
            JsfUtil.addErrorMessage("Ya existe el Código: " + selected.getCodigo());
        }
    }

    public void update() {
        if (!getFacade().getEntityByCodigoOrTipo(selected)) {
            persist(PersistAction.UPDATE, JsfUtil.getMessageBundle(new String[]{"MessageConcepto", "UpdateSuccessM"}));
            if (!JsfUtil.isValidationFailed()) {
                items = null;
                setError("");
                RequestContext.getCurrentInstance().execute("PF('ConceptoEditDialog').hide()");
            }
        } else {
            items = null;
            setError(uiError);
            JsfUtil.addErrorMessage("Ya existe el Código: " + selected.getCodigo());
        }
    }

    public void destroy() {
        persist(PersistAction.DELETE, JsfUtil.getMessageBundle(new String[]{"MessageConcepto", "DeleteSuccessM"}));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Concepto> getItems() {
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

    public List<Concepto> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Concepto> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Concepto.class, value = "conceptoconverter")
    public static class ConceptoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ConceptoController controller = (ConceptoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "conceptoController");
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
            if (object instanceof Concepto) {
                Concepto o = (Concepto) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Concepto.class.getName()});
                return null;
            }
        }

    }

    public String getTipoConcepto(boolean ingreso) {
        return ingreso ? "Ingreso" : "Egreso";
    }

}
