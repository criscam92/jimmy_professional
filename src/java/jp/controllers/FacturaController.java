package jp.controllers;

import jp.entidades.Factura;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.AjaxBehaviorEvent;
import jp.entidades.Cliente;
import jp.entidades.Empleado;
import jp.entidades.Producto;
import jp.entidades.ProductoPromocionHelper;
import jp.entidades.Promocion;
import jp.entidades.Talonario;
import jp.facades.FacturaFacade;
import jp.facades.FacturaProductoFacade;
import jp.facades.ParametrosFacade;
import jp.facades.TalonarioFacade;
import jp.facades.TransactionFacade;
import jp.util.Moneda;
import jp.util.TipoPago;
import org.primefaces.event.SelectEvent;

@ManagedBean(name = "facturaController")
@ViewScoped
public class FacturaController implements Serializable {
    
    @EJB
    private FacturaFacade ejbFacade;
    @EJB
    private FacturaProductoFacade ejbFacturaProductoFacade;
    @EJB
    private ParametrosFacade ejbParametrosFacade;
    @EJB
    private TransactionFacade ejbTransactionFacade;
    @EJB
    private TalonarioFacade talonarioFacade;
    
    private List<Factura> items = null;
    private List<ProductoPromocionHelper> objects;
    private Factura selected;
    private double precio;
    private int moneda, selectOneButton, unidadesVenta, unidadesBonificacion;
    private Producto producto;
    private Promocion promocion;
    private final String uiError = "ui-state-error";
    private String errorProducto, errorPromocion, errorVenta, errorBonificacion;
    
    public FacturaController() {
        selectOneButton = 1;
        objects = new ArrayList<>();
    }
    
    @PostConstruct
    public void init() {
        selected = new Factura();
        selected.setDescuento(0.0);
    }
    
    public String getErrorProducto() {
        return errorProducto;
    }
    
    public String getErrorPromocion() {
        return errorPromocion;
    }
    
    public String getErrorVenta() {
        return errorVenta;
    }
    
    public String getErrorBonificacion() {
        return errorBonificacion;
    }
    
    public TalonarioFacade getTalonarioFacade() {
        return talonarioFacade;
    }
    
    public int getUnidadesVenta() {
        return unidadesVenta;
    }
    
    public void setUnidadesVenta(int unidadesVenta) {
        this.unidadesVenta = unidadesVenta;
    }
    
    public int getUnidadesBonificacion() {
        return unidadesBonificacion;
    }
    
    public void setUnidadesBonificacion(int unidadesBonificacion) {
        this.unidadesBonificacion = unidadesBonificacion;
    }
    
    public double getPrecio() {
        return precio;
    }
    
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    
    public int getMoneda() {
        return moneda;
    }
    
    public void setMoneda(int moneda) {
        this.moneda = moneda;
    }
    
    public int getSelectOneButton() {
        return selectOneButton;
    }
    
    public void setSelectOneButton(int selectOneButton) {
        this.selectOneButton = selectOneButton;
    }
    
    public Factura getSelected() {
        return selected;
    }
    
    public void setSelected(Factura selected) {
        this.selected = selected;
    }
    
    public List<ProductoPromocionHelper> getObjects() {
        return objects;
    }
    
    public void setObjects(List<ProductoPromocionHelper> objects) {
        this.objects = objects;
    }
    
    protected void setEmbeddableKeys() {
    }
    
    protected void initializeEmbeddableKey() {
    }
    
    private FacturaFacade getFacade() {
        return ejbFacade;
    }
    
    public FacturaProductoFacade getEjbFacturaProductoFacade() {
        return ejbFacturaProductoFacade;
    }
    
    public ParametrosFacade getEjbRecargoFacade() {
        return ejbParametrosFacade;
    }
    
    public TransactionFacade getEjbTransactionFacade() {
        return ejbTransactionFacade;
    }
    
    public Producto getProducto() {
        return producto;
    }
    
    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    
    public Promocion getPromocion() {
        return promocion;
    }
    
    public void setPromocion(Promocion promocion) {
        this.promocion = promocion;
    }
    
    public Factura prepareCreate() {
        selected = new Factura();
        initializeEmbeddableKey();
        return selected;
    }
    
    public String create(boolean despachar) {
        if (objects.size() > 0) {
            
            String opTMP = selected.getOrdenPedido();
            
            if (getFacade().getFacturaByOrdenPedido(opTMP) == null) {
                try {
                    getEjbTransactionFacade().createFacturaProductoPromocion(selected, objects);
                    if (!JsfUtil.isValidationFailed()) {
                        clean();
                        if (despachar) {                            
                            return "Despacho.xhtml?fac=" + opTMP + "&faces-redirect=true";
                        } else {
                            redireccionarFormulario();
                        }
                    } else {
                        JsfUtil.addErrorMessage("NO SE HA PODIDO GUARDAR LA PROMOCION");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
            } else {
                JsfUtil.addErrorMessage("El pedido " + selected.getOrdenPedido() + " ya existe");
            }
        } else {
            JsfUtil.addErrorMessage("La factura debe tener como minimo un producto");
        }
        return "";
    }
    
    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("languages/Bundle").getString("FacturaUpdated"));
    }
    
    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("languages/Bundle").getString("FacturaDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }
    
    public List<Factura> getItems() {
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
    
    public List<Factura> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }
    
    public List<Factura> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }
    
    private void cleanProductoOrPromocion() {
        producto = null;
        promocion = null;
        unidadesBonificacion = 0;
        unidadesVenta = 0;
        precio = 0;
    }
    
    private void getMensajesError() {
        if (getSelectOneButton() == 1) {
            if (producto == null) {
                errorProducto = uiError;
                JsfUtil.addErrorMessage("Debe seleccionar un producto valido");
            } else {
                if (precio <= 0.0) {
                    errorProducto = uiError;
                    JsfUtil.addErrorMessage("Debe seleccionar un producto con un precio valido");
                } else {
                    errorProducto = "";
                }
            }
        } else {
            if (promocion == null) {
                errorPromocion = uiError;
                JsfUtil.addErrorMessage("Debe seleccionar una promocion valida");
            } else {
                if (precio <= 0.0) {
                    errorPromocion = uiError;
                    JsfUtil.addErrorMessage("Debe seleccionar una promocion con un precio valido");
                } else {
                    errorProducto = "";
                }
            }
        }
        
        if (unidadesVenta < 1) {
            errorVenta = uiError;
            JsfUtil.addErrorMessage("El campo venta debe ser mayor a 0");
        } else {
            errorVenta = "";
        }
        
        if (unidadesBonificacion < 0) {
            errorBonificacion = uiError;
            JsfUtil.addErrorMessage("El campo bonificación debe ser mayor a 0");
        } else {
            errorBonificacion = "";
        }
    }
    
    @FacesConverter(forClass = Factura.class)
    public static class FacturaControllerConverter implements Converter {
        
        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            FacturaController controller = (FacturaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "facturaController");
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
            if (object instanceof Factura) {
                Factura o = (Factura) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Factura.class.getName()});
                return null;
            }
        }
    }
    
    public Map<String, Integer> getTiposPagos() {
        return TipoPago.getMapaEstados();
    }
    
    public Map<String, Integer> getMonedas() {
        return Moneda.getMonedas();
    }
    
    public String redirectCreateFactura() {
        selected = new Factura();
        objects = new ArrayList<>();
        return "Create.xhtml?faces-redirect=true";
    }
    
    public void addProductoOrPromocion() {
        if ((producto != null || promocion != null) && unidadesVenta > 0 && precio > 0) {
            boolean isProducto = producto != null;
            
            boolean existe = false;
            for (ProductoPromocionHelper pph : objects) {
                if ((pph.isProducto() && ((Producto) pph.getProductoPromocion()).getId().equals(producto.getId()))
                        || (!pph.isProducto() && ((Promocion) pph.getProductoPromocion()).getId().equals(promocion.getId()))) {
                    existe = true;
                    int io = objects.indexOf(pph);
                    objects.get(io).setPrecio((precio * unidadesVenta) + pph.getPrecio());
                    objects.get(io).setUnidadesBonificacion(unidadesBonificacion + pph.getUnidadesBonificacion());
                    objects.get(io).setUnidadesVenta(unidadesVenta + pph.getUnidadesVenta());
                    break;
                }
            }
            
            if (!existe) {
                ProductoPromocionHelper pph = new ProductoPromocionHelper(objects.size() + 1L, unidadesVenta, unidadesBonificacion,
                        (precio * unidadesVenta), isProducto ? producto : promocion, isProducto);
                objects.add(pph);
            }
            
            cleanProductoOrPromocion();
        } else {
            getMensajesError();
        }
    }
    
    public void removeFacturaProductoPromocion(ProductoPromocionHelper pph) {
        objects.remove(pph);
    }
    
    public int getTotalUnidadVentas() {
        int sum = 0;
        for (ProductoPromocionHelper fp : objects) {
            sum += fp.getUnidadesVenta();
        }
        return sum;
    }
    
    public int getTotalUnidadBonificaciones() {
        int sum = 0;
        for (ProductoPromocionHelper fp : objects) {
            sum += fp.getUnidadesBonificacion();
        }
        return sum;
    }
    
    public double getTotalPrecioBruto() {
        int sum = 0;
        for (ProductoPromocionHelper fp : objects) {
            sum += fp.getPrecio();
        }
        if (selected != null) {
            selected.setTotalBruto(sum);
        }
        return sum;
    }
    
    public double getTotalAPagar() {
        double sum = 0;
        double descuento = 0;
        if (selected.getDescuento() != null) {
            descuento = selected.getDescuento();
        }
        
        for (ProductoPromocionHelper fp : objects) {
            sum += fp.getPrecio();
        }
        
        descuento = sum * descuento / 100;
        if (selected != null) {
            selected.setTotalPagar(sum - descuento);
        }
        return sum - descuento;
    }
    
    public void getDescuentobyCliente(Cliente c) {
        if (c.getTarifaEspecial() != null) {
            selected.setDescuento(c.getTarifaEspecial().doubleValue());
        } else {
            Float descuento = getEjbRecargoFacade().getParametrosByCiudad(c.getCiudad());
            selected.setDescuento(descuento.doubleValue());
        }
    }
    
    public String getTipoPagofactura(int tipo) {
        return TipoPago.getFromValue(tipo).getDetalle();
    }
    
    public Long getCantidadVentasByFactura(Factura f) {
        return getEjbFacturaProductoFacade().getCantidadVentasByFactura(f);
    }
    
    public Long getCantidadBonificacionesByFactura(Factura f) {
        return getEjbFacturaProductoFacade().getCantidadBonificacionByFactura(f);
    }
    
    public void redireccionarFormulario() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("List.xhtml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public List<Promocion> llenarPromocion(String query) {
        return getFacade().getPromocionByQuery(query);
    }
    
    public void onItemSelectProducto(SelectEvent event) {
        Producto p = (Producto) event.getObject();
        if (moneda == 0) {
            precio = p.getValorVenta();
        } else {
            precio = p.getValorVentaUsd();
        }
    }
    
    public void onItemSelectPromocion(SelectEvent event) {
        Promocion p = (Promocion) event.getObject();
        precio = p.getValor();
    }
    
    public void onItemSelectCliente(SelectEvent event) {
        Cliente c = (Cliente) event.getObject();
        getDescuentobyCliente(c);
    }
    
    public void onItemSelectEmpleado(SelectEvent event) {
        Empleado e = (Empleado) event.getObject();
        Talonario t = getTalonarioFacade().getTalonarioByFecha(e);
        selected.setOrdenPedido(t != null ? ("" + t.getActual()) : "0");
    }
    
    private void clean() {
        items = null;
        selected = null;
        objects.clear();
        cleanProductoOrPromocion();
    }
    
    public void changeView(AjaxBehaviorEvent event) {
        cleanProductoOrPromocion();
    }
    
}
