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
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import jp.entidades.CambioDevolucion;
import jp.entidades.Cliente;
import jp.entidades.DevolucionProducto;
import jp.entidades.Factura;
import jp.entidades.Pago;
import jp.entidades.Producto;
import jp.facades.CambioDevolucionFacade;
import jp.facades.DevolucionFacade;
import jp.facades.FacturaFacade;
import jp.facades.TransactionFacade;
import jp.seguridad.UsuarioActual;
import jp.util.EstadoPagoFactura;
import jp.util.Moneda;
import jp.util.TipoPago;
import org.primefaces.event.SelectEvent;

@ManagedBean(name = "devolucionController")
@ViewScoped
public class DevolucionController implements Serializable {

    @EJB
    private jp.facades.DevolucionFacade ejbFacade;
    @EJB
    private jp.facades.TransactionFacade ejbTransactionFacade;
    @EJB
    private jp.facades.CambioDevolucionFacade ejbCambioDevolucion;
    @EJB
    private jp.facades.FacturaFacade ejbFacturaFacade;
    @EJB
    private DevolucionSessionBean devolucionSessionBean;
    
    @Inject
    private UsuarioActual usuarioActual;
    
    private List<Devolucion> items = null;
    private Devolucion selected;
    private DevolucionProducto devolucionProducto;
    private List<DevolucionProducto> itemsTMP;
    private List<Factura> facturasPendientesClienteTMP;
    private List<Factura> facturasSeleccionadas;
    private Float valorAcumulado = 0.0f;
    private boolean deshabilitarAbono;
    private String messageSaldo;
    private Double totalSaldoPendiente;
    private Double totalSaldoSeleccionado;
    private String mensajeAdvertencia;

    public DevolucionController() {
        itemsTMP = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        selected = new Devolucion();
        selected.setValorTotal(valorAcumulado);
        selected.setDolar(false);
        devolucionProducto = new DevolucionProducto();
        deshabilitarAbono = true;
        messageSaldo = "Cliente sin saldo";
        totalSaldoPendiente = 0.0;
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

    public List<Factura> getFacturasPendientesClienteTMP() {
        return facturasPendientesClienteTMP;
    }

    public List<Factura> getFacturasSeleccionadas() {
        return facturasSeleccionadas;
    }

    public void setFacturasSeleccionadas(List<Factura> facturasSeleccionadas) {
        this.facturasSeleccionadas = facturasSeleccionadas;
    }

    public Float getValorAcumulado() {
        return valorAcumulado;
    }

    public void setValorAcumulado(Float valorAcumulado) {
        this.valorAcumulado = valorAcumulado;
    }

    public boolean isDeshabilitarAbono() {
        return deshabilitarAbono;
    }

    public void setDeshabilitarAbono(boolean deshabilitarAbono) {
        this.deshabilitarAbono = deshabilitarAbono;
    }

    public String getMessageSaldo() {
        return messageSaldo;
    }

    public void setMessageSaldo(String messageSaldo) {
        this.messageSaldo = messageSaldo;
    }

    public Double getTotalSaldoPendiente() {
        return totalSaldoPendiente;
    }

    public void setTotalSaldoPendiente(Double saldoTotalPendiente) {
        this.totalSaldoPendiente = saldoTotalPendiente;
    }

    public Double getTotalSaldoSeleccionado() {
        return totalSaldoSeleccionado;
    }

    public void setTotalSaldoSeleccionado(Double totalSaldoSeleccionado) {
        this.totalSaldoSeleccionado = totalSaldoSeleccionado;
    }

    public String getMensajeAdvertencia() {
        return mensajeAdvertencia;
    }

    public void setMensajeAdvertencia(String mensajeAdvertencia) {
        this.mensajeAdvertencia = mensajeAdvertencia;
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

    public CambioDevolucionFacade getEjbCambioDevolucion() {
        return ejbCambioDevolucion;
    }

    public FacturaFacade getEjbFacturaFacade() {
        return ejbFacturaFacade;
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
        if (devolucionProducto.getCantidad() > 0 && devolucionProducto.getCodigoDevolucion() != null
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
            cleanDevolucionProducto();
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
                System.out.println("====NO SE PUDO REDIRECCIONAR A DEVOLUCIONCAMBIO.XHTML");
                e.printStackTrace();
            }

        } else {
            JsfUtil.addErrorMessage("Agregue al menos un Producto a la Devolución");
        }
    }

    public String redireccionarDevolucionProducto(String path) {
        return path + "?faces-redirect=true";
    }

    public double getTotalDevolucion() {
        int sum = 0;
        for (DevolucionProducto dp : itemsTMP) {
            sum += dp.getValor() * dp.getCantidad();
        }
        if (selected != null) {
            selected.setValorTotal(Float.valueOf(sum));
        }
        return sum;
    }

    public Long getProductosByDevolucion(Devolucion d) {
        return getFacade().getCantidadProductoByDevolucion(d);
    }

    public String getTipoMoneda(Devolucion d) {
        return d.getDolar() ? "Dolares" : "Pesos";
    }

    public void onItemSelectProducto(SelectEvent event) {
        Producto p = (Producto) event.getObject();
        devolucionProducto.setValor((float) p.getValorVenta());
    }

    private void cleanDevolucionProducto() {
        devolucionProducto.setProducto(null);
        devolucionProducto.setCantidad(0);
        devolucionProducto.setValor(0f);
        devolucionProducto.setDetalle("");
    }

    public String getCambioDevolucionByDevolucion(Devolucion d) {
        CambioDevolucion cd = getEjbCambioDevolucion().getCambioDevolucionByDevolucion(d);
        return cd != null ? "Cambio mano a mano" : "Abono a Factura";
    }

    public void onItemSelecCliente(SelectEvent event) {
        Cliente c = (Cliente) event.getObject();
        facturasPendientesClienteTMP = getEjbFacturaFacade().getFacturasPendientesByCliente(c, selected.getDolar()?Moneda.DOLAR:Moneda.PESO);
        if (facturasPendientesClienteTMP != null && !facturasPendientesClienteTMP.isEmpty()) {
            totalSaldoPendiente = 0.0;
            for (Factura fp : facturasPendientesClienteTMP) {
                messageSaldo = "Abonar a Factura";
                deshabilitarAbono = false;
                totalSaldoPendiente += fp.getSaldo();
            }

        } else {
            messageSaldo = "Cliente sin Saldo";
            deshabilitarAbono = true;
        }

    }

    public String getSimboloValor() {
        selected.setCliente(null);
        return selected.getDolar() ? "USD " : "$ ";
    }

    public void prepareCreatePagoDevolucion() {
        System.out.println("OK");
        if (facturasSeleccionadas != null && !facturasSeleccionadas.isEmpty()) {
            totalSaldoSeleccionado = 0.0;
            for (Factura f : facturasSeleccionadas) {
                totalSaldoSeleccionado += f.getSaldo();
            }
            mensajeAdvertencia = "";
            if (totalSaldoSeleccionado < selected.getValorTotal()) {
//                mensajeAdvertencia = "La Devolucion será mayor al Saldo total";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "La Devolución será mayor al Saldo total"));
            }
        }
    }

    public void createPagoDevolucion() {
        if (selected.getValorTotal() == 0.0 || selected.getValorTotal() == null) {
            JsfUtil.addWarnMessage("No se han agregado productos a la Devolución para hacer un Abono");
            return;
        }
        for (Factura fac : facturasSeleccionadas) {

            if (selected.getValorTotal() > 0f) {
                Pago pago = new Pago();
                System.out.println("Se puede pagar la factura-> " + fac.getId() + " -->Saldo: " + fac.getSaldo() + "\n");

                pago.setFactura(fac);
                if (selected.getValorTotal() >= fac.getSaldo()) {
                    pago.setValorTotal(fac.getSaldo());
                    selected.setValorTotal(fac.getSaldo().floatValue());
                } else {
                    pago.setValorTotal(selected.getValorTotal());
                    selected.setValorTotal(selected.getValorTotal());
                }
                pago.setFecha(selected.getFecha());
                pago.setEstado(EstadoPagoFactura.REALIZADA.getValor());
                pago.setFormaPago(TipoPago.DEVOLUCION.getValor());
                pago.setDolar(selected.getDolar());
                if (!selected.getObservaciones().trim().equals("")) {
                    pago.setObservaciones(selected.getObservaciones());
                }
                selected.setUsuario(usuarioActual.get());

                getEjbTransactionFacade().createPagoDevolucion(pago, selected, fac);

            }
        }
        JsfUtil.getMessageBundle(new String[]{"MessagePagoDevolucion", "CreateSuccessM"});
        JsfUtil.redirect("List.xhtml");
    }

    public void limpiarSeleccion() {
        facturasSeleccionadas = new ArrayList<>();
        mensajeAdvertencia = "";
    }
}
