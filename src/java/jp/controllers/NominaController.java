package jp.controllers;

import java.io.*;
import java.util.*;
import javax.ejb.*;
import javax.faces.bean.*;
import javax.inject.Inject;
import jp.entidades.*;
import jp.facades.*;
import jp.seguridad.UsuarioActual;
import jp.util.EstadoFactura;
import jp.util.JsfUtil;
import org.primefaces.event.SelectEvent;

@ManagedBean(name = "nominaController")
@ViewScoped
public class NominaController implements Serializable {

    @EJB
    private NominaFacade nominaFacade;
    @EJB
    private CajaFacade cajaFacade;
    @EJB
    private ParametrosFacade parametrosFacade;
    @EJB
    private TransactionFacade transactionFacade;
    @Inject
    private UsuarioActual usuarioActual;
    private ReciboCaja reciboCaja;
    private ReciboCaja reciboCajaCXCM;
    private ReciboCaja reciboCajaCXC;
    private List<ReciboCaja> reciboCajasCXCM;
    private List<ReciboCaja> reciboCajasCXC;
    private Tercero terceroTMP;

    private double totalValorCXC;

    public NominaController() {
        terceroTMP = new Tercero();
        totalValorCXC = 0L;
    }

    public NominaFacade getNominaFacade() {
        return nominaFacade;
    }

    public CajaFacade getCajaFacade() {
        return cajaFacade;
    }

    public ParametrosFacade getParametrosFacade() {
        return parametrosFacade;
    }

    public TransactionFacade getTransactionFacade() {
        return transactionFacade;
    }

    public ReciboCaja getReciboCaja() {
        if (reciboCaja == null) {
            reciboCaja = new ReciboCaja();
        }
        return reciboCaja;
    }

    public void setReciboCaja(ReciboCaja reciboCaja) {
        this.reciboCaja = reciboCaja;
    }

    public ReciboCaja getReciboCajaCXCM() {
        if (reciboCajaCXCM == null) {
            reciboCajaCXCM = new ReciboCaja();
        }
        return reciboCajaCXCM;
    }

    public void setReciboCajaCXCM(ReciboCaja reciboCajaCXCM) {
        this.reciboCajaCXCM = reciboCajaCXCM;
    }

    public ReciboCaja getReciboCajaCXC() {
        if (reciboCajaCXC == null) {
            reciboCajaCXC = new ReciboCaja();
        }
        return reciboCajaCXC;
    }

    public void setReciboCajaCXC(ReciboCaja reciboCajaCXC) {
        this.reciboCajaCXC = reciboCajaCXC;
    }

    public List<ReciboCaja> getReciboCajasCXC() {
        if (reciboCajasCXC == null) {
            reciboCajasCXC = new ArrayList<>();
        }
        return reciboCajasCXC;
    }

    public void setReciboCajasCXC(List<ReciboCaja> reciboCajasCXC) {
        this.reciboCajasCXC = reciboCajasCXC;
    }

    public List<ReciboCaja> getReciboCajasCXCM() {
        if (reciboCajasCXCM == null) {
            reciboCajasCXCM = new ArrayList<>();
        }
        return reciboCajasCXCM;
    }

    public void setReciboCajasCXCM(List<ReciboCaja> reciboCajasCXCM) {
        this.reciboCajasCXCM = reciboCajasCXCM;
    }

    public Tercero prepareCreateTercero() {
        return new Tercero();
    }

    public ReciboCaja prepareCreateReciboCXC() {
        reciboCajaCXC = new ReciboCaja();
        return reciboCajaCXC;
    }

    private void getRecibosCajaCXCM() {
        reciboCajasCXCM = getNominaFacade().getRecibosCajaCxcTercero(terceroTMP);
        getSaldoPendiente(reciboCajasCXCM);
    }

    public void onItemSelectTercero(SelectEvent e) {
        if (e != null && e.getObject() != null) {
            clean();
            terceroTMP = (Tercero) e.getObject();
            getRecibosCajaCXCM();
        }
    }

    public void removeReciboCajaCXC(ReciboCaja reciboCajaTMP) {
        reciboCajasCXC.remove(reciboCajaTMP);
        getRecibosCajaCXCM();
    }

    private void getSaldoPendiente(List<ReciboCaja> reciboCajasCXCMTMP) {
        for (ReciboCaja reciboCajaTMP : reciboCajasCXCMTMP) {
            double valor = 0;
            List<ReciboCaja> recibosCajaTMP = getNominaFacade().recibosCajaCxcByTransaccion(reciboCajaTMP);
            if (recibosCajaTMP != null && !recibosCajaTMP.isEmpty()) {
                for (ReciboCaja rc : recibosCajaTMP) {
                    valor += rc.getValor();
                }
            }

            if (reciboCajasCXC != null && !reciboCajasCXC.isEmpty()) {
                for (ReciboCaja rc : reciboCajasCXC) {
                    if (Objects.equals(rc.getTransaccion().getId(), reciboCajaTMP.getId())) {
                        valor += rc.getValor();
                    }
                }
            }

            if (valor == 0) {
                reciboCajaTMP.setSaldo(0);
            } else {
                reciboCajaTMP.setSaldo(valor);
            }

        }
    }

    public List<Concepto> llenarConceptosIngresoCXC(String query) {
        return getNominaFacade().getConceptosCXCOnlyIngresos(query);
    }

    public void crearConceptoCXC() {
        try {

            double valor = 0L;
            try {
                valor = getReciboCajaCXC().getValor();
            } catch (Exception e) {
                valor = 0L;
            }

            if (valor <= 0) {
                JsfUtil.addErrorMessage("Debe ingresar un valor valido");
            } else if (valor > (getReciboCajaCXCM().getValor() - getReciboCajaCXCM().getSaldo())) {
                JsfUtil.addErrorMessage("El valor sobrepasa la cantidad pendiente de la CXC Empleado");
            } else {
                ReciboCaja reciboCajaTMP = new ReciboCaja();
                reciboCajaTMP.setId(getReciboCajasCXC().size() + 1L);
                reciboCajaTMP.setValor(getReciboCajaCXC().getValor());
                reciboCajaTMP.setConcepto(getReciboCajaCXC().getConcepto());
                reciboCajaTMP.setDetalle(getReciboCajaCXC().getDetalle().trim());
                reciboCajaTMP.setCaja(getCajaFacade().getCaja());
                reciboCajaTMP.setEstado(EstadoFactura.REALIZADA.getValor());
                reciboCajaTMP.setFecha(Calendar.getInstance().getTime());
                reciboCajaTMP.setTercero(terceroTMP);
                reciboCajaTMP.setTransaccion(reciboCajaCXCM);
                reciboCajaTMP.setUsuario(usuarioActual.getUsuario());
                getReciboCajasCXC().add(reciboCajaTMP);
                setReciboCajaCXC(new ReciboCaja());
                getRecibosCajaCXCM();
                JsfUtil.addSuccessMessage("El abono o pago se ha creado correctamente");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsfUtil.addErrorMessage("Ocurrio un error durante la creacion del abono o pago");
        }
    }

    private void clean() {
        setReciboCajaCXC(new ReciboCaja());
        setReciboCajaCXCM(new ReciboCaja());
        setReciboCajasCXC(new ArrayList<ReciboCaja>());
        setReciboCajasCXCM(new ArrayList<ReciboCaja>());
    }

    public void guardar() {

        double valor = 0L;
        try {
            valor = reciboCaja.getValor();
        } catch (Exception e) {
            valor = 0L;
        }

        if (valor == 0) {
            JsfUtil.addErrorMessage("Debe ingresar un valor valido");
        } else {
            reciboCaja.setConcepto(getParametrosFacade().getParametros().getConceptoNomina());
            if (reciboCaja.getDetalle().trim().isEmpty()) {
                reciboCaja.setDetalle("NOMINA: " + terceroTMP.toString());
            }
            reciboCaja.setEstado(EstadoFactura.REALIZADA.getValor());
            reciboCaja.setUsuario(usuarioActual.getUsuario());

            try {
                if (transactionFacade.createNomina(reciboCaja, reciboCajasCXCM, reciboCajasCXC)) {
                    JsfUtil.addSuccessMessage("La nomina fue guardada correctamente");
                    cleanAll();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            JsfUtil.addErrorMessage("Ocurrio un error guardando la nomina");
        }
    }

    public double totalAbonosCXC() {
        totalValorCXC = 0L;
        for (ReciboCaja rc : getReciboCajasCXC()) {
            totalValorCXC += rc.getValor();
        }
        return totalValorCXC;
    }

    public double valorTotal() {
        return (reciboCaja.getValor() - totalValorCXC);
    }

    private void cleanAll() {
        setReciboCaja(new ReciboCaja());
        clean();
        terceroTMP = new Tercero();
        totalValorCXC = 0L;
    }

}
