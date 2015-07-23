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
import jp.entidades.Producto;
import jp.entidades.Salida;
import jp.entidades.SalidaProducto;
import jp.facades.ProductoFacade;
import jp.facades.SalidaFacade;
import jp.facades.TransactionFacade;
import jp.seguridad.UsuarioActual;
import jp.util.EstadoPagoFactura;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;
import org.primefaces.event.SelectEvent;

@ManagedBean(name = "salidaController")
@ViewScoped
public class SalidaController implements Serializable {

    @EJB
    private SalidaFacade ejbFacade;
    @EJB
    private TransactionFacade ejbTransactionFacade;
    @EJB
    private UsuarioActual ejbUsuarioActual;
    @EJB
    private ProductoFacade productoFacade;
    private List<Salida> items = null;
    private Salida selected;
    private List<SalidaProducto> salidasProducto;
    private int cantidad;
    private Producto producto;
    private Integer disponible;

    public SalidaController() {
        selected = new Salida();
        salidasProducto = new ArrayList<>();
    }

    public Salida getSelected() {
        return selected;
    }

    public void setSelected(Salida selected) {
        this.selected = selected;
    }

    public List<SalidaProducto> getSalidasProducto() {
        return salidasProducto;
    }

    public void setSalidasProducto(List<SalidaProducto> salidasProducto) {
        this.salidasProducto = salidasProducto;
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

    public Integer getDisponible() {
        return disponible;
    }

    public void setDisponible(Integer diponible) {
        this.disponible = diponible;
    }

    public TransactionFacade getEjbTransactionFacade() {
        return ejbTransactionFacade;
    }

    public UsuarioActual getEjbUsuarioActual() {
        return ejbUsuarioActual;
    }

    public ProductoFacade getProductoFacade() {
        return productoFacade;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private SalidaFacade getFacade() {
        return ejbFacade;
    }

    public Salida prepareCreate() {
        selected = new Salida();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        if (salidasProducto.size() > 0 && selected != null) {
            try {
//                persist(PersistAction.CREATE, JsfUtil.getMessageBundle(new String[]{"MessageSalida", "CreateSuccessF"}));
                selected.setUsuario(getEjbUsuarioActual().getUsuario());
                selected.setEstado(EstadoPagoFactura.REALIZADA.getValor());
                getEjbTransactionFacade().createSalidaProducto(selected, salidasProducto);
                if (!JsfUtil.isValidationFailed()) {
                    items = null;    // Invalidate list of items to trigger re-query.
                    selected = new Salida();
                    salidasProducto.clear();
                    JsfUtil.addSuccessMessage("Se registró la Salida de los Productos con éxito");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            JsfUtil.addErrorMessage("Se deben agregar Productos a la Salida");
        }

    }

    public void update() {
        persist(PersistAction.UPDATE, JsfUtil.getMessageBundle(new String[]{"MessageSalida", "UpdateSuccessF"}));
    }

    public void destroy() {
        persist(PersistAction.DELETE, JsfUtil.getMessageBundle(new String[]{"MessageSalida", "DeleteSuccessF"}));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Salida> getItems() {
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/BundleSalida2").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/BundleSalida2").getString("PersistenceErrorOccured"));
            }
        }
    }

    public List<Salida> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Salida> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Salida.class, value = "salidaconverter")
    public static class SalidaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SalidaController controller = (SalidaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "salidaController");
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
            if (object instanceof Salida) {
                Salida o = (Salida) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Salida.class.getName()});
                return null;
            }
        }

    }

    public void removeSalidaProducto(SalidaProducto sp) {
        salidasProducto.remove(sp);
    }

    public void addSalidaProducto() {
        if (salidaProductoValido()) {
            if (producto != null && cantidad > 0) {
                for (SalidaProducto sp : salidasProducto) {
                    if (sp.getProducto().getId().equals(producto.getId())) {
                        int indice = salidasProducto.indexOf(sp);
                        salidasProducto.get(indice).setCantidad(sp.getCantidad() + cantidad);
                        producto = null;
                        cantidad = 0;
                        disponible = null;
                        return;
                    }
                }
                SalidaProducto sp = new SalidaProducto();
                sp.setId(salidasProducto.size() + 1l);
                sp.setCantidad(cantidad);
                sp.setProducto(producto);
                sp.setSalida(selected);
                salidasProducto.add(sp);
                producto = null;
                cantidad = 0;
                disponible = null;
            } else {
                JsfUtil.addErrorMessage("El campo cantidad debe ser mayor a 0");
            }
        }
    }

    private boolean salidaProductoValido() {
        if (disponible != null) {
            if (cantidad > disponible) {
                JsfUtil.addErrorMessage("La cantidad no puede ser mayor a la cantidad disponible");
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    public void onItemSelectProducto(SelectEvent event) {
        Producto p = (Producto) event.getObject();
        disponible = getProductoFacade().getCantidadDisponibleByProducto(p) - getCantidadProductosAgregados(p);
        if (disponible <= 0) {
            disponible = null;
            cantidad = 1;
            producto = null;
            JsfUtil.addErrorMessage("No hay existencias del producto " + p.toString());
        }
    }

    private int getCantidadProductosAgregados(Producto p) {
        for (SalidaProducto sp : salidasProducto) {
            if (sp.getProducto().getId().equals(p.getId())) {
                return sp.getCantidad();
            }
        }
        return 0;
    }
}
