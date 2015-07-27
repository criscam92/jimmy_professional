package jp.controllers;

import java.io.IOException;
import jp.entidades.CambioDevolucion;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;
import jp.facades.CambioDevolucionFacade;
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
import javax.inject.Inject;
import jp.entidades.Devolucion;
import jp.entidades.DevolucionProducto;
import jp.entidades.Factura;
import jp.entidades.FacturaProducto;
import jp.entidades.Talonario;
import jp.facades.DevolucionFacade;
import jp.facades.FacturaFacade;
import jp.facades.ParametrosFacade;
import jp.facades.TalonarioFacade;
import jp.facades.TransactionFacade;
import jp.seguridad.UsuarioActual;
import jp.util.EstadoFactura;
import jp.util.TipoPago;
import jp.util.TipoTalonario;

@ManagedBean(name = "cambioDevolucionController")
@ViewScoped
public class CambioDevolucionController implements Serializable {

    @EJB
    private CambioDevolucionFacade ejbFacade;
    @EJB
    private DevolucionFacade ejbDevolucionFacade;
    @EJB
    private ParametrosFacade ejbParametrosFacade;
    @EJB
    private TalonarioFacade ejbTalonarioFacade;
    @EJB
    private FacturaFacade ejbFacturaFacade;
    @EJB
    private TransactionFacade ejbTransaccionFacade;
    @EJB
    private DevolucionSessionBean devolucionSessionBean;

    @Inject
    private UsuarioActual usuarioActual;

    private List<CambioDevolucion> items = null;
    private CambioDevolucion selected;
    private Devolucion devolucion;
    private List<DevolucionProducto> devolucionesProducto;
    private Factura factura;
    private FacturaProducto facturaProducto;
    private List<FacturaProducto> itemsTMP;

    public CambioDevolucionController() {
        itemsTMP = new ArrayList<>();
        devolucionSessionBean = new DevolucionSessionBean();
    }
//
//    @PostConstruct
//    public void init() {
//        try {
//            devolucion = devolucionSessionBean.getDevolucion();
//            devolucion.setUsuario(usuarioActual.get());
//            if (devolucion.getDolar()) {
//                devolucion.setDolarActual(getEjbParametrosFacade().getParametros().getPrecioDolar());
//            }
//            devolucionesProducto = devolucionSessionBean.getDevolucionProductoList();
//
//        } catch (Exception e) {
//            System.out.println("====================No se recibió parametro por GET en CambioDevContr====================");
//            e.printStackTrace();
//        }
//        selected = new CambioDevolucion();
//        facturaProducto = new FacturaProducto();
//        if (selected != null && selected.getFactura() == null) {
//            cargarFactura();
//        }
//    }

    public CambioDevolucion getSelected() {
        return selected;
    }

    public void setSelected(CambioDevolucion selected) {
        this.selected = selected;
    }

    public Devolucion getDevolucion() {
        return devolucion;
    }

    public void setDevolucion(Devolucion devolucion) {
        this.devolucion = devolucion;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
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

    private CambioDevolucionFacade getFacade() {
        return ejbFacade;
    }

    public DevolucionFacade getEjbDevolucionFacade() {
        return ejbDevolucionFacade;
    }

    public ParametrosFacade getEjbParametrosFacade() {
        return ejbParametrosFacade;
    }

    public TalonarioFacade getEjbTalonarioFacade() {
        return ejbTalonarioFacade;
    }

    public TransactionFacade getEjbTransaccionFacade() {
        return ejbTransaccionFacade;
    }

    public FacturaFacade getEjbFacturaFacade() {
        return ejbFacturaFacade;
    }

    public CambioDevolucion prepareCreate() {
        selected = new CambioDevolucion();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("CambioDevolucionCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("CambioDevolucionUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("CambioDevolucionDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<CambioDevolucion> getItems() {
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

    public List<CambioDevolucion> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<CambioDevolucion> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = CambioDevolucion.class)
    public static class CambioDevolucionControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CambioDevolucionController controller = (CambioDevolucionController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "cambioDevolucionController");
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
            if (object instanceof CambioDevolucion) {
                CambioDevolucion o = (CambioDevolucion) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), CambioDevolucion.class.getName()});
                return null;
            }
        }

    }

    private void cargarFactura() {
        factura = new Factura();
        factura.setFecha(devolucion.getFecha());
        factura.setCliente(devolucion.getCliente());
        factura.setUsuario(usuarioActual.getUsuario());
        //empleado seteado desde el form
        factura.setTipoPago(TipoPago.MANO_A_MANO.getValor());
        if (devolucion.getObservaciones() != null) {
            factura.setObservaciones(devolucion.getObservaciones());
        }
        factura.setTotalBruto(devolucion.getValorTotal());
        factura.setTotalPagar(devolucion.getValorTotal());
        factura.setEstado(EstadoFactura.REALIZADA.getValor());
        factura.setDolar(devolucion.getDolar());
        //orden_pedido desde el form
        if (factura.getDolar()) {
            factura.setDolarActual(ejbParametrosFacade.getParametros().getPrecioDolar());
        }
    }


    public void removeFacturaProducto(FacturaProducto fp) {
        itemsTMP.remove(fp);
    }

    public double getTotalPrecioFactura() {
        int sum = 0;
        for (FacturaProducto fp : itemsTMP) {
            sum += fp.getPrecio() * fp.getUnidadesVenta();
        }
        if (factura != null) {
            factura.setTotalBruto(sum);
            factura.setTotalPagar(sum);
        }
        return sum;
    }

    public int getTotalUnidadVentas() {
        int sum = 0;
        for (FacturaProducto fp : itemsTMP) {
            sum += fp.getUnidadesVenta();
        }
        return sum;
    }

    public String getSimboloValor() {
        return devolucion.getDolar() ? "USD " : "$ ";
    }

    public void cancelar() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("List.xhtml");
    }

    public void createPagoDevolucionManoaMano() {
        boolean result;
        if (itemsTMP.size() > 0 && devolucion != null && factura != null) {

            String opTMP = factura.getOrdenPedido();

            if (getEjbFacturaFacade().getFacturaByOrdenPedido(opTMP) == null) {
                if (actualizarTalonario(opTMP)) {
                    result = getEjbTransaccionFacade().createPagoDevolucionProducto(devolucion, devolucionesProducto, factura, itemsTMP);
                    if (result) {
                        if (factura.getDolar()) {
                            factura.setDolarActual(ejbParametrosFacade.getParametros().getPrecioDolar());
                        }
                        redireccionarFormulario();
                        clean();
                    } else {
                        JsfUtil.addErrorMessage("No se ha podido realizar el Pago de la Devolución");
                    }
                }

            } else {
                JsfUtil.addErrorMessage("El pedido " + factura.getOrdenPedido() + " ya existe");
            }

        } else {
            JsfUtil.addErrorMessage("Debe agregar productos a la Factura");
        }
    }

    public void redireccionarFormulario() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("List.xhtml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clean() {
        items = null;
        itemsTMP = null;
        selected = null;
        devolucionesProducto = null;
        factura = null;
        devolucion = null;
    }

    private boolean actualizarTalonario(String opTMP) {
        List<Talonario> talonarios = getEjbTalonarioFacade().getTalonariosByTipo(TipoTalonario.REMISION, factura.getEmpleado());

        if (talonarios != null && !talonarios.isEmpty()) {
            Long ordenPedido = Long.valueOf(opTMP);
            Talonario talonarioTMP = new Talonario();
            for (Talonario talonario : talonarios) {
                if (ordenPedido >= talonario.getInicial() && ordenPedido <= talonario.getFinal1()) {
                    talonarioTMP = talonario;
                    break;
                }
            }

            if (talonarioTMP.getId() == null) {
                JsfUtil.addErrorMessage("No existe un talonario valido para el No. de pedido " + opTMP);
                return false;
            } else {
                return getEjbTalonarioFacade().update(talonarioTMP, ordenPedido);
            }
        }

        return false;
    }



    public String getMoneda(boolean dolar) {
        return dolar ? "Dolar" : "Peso";
    }

    public String getTipoFactura(int tipo) {
        return TipoPago.getFromValue(tipo).getDetalle();
    }

}
