package jp.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import jp.entidades.Concepto;
import jp.entidades.ReciboCaja;
import jp.facades.ReciboCajaFacade;
import jp.util.EstadoFactura;
import jp.util.TipoConcepto;

@ManagedBean(name = "resultadoController")
@ViewScoped
public class ResultadoController implements Serializable{
    private Date fechaIni;
    private Date fechaFin;
    private String tipoConsulta;
    private TipoConcepto tipoConcepto;
    private List<Concepto> conceptos;
    private List<ReciboCaja> recibosCaja;
    private boolean mostrarTiposConcepto, mostrarListaConceptos;
    @EJB
    private ReciboCajaFacade EJBReciboCajaFacade;

    public ResultadoController() {
        mostrarListaConceptos = false;
        mostrarTiposConcepto = false;
        recibosCaja = new ArrayList<>();
    }

    public ResultadoController(Date fechaIni, Date fechaFin, String tipoConsulta, TipoConcepto tipoConcepto, List<Concepto> conceptos, List<ReciboCaja> recibosCaja) {
        this.fechaIni = fechaIni;
        this.fechaFin = fechaFin;
        this.tipoConsulta = tipoConsulta;
        this.tipoConcepto = tipoConcepto;
        this.conceptos = conceptos;
        this.recibosCaja = recibosCaja;
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

    public String getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(String tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    public TipoConcepto getTipoConcepto() {
        return tipoConcepto;
    }

    public void setTipoConcepto(TipoConcepto tipoConcepto) {
        this.tipoConcepto = tipoConcepto;
    }

    public List<Concepto> getConceptos() {
        return conceptos;
    }

    public void setConceptos(List<Concepto> conceptos) {
        this.conceptos = conceptos;
    }

    public List<ReciboCaja> getRecibosCaja() {
        return recibosCaja;
    }

    public void setRecibosCaja(List<ReciboCaja> recibosCaja) {
        this.recibosCaja = recibosCaja;
    }

    public boolean isMostrarTiposConcepto() {
        return mostrarTiposConcepto;
    }

    public void setMostrarTiposConcepto(boolean mostrarTiposConcepto) {
        this.mostrarTiposConcepto = mostrarTiposConcepto;
    }

    public boolean isMostrarListaConceptos() {
        return mostrarListaConceptos;
    }

    public void setMostrarListaConceptos(boolean mostrarListaConceptos) {
        this.mostrarListaConceptos = mostrarListaConceptos;
    }

    public ReciboCajaFacade getEJBReciboCajaFacade() {
        return EJBReciboCajaFacade;
    }
    
    public List<String> getTiposConsulta(){
        List<String> tiposConsulta = new ArrayList<>();
        tiposConsulta.add("Tipos Concepto");
        tiposConsulta.add("Lista de Conceptos");
        return tiposConsulta;
    }
    
    public Map<Integer, String> getMapaTiposConceptos(){
        return TipoConcepto.getMapaTiposConceptos();
    }
    
    public void changeTipoConsulta(){
        if (tipoConsulta != null) {
            if (tipoConsulta.equalsIgnoreCase("Tipos Concepto")) {
                mostrarTiposConcepto = true;
                mostrarListaConceptos = false;
            } else if (tipoConsulta.equalsIgnoreCase("Lista de Conceptos")) {
                mostrarListaConceptos = true;
                mostrarTiposConcepto = false;
            } else {
                mostrarListaConceptos = false;
                mostrarTiposConcepto = false;
            }
        }        
    }
    
    public void findResultados(){
        if (recibosCaja != null) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(fechaIni);
            cal1.add(Calendar.HOUR_OF_DAY, 00);
            cal1.add(Calendar.MINUTE, 00);
            cal1.add(Calendar.SECOND, 00);
            
            Calendar cal2 = (Calendar) cal1.clone();
            cal2.setTime(fechaFin);
            cal2.add(Calendar.HOUR_OF_DAY, 23);
            cal2.add(Calendar.MINUTE, 59);
            cal2.add(Calendar.SECOND, 59);
            
            recibosCaja = getEJBReciboCajaFacade().getTransaccionesByTipoConceptos(cal1.getTime(), cal2.getTime(), tipoConcepto, conceptos);
        }
    }
    
    public TipoConcepto[] getTiposConceptos() {
        return TipoConcepto.getFromValue(new Integer[]{1, 2});
    }
}
