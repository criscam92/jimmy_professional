package jp.controllers;

import jp.entidades.Ingreso;
import jp.util.JsfUtil;
import jp.facades.IngresoFacade;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import jp.entidades.IngresoProducto;
import jp.entidades.Producto;
import jp.facades.TransactionFacade;
import jp.seguridad.UsuarioActual;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "ingresoController")
@ViewScoped
public class IngresoController implements Serializable {

    @EJB
    private IngresoFacade ejbFacade;
    @EJB
    private TransactionFacade transactionFacade;
    @Inject
    private UsuarioActual usuarioActual;

    private List<Ingreso> items = null;
    private Ingreso selected;
    private Producto producto;
    private int cantidad;
    private List<IngresoProducto> ingresoProductos = null;
    private final List<IngresoProducto> ingresoProductosEliminar;
    private final List<IngresoProducto> ingresoProductosGuardar;
    private final List<IngresoProducto> ingresoProductosEditar;
    private String header;

    public IngresoController() {
        ingresoProductos = new ArrayList<>();
        ingresoProductosEliminar = new ArrayList<>();
        ingresoProductosGuardar = new ArrayList<>();
        ingresoProductosEditar = new ArrayList<>();
        cantidad = 1;
    }

    public Ingreso getSelected() {
        return selected;
    }

    public void setSelected(Ingreso selected) {
        this.selected = selected;
    }

    public int getCantidad() {
        return cantidad;
    }

    public TransactionFacade getTransactionFacade() {
        return transactionFacade;
    }

    public void setTransactionFacade(TransactionFacade transactionFacade) {
        this.transactionFacade = transactionFacade;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public List<IngresoProducto> getIngresoProductos() {
        return ingresoProductos;
    }

    public void setIngresoProductos(List<IngresoProducto> ingresoProductos) {
        this.ingresoProductos = ingresoProductos;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    private IngresoFacade getFacade() {
        return ejbFacade;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Ingreso prepareCreate() {
        selected = new Ingreso();
        initializeEmbeddableKey();
        header = "CREAR INGRESO";
        return selected;
    }

    public void prepareEdit() {
        header = "EDITAR INGRESO";
        ingresoProductos = getFacade().getProductosByIngreso(selected);
    }

    public void create(boolean guardar) {
        if (ingresoProductos.size() >= 1) {
            selected.setUsuario(usuarioActual.getUsuario());
            if (getTransactionFacade().createIngreso(selected, ingresoProductos)) {
                if (!JsfUtil.isValidationFailed()) {
                    items = null;    // Invalidate list of items to trigger re-query.
                    selected = null;
                    producto = null;
                    cantidad = 1;
                    ingresoProductos.clear();
                    if (guardar) {
                        RequestContext.getCurrentInstance().execute("PF('IngresoFormDialog').hide()");
                    }
                }
            } else {
                JsfUtil.addErrorMessage("NO SE A PODIDO GUARDAR EL INGRESO");
            }

        } else {
            JsfUtil.addErrorMessage("EL INGRESO debe tener como minimo un producto");
        }
    }

    public void update() {
        if (ingresoProductos.size() >= 1) {
            selected.setUsuario(usuarioActual.getUsuario());
            if (getTransactionFacade().updateIngreso(selected, ingresoProductosGuardar, ingresoProductosEliminar, ingresoProductosEditar)) {
                if (!JsfUtil.isValidationFailed()) {
                    items = null;    // Invalidate list of items to trigger re-query.
                    selected = null;
                    producto = null;
                    cantidad = 1;
                    ingresoProductos.clear();
                    ingresoProductosGuardar.clear();
                    ingresoProductosEliminar.clear();
                    ingresoProductosEditar.clear();
                }
            } else {
                JsfUtil.addErrorMessage("NO SE A PODIDO actualizar EL INGRESO");
            }

        } else {
            JsfUtil.addErrorMessage("EL INGRESO debe tener como minimo un producto");
        }
    }

    public void destroy() {
        if (transactionFacade.deleteIngreso(selected)) {
            if (!JsfUtil.isValidationFailed()) {
                selected = null; // Remove selection
                items = null;    // Invalidate list of items to trigger re-query.
            }
        } else {
            JsfUtil.addErrorMessage("OCURRIO UN ERROR ELIMINANDO EL INGRESO");
        }
    }

    public List<Ingreso> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

//    private void persist(PersistAction persistAction, String successMessage) {
//        if (selected != null) {
//            setEmbeddableKeys();
//            try {
//                if (persistAction != PersistAction.DELETE) {
//                    getFacade().edit(selected);
//                } else {
//                    getFacade().remove(selected);
//                }
//                JsfUtil.addSuccessMessage(successMessage);
//            } catch (EJBException ex) {
//                String msg = "";
//                Throwable cause = ex.getCause();
//                if (cause != null) {
//                    msg = cause.getLocalizedMessage();
//                }
//                if (msg.length() > 0) {
//                    JsfUtil.addErrorMessage(msg);
//                } else {
//                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("languages/Bundle").getString("PersistenceErrorOccured"));
//                }
//            } catch (Exception ex) {
//                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
//                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("languages/Bundle").getString("PersistenceErrorOccured"));
//            }
//        }
//    }
    
    public List<Ingreso> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Ingreso> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Ingreso.class)
    public static class IngresoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            IngresoController controller = (IngresoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "ingresoController");
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
            if (object instanceof Ingreso) {
                Ingreso o = (Ingreso) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Ingreso.class.getName()});
                return null;
            }
        }

    }

    public void addIngresoProducto() {
        if (ingresoProductoValido()) {

            boolean productoExiste = false;
            for (IngresoProducto ip : ingresoProductos) {
                if (ip.getProducto().getId().equals(producto.getId())) {
                    productoExiste = true;
                    ip.setCantidad(ip.getCantidad() + cantidad);

                    if (selected.getId() != null) {
                        ingresoProductosEditar.add(ip);
                    }
                    break;
                }
            }

            if (!productoExiste) {
                IngresoProducto ip = new IngresoProducto();

                ip.setId(ingresoProductos.size() + 9000000000L);
                ip.setProducto(producto);
                ip.setCantidad(cantidad);

                if (selected.getId() != null) {
                    ingresoProductosGuardar.add(ip);
                }

                ingresoProductos.add(ip);
            }

            producto = null;
            cantidad = 1;
        }

    }

    public void removeIngresoProducto(IngresoProducto ingresoProducto) {
        if (selected.getId() != null && !ingresoProductosGuardar.contains(ingresoProducto)) {
            ingresoProductosEliminar.add(ingresoProducto);
        }
        ingresoProductos.remove(ingresoProducto);
    }

    private boolean ingresoProductoValido() {
        boolean productoNulo = false, cantidadMayorCero = true;
        if (producto == null) {
            productoNulo = true;
            JsfUtil.addErrorMessage("El campo producto es obligatorio");
        }

        if (cantidad <= 0) {
            cantidadMayorCero = false;
            JsfUtil.addErrorMessage("el campo cantidad debe se mayor a cero");
        }
        return !productoNulo && cantidadMayorCero;
    }

}
