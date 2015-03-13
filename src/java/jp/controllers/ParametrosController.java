package jp.controllers;

import jp.entidades.Ciudad;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
import javax.faces.event.ValueChangeEvent;
import jp.entidades.Pais;
import jp.entidades.Parametros;
import jp.facades.CiudadFacade;
import jp.facades.RecargoFacade;
import jp.facades.TransactionFacade;

@ManagedBean(name = "parametrosController")
@ViewScoped
public class ParametrosController implements Serializable {

    @EJB
    private RecargoFacade ejbFacade;
    @EJB
    private TransactionFacade ejbTransactionFacade;
    @EJB
    private CiudadFacade ciudadFacade;
    
    private List<Parametros> items = null;
    private Parametros selected;
    private Pais pais;
    private List<Ciudad> ciudades;

    public ParametrosController() {
    }

    @PostConstruct
    public void init() {
        selected = new Parametros();
        comprobarRegistros();
        ciudades = new ArrayList<>();
    }

    public Parametros getSelected() {
        return selected;
    }

    public void setSelected(Parametros selected) {
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

    public Parametros prepareCreate() {
        selected = new Parametros();
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

    public List<Parametros> getItems() {
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

    public List<Parametros> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Parametros> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    private void comprobarRegistros() {
        selected = getFacade().getParametros();
        if (selected == null) {
            selected = new Parametros();
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
            ParametrosController controller = (ParametrosController) facesContext.getApplication().getELResolver().
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
            if (object instanceof Parametros) {
                Parametros p = (Parametros) object;
                return getStringKey(p.getId().longValue());
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
