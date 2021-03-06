package jp.controllers;

import jp.entidades.DespachoFactura;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;
import jp.facades.DespachoFacturaFacade;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
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
import javax.inject.Inject;
import jp.entidades.DespachoFacturaProducto;
import jp.entidades.Factura;
import jp.entidades.FacturaProducto;
import jp.entidades.FacturaPromocion;
import jp.entidades.Producto;
import jp.entidades.ProductoHelper;
import jp.entidades.PromocionProducto;
import jp.facades.DespachoFacturaProductoFacade;
import jp.facades.FacturaFacade;
import jp.facades.FacturaPromocionFacade;
import jp.facades.ProductoFacade;
import jp.facades.PromocionProductoFacade;
import jp.facades.TransactionFacade;
import jp.seguridad.UsuarioActual;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "despachoFacturaController")
@ViewScoped
public class DespachoFacturaController implements Serializable {

//<editor-fold defaultstate="collapsed" desc="EJB's">
    @EJB
    private DespachoFacturaFacade ejbFacade;
    @EJB
    private ProductoFacade productoFacade;
    @EJB
    private FacturaFacade facturaFacade;
    @EJB
    private TransactionFacade transactionFacade;
    @EJB
    private DespachoFacturaFacade despachoFacturaFacade;
    @EJB
    private FacturaPromocionFacade facturaPromocionFacade;
    @EJB
    private PromocionProductoFacade promocionProductoFacade;
    @EJB
    private DespachoFacturaProductoFacade despachoFacturaProductoFacade;
    @Inject
    private UsuarioActual usuarioActual;

//</editor-fold>
    private List<DespachoFactura> items = null;
    private DespachoFactura selected;
    private List<ProductoHelper> productoHelpers;
    private Factura factura;
    boolean sePuedeDespachar;

    public DespachoFacturaController() {
        productoHelpers = new ArrayList<>();
        factura = new Factura();
        selected = new DespachoFactura();
    }

    @PostConstruct
    public void init() {
        Map<String, String> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        try {
            FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            String ordenPedido = requestMap.get("fac");
            factura = new Factura();
            factura = getFacturaFacade().getFacturaByOrdenPedido(ordenPedido);
            selected.setFactura(factura);

            List<FacturaProducto> facturaProductos = getFacturaFacade().getFacturaProductoByFactura(factura);
            for (FacturaProducto fp : facturaProductos) {
                ProductoHelper ph = new ProductoHelper(productoHelpers.size() + 1, fp.getProducto(), (fp.getUnidadesVenta() + fp.getUnidadesBonificacion()));
                productoHelpers.add(ph);
            }

            List<FacturaPromocion> facturaPromociones = getFacturaPromocionFacade().getFacturaPromocionByFactura(factura);
            List<ProductoHelper> productoHelpersTMP = new ArrayList<>();
            for (FacturaPromocion fp : facturaPromociones) {
                List<PromocionProducto> promocionProductos = getPromocionProductoFacade().getPromocionProductoByProducto(fp.getPromocion());
                for (PromocionProducto pp : promocionProductos) {
                    ProductoHelper ph = new ProductoHelper(productoHelpers.size() + 1, pp.getProducto(), (fp.getUnidadesVenta() + fp.getUnidadesBonificacion()) * (pp.getCantidad()));
                    productoHelpersTMP.add(ph);
                }
            }

            for (ProductoHelper phTMP : productoHelpersTMP) {
                boolean existe = false;
                for (ProductoHelper ph : productoHelpers) {
                    if (ph.getProducto().getId().equals(phTMP.getProducto().getId())) {
                        ph.setCantidadFacturada(phTMP.getCantidadFacturada() + ph.getCantidadFacturada());
                        existe = true;
                    }
                }

                if (!existe) {
                    productoHelpers.add(phTMP);
                }
            }

            for (ProductoHelper ph : productoHelpers) {
                ph.setCantidadDisponible(getProductoFacade().getCantidadDisponibleByProducto(ph.getProducto()));
                ph.setCantidadDespachada(getDespachosByProducto(ph.getProducto(), factura));
                ph.obtenerCantidadADespachar();
            }

            comprobarProductos();
        } catch (Exception e) {
            System.out.println("==========ERROR==========");
            e.printStackTrace();
            System.out.println("==========ERROR==========");
        }

    }

    public int cantidadMaxima(ProductoHelper ph) {
        int cantMax = ph.getCantidadFacturada() - ph.getCantidadDespachada();
        return cantMax > ph.getCantidadDisponible() ? ph.getCantidadDisponible() : cantMax;
    }

//<editor-fold defaultstate="collapsed" desc="Getter EJB's">
    private DespachoFacturaFacade getFacade() {
        return ejbFacade;
    }

    public FacturaFacade getFacturaFacade() {
        return facturaFacade;
    }

    public DespachoFactura getSelected() {
        return selected;
    }

    public TransactionFacade getTransactionFacade() {
        return transactionFacade;
    }

    public DespachoFacturaFacade getDespachoFacturaFacade() {
        return despachoFacturaFacade;
    }

    public FacturaPromocionFacade getFacturaPromocionFacade() {
        return facturaPromocionFacade;
    }

    public PromocionProductoFacade getPromocionProductoFacade() {
        return promocionProductoFacade;
    }

    public DespachoFacturaProductoFacade getDespachoFacturaProductoFacade() {
        return despachoFacturaProductoFacade;
    }

    public ProductoFacade getProductoFacade() {
        return productoFacade;
    }

    public void setProductoFacade(ProductoFacade productoFacade) {
        this.productoFacade = productoFacade;
    }
//</editor-fold>

    public List<ProductoHelper> getProductoHelpers() {
        return productoHelpers;
    }

    public void setProductoHelpers(List<ProductoHelper> productoHelpers) {
        this.productoHelpers = productoHelpers;
    }

    public void setSelected(DespachoFactura selected) {
        this.selected = selected;
    }

    public boolean isSePuedeDespachar() {
        return sePuedeDespachar;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    public DespachoFactura prepareCreate() {
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        selected.setDespacho(null);
        selected.setFecha(Calendar.getInstance().getTime());
        selected.setRealizado(true);
        selected.setUsuario(usuarioActual.getUsuario());

        switch (comprobarIngresos()) {
            case 0:
                if (comprobarProductos()) {
                    getTransactionFacade().createDespachoFactura(selected, productoHelpers);
                    if (!JsfUtil.isValidationFailed()) {
                        items = null;
                        selected = null;
                        productoHelpers.clear();
                        JsfUtil.addSuccessMessage("La factura " + factura.getOrdenPedido() + " se ha despachado correctamente");
                        JsfUtil.redirect("List.xhtml");
                    }
                } else {
                    RequestContext.getCurrentInstance().update("DespacharForm:btnDespachar");
                }
                break;
            default:
                JsfUtil.addErrorMessage("Ocurrio un error despachando la factura "
                        + factura.getOrdenPedido() + ", verifique los datos y vuelva a despacharla");
                break;
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("DespachoFacturaUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("DespachoFacturaDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<DespachoFactura> getItems() {
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

    public List<DespachoFactura> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<DespachoFactura> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    /**
     *
     * @return 0 - La factura ya fue despachada, 1 - Ingresos validos, 2 - Otro
     * usuario hizo un despacho al mismo tiempo y a la misma factura
     */
    private Integer comprobarIngresos() {

        boolean ingresosValidos = true;
        boolean productosDespachados = true;

        for (ProductoHelper ph : productoHelpers) {
            ph.setCantidadDisponible(getProductoFacade().getCantidadDisponibleByProducto(ph.getProducto()));
            ph.setCantidadDespachada(getDespachosByProducto(ph.getProducto(), factura));
        }

        for (ProductoHelper ph : productoHelpers) {
            if (ph.getCantidadFacturada() > ph.getCantidadDespachada()) {
                productosDespachados = false;
                break;
            }
        }

        if (productosDespachados) {
            return null;
        }

        for (ProductoHelper ph : productoHelpers) {
            int cantidadPendiente = ph.getCantidadFacturada() - ph.getCantidadDespachada();
            if (ph.getCantidadADespachar() > cantidadPendiente && ph.getCantidadDisponible() < ph.getCantidadADespachar()) {
                ingresosValidos = false;
                break;
            }
        }

        if (ingresosValidos) {
            return 0;
        }

        return null;
    }

    @FacesConverter(forClass = DespachoFactura.class)
    public static class DespachoFacturaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            DespachoFacturaController controller = (DespachoFacturaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "despachoFacturaController");
            return controller.getFacade().find(getKey(value));
        }

        java.lang.Long getKey(String value) {
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
            if (object instanceof DespachoFactura) {
                DespachoFactura o = (DespachoFactura) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), DespachoFactura.class.getName()});
                return null;
            }
        }

    }

    private int getDespachosByProducto(Producto producto, Factura factura) {
        int cantDespachada = 0;

        List<DespachoFactura> despachoFacturas = getDespachoFacturaFacade().getDespachosFacturaByFactura(factura, true);
        for (DespachoFactura df : despachoFacturas) {

            List<DespachoFacturaProducto> despachoFacturaProductos = getDespachoFacturaProductoFacade().getDespachosFacturaProductoByDespachoFactura(df);
            for (DespachoFacturaProducto dfp : despachoFacturaProductos) {
                if (dfp.getProducto().getId().equals(producto.getId())) {
                    cantDespachada += dfp.getCantidad();
                }
            }
        }
        return cantDespachada;
    }

    public boolean comprobarProductos() {
        sePuedeDespachar = false;
        for (ProductoHelper ph : productoHelpers) {
            if (ph.getCantidadADespachar() > 0) {
                sePuedeDespachar = true;
                break;
            }
        }
        return sePuedeDespachar;
    }

}
