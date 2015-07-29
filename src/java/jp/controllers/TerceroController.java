package jp.controllers;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
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
import jp.entidades.Tercero;
import jp.facades.TerceroFacade;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;
import jp.util.TipoDocumento;

@ManagedBean(name = "terceroController")
@ViewScoped
public class TerceroController implements Serializable {

    @EJB
    private TerceroFacade ejbFacade;
    private List<Tercero> items = null;
    private Tercero selected;

    public TerceroController() {
    }

    public Tercero getSelected() {
        return selected;
    }

    public void setSelected(Tercero selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private TerceroFacade getFacade() {
        return ejbFacade;
    }

    public Tercero prepareCreate() {
        selected = new Tercero();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        if (!existeDocumentoTercero()) {
            persist(PersistAction.CREATE, JsfUtil.getMessageBundle(new String[]{"MessageTercero", "CreateSuccessM"}));
            if (!JsfUtil.isValidationFailed()) {
                items = null;    // Invalidate list of items to trigger re-query.
            }
        } else {
            JsfUtil.addErrorMessage("El Documento ya se encuentra en la base de datos.");
        }
    }
    
    public boolean existeDocumentoTercero(){
        boolean existe = getFacade().existeDocumento(selected);
        if (existe) {
            selected.setNumdocumento("");
            JsfUtil.addErrorMessage("El Documento ya se encuentra en la base de datos.");
        }
        return existe;
    }

    public void update() {
        persist(PersistAction.UPDATE, JsfUtil.getMessageBundle(new String[]{"MessageTercero", "UpdateSuccessM"}));
    }

    public void destroy() {
        persist(PersistAction.DELETE, JsfUtil.getMessageBundle(new String[]{"MessageTercero", "DeleteSuccessM"}));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Tercero> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public Map<String, Integer> getTipoDocumentos() {
        return TipoDocumento.getMapTipoDocumentos();
    }

    public String getTipoDocumento(int tipo) {
        return TipoDocumento.getFromValue(tipo).getDetalle();
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

    public List<Tercero> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Tercero> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Tercero.class, value = "terceroconverter")
    public static class TerceroControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TerceroController controller = (TerceroController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "terceroController");
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
            if (object instanceof Tercero) {
                Tercero o = (Tercero) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Tercero.class.getName()});
                return null;
            }
        }

    }

}
