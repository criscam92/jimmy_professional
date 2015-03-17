package jp.controllers;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;
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
import jp.entidades.FacturaProducto;
import jp.entidades.FacturaPromocion;
import jp.entidades.Producto;
import jp.entidades.ProductoPromocionHelper;
import jp.entidades.Promocion;
import jp.facades.FacturaFacade;
import jp.facades.FacturaProductoFacade;
import jp.facades.RecargoFacade;
import jp.facades.TransactionFacade;
import jp.util.Moneda;
import jp.util.TipoPago;
import org.primefaces.event.SelectEvent;

@ManagedBean(name = "facturaController")
@ViewScoped
public class FacturaController implements Serializable {

    @EJB
    private jp.facades.FacturaFacade ejbFacade;
    @EJB
    private jp.facades.FacturaProductoFacade ejbFacturaProductoFacade;
    @EJB
    private jp.facades.RecargoFacade ejbRecargoFacade;
    @EJB
    private jp.facades.TransactionFacade ejbTransactionFacade;
    private List<Factura> items = null;
    private List<ProductoPromocionHelper> objects;
    private List<FacturaProducto> itemsProducto = null;
    private List<FacturaPromocion> itemsPromocion = null;
    private Factura selected;
    private ProductoPromocionHelper object;
    private FacturaProducto facturaProducto;
    private FacturaPromocion facturaPromocion;
    private int selectOneButton;
    private int moneda;
    private double precio;

    public FacturaController() {
        selectOneButton = 1;
        this.objects = new ArrayList<>();
        itemsProducto = new ArrayList<>();
        itemsPromocion = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        selected = new Factura();
        selected.setDescuento(0.0);
        object = new ProductoPromocionHelper();
        facturaProducto = new FacturaProducto();
        facturaPromocion = new FacturaPromocion();
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

    public ProductoPromocionHelper getObject() {
        return object;
    }

    public void setObject(ProductoPromocionHelper object) {
        this.object = object;
    }

    public FacturaProducto getFacturaProducto() {
        return facturaProducto;
    }

    public void setFacturaProducto(FacturaProducto facturaProducto) {
        this.facturaProducto = facturaProducto;
    }

    public FacturaPromocion getFacturaPromocion() {
        return facturaPromocion;
    }

    public void setFacturaPromocion(FacturaPromocion facturaPromocion) {
        this.facturaPromocion = facturaPromocion;
    }

    public List<ProductoPromocionHelper> getObjects() {
        return objects;
    }

    public void setObjects(List<ProductoPromocionHelper> objects) {
        this.objects = objects;
    }

    public List<FacturaProducto> getItemsProducto() {
        return itemsProducto;
    }

    public void setItemsProducto(List<FacturaProducto> itemsTMP) {
        this.itemsProducto = itemsTMP;
    }

    public List<FacturaPromocion> getItemsPromocion() {
        return itemsPromocion;
    }

    public void setItemsPromocion(List<FacturaPromocion> itemsPromocion) {
        this.itemsPromocion = itemsPromocion;
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

    public RecargoFacade getEjbRecargoFacade() {
        return ejbRecargoFacade;
    }

    public TransactionFacade getEjbTransactionFacade() {
        return ejbTransactionFacade;
    }

    public Factura prepareCreate() {
        selected = new Factura();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        if (objects.size() > 0) {
            try {
                getEjbTransactionFacade().createFacturaProductoPromocion(selected, itemsProducto, itemsPromocion);
                if (!JsfUtil.isValidationFailed()) {
                    items = null;    // Invalidate list of items to trigger re-query.
                    selected = new Factura();
                    itemsProducto.clear();
                    itemsPromocion.clear();
                    objects.clear();
                    redireccionarFormulario();
                } else {
                    JsfUtil.addErrorMessage("NO SE HA PODIDO GUARDAR LA PROMOCION");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            JsfUtil.addErrorMessage("La factura debe tener como minimo un producto");
        }

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
        object = new ProductoPromocionHelper();
        facturaProducto = new FacturaProducto();
        facturaPromocion = new FacturaPromocion();
        objects = new ArrayList<>();
        itemsProducto = new ArrayList<>();
        itemsPromocion = new ArrayList<>();
        return "Create.xhtml?faces-redirect=true";
    }

    public void addProductoOrPromocion(boolean isProducto) {
        if (isProducto) {
            System.out.println("ESTOY AGREGANDO UN PRODUCTO");
            if (facturaProducto.getProducto() != null && facturaProducto.getUnidadesVenta() > 0 && facturaProducto.getPrecio() > 0) {
                FacturaProducto facturaProductoTMP = new FacturaProducto();
                facturaProductoTMP.setProducto(facturaProducto.getProducto());
                facturaProductoTMP.setUnidadesVenta(facturaProducto.getUnidadesVenta());
                facturaProductoTMP.setUnidadesBonificacion(facturaProducto.getUnidadesBonificacion());
                facturaProductoTMP.setPrecio(facturaProducto.getPrecio() * facturaProducto.getUnidadesVenta());
                facturaProductoTMP.setId(itemsProducto.size() + 1l);

                itemsProducto.add(facturaProductoTMP);
                ProductoPromocionHelper pph = new ProductoPromocionHelper();
                pph.setProductoPromocion(facturaProductoTMP.getProducto());
                pph.setUnidadesVenta(facturaProductoTMP.getUnidadesVenta());
                pph.setUnidadesBonificacion(facturaProductoTMP.getUnidadesBonificacion());
                pph.setPrecio(facturaProductoTMP.getPrecio());
                objects.add(pph);
            }
        } else {
            System.out.println("ESTOY AGREGANDO UNA PROMOCION");
            if (facturaPromocion.getPromocion() != null && facturaPromocion.getUnidadesVenta() > 0 && facturaPromocion.getPrecio() > 0) {
                FacturaPromocion facturaPromocionTMP = new FacturaPromocion();
                facturaPromocionTMP.setPromocion(facturaPromocion.getPromocion());
                facturaPromocionTMP.setUnidadesVenta(facturaPromocion.getUnidadesVenta());
                facturaPromocionTMP.setUnidadesBonificacion(facturaPromocion.getUnidadesBonificacion());
                facturaPromocionTMP.setPrecio(facturaPromocion.getPrecio() * facturaPromocion.getUnidadesVenta());
                facturaPromocionTMP.setId(itemsPromocion.size() + 1l);

                itemsPromocion.add(facturaPromocionTMP);
                ProductoPromocionHelper pph = new ProductoPromocionHelper();
                pph.setProductoPromocion(facturaPromocionTMP.getPromocion());
                pph.setUnidadesVenta(facturaPromocionTMP.getUnidadesVenta());
                pph.setUnidadesBonificacion(facturaPromocionTMP.getUnidadesBonificacion());
                pph.setPrecio(facturaPromocionTMP.getPrecio());
                objects.add(pph);
            }
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

    public void changeView(AjaxBehaviorEvent event) {
//        LOGGER.info(this.getSelectOneButton() + "");
//        System.out.println("" + this.getSelectOneButton());
    }

    public void onItemSelectProducto(SelectEvent event) {
        Producto p = (Producto) event.getObject();

        if (moneda == 0) {
            precio = p.getValorVenta();
        } else {
            precio = p.getValorVentaUsd();
        }

        facturaProducto.setPrecio(precio);
    }

    public void onItemSelectPromocion(SelectEvent event) {
        Promocion p = (Promocion) event.getObject();
        precio = p.getValor();
        facturaPromocion.setPrecio(precio);
    }

}
