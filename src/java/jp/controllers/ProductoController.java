package jp.controllers;

import jp.entidades.Producto;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;
import java.io.Serializable;
import java.util.List;
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
import jp.facades.ProductoFacade;
import jp.facades.RecargoFacade;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "productoController")
@ViewScoped
public class ProductoController implements Serializable {

    @EJB
    private jp.facades.ProductoFacade ejbFacade;
    @EJB
    private RecargoFacade recargoFacade;
    private List<Producto> items = null;
    private Producto selected;
    private String uiError, error;

    private Float recargoPublico;

    public ProductoController() {
        uiError = "ui-state-error";
    }

    public Producto getSelected() {
        return selected;
    }

    public void setSelected(Producto selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ProductoFacade getFacade() {
        return ejbFacade;
    }

    public Producto prepareCreate() {
        selected = new Producto();
        initializeEmbeddableKey();
        return selected;
    }

    public String getValorRecargoPublico(Producto producto, float valor) {

        if (producto.getVentaPublico()) {

            if (recargoPublico == null) {
                recargoPublico = recargoFacade.getParametros().getPorcentajeVentaPublic();
                recargoPublico = (recargoPublico / 100) + 1;
                if (recargoPublico == null) {
                    recargoPublico = 1.0f;
                }
            }
            String resultado = Float.toString(valor * recargoPublico);
            return "USD " + resultado;
        } else {
            return "No aplica";
        }
    }

    public void create() {
        if (!getFacade().getEntityByCodigoOrTipo(selected)) {
            persist(PersistAction.CREATE, JsfUtil.getMessageBundle(new String[]{"MessageProducto", "CreateSuccessM"}));
            if (!JsfUtil.isValidationFailed()) {
                items = null;    // Invalidate list of items to trigger re-query.
                setError("");
                RequestContext.getCurrentInstance().execute("PF('ProductoCreateDialog').hide()");
            }
        } else {
            setError(uiError);
            JsfUtil.addErrorMessage("Ya existe el Código: " + selected.getCodigo());
        }
    }

    public void update() {
        if (!getFacade().getEntityByCodigoOrTipo(selected)) {
            persist(PersistAction.UPDATE, JsfUtil.getMessageBundle(new String[]{"MessageProducto", "UpdateSuccessM"}));
            if (!JsfUtil.isValidationFailed()) {
                items = null;
                setError("");
                RequestContext.getCurrentInstance().execute("PF('ProductoEditDialog').hide()");
            }

        } else {
            items = null;
            setError(uiError);
            JsfUtil.addErrorMessage("Ya existe el Código: "+selected.getCodigo());
        }
    }

    public void destroy() {
        persist(PersistAction.DELETE, JsfUtil.getMessageBundle(new String[]{"MessageProducto", "DeleteSuccessM"}));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Producto> getItems() {
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

    public List<Producto> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Producto> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @FacesConverter(forClass = Producto.class, value = "productoconverter")
    public static class ProductoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ProductoController controller = (ProductoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "productoController");
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
            if (object instanceof Producto) {
                Producto o = (Producto) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Producto.class.getName()});
                return null;
            }
        }

    }
}
