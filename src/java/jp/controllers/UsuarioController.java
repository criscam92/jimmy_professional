package jp.controllers;

import jp.entidades.Usuario;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
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
import jp.facades.UsuarioFacade;
import jp.seguridad.Encrypt;
import jp.util.TipoUsuario;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "usuarioController")
@SessionScoped
public class UsuarioController implements Serializable {

    @EJB
    private jp.facades.UsuarioFacade ejbFacade;
    private List<Usuario> items = null;
    private Usuario selected;
    private String pass;
    private String uiError, error;

    public UsuarioController() {
        uiError = "ui-state-error";
    }

    public Usuario getSelected() {
        return selected;
    }

    public void setSelected(Usuario selected) {
        this.selected = selected;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private UsuarioFacade getFacade() {
        return ejbFacade;
    }

    public Usuario prepareCreate() {
        selected = new Usuario();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        if (!getFacade().getUsuarioByNombre(selected)) {
            if (verificarClave()) {
                persist(PersistAction.CREATE, JsfUtil.getMessageBundle(new String[]{"MessengerUser", "CreateSuccessM"}));
                if (!JsfUtil.isValidationFailed()) {
                    selected = null;
                    items = null;    // Invalidate list of items to trigger re-query.
                    setError("");
                    RequestContext.getCurrentInstance().execute("PF('UsuarioCreateDialog').hide()");
                }
            }
        } else {
            setError(uiError);
            JsfUtil.addErrorMessage(JsfUtil.getMessageBundle("MessengerUserExist").replaceAll("%USUARIO%", selected.getUsuario()));
        }
    }

    public void update() {
        if (!getFacade().getUsuarioByNombre(selected)) {
            if (verificarClave()) {
                items = null;
                persist(PersistAction.UPDATE, JsfUtil.getMessageBundle(new String[]{"MessengerUser", "UpdateSuccessM"}));
                RequestContext.getCurrentInstance().execute("PF('UsuarioEditDialog').hide()");
            }
        } else {
            items = null;
            setError(uiError);
            JsfUtil.addErrorMessage(JsfUtil.getMessageBundle("MessengerUserExist").replaceAll("%USUARIO%", selected.getUsuario()));
        }
    }

    public void destroy() {
        persist(PersistAction.DELETE, JsfUtil.getMessageBundle(new String[]{"MessengerUser", "DeleteSuccessM"}));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Usuario> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public Map<String, Integer> getTipoUsuarios() {
        return TipoUsuario.getMapTipoUsuarios();
    }

    public TipoUsuario[] getTipoUsuario() {
        return TipoUsuario.values();
    }

    public String getTipoUsuario(int tipo) {
        return TipoUsuario.getFromValue(tipo).getDetalle();
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

    public List<Usuario> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Usuario> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @FacesConverter(forClass = Usuario.class)
    public static class UsuarioControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UsuarioController controller = (UsuarioController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "usuarioController");
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
            if (object instanceof Usuario) {
                Usuario o = (Usuario) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Usuario.class.getName()});
                return null;
            }
        }

    }

    private boolean verificarClave() {
        if (selected.getId() == null) {
            if (pass == null || pass.isEmpty()) {
                JsfUtil.addErrorMessage(JsfUtil.getMessageBundle("MessegerPass"));
                return false;
            } else {
                String nuevaClave = Encrypt.getStringMessageDigest(pass);
                selected.setContrasena(nuevaClave);
            }
        } else {
            if (pass != null && !pass.isEmpty()) {
                String nuevaClave = Encrypt.getStringMessageDigest(pass);
                selected.setContrasena(nuevaClave);
            }
        }
        return true;
    }
}
