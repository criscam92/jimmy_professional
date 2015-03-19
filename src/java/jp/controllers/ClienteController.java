package jp.controllers;

import jp.entidades.Cliente;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.AjaxBehaviorEvent;
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
    private Boolean disable = false;
    private boolean recargo = false;
    private int valorSelect;
    private String visibility;
    private String header;

    public ClienteController() {
    }

    @PostConstruct
    public void init() {
    }

    public Cliente getSelected() {
        return selected;
    }

    public void setSelected(Cliente selected) {
        this.selected = selected;
    }

    public Boolean getDisable() {
        return disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public boolean getRecargo() {
        return recargo;
    }

    public void setRecargo(boolean recargo) {
        this.recargo = recargo;
    }

    public int getValorSelect() {
        return valorSelect;
    }

    public void setValorSelect(int valorSelect) {
        this.valorSelect = valorSelect;
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

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Cliente prepareCreate() {
        selected = new Cliente();
        initializeEmbeddableKey();
        disable = true;
        recargo = false;
        visibility = "hidden";
        header = JsfUtil.getMessageBundle("CreateClienteTitle");
        return selected;
    }

    public void prepareEdit() {
        header = JsfUtil.getMessageBundle("EditClienteTitle");
        if (selected.getTarifaEspecial() == null) {
            recargo = false;
            disable = false;
            visibility = "hidden";
        } else if (selected.getTarifaEspecial() >= 0.0f) {
            recargo = true;
            disable = false;
            visibility = "visible";
        } else {
            recargo = false;
            disable = true;
            visibility = "hidden";
        }
    }

    public void createOrEdit() {
        String msm = (selected.getId() != null ? JsfUtil.getMessageBundle(new String[]{"MessageCliente", "UpdateSuccessM"})
                : JsfUtil.getMessageBundle(new String[]{"MessageCliente", "CreateSuccessM"}));

        persist(PersistAction.CREATE, msm);

        if (!JsfUtil.isValidationFailed() && selected.getId() == null) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
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
                    JsfUtil.addErrorMessage(ex, JsfUtil.getMessageBundle("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, JsfUtil.getMessageBundle("PersistenceErrorOccured"));
            }
        }
    }

    public List<Cliente> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Cliente> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Cliente.class, value = "clienteconverter")
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

    public void onchange(AjaxBehaviorEvent e) {
        if (e != null) {
            UISelectOne select = (UISelectOne) e.getSource();
            disable = (select.getSubmittedValue() == null || select.getSubmittedValue().toString().equals(JsfUtil.getMessageBundle("SelectOneMessage")));
        } else {
            disable = true;
        }
        recargo = false;
        visibility = "hidden";
        selected.setTarifaEspecial(null);
    }

    public void showTarifaEspecial() {
        if (recargo) {
            visibility = "visible";
            selected.setTarifaEspecial(getEjbRecargoFacade().getParametrosByCiudad(selected.getCiudad()));
        } else {
            visibility = "hidden";
            selected.setTarifaEspecial(null);
        }
    }

    public List<Cliente> llenarCliente(String query) {
        return getFacade().getClienteByQuery(query);
    }

}
