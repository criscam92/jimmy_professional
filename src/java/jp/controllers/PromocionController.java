package jp.controllers;

import jp.entidades.Promocion;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;

import java.io.Serializable;
import java.util.ArrayList;
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
import jp.facades.TransactionFacade;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "promocionController")
@SessionScoped
public class PromocionController implements Serializable {
    
    @EJB
    private PromocionFacade ejbFacade;
    @EJB
    private TransactionFacade transactionFacade;
    private List<Promocion> items = null;
    private Promocion selected;
    private Producto producto;
    private int cantidad;
    private List<PromocionProducto> promocionProductos = null;
    private final List<PromocionProducto> promocionProductosEliminar;
    private final List<PromocionProducto> promocionProductosGuardar;
    
    public PromocionController() {
        promocionProductos = new ArrayList<>();
        promocionProductosEliminar = new ArrayList<>();
        promocionProductosGuardar = new ArrayList<>();
        cantidad = 1;
    }
    
    public Promocion getSelected() {
        return selected;
    }
    
    public void setSelected(Promocion selected) {
        this.selected = selected;
    }
    
    public List<PromocionProducto> getPromocionProductos() {
        return promocionProductos;
    }
    
    public void setPromocionProductos(List<PromocionProducto> promocionProductos) {
        this.promocionProductos = promocionProductos;
    }
    
    public Producto getProducto() {
        return producto;
    }
    
    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    
    public int getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
    public PromocionFacade getEjbFacade() {
        return ejbFacade;
    }
    
    public void setEjbFacade(PromocionFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }
    
    public TransactionFacade getTransactionFacade() {
        return transactionFacade;
    }
    
    public void setTransactionFacade(TransactionFacade transactionFacade) {
        this.transactionFacade = transactionFacade;
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
        initializeEmbeddableKey();
        return selected;
    }
    
    public void preparaEditar() {
        promocionProductos = getFacade().getProductosByPromocion(selected);
    }
    
    public void create(boolean guardar) {
        if (promocionProductos.size() >= 1) {
            
            if (transactionFacade.createPromocion(selected, promocionProductos)) {
                if (!JsfUtil.isValidationFailed()) {
                    items = null;    // Invalidate list of items to trigger re-query.
                    selected = new Promocion();
                    producto = null;
                    cantidad = 1;
                    promocionProductos.clear();
                    if (guardar) {
                        RequestContext.getCurrentInstance().execute("PF('PromocionCreateDialog').hide()");
                    }
                }
            } else {
                JsfUtil.addErrorMessage("NO SE A PODIDO GUARDAR LA PROMOCION");
            }
            
        } else {
            JsfUtil.addErrorMessage("La promocion debe tener como minimo un producto");
        }
    }
    
    public void update() {
        if (promocionProductos.size() >= 1) {
            if (transactionFacade.updatePromocion(selected, promocionProductosGuardar, promocionProductosEliminar)) {
                if (!JsfUtil.isValidationFailed()) {
                    items = null;    // Invalidate list of items to trigger re-query.
                    selected = new Promocion();
                    producto = null;
                    cantidad = 1;
                    promocionProductos.clear();
                    promocionProductosGuardar.clear();
                    promocionProductosEliminar.clear();
                }
            } else {
                JsfUtil.addErrorMessage("NO SE A PODIDO actualizar LA PROMOCION");
            }
            
        } else {
            JsfUtil.addErrorMessage("La promocion debe tener como minimo un producto");
        }
    }
    
    public void destroy() {
        if (transactionFacade.deletePromocion(selected)) {
            if (!JsfUtil.isValidationFailed()) {
                selected = null; // Remove selection
                items = null;    // Invalidate list of items to trigger re-query.
            }
        } else {
            JsfUtil.addErrorMessage("OCCURRIO UN ERROR ELIMINANDO LA PROMOCION");
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
    
    private boolean promocionProductoValido() {
        System.out.println("ff");
        boolean productoNulo = false, cantidadMayorCero = true;
        if (producto == null) {
            System.out.println("fff");
            productoNulo = true;
            JsfUtil.addErrorMessage("El campo producto es obligatorio");
        }
        
        if (cantidad <= 0) {
            System.out.println("ffff");
            cantidadMayorCero = false;
            JsfUtil.addErrorMessage("el campo cantidad debe se mayor a cero");
        }
        
        return !productoNulo && cantidadMayorCero;
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
        if (promocionProductoValido()) {
            PromocionProducto pp = new PromocionProducto();
            
            pp.setId(promocionProductos.size() + 1L);
            pp.setProducto(producto);
            pp.setCantidad(cantidad);
            
            if (selected.getId() != null) {
                promocionProductosGuardar.add(pp);
            }
            
            promocionProductos.add(pp);
            producto = null;
            cantidad = 1;
        }
        
    }
    
    public void removePromocionProducto(PromocionProducto promocionProducto) {
        if (selected.getId() != null && !promocionProductosGuardar.contains(promocionProducto)) {
            promocionProductosEliminar.add(promocionProducto);
        }
        promocionProductos.remove(promocionProducto);
        
    }
    
}
