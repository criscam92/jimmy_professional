package jp.controllers;

import jp.entidades.Devolucion;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
import jp.entidades.DevolucionProducto;
import jp.facades.DevolucionFacade;
import jp.facades.TransactionFacade;
import jp.util.Moneda;

@ManagedBean(name = "devolucionController")
@ViewScoped
public class DevolucionController implements Serializable {

    @EJB
    private jp.facades.DevolucionFacade ejbFacade;
    @EJB
    private jp.facades.TransactionFacade ejbTransactionFacade;
    @EJB
    private DevolucionSessionBean devolucionSessionBean;
    private List<Devolucion> items = null;
    private Devolucion selected;
    private DevolucionProducto devolucionProducto;
    private List<DevolucionProducto> itemsTMP;
    private Float valorAcumulado = 0.0f;
    

    public DevolucionController() {
        itemsTMP = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        selected = new Devolucion();
        selected.setValorTotal(valorAcumulado);
        devolucionProducto = new DevolucionProducto();
    }

    public Devolucion getSelected() {
        return selected;
    }

    public void setSelected(Devolucion selected) {
        this.selected = selected;
    }

    public DevolucionProducto getDevolucionProducto() {
        return devolucionProducto;
    }

    public void setDevolucionProducto(DevolucionProducto devolucionProducto) {
        this.devolucionProducto = devolucionProducto;
    }

    public List<DevolucionProducto> getItemsTMP() {
        return itemsTMP;
    }

    public void setItemsTMP(List<DevolucionProducto> itemsTMP) {
        this.itemsTMP = itemsTMP;
    }

    public Float getValorAcumulado() {
        return valorAcumulado;
    }

    public void setValorAcumulado(Float valorAcumulado) {
        this.valorAcumulado = valorAcumulado;
    }

    public DevolucionSessionBean getDevolucionSessionBean() {
        return devolucionSessionBean;
    }

    public void setDevolucionSessionBean(DevolucionSessionBean devolucionSessionBean) {
        this.devolucionSessionBean = devolucionSessionBean;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private DevolucionFacade getFacade() {
        return ejbFacade;
    }

    public TransactionFacade getEjbTransactionFacade() {
        return ejbTransactionFacade;
    }

    public Devolucion prepareCreate() {
        selected = new Devolucion();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, JsfUtil.getMessageBundle(new String[]{"MessageDevolucion", "CreateSuccessF"}));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, JsfUtil.getMessageBundle(new String[]{"MessageDevolucion", "UpdateSuccessF"}));
    }

    public void destroy() {
        persist(PersistAction.DELETE, JsfUtil.getMessageBundle(new String[]{"MessageDevolucion", "DeleteSuccessF"}));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Devolucion> getItems() {
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

    public List<Devolucion> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Devolucion> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Devolucion.class)
    public static class DevolucionControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            DevolucionController controller = (DevolucionController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "devolucionController");
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
            if (object instanceof Devolucion) {
                Devolucion o = (Devolucion) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Devolucion.class.getName()});
                return null;
            }
        }

    }

    public Moneda[] getTipoMoneda() {
        return Moneda.values();
    }

    public void addDevolucionProducto() {
        if (devolucionProducto.getCantidad() > 0 && devolucionProducto.getCodigoDevolucion() != null && !devolucionProducto.getDetalle().trim().equals("")
                && devolucionProducto.getProducto() != null && devolucionProducto.getValor() > 0) {

            DevolucionProducto devolucionProductoTMP = new DevolucionProducto();
            devolucionProductoTMP.setCantidad(devolucionProducto.getCantidad());
            devolucionProductoTMP.setCodigoDevolucion(devolucionProducto.getCodigoDevolucion());
            devolucionProductoTMP.setDetalle(devolucionProducto.getDetalle());
            devolucionProductoTMP.setDevolucion(selected);
            devolucionProductoTMP.setProducto(devolucionProducto.getProducto());
            devolucionProductoTMP.setValor(devolucionProducto.getValor());
            devolucionProductoTMP.setId(itemsTMP.size() + 1l);

            itemsTMP.add(devolucionProductoTMP);
            getTotalDevolucion();
        }
    }

    public void removeDevolucionProducto(DevolucionProducto devolucionProductoArg) {
        selected.setValorTotal(valorAcumulado);
        itemsTMP.remove(devolucionProductoArg);
    }

    public void prepareCreateDevolucionProducto(String path) {
        if (selected.getDolar() != null && selected.getValorTotal() > 0 && selected.getFecha() != null
                && selected.getCliente() != null && itemsTMP.size() > 0) {
            for (DevolucionProducto dp : itemsTMP) {
                dp.setDevolucion(selected);
            }
            devolucionSessionBean.setDevolucion(selected);
            devolucionSessionBean.setDevolucionProductoList(itemsTMP);
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect(redireccionarDevolucionProducto(path));
            } catch (Exception e) {
                System.out.println("====NO SE PUDO REDIRRECCIONAR A DEVOLUCIONCAMBIO.XHTML");
                e.printStackTrace();
            }

        }
    }

    public String redireccionarDevolucionProducto(String path) {
        return path + "?faces-redirect=true";
    }
    
    public double getTotalDevolucion() {
        int sum = 0;
        for (DevolucionProducto dp : itemsTMP) {
            sum += dp.getValor()* dp.getCantidad();
        }
        if (selected != null) {
            selected.setValorTotal(sum);
        }
        return sum;
    }

}
