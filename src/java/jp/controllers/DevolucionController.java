package jp.controllers;

import jp.entidades.Devolucion;
import jp.util.JsfUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import jp.entidades.CambioDevolucion;
import jp.entidades.Cliente;
import jp.entidades.DevolucionProducto;
import jp.entidades.DevolucionProductoHelper;
import jp.entidades.Empleado;
import jp.entidades.Factura;
import jp.entidades.FacturaProducto;
import jp.entidades.Pago;
import jp.entidades.Producto;
import jp.entidades.Talonario;
import jp.facades.CambioDevolucionFacade;
import jp.facades.DevolucionFacade;
import jp.facades.FacturaFacade;
import jp.facades.TalonarioFacade;
import jp.facades.TransactionFacade;
import jp.seguridad.UsuarioActual;
import jp.util.EstadoPagoFactura;
import jp.util.Moneda;
import jp.util.TipoPago;
import jp.util.TipoTalonario;
import org.primefaces.event.SelectEvent;

@ManagedBean(name = "devolucionController")
@ViewScoped
public class DevolucionController implements Serializable {

    @EJB
    private DevolucionFacade ejbFacade;
    @EJB
    private TransactionFacade ejbTransactionFacade;
    @EJB
    private CambioDevolucionFacade ejbCambioDevolucion;
    @EJB
    private FacturaFacade ejbFacturaFacade;
    @EJB
    private DevolucionSessionBean devolucionSessionBean;
    @EJB
    private TalonarioFacade talonarioFacade;
    @Inject
    private UsuarioActual usuarioActual;

    private List<Devolucion> items = null;
    private Devolucion selected;
    private DevolucionProducto devolucionProducto;

    private List<DevolucionProducto> devolucionProductos;
    private List<DevolucionProductoHelper> devolucionProductosHelper;

    private List<Factura> facturasPendientesClienteTMP;
    private List<Factura> facturasSeleccionadas;
    private Float valorAcumulado = 0.0f;
    private Double totalSaldoPendiente;
    private Double totalSaldoSeleccionado;
    private Factura factura;
    private FacturaProducto facturaProducto;
    private List<FacturaProducto> itemsTMP;
    private boolean cambioManoAMano, ingresar;
    private int index;

    public DevolucionController() {
        devolucionProductosHelper = new ArrayList<>();
        devolucionProductos = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        selected = new Devolucion();
        selected.setValorTotal(valorAcumulado);
        selected.setDolar(false);
        devolucionProducto = new DevolucionProducto();
        totalSaldoPendiente = 0.0;
        devolucionProducto.setCantidad(1);
    }

    public boolean isCambioManoAMano() {
        return cambioManoAMano;
    }

    public void setCambioManoAMano(boolean cambioManoAMano) {
        this.cambioManoAMano = cambioManoAMano;
    }

    public boolean isIngresar() {
        return ingresar;
    }

    public void setIngresar(boolean ingresar) {
        this.ingresar = ingresar;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    public List<DevolucionProducto> getDevolucionProductos() {
        return devolucionProductos;
    }

    public void setDevolucionProductos(List<DevolucionProducto> devolucionProductos) {
        this.devolucionProductos = devolucionProductos;
    }

    public List<DevolucionProductoHelper> getDevolucionProductosHelper() {
        return devolucionProductosHelper;
    }

    public void setDevolucionProductosHelper(List<DevolucionProductoHelper> devolucionProductosHelper) {
        this.devolucionProductosHelper = devolucionProductosHelper;
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

    public TalonarioFacade getTalonarioFacade() {
        return talonarioFacade;
    }

    public Devolucion prepareCreate() {
        selected = new Devolucion();
        initializeEmbeddableKey();
        return selected;
    }

    public List<Devolucion> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public Moneda[] getTipoMoneda() {
        return Moneda.values();
    }

    public void removeDevolucionProducto(DevolucionProducto devolucionProductoArg) {
        selected.setValorTotal(valorAcumulado);
        devolucionProductos.remove(devolucionProductoArg);
    }

//    public void prepareCreateDevolucionProducto(String path) {
//
//        if (selected.getDolar() != null && selected.getValorTotal() > 0 && selected.getFecha() != null
//                && selected.getCliente() != null && devolucionProductos.size() > 0) {
//
//            for (DevolucionProducto dp : devolucionProductos) {
//                dp.setDevolucion(selected);
//            }
//            devolucionSessionBean.setDevolucion(selected);
//            devolucionSessionBean.setDevolucionProductoList(devolucionProductos);
//            try {
//                FacesContext.getCurrentInstance().getExternalContext().redirect(redireccionarDevolucionProducto(path));
//            } catch (Exception e) {
//                System.out.println("====NO SE PUDO REDIRECCIONAR A DEVOLUCIONCAMBIO.XHTML");
//                e.printStackTrace();
//            }
//
//        } else {
//            JsfUtil.addErrorMessage("Agregue al menos un Producto a la Devolución");
//        }
//    }
    public void redireccionarDevolucionProducto() {
        cambioManoAMano = false;
        JsfUtil.redirect("Create.xhtml");
    }

    public Long getProductosByDevolucion(Devolucion d) {
        return getFacade().getCantidadProductoByDevolucion(d);
    }

    public String getTipoMoneda(Devolucion d) {
        return d.getDolar() ? "Dolares" : "Pesos";
    }

    private void cleanDevolucionProducto() {
        devolucionProducto.setProducto(null);
        devolucionProducto.setCantidad(1);
        devolucionProducto.setCodigoDevolucion(null);
        devolucionProducto.setDetalle("");
        ingresar = false;
    }

    public String getCambioDevolucionByDevolucion(Devolucion d) {
        CambioDevolucion cd = getEjbCambioDevolucion().getCambioDevolucionByDevolucion(d);
        return cd != null ? "Cambio mano a mano" : "Abono a Factura";
    }

    public void onItemSelecCliente(SelectEvent event) {
        Cliente c = (Cliente) event.getObject();
        facturasPendientesClienteTMP = getEjbFacturaFacade().getFacturasPendientesByCliente(c, selected.getDolar() ? Moneda.DOLAR : Moneda.PESO);
        if (facturasPendientesClienteTMP != null && !facturasPendientesClienteTMP.isEmpty()) {
            totalSaldoPendiente = 0.0;
            for (Factura fp : facturasPendientesClienteTMP) {
                totalSaldoPendiente += fp.getSaldo();
            }
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

            if (totalSaldoSeleccionado < selected.getValorTotal()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "La Devolución será mayor al Saldo total"));
            }
        }
    }

    public void createPagoDevolucion() {
        if (selected.getValorTotal() == 0.0 || selected.getValorTotal() == null) {
            JsfUtil.addErrorMessage("No se han agregado productos a la Devolución para hacer un Abono");
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

                if (getEjbTransactionFacade().createPagoDevolucion(pago, selected, fac)) {
                    JsfUtil.getMessageBundle(new String[]{"MessagePagoDevolucion", "CreateSuccessM"});
                    JsfUtil.redirect("List.xhtml");
                } else {
                    JsfUtil.addErrorMessage("Ocurrio un error creando el pago devolucion");
                }

            } else {
                JsfUtil.addErrorMessage("El campo valor debe ser mayo a 0");
            }

        }

    }

    public void limpiarSeleccion() {
        facturasSeleccionadas = new ArrayList<>();
    }

//============================ PRODUCTOS DEVOLUCION ============================
    public void addDevolucionProducto() {
        if (validarProducto()) {

            boolean productoExiste = false;
            for (DevolucionProductoHelper dph : devolucionProductosHelper) {
                if (dph.getDevolucionProducto().getProducto().getId().equals(devolucionProducto.getProducto().getId())
                        && dph.getDevolucionProducto().getCodigoDevolucion().getId().equals(devolucionProducto.getCodigoDevolucion().getId())) {
                    productoExiste = true;
                    dph.getDevolucionProducto().setCantidad(dph.getDevolucionProducto().getCantidad() + devolucionProducto.getCantidad());
                }
            }

            if (!productoExiste) {
                DevolucionProducto devolucionProductoTMP = new DevolucionProducto();
                devolucionProductoTMP.setCantidad(devolucionProducto.getCantidad());
                devolucionProductoTMP.setCodigoDevolucion(devolucionProducto.getCodigoDevolucion());
                devolucionProductoTMP.setDetalle(devolucionProducto.getDetalle());
                devolucionProductoTMP.setProducto(devolucionProducto.getProducto());

                DevolucionProductoHelper dph = new DevolucionProductoHelper();
                dph.setId(devolucionProductosHelper.size() + 9000000000L);
                dph.setDevolucionProducto(devolucionProductoTMP);
                dph.setIngresar(ingresar);
                
                devolucionProductosHelper.add(dph);
            }
            cleanDevolucionProducto();
        }
    }

    //======
    private boolean validarProducto() {
        boolean productoNulo = false, cantidadMayorCero = true, codigoDevolucionNulo = false;
        if (devolucionProducto.getProducto() == null) {
            productoNulo = true;
            JsfUtil.addErrorMessage("El campo producto es obligatorio");
        }

        if (devolucionProducto.getCantidad() <= 0) {
            cantidadMayorCero = false;
            JsfUtil.addErrorMessage("el campo cantidad debe se mayor a cero");
        }

        if (devolucionProducto.getCodigoDevolucion() == null) {
            codigoDevolucionNulo = true;
            JsfUtil.addErrorMessage("Debe ingresar un codigo de devolucion");
        }
        return !productoNulo && cantidadMayorCero && !codigoDevolucionNulo;
    }

    //==========================================================================
    public void onItemSelectEmpleado(SelectEvent event) {
        Empleado e = (Empleado) event.getObject();
        Talonario t = getTalonarioFacade().getActiveTalonario(TipoTalonario.REMISION, e);

        if (t == null) {
            factura.setEmpleado(null);
            JsfUtil.addErrorMessage("No existen talonarios para el empleado " + e.toString());
        } else {
            factura.setOrdenPedido("" + t.getActual());
        }
    }

    public void onItemSelectProducto(SelectEvent event) {
        Producto p = (Producto) event.getObject();
        facturaProducto.setPrecio(p.getValorVenta());
    }

    //========================== PRODUCTOS FACTURA =============================
    public void addFacturaProducto() {
        if (facturaProducto.getUnidadesVenta() > 0 && facturaProducto.getPrecio() > 0 && facturaProducto.getProducto() != null && factura != null) {

            FacturaProducto facturaProductoTMP = new FacturaProducto();
            facturaProductoTMP.setUnidadesVenta(facturaProducto.getUnidadesVenta());
            facturaProductoTMP.setUnidadesBonificacion(0);
            facturaProductoTMP.setPrecio(facturaProducto.getPrecio());
            facturaProductoTMP.setProducto(facturaProducto.getProducto());
            facturaProductoTMP.setFactura(factura);
            facturaProductoTMP.setId(itemsTMP.size() + 9000000000L);

            itemsTMP.add(facturaProductoTMP);
            cleanFacturaProducto();
        } else {
            JsfUtil.addErrorMessage("Debe agregar productos a la Factura");
        }
    }

    public void removeFacturaProducto(FacturaProducto fp) {
        itemsTMP.remove(fp);
    }

    private void cleanFacturaProducto() {
        facturaProducto.setProducto(null);
        facturaProducto.setUnidadesVenta(0);
        facturaProducto.setPrecio(0d);
    }

    //Tab
    public void onTabChange() {
        if (index == 1) {
            if (selected.getFecha() == null || selected.getCliente() == null || (selected.getValorTotal() <= 0)
                    && (devolucionProductos == null || devolucionProductos.isEmpty())) {
                cambioManoAMano = true;
                index = 0;
                JsfUtil.addErrorMessage("Faltan campos por llenar en Devolucion");
            } else {

            }
        }
    }

}
