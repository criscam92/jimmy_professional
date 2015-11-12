package jp.controllers;

//==============================================================================
import java.io.*;
import java.util.*;
import javax.ejb.*;
import javax.faces.bean.*;
import javax.faces.event.*;
import javax.inject.Inject;
import jp.entidades.*;
import jp.facades.*;
import jp.seguridad.UsuarioActual;
import jp.util.*;
import org.primefaces.context.*;
import org.primefaces.event.*;

//==============================================================================
@ManagedBean(name = "nuevaRelacionFacturaController")
@ViewScoped
public class NuevaRelacionFacturaController implements Serializable {

    //==========================================================================
    @EJB
    private RelacionFacturaFacade relacionFacturaFacade;
    @EJB
    private TalonarioFacade talonarioFacade;
    @EJB
    private FacturaFacade facturaFacade;
    @EJB
    private PagoFacade pagoFacade;
    @EJB
    private ParametrosFacade parametrosFacade;
    @EJB
    private PagoDetalleFacade pagoDetalleFacade;
    @EJB
    private PagoPublicidadFacade pagoPublicidadFacade;
    @EJB
    private TransactionFacade transactionFacade;
    @EJB
    private ClienteFacade clienteFacade;
    @Inject
    private UsuarioActual usuarioActual;

    private PagoHelper pagoHelper;
    private PagoHelper pagoHelperTMP;
    private TipoPagoHelper tipoPagoHelper;
    private Cliente clienteTMP;
    private RelacionFactura relacionFactura;
    private Pago pago;
    private String orden_pago;

    private List<PagoHelper> pagoHelpers;
    private List<TipoPagoHelper> tipoPagoHelpers;
    private List<TipoPagoHelper> tipoPagoHelpersTMP;
    private List<Factura> facturasTMP;
    private List<Talonario> talonarios;

    private final String uiError = "ui-state-error";
    private String error;
    private boolean desabilitarButton;
    private double valorPublicidad;
    private double valorComision;
    private boolean editar;

    //==========================================================================  
    public NuevaRelacionFacturaController() {
        relacionFactura = new RelacionFactura();
        relacionFactura.setFecha(Calendar.getInstance().getTime());
        valorComision = 0.0;
        valorPublicidad = 0.0;
    }

    //==========================================================================
    public RelacionFacturaFacade getRelacionFacturaFacade() {
        return relacionFacturaFacade;
    }

    public TalonarioFacade getTalonarioFacade() {
        return talonarioFacade;
    }

    public FacturaFacade getFacturaFacade() {
        return facturaFacade;
    }

    public PagoFacade getPagoFacade() {
        return pagoFacade;
    }

    public ParametrosFacade getParametrosFacade() {
        return parametrosFacade;
    }

    public PagoDetalleFacade getPagoDetalleFacade() {
        return pagoDetalleFacade;
    }

    public PagoPublicidadFacade getPagoPublicidadFacade() {
        return pagoPublicidadFacade;
    }

    public TransactionFacade getTransactionFacade() {
        return transactionFacade;
    }

    public ClienteFacade getClienteFacade() {
        return clienteFacade;
    }

    public UsuarioActual getUsuarioActual() {
        return usuarioActual;
    }

    public PagoHelper getPagoHelper() {
        return pagoHelper == null ? new PagoHelper() : pagoHelper;
    }

    public void setPagoHelper(PagoHelper pagoHelper) {
        this.pagoHelper = pagoHelper;
    }

    public PagoHelper getPagoHelperTMP() {
        return pagoHelperTMP == null ? new PagoHelper() : pagoHelperTMP;
    }

    public void setPagoHelperTMP(PagoHelper pagoHelperTMP) {
        this.pagoHelperTMP = pagoHelperTMP;
    }

    public TipoPagoHelper getTipoPagoHelper() {
        return tipoPagoHelper == null ? new TipoPagoHelper() : tipoPagoHelper;
    }

    public void setTipoPagoHelper(TipoPagoHelper tipoPagoHelper) {
        this.tipoPagoHelper = tipoPagoHelper;
    }

    public RelacionFactura getRelacionFactura() {
        return relacionFactura == null ? new RelacionFactura() : relacionFactura;
    }

    public void setRelacionFactura(RelacionFactura relacionFactura) {
        this.relacionFactura = relacionFactura;
    }

    public Pago getPago() {
        return pago == null ? new Pago() : pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    public Cliente getClienteTMP() {
        return clienteTMP;
    }

    public void setClienteTMP(Cliente clienteTMP) {
        this.clienteTMP = clienteTMP;
    }

    public List<PagoHelper> getPagoHelpers() {
        return pagoHelpers == null ? new ArrayList<PagoHelper>() : pagoHelpers;
    }

    public void setPagoHelpers(List<PagoHelper> pagoHelpers) {
        this.pagoHelpers = pagoHelpers;
    }

    public List<TipoPagoHelper> getTipoPagoHelpers() {
        return tipoPagoHelpers == null ? new ArrayList<TipoPagoHelper>() : tipoPagoHelpers;
    }

    public void setTipoPagoHelpers(List<TipoPagoHelper> tipoPagoHelpers) {
        this.tipoPagoHelpers = tipoPagoHelpers;
    }

    public List<TipoPagoHelper> getTipoPagoHelpersTMP() {
        return tipoPagoHelpersTMP == null ? new ArrayList<TipoPagoHelper>() : tipoPagoHelpersTMP;
    }

    public void setTipoPagoHelpersTMP(List<TipoPagoHelper> tipoPagoHelpersTMP) {
        this.tipoPagoHelpersTMP = tipoPagoHelpersTMP;
    }

    public List<Factura> getFacturasTMP() {
        return facturasTMP;
    }

    public void setFacturasTMP(List<Factura> facturasTMP) {
        this.facturasTMP = facturasTMP;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isDesabilitarButton() {
        return desabilitarButton;
    }

    public void setDesabilitarButton(boolean desabilitarButton) {
        this.desabilitarButton = desabilitarButton;
    }

    public double getValorPublicidad() {
        return valorPublicidad;
    }

    public void setValorPublicidad(double valorPublicidad) {
        this.valorPublicidad = valorPublicidad;
    }

    public double getValorComision() {
        return valorComision;
    }

    public void setValorComision(double valorComision) {
        this.valorComision = valorComision;
    }

    //==========================================================================    
    public void changedEmpleado(SelectEvent e) {
        if (e != null && e.getObject() != null) {
            Empleado empleado = (Empleado) e.getObject();
            talonarios = getTalonarioFacade().getTalonariosByTipo(TipoTalonario.RECIBO_CAJA, empleado);

            if (talonarios == null || talonarios.isEmpty()) {
                JsfUtil.addWarnMessage("El empleado seleccionado no cuenta con un talonario de pagos actualmente");
            } else {
                Talonario t = getTalonarioFacade().getActiveTalonario(TipoTalonario.RECIBO_CAJA, empleado);
                if (t != null) {
                    orden_pago = "" + t.getActual();
                    return;
                }
            }
        }
        getRelacionFactura().setVendedor(null);
    }

    //==========================================================================    
    public TipoPago[] getTiposPago() {
        return new TipoPago[]{TipoPago.CONTADO, TipoPago.CHEQUE, TipoPago.CONSIGNACION};
    }

    //==========================================================================    
    public TipoPagoAbono[] getTiposPagoAbono() {
        if (pago != null && getPago().getFormaPago() != TipoPago.CONTADO.getValor()) {
            return new TipoPagoAbono[]{TipoPagoAbono.ABONO};
        }
        return TipoPagoAbono.values();
    }

    //==========================================================================    
    public String getTiposPagoAbono(int tipo) {
        return TipoPagoAbono.getFromValue(tipo).getDetalle();
    }

    //==========================================================================    
    public String getFormaPago(int tipo) {
        String tipoPago = TipoPago.getFromValue(tipo).getDetalle();
        if (tipoPago.equals(TipoPago.CONTADO.getDetalle())) {
            tipoPago = "Efectivo";
        }
        return tipoPago;
    }

    //==========================================================================    
    public boolean requiereCheque() {
        return pago != null ? (getPago().getFormaPago() == TipoPago.CHEQUE.getValor()) : false;
    }

    //==========================================================================    
    public String mostrarCheque() {
        return requiereCheque() ? "" : "display:none;";
    }

    //==========================================================================    
    public boolean requiereCuenta() {
        if (pago != null) {
            return getPago().getFormaPago() == TipoPago.CONSIGNACION.getValor();
        }
        return false;
    }

    //==========================================================================    
    public boolean requiereTipoPublicidad() {
        return pago != null && getPago().getFormaPago() == TipoPago.CONTADO.getValor()
                && tipoPagoHelper != null
                && getTipoPagoHelper().getTipo() == TipoPagoAbono.PUBLICIDAD.getValor();
    }

    //==========================================================================    
    public String mostrarTipoPublicidad() {
        return requiereTipoPublicidad() ? "" : "display:none";
    }

    //==========================================================================    
    public String mostrarCuenta() {
        return requiereCuenta() ? "" : "display:none";
    }

    //==========================================================================    
    public String mostrarContado() {
        return pago != null ? (getPago().getFormaPago() == TipoPago.CONTADO.getValor() ? "" : "display:none;") : "";
    }

    //==========================================================================    
    public void onItemSelecCliente(SelectEvent event) {
        if (event.getObject() != null) {
            Cliente c = (Cliente) event.getObject();
            setFacturasTMP(getFacturaFacade().getFacturasPendientesByCliente(c));

            String ordenPago = getPago().getOrdenPago();
            setPago(new Pago());
            getPago().setOrdenPago(ordenPago);

            setTipoPagoHelper(new TipoPagoHelper());
            setTipoPagoHelpers(new ArrayList<TipoPagoHelper>());
            setTipoPagoHelpersTMP(new ArrayList<TipoPagoHelper>());
            setValorPublicidad(0.0);
            setValorComision(0.0);

            setDesabilitarButton(true);
        }
    }

    //==========================================================================    
    public void changedFactura(final AjaxBehaviorEvent event) {
        if (getPago().getFactura() != null) {
            Factura factura = getFacturaFacade().updatePagoPendiente(getPago().getFactura());
            getPago().setFactura(factura);
            setTipoPagoHelpers(new ArrayList<TipoPagoHelper>());
            setTipoPagoHelpersTMP(new ArrayList<TipoPagoHelper>());
            addPagosAnteriores();
            updatesValores();
            setDesabilitarButton(false);
        } else {
            getPago().setFactura(new Factura());
        }
    }

    //==========================================================================    
    public void cancelar() {
        RequestContext.getCurrentInstance().execute("PF('PagoCreateDialog').hide()");
    }

    //==========================================================================    
    public List<TipoPagoHelper> getTipoPagoHelpersPublicidad() {
        System.out.println("Publicidad");
        if (clienteTMP != null) {
            System.out.println("El cliente existe");
            return getPagoFacade().getPublicidadOrComisionByCliente(clienteTMP, 4, 0);            
        }
        return new ArrayList<>();
    }

    //==========================================================================    
    public List<TipoPagoHelper> getTipoPagoHelpersComision() {
        if (clienteTMP != null) {
            return getPagoFacade().getPublicidadOrComisionByCliente(clienteTMP, 4, 1);
        }
        return new ArrayList<>();
    }

    //==========================================================================  
    public boolean validarNumeroRecibo() {
        boolean isValid = true;
        if (getPagoFacade().existePago(getPago().getOrdenPago())) {
            isValid = false;
            setError(uiError);
            JsfUtil.addErrorMessage("El numero de recibo " + getPago().getOrdenPago() + " ya esta en uso");
        } else {
            for (PagoHelper ph : getPagoHelpers()) {
                if (ph.getPago().getOrdenPago().equals(getPago().getOrdenPago())) {
                    if ((pagoHelper != null) && (ph.getPago().getOrdenPago().equals(getPagoHelper().getPago().getOrdenPago()))) {
                    } else {
                        isValid = false;
                        setError(uiError);
                        JsfUtil.addErrorMessage("El numero de recibo " + getPago().getOrdenPago() + " ya esta en uso");
                        break;
                    }
                }
            }
        }

        if (isValid) {
            Long reciboActual;
            try {
                reciboActual = Long.valueOf(getPago().getOrdenPago());
            } catch (Exception e) {
                reciboActual = null;
            }

            if (reciboActual != null) {
                boolean numValido = false;
                for (Talonario t : talonarios) {
                    if ((reciboActual >= t.getInicial()) && (reciboActual <= t.getFinal1())) {
                        setError("");
                        numValido = true;
                        break;
                    }
                }
                if (!numValido) {
                    isValid = false;
                    setError(uiError);
                    JsfUtil.addErrorMessage("el numero de recibo " + pago.getOrdenPago() + " no esta permitido");
                }
            } else {
                isValid = false;
                setError(uiError);
                JsfUtil.addErrorMessage("Debe poner un numero de recibo valido");
            }
        }
        return isValid;
    }

    //==========================================================================  
    public void prepareCreatePago() {
        editar = false;
        setPago(new Pago());
        getPago().setOrdenPago(orden_pago);
        setClienteTMP(null);
        setPagoHelper(null);
        setTipoPagoHelper(new TipoPagoHelper());
        setPagoHelperTMP(new PagoHelper());
        setTipoPagoHelpers(new ArrayList<TipoPagoHelper>());
        setTipoPagoHelpersTMP(new ArrayList<TipoPagoHelper>());
        setValorComision(0.0);
        setValorPublicidad(0.0);
        setFacturasTMP(new ArrayList<Factura>());
        setDesabilitarButton(true);
    }

    //==========================================================================  
    public void changedFormaPago(final AjaxBehaviorEvent event) {
        if (getPago().getFactura() != null) {
            setTipoPagoHelpers(new ArrayList<TipoPagoHelper>());
            setTipoPagoHelpersTMP(new ArrayList<TipoPagoHelper>());
            addPagosAnteriores();
            updatesValores();
        }
    }

    //==========================================================================
    private void updatesValores() {
        if (getPago().getFactura() != null) {

            double valorTotal = 0.0;

            for (PagoHelper ph : getPagoHelpers()) {
                if (Objects.equals(ph.getPago().getFactura().getId(), getPago().getFactura().getId())) {
                    if ((pagoHelper != null) && (ph.getId() == getPagoHelper().getId())) {
                    } else {
                        for (TipoPagoHelper tph : ph.getTipoPagoHelpers()) {

                            boolean repetido = false;
                            for (TipoPagoHelper tph1 : getTipoPagoHelpersTMP()) {
                                if (tph.getTipo() == tph1.getTipo()) {
                                    if (tph.getTipo() == TipoPagoAbono.PUBLICIDAD.getValor()) {
                                        if (Objects.equals(tph.getTipoPublicidad().getId(), tph1.getTipoPublicidad().getId())) {
                                            tph1.setValor(tph1.getValor() + tph.getValor());
                                            repetido = true;
                                        }
                                    } else {
                                        tph1.setValor(tph1.getValor() + tph.getValor());
                                        repetido = true;
                                    }
                                }
                            }

                            if (!repetido) {
                                TipoPagoHelper tphTMP = new TipoPagoHelper();
                                tphTMP.setId(tph.getId());
                                tphTMP.setTipo(tph.getTipo());
                                tphTMP.setTipoPublicidad(tph.getTipoPublicidad());
                                tphTMP.setValor(tph.getValor());
                                getTipoPagoHelpersTMP().add(tphTMP);
                            }

                        }
                    }
                }
            }

            for (TipoPagoHelper tph : getTipoPagoHelpersTMP()) {
                valorTotal += tph.getValor();
            }

            getPago().getFactura().setSaldo(getPago().getFactura().getTotalPagar() - valorTotal);
            getPago().setValorTotal(valorTotal);
        }
    }

    //==========================================================================  
    private void addPagosAnteriores() {
        int count = 1;
        List<Pago> listPagos = getPagoFacade().getPagosByFactura(getPago().getFactura());
        for (Pago p : listPagos) {
            List<PagoDetalle> listPagoDetalle = getPagoDetalleFacade().getPagoDetallesByPago(p);
            for (PagoDetalle pd : listPagoDetalle) {
                PagoPublicidad pp = null;
                if (pd.getTipo() == TipoPagoAbono.PUBLICIDAD.getValor()) {
                    pp = getPagoPublicidadFacade().getPagoPublicidadByPagoDetalle(pd);
                }
                validarPago(pd, pp, count);
            }
        }
    }

    //==========================================================================  
    private void validarPago(PagoDetalle pagoDetalle, PagoPublicidad pagoPublicidad, int count) {
        boolean repetido = false;
        for (TipoPagoHelper tph : getTipoPagoHelpersTMP()) {
            if ((tph.getTipo() == TipoPagoAbono.PUBLICIDAD.getValor()) && pagoPublicidad != null) {
                if (tph.getTipoPublicidad().getId().equals(pagoPublicidad.getTipo().getId())) {
                    tph.setValor(tph.getValor() + pagoDetalle.getValor());
                    repetido = true;
                }
            } else if (pagoDetalle.getTipo() == tph.getTipo()) {
                tph.setValor(tph.getValor() + pagoDetalle.getValor());
                repetido = true;
            }
        }

        if (!repetido) {
            getTipoPagoHelpersTMP().add(new TipoPagoHelper(count++, null, pagoDetalle.getTipo(), pagoDetalle.getValor(), (pagoPublicidad == null ? null : pagoPublicidad.getTipo()), null));
        }
    }

    //==========================================================================  
    public void prepareList() {
        if (clienteTMP != null && clienteTMP.getId() != null) {            
        } else {
            JsfUtil.addErrorMessage("Debe seleccionar un cliente");
        }
    }

    //==========================================================================   
    public void addPagoDetalle() {
        if (validarPagoDetalle()) {
            boolean existe = false;
            for (TipoPagoHelper tph : getTipoPagoHelpers()) {
                boolean repetido = false;

                if (tph.getTipo() == getTipoPagoHelper().getTipo()) {
                    if (tph.getTipo() == TipoPagoAbono.PUBLICIDAD.getValor()) {
                        if (Objects.equals(tph.getTipoPublicidad().getId(), getTipoPagoHelper().getTipoPublicidad().getId())) {
                            repetido = true;
                        }
                    } else {
                        repetido = true;
                    }
                }

                if (repetido) {
                    existe = true;
                    int io = getTipoPagoHelpers().indexOf(tph);
                    getTipoPagoHelpers().get(io).setValor(getTipoPagoHelpers().get(io).getValor() + getTipoPagoHelper().getValor());
                    break;
                }
            }

            if (!existe) {
                TipoPagoHelper tph = new TipoPagoHelper();
                tph.setId(getTipoPagoHelpers().size() + 1);
                tph.setTipo(getTipoPagoHelper().getTipo());
                tph.setValor(getTipoPagoHelper().getValor());
                tph.setTipoPublicidad(getTipoPagoHelper().getTipoPublicidad());
                getTipoPagoHelpers().add(tph);
            }

            setTipoPagoHelpersTMP(new ArrayList<TipoPagoHelper>());
            updateListTMP(getTipoPagoHelpers());
            setTipoPagoHelper(new TipoPagoHelper());
            addPagosAnteriores();
            updatesValores();
        }

    }

    //==========================================================================   
    private boolean validarPagoDetalle() {
        boolean isValid = true;
        if ((getTipoPagoHelper().getValor() <= 0.0) || ((getTipoPagoHelper().getTipo() == TipoPagoAbono.PUBLICIDAD.getValor()) && (getTipoPagoHelper().getTipoPublicidad() == null))) {
            if (getTipoPagoHelper().getValor() <= 0.0) {
                JsfUtil.addErrorMessage("El campo valor debe ser mayor a 0");
                isValid = false;
            }
            if (getTipoPagoHelper().getTipo() == TipoPagoAbono.PUBLICIDAD.getValor()) {
                if (getTipoPagoHelper().getTipoPublicidad() == null) {
                    JsfUtil.addErrorMessage("Debe seleccionar un tipo de publicidad");
                    isValid = false;
                }
            }
        } else if (getTipoPagoHelper().getValor() > getPago().getFactura().getSaldo()) {
            JsfUtil.addErrorMessage("El valor supera el valor pendiente de la factura");
            isValid = false;
        }
        
        return isValid;
    }

    //==========================================================================   
    public void removePagoDetalle(TipoPagoHelper tph) {
        getTipoPagoHelpers().remove(tph);
        getTipoPagoHelpersTMP().clear();
        addPagosAnteriores();
        updateListTMP(getTipoPagoHelpers());
        updatesValores();
    }

    //==========================================================================   
    public void create(boolean crearNuevo) {
        if (validarNumeroRecibo()) {

            try {
                System.out.println("Numero Orden: " + getPago().getOrdenPago());
                System.out.println("Vendedor: " + getRelacionFactura().getVendedor());
                if (!actulizarTalonario(getPago().getOrdenPago())) {
                    JsfUtil.addErrorMessage("Error actualizando el talonario");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!getTipoPagoHelpers().isEmpty()) {

                getPago().setFecha(getRelacionFactura().getFecha());
                getPago().setEstado(EstadoPago.REALIZADO.getValor());
                getPago().setUsuario(getUsuarioActual().getUsuario());
                getPago().setValorTotal(getValorReal());
                boolean isValid = true;
                try {
                    if (editar) {
                        for (PagoHelper ph : getPagoHelpers()) {
                            if (ph.getId() == getPagoHelper().getId()) {
                                int io = getPagoHelpers().indexOf(getPagoHelper());
                                getPagoHelpers().get(io).setPago(getPago());
                                getPagoHelpers().get(io).setTipoPagoHelpers(getTipoPagoHelpers());
                                break;
                            }
                        }
                    } else {
                        if (pagoHelpers == null) {
                            pagoHelpers = new ArrayList<>();
                        }
                        PagoHelper ph = new PagoHelper();
                        ph.setId(getPagoHelpers().size() + 1);
                        ph.setPago(getPago());
                        ph.setTipoPagoHelpers(getTipoPagoHelpers());
                        getPagoHelpers().add(ph);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    isValid = false;
                }

                if (isValid) {
                    JsfUtil.addSuccessMessage("El recibo fue " + (!editar ? "agregado" : "modificado") + " correctamente");
                    if (!crearNuevo) {
                        RequestContext.getCurrentInstance().execute("PF('PagoCreateDialog').hide()");
                    } else {
                        prepareCreatePago();
                    }
                } else {
                    JsfUtil.addErrorMessage("Ocurrio un error agregando el recibo");
                }

            } else {
                JsfUtil.addErrorMessage("Debe agregar al menos un pago");
            }
        }
    }

    //==========================================================================   
    private double getValorReal() {
        double valor = 0.0;
        for (TipoPagoHelper tph : getTipoPagoHelpers()) {
            valor += tph.getValor();
        }
        return valor;
    }

    //==========================================================================
    public void removeRecibo() {
        int io = getPagoHelpers().indexOf(getPagoHelper());
        getPagoHelpers().remove(io);
    }

    //==========================================================================    
    public void prepareEditPago() {
        editar = true;
        guardarObjetTMP();
        setClienteTMP(getPagoHelper().getPago().getFactura().getCliente());
        setPago(getPagoHelper().getPago());
        setTipoPagoHelpers(new ArrayList<TipoPagoHelper>());
        getTipoPagoHelpers().addAll(getPagoHelper().getTipoPagoHelpers());
        setTipoPagoHelpersTMP(new ArrayList<TipoPagoHelper>());

        addPagosAnteriores();

        updateListTMP(getTipoPagoHelpers());

        updatesValores();
        setDesabilitarButton(false);
    }

    //==========================================================================    
    private void guardarObjetTMP() {
        setPagoHelperTMP(new PagoHelper());
        getPagoHelperTMP().setId(getPagoHelper().getId());
        getPagoHelperTMP().setPago(getPagoHelper().getPago());
        getPagoHelperTMP().setTipoPagoHelpers(getPagoHelper().getTipoPagoHelpers());
    }

    //==========================================================================
    private void updateListTMP(List<TipoPagoHelper> tipoPagoHelpers) {
        for (TipoPagoHelper tph : tipoPagoHelpers) {
            boolean repetido = false;
            for (TipoPagoHelper tph1 : getTipoPagoHelpersTMP()) {
                if (tph.getTipo() == tph1.getTipo()) {
                    if (tph.getTipo() == TipoPagoAbono.PUBLICIDAD.getValor()) {
                        if (Objects.equals(tph.getTipoPublicidad().getId(), tph1.getTipoPublicidad().getId())) {
                            tph1.setValor(tph1.getValor() + tph.getValor());
                            repetido = true;
                        }
                    } else {
                        tph1.setValor(tph1.getValor() + tph.getValor());
                        repetido = true;
                    }
                }
            }

            if (!repetido) {
                TipoPagoHelper tphTMP = new TipoPagoHelper();
                tphTMP.setId(tph.getId());
                tphTMP.setTipo(tph.getTipo());
                tphTMP.setTipoPublicidad(tph.getTipoPublicidad());
                tphTMP.setValor(tph.getValor());
                getTipoPagoHelpersTMP().add(tphTMP);
            }
        }
    }

    //==========================================================================
    public void crearRecibo() {
        if (pagoHelpers.size() > 0) {
            try {
                getTransactionFacade().crearPago(pagoHelpers, relacionFactura);
                if (!JsfUtil.isValidationFailed()) {
                    JsfUtil.addSuccessMessage("El pago fue guardado correctamente");
                    pagoHelpers = null;
                } else {
                    JsfUtil.addErrorMessage("Error guardando el Pago");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            JsfUtil.addErrorMessage("Debe agregar al menos un recibo");
        }
    }

    public List<Cliente> llenarCliente(String query) {
        return getClienteFacade().getClienteByQuery(query, relacionFactura.getVendedor());
    }

    private boolean actulizarTalonario(String opTMP) {
        try {
            if (talonarios != null && !talonarios.isEmpty()) {
                Long ordenPago = Long.valueOf(opTMP);
                Talonario talonarioTMP = new Talonario();
                for (Talonario talonario : talonarios) {
                    if (ordenPago >= talonario.getInicial() && ordenPago <= talonario.getFinal1()) {
                        talonarioTMP = talonario;
                        break;
                    }
                }

                if (talonarioTMP.getId() != null) {
                    return getTalonarioFacade().update(talonarioTMP, ordenPago, 2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
