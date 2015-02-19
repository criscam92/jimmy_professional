package jp.controllers;

import jp.entidades.Cliente;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;

import java.io.Serializable;
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
import jp.entidades.Ciudad;
import jp.facades.ClienteFacade;
import jp.facades.RecargoFacade;

@ManagedBean(name = "clienteController")
@SessionScoped
public class ClienteController implements Serializable {

    @EJB
    private jp.facades.ClienteFacade ejbFacade;
    @EJB
    private RecargoFacade ejbRecargoFacade;
    private List<Cliente> items = null;
    private Cliente selected;
    private Boolean ciudad = false;
    private boolean recargo = false;

    public ClienteController() {
    }

    @PostConstruct
    public void init() {
        ciudad = false;
        recargo = false;
    }

    public Cliente getSelected() {
        return selected;
    }

    public void setSelected(Cliente selected) {
        this.selected = selected;
    }

    public Boolean getCiudad() {
        return ciudad;
    }

    public void setCiudad(Boolean ciudad) {
        this.ciudad = ciudad;
    }

    public boolean getRecargo() {
        return recargo;
    }

    public void setRecargo(boolean recargo) {
        this.recargo = recargo;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ClienteFacade getFacade() {
        return ejbFacade;
    }

    public RecargoFacade getEjbRecargoFacade() {
        return ejbRecargoFacade;
    }

    public Cliente prepareCreate() {
        selected = new Cliente();
        initializeEmbeddableKey();
        ciudad = false;
        recargo = false;
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, JsfUtil.getMessageBundle(new String[]{"MessageCliente", "CreateSuccessM"}));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, JsfUtil.getMessageBundle(new String[]{"MessageCliente", "UpdateSuccessM"}));
    }

    public void destroy() {
        persist(PersistAction.DELETE, JsfUtil.getMessageBundle(new String[]{"MessageCliente", "DeleteSuccessM"}));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Cliente> getItems() {
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

    public List<Cliente> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Cliente> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Cliente.class)
    public static class ClienteControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ClienteController controller = (ClienteController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "clienteController");
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
            if (object instanceof Cliente) {
                Cliente o = (Cliente) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Cliente.class.getName()});
                return null;
            }
        }

    }

    public boolean applyRecargo(Ciudad c) {
        try {
            if (getFacade().applyRecargo(c) != null) {
                ciudad = getFacade().applyRecargo(c).equals(c);
            } else {
                ciudad = false;
            }
        } catch (Exception e) {
            ciudad = false;
        }

        return getFacade().applyRecargo(c).equals(c);
    }

    public Float getRecargoByUbicacionCiudad(Ciudad c) {
        try {
            Float tarifaEspecial = getEjbRecargoFacade().getRecargoByUbicacionCiudad(c);
            selected.setTarifaEspecial(tarifaEspecial);
            return getEjbRecargoFacade().getRecargoByUbicacionCiudad(c);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean comprobarCiudad(){
        System.out.println("La ciudad = null?"+selected.getCiudad() == null);
        return selected.getCiudad() == null;
    }

}
