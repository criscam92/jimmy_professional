package jp.controllers;

import jp.entidades.Ciudad;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ValueChangeEvent;
import jp.entidades.Pais;
import jp.entidades.Recargo;
import jp.facades.CiudadFacade;
import jp.facades.RecargoFacade;
import jp.facades.TransactionFacade;

@ManagedBean(name = "recargoController")
@SessionScoped
public class RecargoController implements Serializable {

    @EJB
    private RecargoFacade ejbFacade;
    @EJB
    private TransactionFacade ejbTransactionFacade;
    @EJB
    private CiudadFacade ciudadFacade;
    private List<Recargo> items = null;
    private Recargo selected;
    private Pais pais;
    private List<Ciudad> ciudades;

    public RecargoController() {
    }

    @PostConstruct
    public void init() {
        selected = new Recargo();
        comprobarRegistros();
        ciudades = new ArrayList<>();
    }

    public Recargo getSelected() {
        return selected;
    }

    public void setSelected(Recargo selected) {
        this.selected = selected;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    public List<Ciudad> getCiudades() {
        return ciudades;
    }

    public CiudadFacade getCiudadFacade() {
        return ciudadFacade;
    }

    public void setCiudadFacade(CiudadFacade ciudadFacade) {
        this.ciudadFacade = ciudadFacade;
    }

    public void setCiudades(List<Ciudad> ciudades) {
        this.ciudades = ciudades;
    }

    private RecargoFacade getFacade() {
        return ejbFacade;
    }

    private TransactionFacade getTransactionFacade() {
        return ejbTransactionFacade;
    }

    public Recargo prepareCreate() {
        selected = new Recargo();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, JsfUtil.getMessageBundle(new String[]{"MessageRecargo", "CreateSuccessM"}));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void crearRecargo() {
        if (selected != null) {
            if (getTransactionFacade().createUpdateRecargo(selected)) {
                JsfUtil.addSuccessMessage(JsfUtil.getMessageBundle("MessageCreateUpdateRecargo"));
                if (!JsfUtil.isValidationFailed()) {
                    items = null;    // Invalidate list of items to trigger re-query.
                }
            } else {
                JsfUtil.addErrorMessage(JsfUtil.getMessageBundle("ErrorCreateUpdateRecargo"));
            }
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, JsfUtil.getMessageBundle(new String[]{"MessageRecargo", "UpdateSuccessM"}));
    }

    public void destroy() {
        persist(PersistAction.DELETE, JsfUtil.getMessageBundle(new String[]{"MessageRecargo", "DeleteSuccessM"}));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Recargo> getItems() {
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

    public List<Recargo> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Recargo> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    private void comprobarRegistros() {
        selected = getFacade().getRecargo();
        if (selected == null) {
            selected = new Recargo();
        } else {
            pais = selected.getCiudad().getPais();
            ciudades = getCiudadFacade().getCiudadesByPais(pais);
        }
    }

    @FacesConverter(forClass = Ciudad.class)
    public static class RecargoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            RecargoController controller = (RecargoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "recargoController");
            return controller.getFacade().find(getKey(value));
        }

        Long getKey(String value) {
            Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Recargo) {
                Recargo o = (Recargo) object;
                return getStringKey(o.getId().longValue());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Ciudad.class.getName()});
                return null;
            }
        }

    }

    public void countryChangeListener(ValueChangeEvent event) {
        if (event.getNewValue() != null && !event.getNewValue().toString().endsWith(JsfUtil.getMessageBundle("SelectOneMessage"))) {
            pais = ((Pais) event.getNewValue());
            ciudades = getCiudadFacade().getCiudadesByPais(pais);
        } else {
            ciudades.clear();
        }
    }

}
