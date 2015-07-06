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
import jp.entidades.CajaMenor;
import jp.facades.CajaMenorFacade;

@ManagedBean(name = "cajaMenorController")
@ViewScoped
public class CajaMenorController implements Serializable {

    @EJB
    private CajaMenorFacade ejbFacade;
    private CajaMenor selected;
    private List<CajaMenor> items = null;

    @PostConstruct
    private void init() {
        findCajaMenor();
    }

    public CajaMenor getSelected() {
        return selected;
    }

    public void setSelected(CajaMenor selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private CajaMenorFacade getFacade() {
        return ejbFacade;
    }

    public CajaMenor prepareCreate() {
        selected = new CajaMenor();
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
        if (selected != null) {
            selected.setFechaActualizacion(Calendar.getInstance().getTime());
        }
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
    
    public List<CajaMenor> getItems() {
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

    public List<CajaMenor> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<CajaMenor> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Pais.class, value = "cajamenorconverter")
    public static class CajaMenorControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0 || value.equals(JsfUtil.getMessageBundle("SelectOneMessage"))) {
                return null;
            }
            CajaMenorController controller = (CajaMenorController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "cajaMenorController");
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

    private void findCajaMenor() {
        selected = getFacade().getCajaMenor();
    }

}
