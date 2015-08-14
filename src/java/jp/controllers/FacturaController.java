package jp.controllers;

import java.io.File;
import java.io.IOException;
import jp.entidades.Factura;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import jp.entidades.Cliente;
import jp.entidades.DespachoFactura;
import jp.entidades.Empleado;
import jp.entidades.FacturaProducto;
import jp.entidades.FacturaPromocion;
import jp.entidades.Producto;
import jp.entidades.ProductoPromocionHelper;
import jp.entidades.Promocion;
import jp.entidades.Talonario;
import jp.facades.ClienteFacade;
import jp.facades.DespachoFacturaFacade;
import jp.facades.DespachoFacturaProductoFacade;
import jp.facades.EmpleadoFacade;
import jp.facades.FacturaFacade;
import jp.facades.FacturaProductoFacade;
import jp.facades.PagoFacade;
import jp.facades.ParametrosFacade;
import jp.facades.PromocionFacade;
import jp.facades.TalonarioFacade;
import jp.facades.TransactionFacade;
import jp.seguridad.UsuarioActual;
import jp.util.EstadoDespachoFactura;
import jp.util.EstadoFactura;
import jp.util.EstadoPagoFactura;
import jp.util.Moneda;
import jp.util.TipoPago;
import jp.util.TipoTalonario;
import jp.util.TipoUsuario;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.primefaces.event.SelectEvent;

@ManagedBean(name = "facturaController")
@ViewScoped
public class FacturaController implements Serializable {

    @EJB
    private FacturaFacade ejbFacade;
    @EJB
    private PromocionFacade promocionFacade;
    @EJB
    private FacturaProductoFacade ejbFacturaProductoFacade;
    @EJB
    private ParametrosFacade ejbParametrosFacade;
    @EJB
    private TransactionFacade ejbTransactionFacade;
    @EJB
    private TalonarioFacade talonarioFacade;
    @EJB
    private ParametrosFacade parametrosFacade;
    @EJB
    private DespachoFacturaFacade despachoFacturaFacade;
    @EJB
    private DespachoFacturaProductoFacade despachoFacturaProductoFacade;
    @EJB
    private PagoFacade pagoFacade;
    @EJB
    private EmpleadoFacade empleFacade;
    @EJB
    private ClienteFacade clienteFacade;
    @Inject
    private UsuarioActual usuarioActual;

    private List<Factura> items = null;
    private List<ProductoPromocionHelper> objects;
    private final List<ProductoPromocionHelper> objectsCreate;
    private final List<ProductoPromocionHelper> objectsEditar;
    private final List<ProductoPromocionHelper> objectsEliminar;
    private Factura selected;
    private double precio;
    private int moneda, selectOneButton, unidadesVenta, unidadesBonificacion;
    private Producto producto;
    private Promocion promocion;
    private final String uiError = "ui-state-error";
    private String errorProducto, errorPromocion, errorVenta, errorBonificacion, errorCliente;
    private List<Producto> productosTMP = null;
    private final SimpleDateFormat sdf;
    private boolean hayPagos = false, hayDespachos = false;
    private String ordenPedidoTMP;

    //=== DATOS FILTRO ===
    private Empleado empleadoFiltro;
    private Cliente clienteFiltro;
    private int tipoPago;
    private int estado;
    private int estadoDespacho;
    private int estadoPago;
    private Date fechaIni;
    private Date fechaFin;
    //====================

    boolean emple, clien, tPago, mon, agre, qui;

    private DespachoFactura despachoFactura;
    private List<DespachoFactura> despachosFactura = null;

    public FacturaController() {
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        selected = new Factura();
        selected.setDescuento(0.0);
        selectOneButton = 1;
        objects = new ArrayList<>();
        objectsCreate = new ArrayList<>();
        objectsEditar = new ArrayList<>();
        objectsEliminar = new ArrayList<>();
    }

    @PostConstruct
    public void init() {

        Map<String, String> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        FacesContext.getCurrentInstance().getExternalContext().getSession(true);

        String ordenPedido = requestMap.get("id");
        ordenPedidoTMP = ordenPedido;

        if (ordenPedido != null && !ordenPedido.trim().isEmpty()) {//
            selected = getFacade().getFacturaByOrdenPedido(ordenPedido);

            if (selected.getId() != null) {
                long despachos = getDespachoFacturaFacade().countDespachoFacturaByFactura(selected);
                System.out.println("Despachos: " + despachos);
                if (despachos > 0) {
                    hayDespachos = true;
                }

                long pagos = getPagoFacade().countPagosFacturaByFactura(selected);
                System.out.println("Pagos: " + pagos);
                if (pagos > 0) {
                    hayPagos = true;
                }
            }

            bloquearCampos();

            List<FacturaProducto> listProductos = getFacade().getFacturaProductoByFactura(selected);
            objects = new ArrayList<>();
            for (FacturaProducto fp : listProductos) {
                Long id = objects.size() + 1L;
                objects.add(new ProductoPromocionHelper(id, fp.getId(), fp.getUnidadesVenta(), fp.getUnidadesBonificacion(), (fp.getProducto().getValorVenta() * fp.getUnidadesVenta()), (fp.getProducto().getValorVentaUsd() * fp.getUnidadesVenta()), fp.getProducto(), true));
                objectsEditar.add(new ProductoPromocionHelper(id, fp.getId(), fp.getUnidadesVenta(), fp.getUnidadesBonificacion(), (fp.getProducto().getValorVenta() * fp.getUnidadesVenta()), (fp.getProducto().getValorVentaUsd() * fp.getUnidadesVenta()), fp.getProducto(), true));
            }

            List<FacturaPromocion> listPromociones = getPromocionFacade().getFacturaPromocionByFactura(selected);
            for (FacturaPromocion fp : listPromociones) {
                Long id = objects.size() + 1L;
                objects.add(new ProductoPromocionHelper(id, fp.getId(), fp.getUnidadesVenta(), fp.getUnidadesBonificacion(), (fp.getPromocion().getValor() * fp.getUnidadesVenta()), (fp.getPromocion().getValorVentaUsd() * fp.getUnidadesVenta()), fp.getPromocion(), false));
                objectsEditar.add(new ProductoPromocionHelper(id, fp.getId(), fp.getUnidadesVenta(), fp.getUnidadesBonificacion(), (fp.getPromocion().getValor() * fp.getUnidadesVenta()), (fp.getPromocion().getValorVentaUsd() * fp.getUnidadesVenta()), fp.getPromocion(), false));
            }
        } else {

            String date1 = requestMap.get("date1");
            String date2 = requestMap.get("date2");

            try {
                empleadoFiltro = getEmpleFacade().find(Long.valueOf(requestMap.get("empleado")));
            } catch (Exception e) {
                empleadoFiltro = null;
            }

            try {
                clienteFiltro = getClienteFacade().find(Long.valueOf(requestMap.get("cliente")));
            } catch (Exception e) {
                clienteFiltro = null;
            }

            try {
                tipoPago = Integer.valueOf(requestMap.get("tipo"));
            } catch (Exception e) {
                tipoPago = -1;
            }

            try {
                estado = Integer.valueOf(requestMap.get("estado"));
            } catch (Exception e) {
                estado = -1;
            }

            try {
                estadoDespacho = Integer.valueOf(requestMap.get("estadoDespacho"));
            } catch (Exception e) {
                estadoDespacho = -1;
            }

            try {
                estadoPago = Integer.valueOf(requestMap.get("estadoPago"));
            } catch (Exception e) {
                estadoPago = -1;
            }

            try {
                fechaIni = sdf.parse(date1);
            } catch (Exception e) {
                fechaIni = null;
            }

            try {
                fechaFin = sdf.parse(date2);
            } catch (Exception e) {
                fechaFin = null;
            }

            items = getFacade().filterFactura(empleadoFiltro, clienteFiltro, tipoPago, estado, estadoDespacho, estadoPago, fechaIni, fechaFin);
        }
    }

    public boolean isEmple() {
        return emple;
    }

    public void setEmple(boolean emple) {
        this.emple = emple;
    }

    public boolean istPago() {
        return tPago;
    }

    public void settPago(boolean tPago) {
        this.tPago = tPago;
    }

    public boolean isMon() {
        return mon;
    }

    public void setMon(boolean mon) {
        this.mon = mon;
    }

    public boolean isAgre() {
        return agre;
    }

    public void setAgre(boolean agre) {
        this.agre = agre;
    }

    public boolean isQui() {
        return qui;
    }

    public void setQui(boolean qui) {
        this.qui = qui;
    }

    public boolean isClien() {
        return clien;
    }

    public void setClien(boolean clien) {
        this.clien = clien;
    }

    //=== DATOS FILTRO ===
    public Date getFechaIni() {
        return fechaIni;
    }

    public void setFechaIni(Date fechaIni) {
        this.fechaIni = fechaIni;
    }

    public int getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(int tipoPago) {
        this.tipoPago = tipoPago;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getEstadoDespacho() {
        return estadoDespacho;
    }

    public void setEstadoDespacho(int estadoDespacho) {
        this.estadoDespacho = estadoDespacho;
    }

    public int getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(int estadoPago) {
        this.estadoPago = estadoPago;
    }

    public Empleado getEmpleadoFiltro() {
        return empleadoFiltro;
    }

    public void setEmpleadoFiltro(Empleado empleadoFiltro) {
        this.empleadoFiltro = empleadoFiltro;
    }

    public Cliente getClienteFiltro() {
        return clienteFiltro;
    }

    public void setClienteFiltro(Cliente clienteFiltro) {
        this.clienteFiltro = clienteFiltro;
    }

    //=====================
    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public PagoFacade getPagoFacade() {
        return pagoFacade;
    }

    public EmpleadoFacade getEmpleFacade() {
        return empleFacade;
    }

    public ClienteFacade getClienteFacade() {
        return clienteFacade;
    }

    public PromocionFacade getPromocionFacade() {
        return promocionFacade;
    }

    public List<Producto> getProductosTMP() {
        return productosTMP;
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

    public String getErrorCliente() {
        return errorCliente;
    }

    public TalonarioFacade getTalonarioFacade() {
        return talonarioFacade;
    }

    public DespachoFacturaFacade getDespachoFacturaFacade() {
        return despachoFacturaFacade;
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

    public DespachoFactura getDespachoFactura() {
        return despachoFactura;
    }

    public void setDespachoFactura(DespachoFactura despachoFactura) {
        this.despachoFactura = despachoFactura;
    }

    public List<DespachoFactura> getDespachosFactura() {
        return despachosFactura;
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

    public DespachoFacturaProductoFacade getDespachoFacturaProductoFacade() {
        return despachoFacturaProductoFacade;
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

    private void updateFactura() {
        if (moneda == 1) {
            selected.setDolar(true);
        } else {
            selected.setDolar(false);
        }
        if (getEjbTransactionFacade().updateFacturaProductoPromocion(selected, objectsCreate, objectsEditar, objectsEliminar)) {
            clean();
            redireccionarFormulario();
        } else {
            JsfUtil.addErrorMessage("Ocurrio un error durante la modificacion de la factura");
        }
    }

    public String create(boolean despachar) {
        if (objects.size() > 0) {

            String opTMP = selected.getOrdenPedido();

            if (selected.getId() != null) {
                if (!selected.getOrdenPedido().equals(ordenPedidoTMP)) {
                    if (getFacade().getFacturaByOrdenPedido(opTMP) == null) {
                        if (actulizarTalonario(opTMP)) {
                            updateFactura();
                        }
                    } else {
                        JsfUtil.addErrorMessage("El pedido " + selected.getOrdenPedido() + " ya existe");
                    }
                } else {
                    updateFactura();
                }
            } else {
                if (getFacade().getFacturaByOrdenPedido(opTMP) == null) {
                    if (actulizarTalonario(opTMP)) {
                        selected.setUsuario(usuarioActual.getUsuario());
                        asinarEstados();
                        if (moneda == 1) {
                            selected.setDolar(true);
                            selected.setDolarActual(parametrosFacade.getParametros().getPrecioDolar());
                        } else {
                            selected.setDolar(false);
                        }
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
                                JsfUtil.addErrorMessage("Error guardando la ");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    JsfUtil.addErrorMessage("El pedido " + selected.getOrdenPedido() + " ya existe");
                }
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

    public List<Factura> getItemsOrdenados() {
        if (items == null) {
            items = getFacade().getFacturasOrdenadas();
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
        errorProducto = "";
        errorPromocion = "";
        errorVenta = "";
        errorBonificacion = "";
    }

    private void getMensajesError() {

        if (selected.getCliente() != null) {
            errorCliente = "";
            boolean comprobar = false;
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
                comprobar = true;
            } else {
                if (promocion == null) {
                    errorPromocion = uiError;
                    JsfUtil.addErrorMessage("Debe seleccionar una promocion valida");
                    comprobar = true;
                } else {
                    if (getPromocionFacade().comprobarCategoriaPromocion(selected.getCliente(), promocion)) {
                        if (precio <= 0.0) {
                            errorPromocion = uiError;
                            JsfUtil.addErrorMessage("Debe seleccionar una promocion con un precio valido");
                        } else {
                            errorPromocion = "";
                        }
                        comprobar = true;
                    } else {
                        errorPromocion = uiError;
                        JsfUtil.addErrorMessage("La promocion " + promocion + " no pertenece a la misma categoria del cliente");
                    }
                }
            }

            if (comprobar) {
//                if (unidadesVenta < 1) {
//                    errorVenta = uiError;
//                    JsfUtil.addErrorMessage("El campo venta debe ser mayor a 0");
//                } else {
//                    errorVenta = "";
//                }

                if (unidadesBonificacion < 0) {
                    errorBonificacion = uiError;
                    JsfUtil.addErrorMessage("El campo bonificación debe ser mayor a 0");
                } else {
                    errorBonificacion = "";
                }
            }
        } else {
            errorCliente = uiError;
            JsfUtil.addErrorMessage("Debe seleccionar un cliente valido para poder agregar productos o promociones");
        }
    }

    private boolean actulizarTalonario(String opTMP) {
        List<Talonario> talonarios = getTalonarioFacade().getTalonariosByTipo(TipoTalonario.REMISION, selected.getEmpleado());

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
                return getTalonarioFacade().update(talonarioTMP, ordenPedido);
            }
        }
        return false;
    }

    private boolean validaUnidades() {
        if (unidadesVenta == 0 && unidadesBonificacion > 0) {
            return true;
        } else if (unidadesVenta > 0 && unidadesBonificacion == 0) {
            return true;
        } else {
            return unidadesVenta > 0 && unidadesBonificacion > 0;
        }
    }

    private void asinarEstados() {
        boolean soloBonificacion = true;
        for (ProductoPromocionHelper pph : objects) {
            if (pph.getUnidadesVenta() > 0 && pph.getUnidadesBonificacion() >= 0) {
                soloBonificacion = false;
                break;
            }
        }

        selected.setEstado(EstadoFactura.PENDIENTE.getValor());
        selected.setEstadoDespacho(EstadoDespachoFactura.SIN_DESPACHAR.getValor());
        if ((soloBonificacion) || (selected.getTipoPago() == TipoPago.CONTADO.getValor())) {
            selected.setEstadoPago(EstadoPagoFactura.PAGADA.getValor());
        } else {
            selected.setEstadoPago(EstadoPagoFactura.SIN_PAGO.getValor());
        }
    }

    @FacesConverter(forClass = Factura.class, value = "facturaConverter")
    public static class FacturaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            try {
                if (value == null || value.length() == 0) {
                    return null;
                }
                FacturaController controller = (FacturaController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "facturaController");
                Long key = getKey(value);
                if (key == null) {
                    return null;
                }
                return controller.getFacade().find(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        java.lang.Long getKey(String value) {
            try {
                java.lang.Long key;
                key = Long.valueOf(value);
                return key;
            } catch (Exception e) {
            }
            return null;
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

    public TipoPago[] getTiposPagos() {
        return TipoPago.getFromValue(new Integer[]{0, 1});
    }

    public Map<String, Integer> getMonedas() {
        return Moneda.getMonedas();
    }

    public EstadoFactura[] getEstadosFactura() {
        return EstadoFactura.getFromValue(new Integer[]{0, 1, 2});
    }

    public EstadoDespachoFactura[] getEstadosDespachoFactura() {
        return EstadoDespachoFactura.getFromValue(new Integer[]{0, 1, 2});
    }

    public EstadoPagoFactura[] getEstadosPagoFactura() {
        return EstadoPagoFactura.getFromValue(new Integer[]{0, 1, 2});
    }

    public String redirectCreateFactura() {
        selected = new Factura();
        objects = new ArrayList<>();
        return "Create.xhtml?faces-redirect=true";
    }

    public String redirectEditFactura() {
        return "Create.xhtml?id=" + selected.getOrdenPedido() + "&faces-redirect=true";
    }

    public void addProductoOrPromocion() {
        if ((producto != null || promocion != null) && validaUnidades() && precio > 0 && selected.getCliente() != null) {
            boolean isProducto = producto != null;

            boolean existe = false;
            for (ProductoPromocionHelper pph : objects) {

                boolean repetido = false;
                if (isProducto && pph.isProducto()) {
                    repetido = ((Producto) pph.getProductoPromocion()).getId().equals(producto.getId());
                } else if (!isProducto && !pph.isProducto()) {
                    repetido = ((Promocion) pph.getProductoPromocion()).getId().equals(promocion.getId());
                }

                if (repetido) {
                    existe = true;
                    boolean existeCreate = false;

                    for (ProductoPromocionHelper pph2 : objectsCreate) {
                        existeCreate = true;
                        int io = objectsCreate.indexOf(pph2);
                        objectsCreate.get(io).setPrecio(((isProducto ? producto.getValorVenta() : promocion.getValor()) * unidadesVenta) + pph.getPrecio());
                        objectsCreate.get(io).setPrecioUs(((isProducto ? producto.getValorVentaUsd() : promocion.getValorVentaUsd()) * unidadesVenta) + pph.getPrecioUs());
                        objectsCreate.get(io).setUnidadesBonificacion(unidadesBonificacion + pph.getUnidadesBonificacion());
                        objectsCreate.get(io).setUnidadesVenta(unidadesVenta + pph.getUnidadesVenta());
                    }

                    int io = objects.indexOf(pph);
                    objects.get(io).setPrecio(((isProducto ? producto.getValorVenta() : promocion.getValor()) * unidadesVenta) + pph.getPrecio());
                    objects.get(io).setPrecioUs(((isProducto ? producto.getValorVentaUsd() : promocion.getValorVentaUsd()) * unidadesVenta) + pph.getPrecioUs());
                    objects.get(io).setUnidadesBonificacion(unidadesBonificacion + pph.getUnidadesBonificacion());
                    objects.get(io).setUnidadesVenta(unidadesVenta + pph.getUnidadesVenta());

                    if (!existeCreate) {
                        if (objects.get(io).getIdObject() != null) {
                            objectsEditar.add(new ProductoPromocionHelper(objects.get(io).getId(), objects.get(io).getIdObject(),
                                    objects.get(io).getUnidadesVenta(), objects.get(io).getUnidadesBonificacion(), objects.get(io).getPrecio(),
                                    objects.get(io).getPrecioUs(), objects.get(io).getProductoPromocion(), objects.get(io).isProducto()));
                        }
                    }

                    break;
                }
            }

            if (!existe) {
                ProductoPromocionHelper pph = new ProductoPromocionHelper(objects.size() + 1L, null, unidadesVenta, unidadesBonificacion,
                        ((isProducto ? producto.getValorVenta() : promocion.getValor()) * unidadesVenta),
                        ((isProducto ? producto.getValorVentaUsd() : promocion.getValorVentaUsd()) * unidadesVenta),
                        isProducto ? producto : promocion, isProducto);

                if (selected.getId() != null) {
                    objectsCreate.add(pph);
                }

                objects.add(pph);
            }

            cleanProductoOrPromocion();
        } else {
            getMensajesError();
        }
    }

    public void removeFacturaProductoPromocion(ProductoPromocionHelper pph) {
        if (objectsCreate.contains(pph)) {
            objectsCreate.remove(pph);
        }

        if (objectsEditar.contains(pph)) {
            objectsEditar.remove(pph);
        }

        if ((selected != null && selected.getId() != null) && pph.getIdObject() != null) {
            objectsEliminar.add(pph);
        }

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
            if (moneda == 0) {
                sum += fp.getPrecio();
            } else {
                sum += fp.getPrecioUs();
            }
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
            if (moneda == 0) {
                sum += fp.getPrecio();
            } else {
                sum += fp.getPrecioUs();
            }
        }

        descuento = sum * descuento / 100;
        if (selected != null) {
            selected.setTotalPagar(sum + descuento);
        }
        return sum + descuento;
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

    public Long getCantidadVentasOrBonificacionesByFactura(Factura f, int tipo) {
        return getEjbFacturaProductoFacade().getCantidadVentaOrBonificacionByFactura(f, tipo);
    }

    public void redireccionarFormulario() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("List.xhtml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Promocion> llenarPromocion(String query) {
        if (selected.getCliente() != null) {
            return getFacade().getPromocionByQuery(query, selected.getCliente());
        } else {
            JsfUtil.addErrorMessage("Debe seleccionar un cliente para agregar promociones");
        }
        return new ArrayList<>();
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
        if (moneda == 0) {
            precio = p.getValor();
        } else {
            precio = p.getValorVentaUsd();
        }
    }

    public void onItemSelectCliente(SelectEvent event) {
        Cliente c = (Cliente) event.getObject();
        items = null;        
        getDescuentobyCliente(c);
        cleanPromociones();
    }

    public void onItemSelectEmpleado(SelectEvent event) {
        Empleado e = (Empleado) event.getObject();
        Talonario t = getTalonarioFacade().getActiveTalonario(TipoTalonario.REMISION, e);

        if (t == null) {
            selected.setEmpleado(null);
            JsfUtil.addErrorMessage("No existen talonarios para el empleado " + e.toString());
        } else {
            selected.setOrdenPedido("" + t.getActual());
        }

        selected.setCliente(null);
        removeAllPromociones();
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

    public String redirect() {
        return "Despacho.xhtml?fac=" + selected.getOrdenPedido() + "&faces-redirect=true";
    }

    public int countProductoByDespacho(Producto producto) {
        return getDespachoFacturaProductoFacade().countDespachoFacturaProductoByDespachoFacturaAndProducto(despachoFactura, producto);
    }

    public void prepararProductos() {
        productosTMP = null;
        productosTMP = getDespachoFacturaProductoFacade().getListProductosByDespachoFactura(despachoFactura);
    }

    public void generarReporte(ActionEvent actionEvent) {
        Factura factura = getFacade().getFacturaById(selected.getId());

        if (factura != null) {
            File reporte1 = new File(JsfUtil.getRutaReporte("factura.jasper"));
            File reporte2 = new File(JsfUtil.getRutaReporte("subReporteProductos.jasper"));
            File reporte3 = new File(JsfUtil.getRutaReporte("subReportePromocion.jasper"));
            File reporte4 = new File(JsfUtil.getRutaReporte("subReportePromocionProducto.jasper"));

            if (reporte1.exists() && reporte2.exists() && reporte3.exists() && reporte4.exists()) {
                List<Factura> facturas = new ArrayList<>();
                facturas.add(factura);

                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(facturas);

                HashMap map = new HashMap();
                map.put("venta", "" + getCantidadVentasOrBonificacionesByFactura(factura, 1));
                map.put("bonificacion", "" + getCantidadVentasOrBonificacionesByFactura(factura, 2));

                try {
                    JasperPrint jasperPrint = JasperFillManager.fillReport(reporte1.getPath(), map, dataSource);

                    HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
                    response.addHeader("Content-disposition", "attachment; filename=Factura.pdf");
                    ServletOutputStream sos = response.getOutputStream();
                    JasperExportManager.exportReportToPdfStream(jasperPrint, sos);
                    FacesContext.getCurrentInstance().responseComplete();
                } catch (JRException | IOException e) {
                    JsfUtil.addErrorMessage("Ha ocurrido un error durante la generacion"
                            + " del reporte, Por favor intente de nuevo, si el error persiste"
                            + " comuniquese con el administrador del sistema");

                    e.printStackTrace();
                }

            } else {
                if (reporte1.exists()) {
                    JsfUtil.addErrorMessage("No existe el archivo " + reporte1.getName());
                }
                if (reporte2.exists()) {
                    JsfUtil.addErrorMessage("No existe el archivo " + reporte2.getName());
                }
                if (reporte3.exists()) {
                    JsfUtil.addErrorMessage("No existe el archivo " + reporte3.getName());
                }
                if (reporte4.exists()) {
                    JsfUtil.addErrorMessage("No existe el archivo " + reporte4.getName());
                }
            }

        } else {
            JsfUtil.addErrorMessage("Seleccione la Factura que desea imprimir");
        }

    }

    public void eventMoneda() {
        cleanProductoOrPromocion();
    }

    public String getEstado(int estado) {
        return EstadoFactura.getFromValue(estado).getDetalle();
    }

    public String getEstadoDespacho(int estado) {
        return EstadoDespachoFactura.getFromValue(estado).getDetalle();
    }

    public String getEstadoPago(int estado) {
        return EstadoPagoFactura.getFromValue(estado).getDetalle();
    }

    public void prepararDespachos() {
        despachosFactura = getDespachoFacturaFacade().getDespachosFacturaByFactura(selected, false);
    }

    public void anularDespacho() {
        if (!getEjbTransactionFacade().anularDespachoFactura(despachoFactura)) {
            JsfUtil.addErrorMessage("Error anulando el despacho");
        } else {
            items = null;
            despachosFactura = getDespachoFacturaFacade().getDespachosFacturaByFactura(selected, false);
        }
    }

    public void anularFactura() {
        if (selected != null) {
            long despachos = getDespachoFacturaFacade().countDespachoFacturaByFactura(selected);
            if (despachos > 0) {
                JsfUtil.addErrorMessage("La factura " + selected.getOrdenPedido() + " no se puede anular porque tiene despachos asociados");
            }

            long pagos = getPagoFacade().countPagosFacturaByFactura(selected);
            if (pagos > 0) {
                JsfUtil.addErrorMessage("La factura " + selected.getOrdenPedido() + " no se puede anular porque tiene pagos asociados");
            }

            if (despachos <= 0 && pagos <= 0) {
                if (getFacade().anularFactura(selected)) {
                    items = null;
                    JsfUtil.addSuccessMessage("La factura " + selected.getOrdenPedido() + " ha sido anulada exitosamente");
                } else {
                    JsfUtil.addErrorMessage("Error anulando la factura " + selected.getOrdenPedido());
                }
            }
        } else {
            JsfUtil.addErrorMessage("Seleccione la factura que desea anular");
        }
    }

    public int estadoRealizado() {
        return EstadoFactura.REALIZADA.getValor();
    }

    public int estadoAnulado() {
        return EstadoFactura.ANULADO.getValor();
    }

    public void redirec() throws IOException {
        String url = "List.xhtml?";
        if (empleadoFiltro != null) {
            url += "&empleado=" + empleadoFiltro.getId();
        }
        if (clienteFiltro != null) {
            url += "&cliente=" + clienteFiltro.getId();
        }
        if (tipoPago != -1) {
            url += "&tipo=" + tipoPago;
        }
        if (estado != -1) {
            url += "&estado=" + estado;
        }
        if (estadoDespacho != -1) {
            url += "&estadoDespacho=" + estadoDespacho;
        }
        if (estadoPago != -1) {
            url += "&estadoPago=" + estadoPago;
        }
        if (fechaIni != null && fechaFin != null) {
            url += "&date1=" + sdf.format(fechaIni) + "&date2=" + sdf.format(fechaFin);
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect(url);
    }

    public void cleanFilter() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("List.xhtml");
    }

    public Date getFechaActual() {
        if (fechaIni != null) {
            return fechaIni;
        } else {
            return null;
        }
    }

    public String tipo(int tipo) {
        return TipoPago.getFromValue(tipo).getDetalle();
    }

    public String estadoFactura(int estado) {
        return EstadoFactura.getFromValue(estado).getDetalle();
    }

    public String getInfo() {
        String info;
        if (hayPagos && hayDespachos) {
            info = "La factura que está editando tiene pago y despachos asociados por tal razón algunos de los campos aparecerán bloqueados.";
        } else if (hayPagos && !hayDespachos) {
            info = "La factura que está editando tiene pago asociados por tal razón algunos de los campos aparecerán bloqueados.";
        } else if (!hayPagos && hayDespachos) {
            info = "La factura que está editando tiene despachos asociados por tal razón algunos de los campos aparecerán bloqueados.";
        } else {
            info = "";
        }
        return info;
    }

    public boolean mostrarInfo() {
        return !hayPagos && !hayDespachos;
    }

    public void bloquearCampos() {
        if (selected.getId() != null) {

            if (hayPagos && hayDespachos) {
                mostrarTrue();
            } else if (hayPagos && !hayDespachos) {
                mostrarTrue();
            } else if (!hayPagos && hayDespachos) {
                emple = true;
                clien = true;
                tPago = false;
                mon = false;
                agre = true;
                qui = true;
            } else {
                if (usuarioActual.getUsuario().getTipo() == TipoUsuario.Administrador.getValor()) {
                    emple = false;
                    clien = false;
                    tPago = false;
                    mon = false;
                    agre = false;
                    qui = false;
                } else {
                    mostrarTrue();
                }

            }
        } else {
            mostrarTrue();
        }
    }

    private void mostrarTrue() {
        emple = true;
        clien = true;
        tPago = true;
        mon = true;
        agre = true;
        qui = true;
    }

    public String getValueButton() {
        return (selected.getId() != null) ? "Modificar" : "Guardar";
    }

    public String displayButton() {
        return (selected.getId() != null) ? "display: none" : "";
    }

    public boolean habilitarAnular() {
        System.out.println("==========================================================");
        System.out.println("Usuario Nombre: " + usuarioActual.getUsuario().getUsuario());
        System.out.println("Usuario Actual #: " + usuarioActual.getUsuario().getTipo());
        System.out.println("Usuario Actual: " + TipoUsuario.getFromValue(Integer.valueOf(TipoUsuario.Administrador.getValor() + "")));
        System.out.println("==========================================================");
        if (selected != null && selected.getId() != null) {
            if (usuarioActual.getUsuario().getTipo() == Long.valueOf(TipoUsuario.Administrador.getValor() + "")) {
                if (selected.getEstado() == EstadoFactura.ANULADO.getValor()) {
                    return true;
                } else {
                    return selected.getEstado() == EstadoFactura.REALIZADA.getValor();
                }
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public List<Cliente> llenarCliente(String query) {
        if (selected != null && selected.getEmpleado() != null) {
            return getClienteFacade().getClienteByQuery(query, selected.getEmpleado());
        } else {
            if (selected.getEmpleado() == null) {
                JsfUtil.addErrorMessage("Debe seleccionar un empleado antes de ingresar el cliente");
            }
        }
        return new ArrayList<>();
    }

    public List<Promocion> llenarPromociones(String query) {
        if (selected != null && selected.getCliente() != null) {
            return getFacade().getPromocionByQuery(query, selected.getCliente());
        } else {
            if (selected.getEmpleado() == null) {
                JsfUtil.addErrorMessage("Debe seleccionar un cliente antes de ingresar promociones");
            }
        }
        return new ArrayList<>();
    }

    public void removeAllPromociones() {
        List<ProductoPromocionHelper> listTMP = new ArrayList<>();
        for (ProductoPromocionHelper pph : objects) {
            if (!pph.isProducto()) {
                listTMP.add(pph);
            }
        }

        if (selected != null && selected.getId() != null) {
            for (ProductoPromocionHelper pph : listTMP) {
                if (objectsCreate.contains(pph)) {
                    objectsCreate.remove(pph);
                }

                if (objectsEditar.contains(pph)) {
                    objectsEditar.remove(pph);
                }

                if (pph.getIdObject() != null) {
                    objectsEliminar.add(pph);
                }
                objects.remove(pph);
            }
        } else {
            for (ProductoPromocionHelper pph : listTMP) {
                objects.remove(pph);
            }
        }

        if (!listTMP.isEmpty()) {
            JsfUtil.addWarnMessage("Se han eliminado las promociones que no pertenecen a la misma categoria del cliente");
        }
    }

    public void cleanPromociones() {
        List<ProductoPromocionHelper> listTMP = new ArrayList<>();
        for (ProductoPromocionHelper pph : objects) {
            if (!pph.isProducto()) {
                if ((((Promocion) pph.getProductoPromocion()).getCategoria() != null && selected.getCliente() != null) && !((Promocion) pph.getProductoPromocion()).getCategoria().getId().equals(selected.getCliente().getCategoria().getId())) {
                    listTMP.add(pph);
                }
            }
        }

        if (selected != null && selected.getId() != null) {
            for (ProductoPromocionHelper pph : listTMP) {
                if (objectsCreate.contains(pph)) {
                    objectsCreate.remove(pph);
                }

                if (objectsEditar.contains(pph)) {
                    objectsEditar.remove(pph);
                }

                if (pph.getIdObject() != null) {
                    objectsEliminar.add(pph);
                }
                objects.remove(pph);
            }
        } else {
            for (ProductoPromocionHelper pph : listTMP) {
                objects.remove(pph);
            }
        }

        if (!listTMP.isEmpty()) {
            JsfUtil.addWarnMessage("Se han eliminado las promociones que no pertenecen a la misma categoria del cliente");
        }

    }
}
