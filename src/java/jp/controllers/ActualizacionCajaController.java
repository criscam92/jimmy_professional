package jp.controllers;

import jp.entidades.Pais;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;
import java.io.Serializable;
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
import jp.entidades.ActualizaBaseCajaMenor;
import jp.entidades.CajaMenor;
import jp.facades.ActualizaCajaFacade;
import jp.facades.CajaMenorFacade;

@ManagedBean(name = "actualizacionCajaController")
@ViewScoped
public class ActualizacionCajaController implements Serializable {

    @EJB
    private ActualizaCajaFacade ejbFacade;
    private ActualizaBaseCajaMenor selected;
    private List<ActualizaBaseCajaMenor> items = null;


    public ActualizaBaseCajaMenor getSelected() {
        return selected;
    }

    public void setSelected(ActualizaBaseCajaMenor selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ActualizaCajaFacade getFacade() {
        return ejbFacade;
    }

    public ActualizaBaseCajaMenor prepareCreate() {
        selected = new ActualizaBaseCajaMenor();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, JsfUtil.getMessageBundle(new String[]{"MessageCajaMenor", "CreateSuccessF"}));
        if (!JsfUtil.isValidationFailed()) {
//            RequestContext.getCurrentInstance().execute("PF('PaisCreateDialog').hide()");
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, JsfUtil.getMessageBundle(new String[]{"MessageCajaMenor", "UpdateSuccessF"}));
        if (!JsfUtil.isValidationFailed()) {
//            RequestContext.getCurrentInstance().execute("PF('PaisEditDialog').hide()");
        }
    }

    public void destroy() {
        persist(PersistAction.DELETE, JsfUtil.getMessageBundle(new String[]{"MessageCajaMenor", "DeleteSuccessF"}));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
        }
    }
    
    public List<ActualizaBaseCajaMenor> getItems() {
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

    public List<ActualizaBaseCajaMenor> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<ActualizaBaseCajaMenor> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Pais.class, value = "actualizacioncajaconverter")
    public static class CajaMenorControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0 || value.equals(JsfUtil.getMessageBundle("SelectOneMessage"))) {
                return null;
            }
            ActualizacionCajaController controller = (ActualizacionCajaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "actualizacionCajaController");
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
            if (object == null || object.equals(JsfUtil.getMessageBundle("SelectOneMessage"))) {
                return null;
            }
            if (object instanceof Pais) {
                Pais o = (Pais) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Pais.class.getName()});
                return null;
            }
        }

    }

}
