package jp.controllers;

import jp.entidades.Pago;
import jp.util.JsfUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import jp.entidades.Cliente;
import jp.entidades.Empleado;
import jp.entidades.Factura;
import jp.entidades.PagoDetalle;
import jp.entidades.PagoHelper;
import jp.entidades.PagoPublicidad;
import jp.entidades.RelacionFactura;
import jp.entidades.Talonario;
import jp.entidades.TipoPagoHelper;
import jp.facades.FacturaFacade;
import jp.facades.PagoDetalleFacade;
import jp.facades.PagoFacade;
import jp.facades.PagoPublicidadFacade;
import jp.facades.ParametrosFacade;
import jp.facades.RelacionFacturaFacade;
import jp.facades.TalonarioFacade;
import jp.facades.TransactionFacade;
import jp.seguridad.UsuarioActual;
import jp.util.EstadoPago;
import jp.util.TipoPago;
import jp.util.TipoPagoAbono;
import jp.util.TipoTalonario;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

@ManagedBean(name = "nuevaRelacionFacturaController")
@ViewScoped
public class NuevaRelacionFacturaController implements Serializable {

    @EJB
    private RelacionFacturaFacade ejbFacade;
    @EJB
    private TalonarioFacade talonarioFacade;
    @EJB
    private FacturaFacade facturaFacade;
    @EJB
    private PagoFacade pagoFacade;
    @EJB
    private PagoDetalleFacade pagoDetalleFacade;
    @EJB
    private PagoPublicidadFacade pagoPublicidadFacade;
    @EJB
    private ParametrosFacade parametrosFacade;
    @EJB
    private TransactionFacade transactionFacade;
    @Inject
    private UsuarioActual usuarioActual;

    private List<PagoHelper> pagoHelpers;
    private List<TipoPagoHelper> tipoPagoHelpers;
    private List<TipoPagoHelper> tipoPagoHelpersTMP;
    private List<TipoPagoHelper> pagosAnteriores;

    private List<Factura> facturasTMP;
    private List<Talonario> talonarios;
    private List<Pago> pagos = null;

    private Pago pago;
    private PagoHelper pagoHelper;
    private PagoHelper pagoHelperTMP;
    private Cliente clienteTMP;
    private TipoPagoHelper tipoPagoHelper;
    private RelacionFactura selected;

    private double valorPublicidad;
    private double valorComision;

    private boolean desabilitarButton;
    private boolean editar;

    private final String uiError;
    private String error;
    private int count = 0;

    public NuevaRelacionFacturaController() {
        uiError = "ui-state-error";
    }

    @PostConstruct
    public void init() {
        selected = prepareCreate();
        valorComision = 0.0;
        valorPublicidad = 0.0;
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

    public Pago getPago() {
        if (pago == null) {
            return new Pago();
        }
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    public PagoHelper getPagoHelper() {
        if (pagoHelper == null) {
            return new PagoHelper();
        }
        return pagoHelper;
    }

    public void setPagoHelper(PagoHelper pagoHelper) {
        this.pagoHelper = pagoHelper;
    }

    public TipoPagoHelper getTipoPagoHelper() {
        if (tipoPagoHelper == null) {
            return new TipoPagoHelper();
        }
        return tipoPagoHelper;
    }

    public void setTipoPagoHelper(TipoPagoHelper tipoPagoHelper) {
        this.tipoPagoHelper = tipoPagoHelper;
    }

    public List<PagoHelper> getPagoHelpers() {
        if (pagoHelpers == null) {
            pagoHelpers = new ArrayList<>();
        }
        return pagoHelpers;
    }

    public List<Pago> getPagos() {
        if (pagos == null) {
            pagos = getPagoFacade().findAll();
        }
        return pagos;
    }

    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }

    public List<TipoPagoHelper> getTipoPagoHelpersPublicidad() {
        if (pagosAnteriores == null) {
            return pagosAnteriores = new ArrayList<>();
        }

        List<TipoPagoHelper> listTMP = new ArrayList<>();
        for (TipoPagoHelper tph : pagosAnteriores) {
            if (tph.getTipo() == TipoPagoAbono.PUBLICIDAD.getValor()) {
                listTMP.add(tph);
            }
        }
        return listTMP;
    }

    public List<TipoPagoHelper> getTipoPagoHelpersComision() {
        if (pagosAnteriores == null) {
            pagosAnteriores = new ArrayList<>();
        }
        List<TipoPagoHelper> listTMP = new ArrayList<>();
        for (TipoPagoHelper tph : pagosAnteriores) {
            if (tph.getTipo() == TipoPagoAbono.COMISION.getValor()) {
                listTMP.add(tph);
            }
        }
        return listTMP;
    }

    public List<TipoPagoHelper> getTipoPagoHelpers() {
        if (tipoPagoHelpers == null) {
            tipoPagoHelpers = new ArrayList<>();
        }
        return tipoPagoHelpers;
    }

    public RelacionFactura getSelected() {
        return selected;
    }

    public void setSelected(RelacionFactura selected) {
        this.selected = selected;
    }

    public Cliente getClienteTMP() {
        return clienteTMP;
    }

    public void setClienteTMP(Cliente clienteTMP) {
        this.clienteTMP = clienteTMP;
    }

    public List<Factura> getFacturasTMP() {
        if (facturasTMP == null) {
            facturasTMP = new ArrayList<>();
        }
        return facturasTMP;
    }

    public void setFacturasTMP(List<Factura> facturasTMP) {
        this.facturasTMP = facturasTMP;
    }

    public void onItemSelecCliente(SelectEvent event) {
        if (event.getObject() != null) {
            Cliente c = (Cliente) event.getObject();
            facturasTMP = getFacturaFacade().getFacturasPendientesByCliente(c);
            clean();
            desabilitarButton = true;
        }
    }

    public TransactionFacade getTransactionFacade() {
        return transactionFacade;
    }

    public PagoPublicidadFacade getPagoPublicidadFacade() {
        return pagoPublicidadFacade;
    }

    public PagoDetalleFacade getPagoDetalleFacade() {
        return pagoDetalleFacade;
    }

    public PagoFacade getPagoFacade() {
        return pagoFacade;
    }

    private RelacionFacturaFacade getFacade() {
        return ejbFacade;
    }

    public ParametrosFacade getParametrosFacade() {
        return parametrosFacade;
    }

    public TalonarioFacade getTalonarioFacade() {
        return talonarioFacade;
    }

    public FacturaFacade getFacturaFacade() {
        return facturaFacade;
    }

    public RelacionFactura prepareCreate() {
        selected = new RelacionFactura();
        selected.setFecha(Calendar.getInstance().getTime());
        return selected;
    }

    public Pago prepareCreatePago() {
        editar = false;
        pago = new Pago();
        clienteTMP = null;
        pagoHelper = null;
        pagoHelperTMP = new PagoHelper();
        limpiar();
        desabilitarButton = true;
        return pago;
    }

    public void prepareEditPago() {
        editar = true;
        guardarObjetTMP();
        clienteTMP = pagoHelper.getPago().getFactura().getCliente();
        pago = pagoHelper.getPago();
        tipoPagoHelpers = new ArrayList<>();
        tipoPagoHelpers.addAll(pagoHelper.getTipoPagoHelpers());
        tipoPagoHelpersTMP = new ArrayList<>();
        tipoPagoHelpersTMP.addAll(tipoPagoHelpers);
        updatePublicidadAndComicion();
        updateValorPendienteOnly();
    }

    private void guardarObjetTMP() {
        pagoHelperTMP = new PagoHelper();
        pagoHelperTMP.setId(pagoHelper.getId());
        pagoHelperTMP.setPago(pagoHelper.getPago());
        pagoHelperTMP.setTipoPagoHelpers(pagoHelper.getTipoPagoHelpers());
    }

    public void cancelar() {
        if (editar) {
            for (PagoHelper ph : pagoHelpers) {
                if (ph.getId() == pagoHelperTMP.getId()) {
                    ph.setPago(pagoHelperTMP.getPago());
                    ph.setTipoPagoHelpers(pagoHelperTMP.getTipoPagoHelpers());
                    break;
                }
            }
        }
        RequestContext.getCurrentInstance().execute("PF('PagoCreateDialog').hide()");
    }

    public void editar() {
    }

    public void create(boolean crearNuevo) {
        if (validarCrear(true)) {
            pago.setFecha(selected.getFecha());
            pago.setEstado(EstadoPago.REALIZADO.getValor());
            pago.setUsuario(usuarioActual.getUsuario());
            getValorPago();
            boolean isValid = true;
            try {
                if (pagoHelper != null) {
                    for (PagoHelper ph : pagoHelpers) {
                        if (ph.getId() == pagoHelper.getId()) {
                            int io = pagoHelpers.indexOf(pagoHelper);
                            pagoHelpers.get(io).setPago(pago);
                            pagoHelpers.get(io).setTipoPagoHelpers(tipoPagoHelpers);
                            break;
                        }
                    }
                } else {
                    PagoHelper ph = new PagoHelper();
                    ph.setId(pagoHelpers.size() + 1);
                    ph.setPago(pago);
                    ph.setTipoPagoHelpers(tipoPagoHelpers);
                    pagoHelpers.add(ph);
                }
            } catch (Exception e) {
                isValid = false;
            }

            if (isValid) {
                JsfUtil.addSuccessMessage("El recibo fue " + (pagoHelper == null ? "agregado" : "modificado") + " correctamente");
                if (!crearNuevo) {
                    RequestContext.getCurrentInstance().execute("PF('PagoCreateDialog').hide()");
                } else {
                    prepareCreatePago();
                }
            } else {
                JsfUtil.addErrorMessage("Ocurrio un error agregando el recibo");
            }
            updatePublicidadAndComicion();
        } else {
            updateValorPendienteOnly();
        }
    }

    private boolean validarCrear(boolean validarPagos) {
        boolean isVaild = true;

        if (getPagoFacade().existePago(pago.getOrdenPago())) {
            isVaild = false;
            setError(uiError);
            JsfUtil.addErrorMessage("El numero de recibo " + pago.getOrdenPago() + " ya esta en uso");
        } else {
            for (PagoHelper ph : pagoHelpers) {
                if (ph.getPago().getOrdenPago().equals(pago.getOrdenPago())) {
                    if ((pagoHelper != null) && (ph.getPago().getOrdenPago().equals(pagoHelper.getPago().getOrdenPago()))) {
                    } else {
                        isVaild = false;
                        setError(uiError);
                        JsfUtil.addErrorMessage("El numero de recibo " + pago.getOrdenPago() + " ya esta en uso");
                        break;
                    }
                }
            }
        }

        if (isVaild) {
            Long reciboActual;
            try {
                reciboActual = Long.valueOf(pago.getOrdenPago());
            } catch (Exception e) {
                reciboActual = null;
            }

            if (reciboActual != null) {
                boolean numeroPermitido = false;
                boolean entrar = true;
                for (Talonario t : talonarios) {
                    if ((reciboActual >= t.getInicial()) && (reciboActual <= t.getFinal1())) {
                        setError("");
                        if (validarPagos) {
                            isVaild = validarPagos();
                            if (isVaild) {
                                numeroPermitido = true;
                            }
                        } else {
                            numeroPermitido = true;
                        }
                        entrar = false;
                        break;
                    }
                }

                if (!numeroPermitido || entrar) {
                    isVaild = false;
                    setError(uiError);
                    JsfUtil.addErrorMessage("el numero de recibo " + pago.getOrdenPago() + " no esta permitido");
                }
            } else {
                isVaild = false;
                setError(uiError);
                JsfUtil.addErrorMessage("Debe poner un numero de recibo valido");
            }
        }

        return isVaild;
    }

    private boolean validarPagos() {
        boolean isValid = true;
        if (tipoPagoHelpers.size() <= 0) {
            isValid = false;
            JsfUtil.addErrorMessage("Debe agregar al menos un pago");
        }
        return isValid;
    }

    @FacesConverter(forClass = RelacionFactura.class)
    public static class RelacionFacturaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            NuevaRelacionFacturaController controller = (NuevaRelacionFacturaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "nuevaRelacionPagoController");
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
            if (object instanceof RelacionFactura) {
                RelacionFactura o = (RelacionFactura) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), RelacionFactura.class.getName()});
                return null;
            }
        }

    }

    public void changedEmpleado(SelectEvent e) {
        System.out.println("Se seleccionó el empleado...");
        if (e != null && e.getObject() != null) {
            talonarios = getTalonarioFacade().getTalonariosByTipo(TipoTalonario.RECIBO_CAJA, (Empleado) e.getObject());
            if (talonarios == null || talonarios.isEmpty()) {
                JsfUtil.addWarnMessage("El empleado seleccionado no cuenta con un talonario de pagos actualmente");
            } else {
                return;
            }
        }
        selected.setVendedor(null);
    }

    public void changedFactura(final AjaxBehaviorEvent event) {
        limpiar();
        if (pago.getFactura() != null) {
            Factura factura = facturaFacade.updatePagoPendiente(pago.getFactura());
            pago.setFactura(factura);
            updatePublicidadAndComicion();
            tipoPagoHelpers = null;
            desabilitarButton = false;
        } else {
            pago.setFactura(new Factura());
        }
    }

    private void limpiar() {
        tipoPagoHelper = new TipoPagoHelper();
        tipoPagoHelpers = new ArrayList<>();
        tipoPagoHelpersTMP = new ArrayList<>();
        pagosAnteriores = new ArrayList<>();
        valorPublicidad = 0.0;
        valorComision = 0.0;
    }

    public void changedFormaPago(final AjaxBehaviorEvent event) {
        if (pago.getFactura() != null) {
            limpiar();
            updatePublicidadAndComicion();
        }
    }

    public TipoPago[] getTiposPago() {
        return new TipoPago[]{TipoPago.CONTADO, TipoPago.CHEQUE, TipoPago.CONSIGNACION};
    }

    public TipoPagoAbono[] getTiposPagoAbono() {
        if (pago != null && pago.getFormaPago() != TipoPago.CONTADO.getValor()) {
            return new TipoPagoAbono[]{TipoPagoAbono.ABONO};
        }
        return TipoPagoAbono.values();
    }

    public String getTiposPagoAbono(int tipo) {
        return TipoPagoAbono.getFromValue(tipo).getDetalle();
    }

    public String getFormaPago(int tipo) {
        String tipoPago = TipoPago.getFromValue(tipo).getDetalle();
        if (tipoPago.equals(TipoPago.CONTADO.getDetalle())) {
            tipoPago = "Efectivo";
        }
        return tipoPago;
    }

    public boolean requiereCheque() {
        if (pago != null) {
            return pago.getFormaPago() == TipoPago.CHEQUE.getValor();
        }
        return false;
    }

    public String mostrarCheque() {
        return requiereCheque() ? "" : "display:none;";
    }

    public boolean requiereCuenta() {
        if (pago != null) {
            return pago.getFormaPago() == TipoPago.CONSIGNACION.getValor();
        }
        return false;
    }

    public boolean requiereTipoPublicidad() {
        return pago != null && pago.getFormaPago() == TipoPago.CONTADO.getValor()
                && tipoPagoHelper != null
                && tipoPagoHelper.getTipo() == TipoPagoAbono.PUBLICIDAD.getValor();
    }

    public String mostrarTipoPublicidad() {
        return requiereTipoPublicidad() ? "" : "display:none";
    }

    public String mostrarCuenta() {
        return requiereCuenta() ? "" : "display:none";
    }

    public String mostrarContado() {
        if (pago != null) {
            return pago.getFormaPago() == TipoPago.CONTADO.getValor() ? "" : "display:none;";
        }
        return "";
    }

    public void addPagoDetalle() {
        if (validatePagoDetalle()) {
            boolean existe = false;
            for (TipoPagoHelper tph : tipoPagoHelpers) {
                boolean repetido = false;

                if (tph.getTipo() == tipoPagoHelper.getTipo()) {
                    if (tph.getTipo() == TipoPagoAbono.PUBLICIDAD.getValor()) {
                        if (Objects.equals(tph.getTipoPublicidad().getId(), tipoPagoHelper.getTipoPublicidad().getId())) {
                            repetido = true;
                        }
                    } else {
                        repetido = true;
                    }
                }

                if (repetido) {
                    existe = true;
                    int io = tipoPagoHelpers.indexOf(tph);
                    tipoPagoHelpers.get(io).setValor(tipoPagoHelpers.get(io).getValor() + tipoPagoHelper.getValor());
                    break;
                }
            }

            if (!existe) {
                TipoPagoHelper tph = new TipoPagoHelper();
                tph.setId(tipoPagoHelpers.size() + 1 + (count++));
                tph.setTipo(tipoPagoHelper.getTipo());
                tph.setValor(tipoPagoHelper.getValor());
                tph.setTipoPublicidad(tipoPagoHelper.getTipoPublicidad());
                tipoPagoHelpers.add(tph);
            }

            tipoPagoHelpersTMP.clear();
            tipoPagoHelpersTMP.addAll(tipoPagoHelpers);
            tipoPagoHelper = new TipoPagoHelper();
            updatePublicidadAndComicion();
        }

    }

    public void removePagoDetalle(TipoPagoHelper tph) {
        tipoPagoHelpers.remove(tph);
        tipoPagoHelpersTMP.clear();
        tipoPagoHelpersTMP.addAll(tipoPagoHelpers);
        updatePublicidadAndComicion();
    }

    public void removeRecibo() {
        int io = pagoHelpers.indexOf(pagoHelper);
        pagoHelpers.remove(io);
    }

    private boolean validatePagoDetalle() {
        boolean isValid = true;
        if ((tipoPagoHelper.getValor() <= 0.0) || ((tipoPagoHelper.getTipo() == TipoPagoAbono.PUBLICIDAD.getValor()) && (tipoPagoHelper.getTipoPublicidad() == null))) {
            if (tipoPagoHelper.getValor() <= 0.0) {
                JsfUtil.addErrorMessage("El campo valor debe ser mayor a 0");
                isValid = false;
            }
            if (tipoPagoHelper.getTipo() == TipoPagoAbono.PUBLICIDAD.getValor()) {
                if (tipoPagoHelper.getTipoPublicidad() == null) {
                    JsfUtil.addErrorMessage("Debe seleccionar un tipo de publicidad");
                    isValid = false;
                }
            }
        } else {

            if (tipoPagoHelper.getValor() > pago.getFactura().getSaldo()) {
                JsfUtil.addErrorMessage("El valor supera el valor pendiente de la factura");
                isValid = false;
            }

            if (pago.getFormaPago() == TipoPago.CONTADO.getValor()) {
                if (tipoPagoHelper.getTipo() == TipoPagoAbono.PUBLICIDAD.getValor()) {
                    if (tipoPagoHelper.getValor() > valorPublicidad) {
                        JsfUtil.addErrorMessage("El valor supera el valor permitido por publicidad");
                        isValid = false;
                    }
                } else if (tipoPagoHelper.getTipo() == TipoPagoAbono.COMISION.getValor()) {
                    if (tipoPagoHelper.getValor() > valorComision) {
                        JsfUtil.addErrorMessage("El valor supera el valor permitido por comision");
                        isValid = false;
                    }
                }
            }
        }
        return isValid;
    }

    private void updatePublicidadAndComicion() {
        if (pago.getFactura() != null) {
            float porcentajePublicidad;
            try {
                porcentajePublicidad = getParametrosFacade().getParametros().getPorcentajePublicidad();
            } catch (Exception e) {
                porcentajePublicidad = 0F;
            }
            double valorP = Math.round(pago.getFactura().getTotalPagar() * (porcentajePublicidad / 100));

            double porcentajeComision;
            try {
                porcentajeComision = selected.getVendedor().getTipoEmpleado().getComision();
            } catch (Exception e) {
                porcentajeComision = 0.0;
            }
            double valorC = Math.round(pago.getFactura().getTotalPagar() * (porcentajeComision / 100));

            double vPlucidad = 0.0, vComision = 0.0, aPublicidad = 0.0, aComision = 0.0;

            for (PagoHelper ph : pagoHelpers) {
                if (Objects.equals(ph.getPago().getFactura().getId(), pago.getFactura().getId())) {
                    if ((pagoHelper != null) && (ph.getId() == pagoHelper.getId())) {
                    } else {
                        tipoPagoHelpersTMP.addAll(ph.getTipoPagoHelpers());
                    }
                }
            }

            for (TipoPagoHelper tph : tipoPagoHelpersTMP) {
                if (tph.getTipo() == TipoPagoAbono.PUBLICIDAD.getValor()) {
                    vPlucidad += tph.getValor();
                } else if (tph.getTipo() == TipoPagoAbono.COMISION.getValor()) {
                    vComision += tph.getValor();
                }
            }

            List<Pago> listPagos = getPagoFacade().getPagosByFactura(pago.getFactura());
            getPagosAnteriores(listPagos);
            for (TipoPagoHelper tph : pagosAnteriores) {
                if (tph.getTipo() == TipoPagoAbono.PUBLICIDAD.getValor()) {
                    aPublicidad += tph.getValor();
                } else if (tph.getTipo() == TipoPagoAbono.COMISION.getValor()) {
                    aComision += tph.getValor();
                }
            }

            double valor = 0.0;
            if (listPagos != null && !listPagos.isEmpty()) {
                for (Pago pago : listPagos) {
                    valor += pago.getValorTotal();
                }
            }

            updateValorTotal(valor);
            setValorPublicidad(valorP - vPlucidad - aPublicidad);
            setValorComision(valorC - vComision - aComision);
            updateValorPendiente(valor);
        }
    }

    private void clean() {
        String ordenPago = pago.getOrdenPago();
        pago = new Pago();
        pago.setOrdenPago(ordenPago);
        limpiar();
    }

    private void updateValorPendiente(double valor) {
        if (pago.getFactura() != null) {
            for (TipoPagoHelper tph : tipoPagoHelpersTMP) {
                valor += tph.getValor();
            }

            pago.getFactura().setSaldo(pago.getFactura().getTotalPagar() - valor);
        }
    }

    private void updateValorTotal(double valor) {
//        for (TipoPagoHelper tph : tipoPagoHelpers) {
//            valor += tph.getValor();
//        }

        for (TipoPagoHelper tph : tipoPagoHelpersTMP) {
            valor += tph.getValor();
        }
        pago.setValorTotal(valor);
    }

    private void getPagosAnteriores(List<Pago> listPagos) {
        pagosAnteriores = new ArrayList<>();
        for (Pago pago : listPagos) {
            List<PagoDetalle> listPagoDetalle = getPagoDetalleFacade().getPagoDetallesByPago(pago);
            for (PagoDetalle pd : listPagoDetalle) {
                PagoPublicidad pp = null;
                if (pd.getTipo() == TipoPagoAbono.PUBLICIDAD.getValor()) {
                    pp = getPagoPublicidadFacade().getPagoPublicidadByPagoDetalle(pd);
                }
                validarPago(pd, pp);
            }
        }

        for (TipoPagoHelper tph : tipoPagoHelpersTMP) {
            PagoDetalle pd = new PagoDetalle();
            pd.setTipo(tph.getTipo());
            pd.setValor(tph.getValor());

            if (pd.getTipo() == TipoPagoAbono.PUBLICIDAD.getValor()) {
                PagoPublicidad pp = new PagoPublicidad();
                pp.setTipo(tph.getTipoPublicidad());
                validarPago(pd, pp);
            } else {
                validarPago(pd, null);
            }

        }
    }

    private void validarPago(PagoDetalle pagoDetalle, PagoPublicidad pagoPublicidad) {
        boolean repetido = false;
        for (TipoPagoHelper tph : pagosAnteriores) {
            if ((tph.getTipo() == TipoPagoAbono.PUBLICIDAD.getValor()) && pagoPublicidad != null) {
                if (tph.getTipoPublicidad().getId().equals(pagoPublicidad.getTipo().getId())) {
                    tph.setValor(tph.getValor() + pagoDetalle.getValor());
                    repetido = true;
                }
            } else {
                if (pagoDetalle.getTipo() == tph.getTipo()) {
                    tph.setValor(tph.getValor() + pagoDetalle.getValor());
                    repetido = true;
                }
            }
        }

        if (!repetido) {
            pagosAnteriores.add(new TipoPagoHelper(count++, pagoDetalle.getTipo(), pagoDetalle.getValor(), (pagoPublicidad == null ? null : pagoPublicidad.getTipo())));
        }
    }

    public void prepareList(boolean isPublicidad) {
        if (pago.getFactura() != null) {
            List<Pago> listPagos = getPagoFacade().getPagosByFactura(pago.getFactura());
            getPagosAnteriores(listPagos);
            if (isPublicidad) {
                RequestContext.getCurrentInstance().execute("PF('PublicidadDialog').show()");
            } else {
                RequestContext.getCurrentInstance().execute("PF('ComisionDialog').show()");
            }
        } else {
            JsfUtil.addErrorMessage("Debe seleccionar una factura");
        }
    }

    public void crearRecibo() {
        if (pagoHelpers.size() > 0) {
            try {
                getTransactionFacade().crearPago(pagoHelpers, selected);
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

    public void validateNumeroRecibo(AjaxBehaviorEvent event) {
        validarCrear(false);
    }

    private void updateValorPendienteOnly() {
        if (pago.getFactura() != null) {
            List<Pago> listPagos = getPagoFacade().getPagosByFactura(pago.getFactura());

            double valor = 0.0;
            if (listPagos != null && !listPagos.isEmpty()) {
                for (Pago pago : listPagos) {
                    valor += pago.getValorTotal();
                }
            }

            for (PagoHelper ph : pagoHelpers) {
                if (Objects.equals(ph.getPago().getFactura().getId(), pago.getFactura().getId())) {
                    if ((pagoHelper != null) && (ph.getId() == pagoHelper.getId())) {
                    } else {
                        tipoPagoHelpersTMP.addAll(ph.getTipoPagoHelpers());
                    }
                }
            }

            for (TipoPagoHelper tph : tipoPagoHelpersTMP) {
                valor += tph.getValor();
            }

            pago.getFactura().setSaldo(pago.getFactura().getTotalPagar() - valor);
        }
    }
    
    private void getValorPago(){
        double valor = 0.0;
        for (TipoPagoHelper tph : tipoPagoHelpers) {
            valor += tph.getValor();
        }
        pago.setValorTotal(valor);
    }

}
