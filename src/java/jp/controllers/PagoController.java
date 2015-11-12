package jp.controllers;
//==============================================================================

import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.ejb.*;
import javax.faces.bean.*;
import javax.faces.component.*;
import javax.faces.context.*;
import javax.faces.convert.*;
import javax.faces.event.AjaxBehaviorEvent;
import jp.entidades.*;
import jp.facades.*;
import jp.util.*;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
//==============================================================================

@ManagedBean(name = "pagoController")
@ViewScoped
public class PagoController implements Serializable {
//==============================================================================

    @EJB
    private PagoFacade facade;
    @EJB
    private FacturaFacade facturaFacade;
    @EJB
    private TransactionFacade transactionFacade;
    @EJB
    private PagoDetalleFacade pagoDetalleFacade;
    @EJB
    private PagoPublicidadFacade pagoPublicidadFacade;
    @EJB
    private PagoFacade pagoFacade;
    @EJB
    private ParametrosFacade parametrosFacade;
    //==========================================================================

    private List<Pago> items = null;
    private Pago selected;
    private List<Talonario> talonarios;
    private TipoPagoHelper tipoPagoHelper;
    private List<TipoPagoHelper> tipoPagoHelpers;
    private List<TipoPagoHelper> tipoPagoHelpersTMP;
    private List<TipoPagoHelper> tipoPagoHelpersCrear;
    private List<TipoPagoHelper> tipoPagoHelpersEditar;
    private List<TipoPagoHelper> tipoPagoHelpersEliminar;
    private List<Factura> facturasTMP;
    private Cliente clienteTMP;

    private String numeroFactura;
    private Double valorPendiente;
    private double valorPublicidad;
    private double valorComision;
    private final String uiError = "ui-state-error";
    private String error;
    private String ordenPagoTMP;
    private int count;
    private boolean desabilitarButton;
    //==========================================================================

    public PagoController() {
        selected = new Pago();
        tipoPagoHelper = new TipoPagoHelper();
    }
    //==========================================================================

    public PagoFacade getPagoFacade() {
        return pagoFacade;
    }

    public PagoFacade getFacade() {
        return facade;
    }

    private FacturaFacade getFacturaFacade() {
        return facturaFacade;
    }

    public TransactionFacade getTransactionFacade() {
        return transactionFacade;
    }

    public PagoDetalleFacade getPagoDetalleFacade() {
        return pagoDetalleFacade;
    }

    public PagoPublicidadFacade getPagoPublicidadFacade() {
        return pagoPublicidadFacade;
    }

    public ParametrosFacade getParametrosFacade() {
        return parametrosFacade;
    }

    //==========================================================================
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Pago getSelected() {
        return selected;
    }

    public void setSelected(Pago selected) {
        this.selected = selected;
    }

    public List<Pago> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public Double getValorPendiente() {
        return valorPendiente;
    }

    public void setValorPendiente(Double valorPendiente) {
        this.valorPendiente = valorPendiente;
    }

    public Cliente getClienteTMP() {
        return clienteTMP;
    }

    public void setClienteTMP(Cliente clienteTMP) {
        this.clienteTMP = clienteTMP;
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

    public TipoPagoHelper getTipoPagoHelper() {
        return tipoPagoHelper;
    }

    public void setTipoPagoHelper(TipoPagoHelper tipoPagoHelper) {
        this.tipoPagoHelper = tipoPagoHelper;
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

    public boolean isDesabilitarButton() {
        return desabilitarButton;
    }

    public void setDesabilitarButton(boolean desabilitarButton) {
        this.desabilitarButton = desabilitarButton;
    }

    public List<TipoPagoHelper> getTipoPagoHelpersPublicidad() {
        System.out.println("Publicidad");
        if (clienteTMP != null) {
            System.out.println("El cliente existe");
            return getPagoFacade().getPublicidadOrComisionByCliente(clienteTMP, 4, 0);
        }
        return new ArrayList<>();
    }

    @FacesConverter(forClass = Pago.class)
    public static class PagoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PagoController controller = (PagoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "pagoController");
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
            if (object instanceof Pago) {
                Pago o = (Pago) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Pago.class.getName()});
                return null;
            }
        }
    }

    public void anularPago() {
        if (selected != null) {
            if (getTransactionFacade().anularPago(selected)) {
                JsfUtil.addSuccessMessage("El pago fue anulado correctamente");
                items = null;
            } else {
                JsfUtil.addErrorMessage("Ocurrio un error durante la anulacion del pago " + selected.getOrdenPago());
            }
        } else {
            JsfUtil.addErrorMessage("Debe seleccionar el pago a anular");
        }
    }

    public Factura updateSaldoFactura(Factura factura) {
        return getFacturaFacade().updatePagoPendiente(factura);
    }

    public String getEstadoPago(int estado) {
        return EstadoPago.getFromValue(estado).getDetalle();
    }

    public boolean disabled() {
        return !((selected != null && selected.getId() != null) && (selected.getEstado() == EstadoPago.REALIZADO.getValor()));
    }

    public boolean validarNumeroRecibo() {
        boolean isValid = true;

        if (!selected.getOrdenPago().equals(ordenPagoTMP)) {
            if (getFacade().existePago(selected.getOrdenPago())) {
                isValid = false;
                setError(uiError);
                JsfUtil.addErrorMessage("El numero de recibo " + selected.getOrdenPago() + " ya esta en uso");
            }

            if (isValid) {
                Long reciboActual;
                try {
                    reciboActual = Long.valueOf(selected.getOrdenPago());
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
                        JsfUtil.addErrorMessage("el numero de recibo " + selected.getOrdenPago() + " no esta permitido");
                    }
                } else {
                    isValid = false;
                    setError(uiError);
                    JsfUtil.addErrorMessage("Debe poner un numero de recibo valido");
                }
            }
        }
        return isValid;
    }

    public void prepareEdit() {

        selected = getFacade().find(selected.getId());

        ordenPagoTMP = selected.getOrdenPago();
        count = 0;

        setClienteTMP(selected.getFactura().getCliente());
        setFacturasTMP(getFacturaFacade().getFacturasPendientesByCliente(getClienteTMP()));

        tipoPagoHelpersTMP = new ArrayList<>();
        tipoPagoHelpers = new ArrayList<>();
        tipoPagoHelper = new TipoPagoHelper();
        cargarTipoPagoHelpers();
        tipoPagoHelpersCrear = new ArrayList<>();
        tipoPagoHelpersEditar = new ArrayList<>();
        tipoPagoHelpersEliminar = new ArrayList<>();
        valorPendiente = 0.0;
        valorComision = 0.0;
        setDesabilitarButton(false);
        addPagosAnteriores();
        updatesValores();
    }

    private void cargarTipoPagoHelpers() {
        List<PagoDetalle> pds = getPagoDetalleFacade().getPagoDetallesByPago(selected);
        for (PagoDetalle pd : pds) {
            PagoPublicidad pp = getPagoPublicidadFacade().getPagoPublicidadByPagoDetalle(pd);
            getTipoPagoHelpers().add(new TipoPagoHelper(count++, pd.getId(), pd.getTipo(), pd.getValor(), pp != null ? pp.getTipo() : null, pd.getTransaccion()));
        }
    }

    public void onItemSelecCliente(SelectEvent event) {
        if (event.getObject() != null) {
            Cliente c = (Cliente) event.getObject();
            setFacturasTMP(getFacturaFacade().getFacturasPendientesByCliente(c));

            String ordenPago = selected.getOrdenPago();
            setSelected(new Pago());
            selected.setOrdenPago(ordenPago);

            for (TipoPagoHelper tph : getTipoPagoHelpers()) {
                tipoPagoHelpersEliminar.add(tph);
            }

            setTipoPagoHelper(new TipoPagoHelper());
            setTipoPagoHelpers(new ArrayList<TipoPagoHelper>());
            setTipoPagoHelpersTMP(new ArrayList<TipoPagoHelper>());
            setValorPublicidad(0.0);
            setValorComision(0.0);

            setDesabilitarButton(true);
        }
    }

    public void changedFactura(final AjaxBehaviorEvent event) {
        if (selected.getFactura() != null) {

            for (TipoPagoHelper tph : getTipoPagoHelpers()) {
                tipoPagoHelpersEliminar.add(tph);
            }

            setTipoPagoHelpers(new ArrayList<TipoPagoHelper>());
            setTipoPagoHelpersTMP(new ArrayList<TipoPagoHelper>());
            addPagosAnteriores();
            updatesValores();
            setDesabilitarButton(false);
        } else {
            selected.setFactura(new Factura());
        }
    }

    public void changedFormaPago(final AjaxBehaviorEvent event) {
        if (selected.getFactura() != null) {

            for (TipoPagoHelper tph : getTipoPagoHelpers()) {
                tipoPagoHelpersEliminar.add(tph);
            }

            setTipoPagoHelpers(new ArrayList<TipoPagoHelper>());
            setTipoPagoHelpersTMP(new ArrayList<TipoPagoHelper>());
            addPagosAnteriores();
            updatesValores();
        }
    }

    //==========================================================================    
    public TipoPagoAbono[] getTiposPagoAbono() {
        if (selected != null && selected.getFormaPago() != TipoPago.CONTADO.getValor()) {
            return new TipoPagoAbono[]{TipoPagoAbono.ABONO};
        }
        return TipoPagoAbono.values();
    }

    //==========================================================================    
    public String getTiposPagoAbono(int tipo) {
        return TipoPagoAbono.getFromValue(tipo).getDetalle();
    }

    //==========================================================================
    public TipoPago[] getTiposPago() {
        return new TipoPago[]{TipoPago.CONTADO, TipoPago.CHEQUE, TipoPago.CONSIGNACION};
    }

    public EstadoPago[] getEstadosPago() {
        return new EstadoPago[]{EstadoPago.ANULADO, EstadoPago.REALIZADO};
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
        return selected != null ? (selected.getFormaPago() == TipoPago.CHEQUE.getValor()) : false;
    }

    //==========================================================================    
    public String mostrarCheque() {
        return requiereCheque() ? "" : "display:none;";
    }

    //==========================================================================    
    public boolean requiereCuenta() {
        if (selected != null) {
            return selected.getFormaPago() == TipoPago.CONSIGNACION.getValor();
        }
        return false;
    }

    //==========================================================================    
    public boolean requiereTipoPublicidad() {
        return selected != null && selected.getFormaPago() == TipoPago.CONTADO.getValor()
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
        return selected != null ? (selected.getFormaPago() == TipoPago.CONTADO.getValor() ? "" : "display:none;") : "";
    }

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
                    tipoPagoHelpersEditar.add(getTipoPagoHelpers().get(io));
                    break;
                }
            }

            if (!existe) {
                TipoPagoHelper tph = new TipoPagoHelper();
                tph.setId(getTipoPagoHelpers().size() + 1);
                tph.setIdObject(null);
                tph.setTipo(getTipoPagoHelper().getTipo());
                tph.setValor(getTipoPagoHelper().getValor());
                tph.setTipoPublicidad(getTipoPagoHelper().getTipoPublicidad());
                getTipoPagoHelpers().add(tph);
                tipoPagoHelpersCrear.add(tph);
            }

            setTipoPagoHelper(new TipoPagoHelper());
            tipoPagoHelpersTMP = new ArrayList<>();
            for (TipoPagoHelper tph : tipoPagoHelpersCrear) {
                tipoPagoHelpersTMP.add(new TipoPagoHelper(tph.getId(), null, tph.getTipo(), tph.getValor(), tph.getTipoPublicidad(), null));
            }

            addPagosAnteriores();
            updatesValores();
        }

    }

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
        } else {

            if (getTipoPagoHelper().getValor() > selected.getFactura().getSaldo()) {
                JsfUtil.addErrorMessage("El valor supera el valor pendiente de la factura");
                isValid = false;
            }

            if (selected.getFormaPago() == TipoPago.CONTADO.getValor()) {
                if (getTipoPagoHelper().getTipo() == TipoPagoAbono.PUBLICIDAD.getValor()) {
                    if (getTipoPagoHelper().getValor() > getValorPublicidad()) {
                        JsfUtil.addErrorMessage("El valor supera el valor permitido por publicidad");
                        isValid = false;
                    }
                } else if (getTipoPagoHelper().getTipo() == TipoPagoAbono.COMISION.getValor()) {
                    if (getTipoPagoHelper().getValor() > getValorComision()) {
                        JsfUtil.addErrorMessage("El valor supera el valor permitido por comision");
                        isValid = false;
                    }
                }
            }
        }
        return isValid;
    }

    public void removePagoDetalle(TipoPagoHelper tph) {
        if (tipoPagoHelpersCrear.contains(tph)) {
            tipoPagoHelpersCrear.contains(tph);
        }

        if (tipoPagoHelpersEditar.contains(tph)) {
            tipoPagoHelpersEditar.remove(tph);
        }

        if (tph.getIdObject() != null) {
            tipoPagoHelpersEliminar.add(tph);
        }

        getTipoPagoHelpers().remove(tph);
        tipoPagoHelpersTMP = new ArrayList<>();
        addPagosAnteriores();
        updatesValores();
    }

    private void addPagosAnteriores() {
        List<Pago> listPagos = getFacade().getPagosByFactura(selected.getFactura());
        for (Pago p : listPagos) {
            List<PagoDetalle> listPagoDetalle = getPagoDetalleFacade().getPagoDetallesByPago(p);
            for (PagoDetalle pd : listPagoDetalle) {
                PagoPublicidad pp = null;
                if (pd.getTipo() == TipoPagoAbono.PUBLICIDAD.getValor()) {
                    pp = getPagoPublicidadFacade().getPagoPublicidadByPagoDetalle(pd);
                }
                validarPago(pd, pp);
            }
        }
    }

    //==========================================================================  
    private void validarPago(PagoDetalle pagoDetalle, PagoPublicidad pagoPublicidad) {
        boolean add = true;
        for (TipoPagoHelper tph : tipoPagoHelpersEliminar) {
            if (tph.getIdObject().equals(pagoDetalle.getId())) {
                add = false;
                break;
            }
        }

        if (add) {
            for (TipoPagoHelper tph : tipoPagoHelpersEditar) {
                if (tph.getIdObject().equals(pagoDetalle.getId())) {
                    add = false;
                    getTipoPagoHelpersTMP().add(new TipoPagoHelper(tph.getId(), tph.getIdObject(), tph.getTipo(), tph.getValor(), (tph.getTipoPublicidad() == null ? null : tph.getTipoPublicidad()), tph.getReciboCaja()));
                    break;
                }
            }

            if (add) {
                addPago(pagoPublicidad, pagoDetalle);
            }
        }

    }

    private void addPago(PagoPublicidad pagoPublicidad, PagoDetalle pagoDetalle) {

        boolean repetido = false;
        for (TipoPagoHelper tph : getTipoPagoHelpersTMP()) {
            if ((tph.getTipo() == TipoPagoAbono.PUBLICIDAD.getValor()) && pagoPublicidad != null) {
                if (tph.getTipoPublicidad().getId().equals(pagoPublicidad.getTipo().getId())) {
//                    System.out.println("HOLA");
                    tph.setValor(tph.getValor() + pagoDetalle.getValor());
                    repetido = true;
                }
            } else if (pagoDetalle.getTipo() == tph.getTipo()) {
                tph.setValor(tph.getValor() + pagoDetalle.getValor());
//                    System.out.println("HOLA 2");
                repetido = true;
            }
        }

        if (!repetido) {
            getTipoPagoHelpersTMP().add(new TipoPagoHelper(count++, null, pagoDetalle.getTipo(), pagoDetalle.getValor(), (pagoPublicidad == null ? null : pagoPublicidad.getTipo()), null));
        }
    }

    private void updatesValores() {
        if (selected.getFactura() != null) {
            float porcentajePublicidad;
            try {
                porcentajePublicidad = getParametrosFacade().getParametros().getPorcentajePublicidad();
            } catch (Exception e) {
                porcentajePublicidad = 0F;
            }
            double valorP = Math.round(selected.getFactura().getTotalPagar() * (porcentajePublicidad / 100));

            double porcentajeComision;
            try {
                porcentajeComision = getFacade().getRelacionFacturaByPago(selected).getVendedor().getTipoEmpleado().getComision();
            } catch (Exception e) {
                porcentajeComision = 0.0;
            }
            double valorC = Math.round(selected.getFactura().getTotalPagar() * (porcentajeComision / 100));

            double vPlucidad = 0.0, vComision = 0.0, valorTotal = 0.0;

            for (TipoPagoHelper tph : getTipoPagoHelpersTMP()) {
                if (tph.getTipo() == TipoPagoAbono.PUBLICIDAD.getValor()) {
                    vPlucidad += tph.getValor();
                } else if (tph.getTipo() == TipoPagoAbono.COMISION.getValor()) {
                    vComision += tph.getValor();
                }
                valorTotal += tph.getValor();
                System.out.println("VALOR: " + tph.getValor());
            }

            selected.getFactura().setSaldo(selected.getFactura().getTotalPagar() - valorTotal);
            selected.setValorTotal(valorTotal);
            setValorPublicidad(valorP - vPlucidad);
            setValorComision(valorC - vComision);
        }
    }

    public void update() {
        if (validarNumeroRecibo()) {
            if (!getTipoPagoHelpers().isEmpty()) {
                selected.setValorTotal(getValorReal());
                if (getTransactionFacade().updatePago(selected, tipoPagoHelpersCrear, tipoPagoHelpersEditar, tipoPagoHelpersEliminar)) {
                    items = null;
                    JsfUtil.addSuccessMessage("El pago fue actualizado correctamente");
                    RequestContext.getCurrentInstance().execute("PF('PagoCreateDialog').hide()");
                } else {
                    JsfUtil.addErrorMessage("Ocurrio un error actualizando el pago");
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

    public void cancelar() {
        items = null;
        selected = new Pago();
        RequestContext.getCurrentInstance().execute("PF('PagoCreateDialog').hide()");
    }

    public int getEstadoAnulado() {
        return EstadoPago.ANULADO.getValor();
    }

    public void prepareList() {
        if (clienteTMP != null && clienteTMP.getId() != null) {
        } else {
            JsfUtil.addErrorMessage("Debe seleccionar un cliente");
        }
    }

}
