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
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import jp.entidades.Ciudad;
import jp.entidades.Cliente;
import jp.entidades.FacturaProducto;
import jp.facades.FacturaFacade;
import jp.facades.RecargoFacade;
import jp.util.TipoPago;

@ManagedBean(name = "facturaController")
@SessionScoped
public class FacturaController implements Serializable {

    @EJB
    private jp.facades.FacturaFacade ejbFacade;
    @EJB
    private jp.facades.RecargoFacade ejbRecargoFacade;
    private List<Factura> items = null;
    private List<FacturaProducto> itemsTMP = null;
    private Factura selected;
    private FacturaProducto facturaProducto;

    public FacturaController() {
        itemsTMP = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        facturaProducto = new FacturaProducto();
    }

    public Factura getSelected() {
        return selected;
    }

    public void setSelected(Factura selected) {
        this.selected = selected;
    }

    public FacturaProducto getFacturaProducto() {
        return facturaProducto;
    }

    public void setFacturaProducto(FacturaProducto facturaProducto) {
        this.facturaProducto = facturaProducto;
    }

    public List<FacturaProducto> getItemsTMP() {
        return itemsTMP;
    }

    public void setItemsTMP(List<FacturaProducto> itemsTMP) {
        this.itemsTMP = itemsTMP;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private FacturaFacade getFacade() {
        return ejbFacade;
    }

    public RecargoFacade getEjbRecargoFacade() {
        return ejbRecargoFacade;
    }

    public Factura prepareCreate() {
        selected = new Factura();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("languages/Bundle").getString("FacturaCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
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

    public Map<String, Integer> getTiposEstado() {
        return TipoPago.getMapaEstados();
    }

    public String redirectCreateFactura() {
        selected = new Factura();
        facturaProducto = new FacturaProducto();
        itemsTMP = new ArrayList<>();
        return "Create.xhtml?faces-redirect=true";
    }

    public void addFacturaProducto() {
        if (facturaProducto.getProducto() != null && facturaProducto.getUnidadesVenta() > 0
                && facturaProducto.getPrecio() > 0 && facturaProducto.getUnidadesVenta() > 0) {
            FacturaProducto facturaProductoTMP = new FacturaProducto();
            facturaProductoTMP.setProducto(facturaProducto.getProducto());
            facturaProductoTMP.setUnidadesVenta(facturaProducto.getUnidadesVenta());
            facturaProductoTMP.setUnidadesBonificacion(facturaProducto.getUnidadesBonificacion());
            facturaProductoTMP.setPrecio(facturaProducto.getPrecio());
            facturaProductoTMP.setId(itemsTMP.size() + 1l);

            itemsTMP.add(facturaProductoTMP);
        }
    }

    public void removeFacturaProducto(FacturaProducto facturaProductoArg) {
        itemsTMP.remove(facturaProductoArg);
    }

    public int getTotalUnidadVentas() {
        int sum = 0;
        for (FacturaProducto fp : itemsTMP) {
            sum += fp.getUnidadesVenta();
        }
        return sum;
    }

    public int getTotalUnidadBonificaciones() {
        int sum = 0;
        for (FacturaProducto fp : itemsTMP) {
            sum += fp.getUnidadesBonificacion();
        }
        return sum;
    }
    
    public double getTotalPrecioBruto() {
        int sum = 0;
        for (FacturaProducto fp : itemsTMP) {
            sum += fp.getPrecio();
        }
        return sum;
    }
    
    public double getTotalAPagar() {
        double sum = 0;
        double descuento = 0;
        if (selected.getDescuento() != null) {
            descuento = selected.getDescuento();
        }
        for (FacturaProducto fp : itemsTMP) {
            sum += fp.getPrecio();
        }
        descuento = sum * descuento / 100;
        return sum - descuento;
    }
    
    public void getDescuentobyCliente(Cliente c){
        if (c.getTarifaEspecial() != null) {
            selected.setDescuento(c.getTarifaEspecial().doubleValue());
        }else{
            Float descuento = getEjbRecargoFacade().getRecargoByCiudad(c.getCiudad());
            selected.setDescuento(descuento.doubleValue());
        }
    }
}
