package jp.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
import jp.entidades.ListaPrecio;
import jp.entidades.PrecioProducto;
import jp.entidades.Producto;
import jp.facades.ListaPrecioFacade;
import jp.facades.TransactionFacade;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "listaPrecioController")
@ViewScoped
public class ListaPrecioController implements Serializable {

    @EJB
    private ListaPrecioFacade ejbFacade;
    @EJB
    private TransactionFacade transactionFacade;
    private List<ListaPrecio> items = null;
    private ListaPrecio selected;
    private PrecioProducto precioProducto;
    private List<PrecioProducto> listaPrecioProductos;
    private Producto producto;
    private Double precioNuevo;
    private Double precioNuevoUSD;
    private final String uiError;
    private String error;

    public ListaPrecioController() {
        listaPrecioProductos = new ArrayList<>();
        uiError = "ui-state-error";
    }

    public ListaPrecio getSelected() {
        return selected;
    }

    public void setSelected(ListaPrecio selected) {
        this.selected = selected;
    }

    public PrecioProducto getPrecioProducto() {
        return precioProducto;
    }

    public void setPrecioProducto(PrecioProducto precioProducto) {
        this.precioProducto = precioProducto;
    }

    public List<PrecioProducto> getListaPrecioProductos() {
        return listaPrecioProductos;
    }

    public void setListaPrecioProductos(List<PrecioProducto> precioProductos) {
        this.listaPrecioProductos = precioProductos;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto productos) {
        this.producto = productos;
    }

    public Double getPrecioNuevo() {
        return precioNuevo;
    }

    public void setPrecioNuevo(Double precio) {
        this.precioNuevo = precio;
    }

    public Double getPrecioNuevoUSD() {
        return precioNuevoUSD;
    }

    public void setPrecioNuevoUSD(Double precioNuevoUSD) {
        this.precioNuevoUSD = precioNuevoUSD;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ListaPrecioFacade getFacade() {
        return ejbFacade;
    }

    public TransactionFacade getTransactionFacade() {
        return transactionFacade;
    }

    public ListaPrecio prepareCreate() {
        selected = new ListaPrecio();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        if (!existeCodigoListaPrecio()) {
            if (listaPrecioProductos.size() >= 1) {
                if (getTransactionFacade().createPrecioProducto(selected, listaPrecioProductos)) {
                    if (!JsfUtil.isValidationFailed()) {
                        JsfUtil.addSuccessMessage(JsfUtil.getMessageBundle(new String[]{"MessageListaPrecio", "CreateSuccessF"}));
                        items = null;    // Invalidate list of items to trigger re-query.
                        selected = new ListaPrecio();
                        listaPrecioProductos.clear();
                        limpiarPrecioProducto();
                        setError("");
                        RequestContext.getCurrentInstance().execute("PF('ListaPrecioCreateDialog').hide()");
                    }
                } else {
                    JsfUtil.addErrorMessage("No se ha podido Crear la Lista de Productos");
                }
            } else {
                JsfUtil.addErrorMessage("La Lista de Precios debe tener como mínimo un producto");
            }
        } else {
            setError(uiError);
            JsfUtil.addErrorMessage("El Código de la Lista de Precios ya existe.");
        }
    }

    public boolean existeCodigoListaPrecio() {
        boolean existe = getFacade().getEntityByCodigoOrTipo(selected);
        if (existe) {
            selected.setCodigo("");
            JsfUtil.addErrorMessage("El Código de la Lista de Precios ya existe.");
        }
        return existe;
    }

    public void update() {
        persist(PersistAction.UPDATE, JsfUtil.getMessageBundle(new String[]{"MessageListaPrecio", "UpdateSuccessF"}));
    }

    public void destroy() {
        if (getTransactionFacade().deleteListaPrecio(selected)) {
            JsfUtil.addSuccessMessage(JsfUtil.getMessageBundle(new String[]{"MessageListaPrecio", "DeleteSuccessF"}));
            if (!JsfUtil.isValidationFailed()) {
                selected = null; // Remove selection
                items = null;    // Invalidate list of items to trigger re-query.
            }
        }
    }

    public List<ListaPrecio> getItems() {
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public List<ListaPrecio> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<ListaPrecio> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = ListaPrecio.class)
    public static class ListaPrecioControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ListaPrecioController controller = (ListaPrecioController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "listaPrecioController");
            return controller.getFacade().find(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof ListaPrecio) {
                ListaPrecio o = (ListaPrecio) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), ListaPrecio.class.getName()});
                return null;
            }
        }

    }

    public void addPrecioProducto() {
        if (producto != null && precioNuevo != null && precioNuevoUSD != null) {
            precioProducto = new PrecioProducto();
            precioProducto.setId(listaPrecioProductos.size() + 1);
            precioProducto.setProducto(producto);
            precioProducto.setPrecio(precioNuevo);
            precioProducto.setPrecioUSD(precioNuevoUSD);
            listaPrecioProductos.add(precioProducto);
            limpiarPrecioProducto();
        } else{ 
            JsfUtil.addErrorMessage("Debe seleccionar un producto con sus nuevos precios");
        }
    }

    public void removePrecioProducto(PrecioProducto pp) {
        listaPrecioProductos.remove(pp);
    }

    private void limpiarPrecioProducto() {
        producto = null;
        precioNuevo = null;
        precioNuevoUSD = null;
    }

}
