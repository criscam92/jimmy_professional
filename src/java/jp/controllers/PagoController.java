package jp.controllers;

import jp.entidades.Pago;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import jp.entidades.Factura;
import jp.entidades.Talonario;
import jp.facades.FacturaFacade;
import jp.facades.PagoFacade;
import jp.facades.TransactionFacade;
import jp.util.EstadoPago;
import jp.util.JsfUtil;
import jp.util.TipoPago;

@ManagedBean(name = "pagoController")
@ViewScoped
public class PagoController implements Serializable {

    @EJB
    private PagoFacade ejbFacade;
    @EJB
    private FacturaFacade facturaFacade;
    @EJB
    private TransactionFacade transactionFacade;

    private List<Pago> items = null;
    private Pago selected;
    private List<Talonario> talonarios;
    
    private String numeroFactura;
    private Double valorPendiente;
    private final String uiError = "ui-state-error";
    private String error;

    public PagoController() {
    }

    public PagoFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(PagoFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

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

    private PagoFacade getFacade() {
        return ejbFacade;
    }

    private FacturaFacade getFacturaFacade() {
        return facturaFacade;
    }

    public TransactionFacade getTransactionFacade() {
        return transactionFacade;
    }

    public void setTransactionFacade(TransactionFacade transactionFacade) {
        this.transactionFacade = transactionFacade;
    }

    public void prepareEdit() {
    }

    public void update() {
    }

    public void anular() {
    }

    public TipoPago[] getTiposPago() {
        return new TipoPago[]{TipoPago.CONTADO, TipoPago.CHEQUE};
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
        return isValid;
    }

}
