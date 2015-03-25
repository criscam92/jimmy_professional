package jp.controllers;

import jp.entidades.DespachoFactura;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;
import jp.facades.DespachoFacturaFacade;
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
import jp.entidades.Factura;
import jp.entidades.FacturaProducto;
import jp.entidades.FacturaPromocion;
import jp.entidades.ProductoHelper;
import jp.entidades.PromocionProducto;
import jp.facades.FacturaFacade;
import jp.facades.FacturaProductoFacade;

@ManagedBean(name = "despachoFacturaController")
@ViewScoped
public class DespachoFacturaController implements Serializable {

    @EJB
    private DespachoFacturaFacade ejbFacade;
    @EJB
    private FacturaFacade facturaFacade;
    @EJB
    private FacturaProductoFacade facturaProductoFacade;
    private List<DespachoFactura> items = null;
    private DespachoFactura selected;
    private List<ProductoHelper> productoHelpers;
    private Factura factura;
    private boolean despachos;

    public DespachoFacturaController() {
        productoHelpers = new ArrayList<>();
        factura = new Factura();
    }

    @PostConstruct
    public void init() {
        Map<String, String> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        try {
            FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            String ordenPedido = requestMap.get("fac");
            factura = getFacturaFacade().getFacturaByOrdenPedido(ordenPedido);
            despachos = factura.getDespachoFacturaList().size() > 0;

            for (FacturaProducto fp : factura.getFacturaProductoList()) {
                ProductoHelper ph = new ProductoHelper(productoHelpers.size() + 1, fp.getProducto(), (fp.getUnidadesVenta() + fp.getUnidadesBonificacion()), factura, despachos);
                productoHelpers.add(ph);
            }

            for (FacturaPromocion fp : factura.getFacturaPromocionList()) {
                for (PromocionProducto pp : fp.getPromocion().getPromocionProductoList()) {
                    for (ProductoHelper phr : productoHelpers) {
                        if (phr.getProducto().getId().equals(pp.getProducto().getId())) {
                            int index = productoHelpers.indexOf(phr);
                            productoHelpers.get(index).setCantidadFacturada(phr.getCantidadFacturada() + ((fp.getUnidadesVenta() + fp.getUnidadesBonificacion()) * (pp.getCantidad())));
                        } else {
                            ProductoHelper ph = new ProductoHelper(productoHelpers.size() + 1, pp.getProducto(), (fp.getUnidadesVenta() + fp.getUnidadesBonificacion()) * (pp.getCantidad()), factura, despachos);
                            productoHelpers.add(ph);
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("==========ERROR==========");
            e.printStackTrace();
            System.out.println("==========ERROR==========");
        }

    }

    public boolean isDespachos() {
        return despachos;
    }

    public void setDespachos(boolean despachos) {
        this.despachos = despachos;
    }

    public int cantidadMaxima(ProductoHelper ph) {
        int cantMax = ph.getCantidadFacturada() - ph.getCantidadDespachada();
        return cantMax > ph.getCantidadDisponible() ? ph.getCantidadDisponible() : cantMax;
    }

    public FacturaProductoFacade getFacturaProductoFacade() {
        return facturaProductoFacade;
    }

    public List<ProductoHelper> getProductoHelpers() {
        return productoHelpers;
    }

    public void setProductoHelpers(List<ProductoHelper> productoHelpers) {
        this.productoHelpers = productoHelpers;
    }

    public FacturaFacade getFacturaFacade() {
        return facturaFacade;
    }

    public DespachoFactura getSelected() {
        return selected;
    }

    public void setSelected(DespachoFactura selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private DespachoFacturaFacade getFacade() {
        return ejbFacade;
    }

    public DespachoFactura prepareCreate() {
        selected = new DespachoFactura();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("DespachoFacturaCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
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
            if (object instanceof DespachoFactura) {
                DespachoFactura o = (DespachoFactura) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), DespachoFactura.class.getName()});
                return null;
            }
        }

    }

}
