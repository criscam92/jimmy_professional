package jp.controllers;

import jp.entidades.Promocion;
import jp.util.JsfUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import jp.entidades.Producto;
import jp.entidades.PromocionProducto;
import jp.facades.ProductoFacade;
import jp.facades.PromocionFacade;
import jp.facades.TransactionFacade;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

@ManagedBean(name = "promocionController")
@SessionScoped
public class PromocionController implements Serializable {

    @EJB
    private PromocionFacade ejbFacade;
    @EJB
    private TransactionFacade transactionFacade;
    @EJB
    private ProductoFacade productoFacade;
    private List<Promocion> items = null;
    private Promocion selected;
    private Producto producto;
    private int cantidad;
    private List<PromocionProducto> promocionProductos = null;
    private final List<PromocionProducto> promocionProductosEliminar;
    private final List<PromocionProducto> promocionProductosGuardar;
    private final List<PromocionProducto> promocionProductosEditar;
    private final String uiError;
    private String header, error;

    public PromocionController() {
        uiError = "ui-state-error";
        promocionProductos = new ArrayList<>();
        promocionProductosEliminar = new ArrayList<>();
        promocionProductosGuardar = new ArrayList<>();
        promocionProductosEditar = new ArrayList<>();
        cantidad = 1;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
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

    public ProductoFacade getProductoFacade() {
        return productoFacade;
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
        setError("");
        promocionProductos = new ArrayList<>();
        setHeader(JsfUtil.getMessageBundle("CreatePromocionTitle"));
        initializeEmbeddableKey();
        return selected;
    }

    public void preparaEditar() {
        promocionProductos = getFacade().getProductosByPromocion(selected);
        setHeader(JsfUtil.getMessageBundle("EditPromocionTitle"));
    }

    public void create(boolean guardar) {
        if (!getFacade().getEntityByCodigoOrTipo(selected)) {
            if (promocionProductos.size() >= 1) {
                if (getTransactionFacade().createPromocion(selected, promocionProductos)) {
                    if (!JsfUtil.isValidationFailed()) {
                        items = null;    // Invalidate list of items to trigger re-query.
                        selected = new Promocion();
                        producto = null;
                        cantidad = 1;
                        promocionProductos.clear();
                        if (guardar) {
                            RequestContext.getCurrentInstance().execute("PF('PromocionCreateDialog').hide()");
                        }
                        setError("");
                        JsfUtil.addSuccessMessage(JsfUtil.getMessageBundle(new String[]{"MessagePromocion", "CreateSuccessF"}));
                    }
                } else {
                    JsfUtil.addErrorMessage("No se ha podido guardar la Promoción");
                }

            } else {
                JsfUtil.addErrorMessage("La promocion debe tener como mínimo un producto");
            }
        } else {
            setError(uiError);
            JsfUtil.addErrorMessage("Ya existe el Código " + selected.getCodigo());
        }
    }

    public void update() {
        if (!getFacade().getEntityByCodigoOrTipo(selected)) {
            if (promocionProductos.size() >= 1) {
                if (getTransactionFacade().updatePromocion(selected, promocionProductosGuardar, promocionProductosEliminar, promocionProductosEditar)) {
                    if (!JsfUtil.isValidationFailed()) {
                        items = null;    // Invalidate list of items to trigger re-query.
                        selected = new Promocion();
                        producto = null;
                        cantidad = 1;
                        promocionProductos.clear();
                        promocionProductosGuardar.clear();
                        promocionProductosEliminar.clear();
                        promocionProductosEditar.clear();
                        setError("");
                        RequestContext.getCurrentInstance().execute("PF('PromocionCreateDialog').hide()");
                        JsfUtil.addSuccessMessage(JsfUtil.getMessageBundle(new String[]{"MessagePromocion", "UpdateSuccessF"}));
                    }
                } else {
                    JsfUtil.addErrorMessage("No se ha podido actualizar la Promoción");
                }

            } else {
                JsfUtil.addErrorMessage("La promoción debe tener como mínimo un producto");
            }
        } else {
            items = null;
            setError(uiError);
            JsfUtil.addErrorMessage("Ya existe el código " + selected.getCodigo());
        }
    }

    public void destroy() {
        if (getTransactionFacade().deletePromocion(selected)) {
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

    public List<Promocion> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Promocion> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    private boolean promocionProductoValido() {
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

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @FacesConverter(forClass = Promocion.class, value = "promocionconverter")
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

    public int cantidadTotal() {
        int sum = 0;
        for (PromocionProducto pp : promocionProductos) {
            sum += pp.getCantidad();
        }
        return sum;
    }

    public double precioTotal() {
        double sum = 0.0;
        for (PromocionProducto pp : promocionProductos) {
            sum += (pp.getCantidad() * pp.getProducto().getValorVenta());
        }
        return sum;
    }

    public double precioUsTotal() {
        double sum = 0.0;
        for (PromocionProducto pp : promocionProductos) {
            sum += (pp.getCantidad() * pp.getProducto().getValorVentaUsd());
        }
        return sum;
    }

    public void addPromocionProducto() {
        if (promocionProductoValido()) {

            boolean productoExiste = false;
            for (PromocionProducto ppTMP : promocionProductos) {
                if (ppTMP.getProducto().getId().equals(producto.getId())) {
                    productoExiste = true;
                    ppTMP.setCantidad(ppTMP.getCantidad() + cantidad);

                    if (selected.getId() != null) {
                        promocionProductosEditar.add(ppTMP);
                    }
                    break;
                }
            }

            if (!productoExiste) {
                PromocionProducto pp = new PromocionProducto();

                pp.setId(promocionProductos.size() + 1L);
                pp.setProducto(producto);
                pp.setCantidad(cantidad);

                if (selected.getId() != null) {
                    promocionProductosGuardar.add(pp);
                }

                promocionProductos.add(pp);
            }

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

    /**
     * @param promocionProducto Entity de la que desea obtener el valor
     * @param tipo indica el valor que desea obtener. 1 - Valor (Pesos) y 2
     * Valor (US)
     * @return Retorna el valor seleccionado
     */
    public double getValor(PromocionProducto promocionProducto, int tipo) {
        return tipo == 1 ? (promocionProducto.getCantidad() * promocionProducto.getProducto().getValorVenta())
                : (promocionProducto.getCantidad() * promocionProducto.getProducto().getValorVentaUsd());
    }

}
