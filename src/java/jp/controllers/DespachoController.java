package jp.controllers;

import jp.entidades.Despacho;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;
import jp.facades.DespachoFacade;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import jp.entidades.Factura;
import jp.entidades.FacturaProducto;
import jp.entidades.FacturaPromocion;
import jp.entidades.Producto;
import jp.entidades.PromocionProducto;
import jp.entidades.auxiliar.ProductoAux;
import jp.facades.FacturaFacade;
import jp.facades.ProductoFacade;
import jp.facades.PromocionFacade;

@ManagedBean(name = "despachoController")
@SessionScoped
public class DespachoController implements Serializable {

    @EJB
    private DespachoFacade ejbFacade;
    @EJB
    private FacturaFacade facturaFacade;
    @EJB
    private ProductoFacade productoFacade;
    @EJB
    private PromocionFacade promocionFacade;

    private List<Despacho> items = null;
    private Despacho selected;
    private Factura factura;
    private List<ProductoAux> productoAuxiliar = null;

    public DespachoController() {
        productoAuxiliar = new ArrayList<>();
        factura = new Factura();
    }

    @PostConstruct
    public void init() {
        getListProductos();
    }

    public Despacho getSelected() {
        return selected;
    }

    public void setSelected(Despacho selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private DespachoFacade getFacade() {
        return ejbFacade;
    }

    public Despacho prepareCreate() {
        selected = new Despacho();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("DespachoCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("DespachoUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("DespachoDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Despacho> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public List<ProductoAux> getProductoAuxiliar() {
        return productoAuxiliar;
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

    public List<Despacho> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Despacho> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    private void getListProductos() {
        Map<String, String> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        Long idFactura = Long.parseLong(requestMap.get("factura"));

        factura = facturaFacade.getFacturaById(idFactura);

        Map<Producto, Integer> mapProductos = new HashMap<>();
        if (factura != null) {
            List<FacturaProducto> facturaProductos = productoFacade.getFacturaProductosByFactura(factura);
            List<FacturaPromocion> facturaPromociones = promocionFacade.getFacturaPromocionByFactura(factura);

            for (FacturaProducto fp : facturaProductos) {
                mapProductos.put(fp.getProducto(), fp.getUnidadesVenta() + fp.getUnidadesBonificacion());
            }

            for (FacturaPromocion fp : facturaPromociones) {
                int cantidadPromociones = fp.getUnidadesVenta() + fp.getUnidadesBonificacion();
                for (PromocionProducto pp : fp.getPromocion().getPromocionProductoList()) {
                    if (mapProductos.containsKey(pp.getProducto())) {
                        mapProductos.put(pp.getProducto(), mapProductos.get(pp.getProducto()) + (pp.getCantidad() * cantidadPromociones));
                    } else {
                        mapProductos.put(pp.getProducto(), pp.getCantidad() * cantidadPromociones);
                    }
                }
            }

            for (Map.Entry<Producto, Integer> entrySet : mapProductos.entrySet()) {
                productoAuxiliar.add(new ProductoAux(productoAuxiliar.size() + 1, entrySet.getKey(), entrySet.getValue()));
            }
        }
    }

    @FacesConverter(forClass = Despacho.class)
    public static class DespachoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            DespachoController controller = (DespachoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "despachoController");
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
            if (object instanceof Despacho) {
                Despacho o = (Despacho) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Despacho.class.getName()});
                return null;
            }
        }

    }

}
