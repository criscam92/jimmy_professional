package jp.controllers;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import jp.entidades.ReciboCaja;
import jp.facades.ReciboCajaFacade;
import jp.facades.TransactionFacade;
import jp.seguridad.UsuarioActual;
import jp.util.EstadoPagoFactura;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;

@ManagedBean(name = "reciboCajaController")
@SessionScoped
public class ReciboCajaController implements Serializable {

    @EJB
    private ReciboCajaFacade ejbFacade;
    @EJB
    private UsuarioActual ejbUsuarioFacade;
    @EJB
    private TransactionFacade transactionFacade;
    @EJB
    private UsuarioActual usuarioActual;
    private List<ReciboCaja> items = null;
    private ReciboCaja selected;
    private Long totalIngresos;
    private Long totalEgresos;
    private Long totalRecibos;
    private final SimpleDateFormat formatoDelTexto;
    private Date fechaIni, fechaFin;

    public ReciboCajaController() {
        selected = new ReciboCaja();
        fechaIni = new Date();
        fechaFin = new Date();
        formatoDelTexto = new SimpleDateFormat("dd/MMM/yyyy");
    }

    @PostConstruct
    private void init() {
        Map<String, String> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        try {
            fechaIni = formatoDelTexto.parse(requestMap.get("date1"));
            System.out.println("Fecha Inicio ==> " + formatoDelTexto.format(fechaIni));
        } catch (Exception e) {
            fechaIni = null;
            System.out.println("No se recibe fecha Inicial en el filtro");
        }
        try {
            fechaFin = formatoDelTexto.parse(requestMap.get("date2"));
            System.out.println("Fecha Final ==> " + formatoDelTexto.format(fechaFin));
        } catch (Exception e) {
            fechaFin = null;
            System.out.println("No se recibe fecha Final en el filtro");
        }
    }

    public ReciboCaja getSelected() {
        return selected;
    }

    public void setSelected(ReciboCaja selected) {
        this.selected = selected;
    }

    public Long getTotalEgresos() {
        return totalEgresos;
    }

    public void setTotalEgresos(Long totalEgresos) {
        this.totalEgresos = totalEgresos;
    }

    public Long getTotalIngresos() {
        return totalIngresos;
    }

    public void setTotalIngresos(Long totalIngresos) {
        this.totalIngresos = totalIngresos;
    }

    public Long getTotalRecibos() {
        return totalRecibos;
    }

    public void setTotalRecibos(Long totalRecibos) {
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

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ReciboCajaFacade getFacade() {
        return ejbFacade;
    }

    public UsuarioActual getEjbUsuarioFacade() {
        return ejbUsuarioFacade;
    }

    public TransactionFacade getTransactionFacade() {
        return transactionFacade;
    }

    public ReciboCaja prepareCreate() {
        selected = new ReciboCaja();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        if (selected != null) {
            selected.setUsuario(getEjbUsuarioFacade().get());
            selected.setEstado(EstadoPagoFactura.REALIZADA.getValor());
            if (selected.getFecha() == null) {
                selected.setFecha(Calendar.getInstance().getTime());
            }
            persist(PersistAction.CREATE, JsfUtil.getMessageBundle(new String[]{"MessageReciboCaja", "CreateSuccessM"}));
        }
        if (!JsfUtil.isValidationFailed()) {
            selected = new ReciboCaja();
            items = null;    // Invalidate list of items to trigger re-query.
//            getItems();
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, JsfUtil.getMessageBundle(new String[]{"MessageReciboCaja", "UpdateSuccessM"}));
    }

    public void destroy() {
        persist(PersistAction.DELETE, JsfUtil.getMessageBundle(new String[]{"MessageReciboCaja", "DeleteSuccessM"}));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<ReciboCaja> getItems() {
        if (items == null) {
            items = getFacade().getRecibosCaja(null, null);
            getTotalIngresosEgresos();
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

    public List<ReciboCaja> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<ReciboCaja> getItemsAvailableSelectOne() {
        return getFacade().findAll();
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
        if (getTransactionFacade().anularRecibo(selected)) {
            if (!JsfUtil.isValidationFailed()) {
                JsfUtil.addSuccessMessage(JsfUtil.getMessageBundle(new String[]{"MessageReciboCaja", "AnullSuccessM"}));
                selected = null; // Remove selection
                items = null;
            }
        } else {
            JsfUtil.addErrorMessage("Ocurrió un error anulando el Recibo de Caja");
        }
    }

    public boolean disableAnular() {
        selected = new ReciboCaja();
        return !(selected != null && usuarioActual.isAdmin() && selected.getEstado() == EstadoPagoFactura.REALIZADA.getValor());
    }

    public Long getIngreso(ReciboCaja reciboCaja) {
        if (reciboCaja.getConcepto().getIngreso()) {
            return reciboCaja.getValor();
        } else {
            return 0l;
        }
    }

    public Long getEgreso(ReciboCaja reciboCaja) {
        if (!reciboCaja.getConcepto().getIngreso()) {
            return reciboCaja.getValor();
        } else {
            return 0l;
        }
    }

    public void getTotalIngresosEgresos() {
        totalIngresos = 0l;
        totalEgresos = 0l;
        for (ReciboCaja item : items) {
            if (item.getEstado() == EstadoPagoFactura.ANULADO.getValor()) {
                continue;
            }
            if (item.getConcepto().getIngreso()) {
                totalIngresos += item.getValor();
            } else {
                totalEgresos += item.getValor();
            }
        }
        totalRecibos = totalIngresos - totalEgresos;
    }

    public void redirect() throws IOException {
        String url = "?";
        if (fechaIni != null) {
            url += "&date1=" + formatoDelTexto.format(fechaIni);
        }
        if (fechaFin != null) {
            url += "&date2=" + formatoDelTexto.format(fechaFin);
        }
        items = getFacade().getRecibosCaja(fechaIni, fechaFin);
        System.out.println("TAMAÑO lista despues del filtro --> "+items.size());
        getTotalIngresosEgresos();
        System.out.println("URL");
        FacesContext.getCurrentInstance().getExternalContext().redirect(url);
    }

}
