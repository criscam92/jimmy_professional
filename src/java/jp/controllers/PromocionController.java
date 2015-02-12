package jp.controllers;

import jp.entidades.Promocion;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;

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
import jp.entidades.Producto;
import jp.entidades.PromocionProducto;
import jp.facades.PromocionFacade;

@ManagedBean(name = "promocionController")
@SessionScoped
public class PromocionController implements Serializable {

    @EJB
    private jp.facades.PromocionFacade ejbFacade;
    private List<Promocion> items = null;
    private Promocion selected;
    private Producto producto;
    private PromocionProducto promocionProducto;
    private List<PromocionProducto> promocionProductosTMP = null;

    public PromocionController() {
    }

    public Promocion getSelected() {
        return selected;
    }

    public void setSelected(Promocion selected) {
        this.selected = selected;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public List<PromocionProducto> getPromocionProductosTMP() {
        return promocionProductosTMP;
    }

    public void setPromocionProductosTMP(List<PromocionProducto> promocionProductosTMP) {
        this.promocionProductosTMP = promocionProductosTMP;
    }

    public PromocionProducto getPromocionProducto() {
        return promocionProducto;
    }

    public void setPromocionProducto(PromocionProducto promocionProducto) {
        this.promocionProducto = promocionProducto;
    }

    public PromocionFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(PromocionFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private PromocionFacade getFacade() {
        return ejbFacade;
    }

    public Promocion prepareCreate() {
        selected = new Promocion();
        promocionProducto = new PromocionProducto();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, JsfUtil.getMessageBundle(new String[]{"MessagePromocion", "CreateSuccessF"}));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, JsfUtil.getMessageBundle(new String[]{"MessagePromocion", "UpdateSuccessF"}));
    }

    public void destroy() {
        persist(PersistAction.DELETE, JsfUtil.getMessageBundle(new String[]{"MessagePromocion", "DeleteSuccessF"}));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Promocion> getItems() {
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

    public List<Promocion> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Promocion> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Promocion.class)
    public static class PromocionControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PromocionController controller = (PromocionController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "promocionController");
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
            if (object instanceof Promocion) {
                Promocion o = (Promocion) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Promocion.class.getName()});
                return null;
            }
        }

    }

    public List<Producto> llenarProducto(String query) {
        return getFacade().getProductosByQuery(query);
    }

    public void addPromocionProducto() {
        promocionProductosTMP.add(promocionProducto);
        promocionProducto = new PromocionProducto();
    }

}
