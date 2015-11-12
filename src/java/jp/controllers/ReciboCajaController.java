package jp.controllers;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
import jp.entidades.ReciboCaja;
import jp.entidades.Tercero;
import jp.facades.CajaFacade;
import jp.facades.ReciboCajaFacade;
import jp.facades.TerceroFacade;
import jp.facades.TransactionFacade;
import jp.seguridad.UsuarioActual;
import jp.util.CondicionConcepto;
import jp.util.EstadoFactura;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;
import jp.util.TipoConcepto;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "reciboCajaController")
@ViewScoped
public class ReciboCajaController implements Serializable {

    @EJB
    private ReciboCajaFacade ejbFacade;
    @EJB
    private UsuarioActual ejbUsuarioFacade;
    @EJB
    private TerceroFacade ejbTerceroFacade;
    @EJB
    private TransactionFacade transactionFacade;
    @EJB
    private UsuarioActual usuarioActual;
    @EJB
    private CajaFacade cajaFacade;
    @EJB
    private TerceroFacade terceroFacade;
    private List<ReciboCaja> items = null;
    private List<ReciboCaja> itemsCxcCxp = null;
    private ReciboCaja selected, nuevoRecibo, selectedAPagar, selectedView;
    private Tercero tercero;
    private double totalIngresos, totalEgresos, totalNeutros, totalRecibos;
    private double base, ingresos, egresos;
    private SimpleDateFormat formatoDelTexto;
    private Date fechaIni, fechaFin;
    private DecimalFormat formatter;
    private String tipoConcepto, condicionConcepto;
    private String estiloTipo;

    public ReciboCajaController() {
        fechaIni = new Date();
        fechaFin = new Date();
        tercero = new Tercero();
        tipoConcepto = "Seleccione un Concepto";
        condicionConcepto = "";
        estiloTipo = "estiloConceptoTipoBlack";
    }

    @PostConstruct
    public void init() {
        nuevoRecibo = prepareCreate();
        selectedAPagar = new ReciboCaja();
        selectedView = new ReciboCaja();
        selected = new ReciboCaja();
        formatoDelTexto = new SimpleDateFormat("dd/MMM/yyyy");

        formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);

        calcularSaldo();

    }

    public ReciboCaja getSelected() {
        return selected;
    }

    public void setSelected(ReciboCaja selected) {
        this.selected = selected;
    }

    public ReciboCaja getNuevoRecibo() {
        return nuevoRecibo;
    }

    public void setNuevoRecibo(ReciboCaja nuevoRecibo) {
        this.nuevoRecibo = nuevoRecibo;
    }

    public ReciboCaja getSelectedAPagar() {
        return selectedAPagar;
    }

    public void setSelectedAPagar(ReciboCaja reciboAPagar) {
        this.selectedAPagar = reciboAPagar;
    }

    public ReciboCaja getSelectedView() {
        return selectedView;
    }

    public void setSelectedView(ReciboCaja selectedView) {
        this.selectedView = selectedView;
    }

    public Tercero getTercero() {
        return tercero;
    }

    public void setTercero(Tercero tercero) {
        this.tercero = tercero;
    }

    public String getTotalEgresos() {
        return formatter.format(totalEgresos);
    }

    public void setTotalEgresos(double totalEgresos) {
        this.totalEgresos = totalEgresos;
    }

    public String getTotalNeutros() {
        return formatter.format(totalNeutros);
    }

    public void setTotalNeutros(double totalNeutros) {
        this.totalNeutros = totalNeutros;
    }

    public String getTotalIngresos() {
        return formatter.format(totalIngresos);
    }

    public void setTotalIngresos(double totalIngresos) {
        this.totalIngresos = totalIngresos;
    }

    public String getTotalRecibos() {
        return formatter.format(totalRecibos);
    }

    public void setTotalRecibos(double totalRecibos) {
        this.totalRecibos = totalRecibos;
    }

    public Date getFechaIni() {
        return fechaIni;
    }

    public void setFechaIni(Date fechaIni) {
        this.fechaIni = fechaIni;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getBase() {
        return formatter.format(base);
    }

    public String getIngresos() {
        return formatter.format(ingresos);
    }

    public String getEgresos() {
        return formatter.format(egresos);
    }

    public String getTotal() {
        return formatter.format(base + (ingresos - egresos));
    }

    private ReciboCajaFacade getFacade() {
        return ejbFacade;
    }

    public UsuarioActual getEjbUsuarioFacade() {
        return ejbUsuarioFacade;
    }

    public TerceroFacade getEjbTerceroFacade() {
        return ejbTerceroFacade;
    }

    public TerceroFacade getTerceroFacade() {
        return terceroFacade;
    }

    public TransactionFacade getTransactionFacade() {
        return transactionFacade;
    }

    public String getTipoConcepto() {
        return tipoConcepto;
    }

    public void setTipoConcepto(String tipoConcepto) {
        this.tipoConcepto = tipoConcepto;
    }

    public String getCondicionConcepto() {
        return condicionConcepto;
    }

    public void setCondicionConcepto(String condicionConcepto) {
        this.condicionConcepto = condicionConcepto;
    }

    public String getEstiloTipo() {
        return estiloTipo;
    }

    public void setEstiloTipo(String estiloTipo) {
        this.estiloTipo = estiloTipo;
    }

    private ReciboCaja prepareCreate() {
        nuevoRecibo = new ReciboCaja();
        return nuevoRecibo;
    }

    public void create(boolean isCxcCxp) {
        if (nuevoRecibo != null) {
            nuevoRecibo.setUsuario(getEjbUsuarioFacade().getUsuario());
            if (isCxcCxp) {
                nuevoRecibo.setEstado(EstadoFactura.PENDIENTE.getValor());
            } else {
                nuevoRecibo.setEstado(EstadoFactura.REALIZADA.getValor());
            }

            if (nuevoRecibo.getFecha() == null) {
                nuevoRecibo.setFecha(Calendar.getInstance().getTime());
            }
            if (selected != null && selectedAPagar != null) {
                selectedAPagar.setTransaccion(selected);
            }
            persist(PersistAction.CREATE, JsfUtil.getMessageBundle(new String[]{"MessageReciboCaja", "CreateSuccessM"}));
            getTotalIngresosEgresos();
        }
        if (!JsfUtil.isValidationFailed()) {
            nuevoRecibo = prepareCreate();
            items = null;    // Invalidate list of items to trigger re-query.
            itemsCxcCxp = null;
        }
        calcularSaldo();
        tipoConcepto = "Seleccione un Concepto";
        condicionConcepto = "";
        estiloTipo = "estiloConceptoTipoBlack";
    }

    public void createAbonoPago() {
        if (selected != null && selectedAPagar != null) {
            if (selectedAPagar.getValor() > getValorPendienteCxcCxp(selected)) {
                JsfUtil.addWarnMessage("El valor a pagar no debe ser mayor a la deuda");
                return;
            }
            selectedAPagar.setUsuario(getEjbUsuarioFacade().getUsuario());
            selectedAPagar.setEstado(EstadoFactura.REALIZADA.getValor());
            selectedAPagar.setTercero(selected.getTercero());
            if (selectedAPagar.getFecha() == null) {
                selectedAPagar.setFecha(Calendar.getInstance().getTime());
            }
            selectedAPagar.setTransaccion(selected);
        }
        JsfUtil.addSuccessMessage(JsfUtil.getMessageBundle(new String[]{"MessageReciboCaja", "CreateSuccessM"}));
        getTotalIngresosEgresos();
        if (!JsfUtil.isValidationFailed()) {
            getTransactionFacade().createAbonoPago(selected, selectedAPagar);
            selectedAPagar = new ReciboCaja();
            items = null;    // Invalidate list of items to trigger re-query.
            itemsCxcCxp = null;
            RequestContext.getCurrentInstance().execute("PF('PagoCxcCxpDialog').hide()");
        }
        calcularSaldo();
        tipoConcepto = "Seleccione un Concepto";
        condicionConcepto = "";
        estiloTipo = "estiloConceptoTipoBlack";
    }

    public void update() {
        persist(PersistAction.UPDATE, JsfUtil.getMessageBundle(new String[]{"MessageReciboCaja", "UpdateSuccessM"}));
    }

//    public void destroy() {
//        persist(PersistAction.DELETE, JsfUtil.getMessageBundle(new String[]{"MessageReciboCaja", "DeleteSuccessM"}));
//        if (!JsfUtil.isValidationFailed()) {
//            selected = null; // Remove selection
//            items = null;    // Invalidate list of items to trigger re-query.
//        }
//    }
    public List<ReciboCaja> getItems() {
        if (items == null) {
            items = getFacade().getRecibosCaja(false);
            getTotalIngresosEgresos();
        }
        return items;
    }

    public List<ReciboCaja> getItemsCxcCxp() {
        if (itemsCxcCxp == null) {
            itemsCxcCxp = getFacade().getRecibosCaja(true);
            getTotalIngresosEgresos();
        }
        return itemsCxcCxp;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (nuevoRecibo != null) {
            try {
                if (persistAction != PersistAction.DELETE) {

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(nuevoRecibo.getFecha());

                    Calendar current = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, current.get(Calendar.HOUR_OF_DAY));
                    calendar.set(Calendar.MINUTE, current.get(Calendar.MINUTE));
                    calendar.set(Calendar.SECOND, current.get(Calendar.SECOND));

                    nuevoRecibo.setFecha(calendar.getTime());

                    getFacade().edit(nuevoRecibo);
                } else {
                    getFacade().remove(nuevoRecibo);
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

    public List<ReciboCaja> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<ReciboCaja> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    private void calcularSaldo() {

        base = cajaFacade.getCaja().getBase();
        List<ReciboCaja> transacciones = ejbFacade.getRecibosCaja(null);

        egresos = 0l;
        ingresos = 0l;

        for (ReciboCaja transaccion : transacciones) {
            if (transaccion.getEstado() == EstadoFactura.REALIZADA.getValor()) {
                if (transaccion.getConcepto().getTipo2() == TipoConcepto.INGRESO.getValor()) {
                    ingresos += transaccion.getValor();
                } else if (transaccion.getConcepto().getTipo2() == TipoConcepto.EGRESO.getValor()) {
                    egresos += transaccion.getValor();
                }
            }
        }

        totalRecibos = ingresos - egresos;

    }

    @FacesConverter(forClass = ReciboCaja.class)
    public static class ReciboCajaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ReciboCajaController controller = (ReciboCajaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "reciboCajaController");
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
            if (object instanceof ReciboCaja) {
                ReciboCaja o = (ReciboCaja) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), ReciboCaja.class.getName()});
                return null;
            }
        }

    }

    public void anularRecibo() {
        if (getTransactionFacade().anularPagosRecibosCxcCxp(selected)) {
            if (!JsfUtil.isValidationFailed()) {
                JsfUtil.addSuccessMessage(JsfUtil.getMessageBundle(new String[]{"MessageReciboCaja", "AnullSuccessM"}));
                selected = null; // Remove selection
                items = null;
                itemsCxcCxp = null;
                calcularSaldo();
            }
        } else {
            JsfUtil.addErrorMessage("Ocurrió un error anulando el Recibo de Caja");
        }
    }

    public boolean disableAnular() {
        boolean result = !(usuarioActual.getUsuario().isAdmin() && selected != null && (selected.getEstado() == EstadoFactura.PENDIENTE.getValor()));
        return result;
    }

    public boolean disableVerPagos() {
        boolean tienePagosRealizados = getFacade().getPagosByCxcCxp(selectedView, true).isEmpty();
        boolean result = !(usuarioActual.getUsuario().isAdmin() && selected != null && (selected.getEstado() != EstadoFactura.ANULADO.getValor())
                && tienePagosRealizados);
        return result;
    }

    public double getIngreso(ReciboCaja reciboCaja) {
        if (reciboCaja.getConcepto().getTipo2() == TipoConcepto.INGRESO.getValor()) {
            return reciboCaja.getValor();
        } else {
            return 0.0;
        }
    }

    public double getEgreso(ReciboCaja reciboCaja) {
        if (reciboCaja.getConcepto().getTipo2() == TipoConcepto.EGRESO.getValor()) {
            return reciboCaja.getValor();
        } else {
            return 0.0;
        }
    }

    public double getNeutral(ReciboCaja reciboCaja) {
        if (reciboCaja.getConcepto().getTipo2() == TipoConcepto.NEUTRAL.getValor()) {
            return reciboCaja.getValor();
        } else {
            return 0.0;
        }
    }

    public void getTotalIngresosEgresos() {
        totalIngresos = 0l;
        totalEgresos = 0l;
        totalNeutros = 0l;

        if (items != null) {
            for (ReciboCaja item : items) {
                if (item.getEstado() == EstadoFactura.ANULADO.getValor()) {
                    continue;
                }
                if (item.getConcepto().getTipo2() == TipoConcepto.INGRESO.getValor()) {
                    totalIngresos += item.getValor();
                } else if (item.getConcepto().getTipo2() == TipoConcepto.EGRESO.getValor()) {
                    totalEgresos += item.getValor();
                } else {
                    totalNeutros += item.getValor();
                }
            }
        }
        if (itemsCxcCxp != null) {
            for (ReciboCaja item : itemsCxcCxp) {
                if (item.getEstado() == EstadoFactura.ANULADO.getValor()) {
                    continue;
                }
                if (item.getConcepto().getTipo2() == TipoConcepto.INGRESO.getValor()) {
                    totalIngresos += item.getValor();
                } else if (item.getConcepto().getTipo2() == TipoConcepto.EGRESO.getValor()) {
                    totalEgresos += item.getValor();
                } else {
                    totalNeutros += item.getValor();
                }
            }
        }

        totalRecibos = totalIngresos - totalEgresos;
        System.out.println("total===> " + totalRecibos);
    }

    public void redirect() throws IOException {
        String url = "?";
        if (fechaIni != null) {
            url += "&date1=" + formatoDelTexto.format(fechaIni);
        }
        if (fechaFin != null) {
            url += "&date2=" + formatoDelTexto.format(fechaFin);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaIni);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(fechaFin);
        calendar2.set(Calendar.HOUR_OF_DAY, 23);
        calendar2.set(Calendar.MINUTE, 59);
        calendar2.set(Calendar.SECOND, 59);

        items = getFacade().getRecibosCaja(false);
        itemsCxcCxp = getFacade().getRecibosCaja(true);
        getTotalIngresosEgresos();
        System.out.println("URL");
        FacesContext.getCurrentInstance().getExternalContext().redirect(url);
    }

    public int getEstadoAnulado() {
        return EstadoFactura.ANULADO.getValor();
    }

    public List<Tercero> llenarTercero(String query) {
        return getEjbTerceroFacade().getTerceroByQuery(query);
    }

    public Tercero prepareCreateTercero() {
        tercero = new Tercero();
        return tercero;
    }

    public void createTercero() {
        if (tercero != null) {
            getTerceroFacade().create(tercero);
            if (!JsfUtil.isValidationFailed()) {
                JsfUtil.addSuccessMessage(JsfUtil.getMessageBundle(new String[]{"MessageTercero", "CreateSuccessM"}));
                tercero = new Tercero();    // Invalidate list of items to trigger re-query.
            }
        }
    }

    public void changeTipoConcepto(boolean cxccxp) {
        if (nuevoRecibo.getConcepto() != null) {
            if (!cxccxp) {
                if (nuevoRecibo.getConcepto().getTipo2() == TipoConcepto.INGRESO.getValor()) {
                    tipoConcepto = "El Concepto es un Ingreso a la Caja";
                    estiloTipo = "estiloConceptoTipoGreen";
                } else if (nuevoRecibo.getConcepto().getTipo2() == TipoConcepto.EGRESO.getValor()) {
                    tipoConcepto = "El Concepto es un Egreso a la Caja";
                    estiloTipo = "estiloConceptoTipoRed";
                } else if (nuevoRecibo.getConcepto().getTipo2() == TipoConcepto.NEUTRAL.getValor()) {
                    tipoConcepto = "El Concepto no afecta la Caja";
                    estiloTipo = "estiloConceptoTipoBlack";
                }
            } else {
                tipoConcepto = TipoConcepto.getFromValue(nuevoRecibo.getConcepto().getTipo2()).getDetalle();
                condicionConcepto = CondicionConcepto.getFromValue(nuevoRecibo.getConcepto().getCxccxp()).getDetalle();
                tipoConcepto = tipoConcepto + ", " + condicionConcepto;

                if (nuevoRecibo.getConcepto().getTipo2() == TipoConcepto.INGRESO.getValor()) {
                    estiloTipo = "estiloConceptoTipoGreen";
                } else if (nuevoRecibo.getConcepto().getTipo2() == TipoConcepto.EGRESO.getValor()) {
                    estiloTipo = "estiloConceptoTipoRed";
                } else if (nuevoRecibo.getConcepto().getTipo2() == TipoConcepto.NEUTRAL.getValor()) {
                    estiloTipo = "estiloConceptoTipoBlack";
                }
            }
        }
    }

    public void changeConceptoCxcCxp() {
        if (selectedAPagar != null) {
            tipoConcepto = TipoConcepto.getFromValue(selectedAPagar.getConcepto().getTipo2()).getDetalle();
            condicionConcepto = CondicionConcepto.getFromValue(selectedAPagar.getConcepto().getCxccxp()).getDetalle();
            tipoConcepto = tipoConcepto + ", " + condicionConcepto;

            if (selectedAPagar.getConcepto().getTipo2() == TipoConcepto.INGRESO.getValor()) {
                estiloTipo = "estiloConceptoTipoGreen";
            } else if (selectedAPagar.getConcepto().getTipo2() == TipoConcepto.EGRESO.getValor()) {
                estiloTipo = "estiloConceptoTipoRed";
            } else if (selectedAPagar.getConcepto().getTipo2() == TipoConcepto.NEUTRAL.getValor()) {
                estiloTipo = "estiloConceptoTipoBlack";
            }
        }
    }

    public Double getValorPendienteCxcCxp(ReciboCaja reciboCaja) {
        return getFacade().getValorPendienteCxcCxp(reciboCaja);
    }

    public void prepareReciboAPagar() {
        this.selectedAPagar = selected;
    }

    public void prepareSelectedView() {
        this.selectedView = selected;
    }

    public List<ReciboCaja> getPagosByCxcCxp(ReciboCaja reciboCaja, Integer estado) {
        return getFacade().getPagosByCxcCxp(reciboCaja, null);
    }

    public void anularPago() {
        if (getTransactionFacade().anularPagosRecibosCxcCxp(selectedView)) {
            if (!JsfUtil.isValidationFailed()) {
                JsfUtil.addSuccessMessage(JsfUtil.getMessageBundle(new String[]{"MessageReciboCaja", "AnullSuccessM"}));
                items = null;
                itemsCxcCxp = null;
                calcularSaldo();
            }
        } else {
            JsfUtil.addErrorMessage("Ocurrió un error anulando el Recibo de Caja");
        }
    }

    public String getEstadoCxcCxp(ReciboCaja reciboCaja) {
        double valorPendiente = getFacade().getValorPendienteCxcCxp(reciboCaja);
        if (valorPendiente == 0) {
            return EstadoFactura.CANCELADO.getDetalle();
        } else if (reciboCaja.getEstado() == EstadoFactura.ANULADO.getValor()) {
            return EstadoFactura.ANULADO.getDetalle();
        } else {
            return EstadoFactura.PENDIENTE.getDetalle();
        }
    }
}