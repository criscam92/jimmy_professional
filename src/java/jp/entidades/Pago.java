/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author arturo
 */
@Entity
@Table(catalog = "jimmy_professional", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Pago.findAll", query = "SELECT p FROM Pago p"),
    @NamedQuery(name = "Pago.findById", query = "SELECT p FROM Pago p WHERE p.id = :id"),
    @NamedQuery(name = "Pago.findByFormaPago", query = "SELECT p FROM Pago p WHERE p.formaPago = :formaPago"),
    @NamedQuery(name = "Pago.findByFecha", query = "SELECT p FROM Pago p WHERE p.fecha = :fecha"),
    @NamedQuery(name = "Pago.findByObservaciones", query = "SELECT p FROM Pago p WHERE p.observaciones = :observaciones"),
    @NamedQuery(name = "Pago.findByNumeroCheque", query = "SELECT p FROM Pago p WHERE p.numeroCheque = :numeroCheque"),
    @NamedQuery(name = "Pago.findByValorTotal", query = "SELECT p FROM Pago p WHERE p.valorTotal = :valorTotal"),
    @NamedQuery(name = "Pago.findByDolar", query = "SELECT p FROM Pago p WHERE p.dolar = :dolar"),
    @NamedQuery(name = "Pago.findByEstado", query = "SELECT p FROM Pago p WHERE p.estado = :estado")})
public class Pago implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "forma_pago", nullable = false)
    private int formaPago;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Size(max = 400)
    @Column(length = 400)
    private String observaciones;
    @Size(max = 100)
    @Column(name = "numero_cheque", length = 100)
    private String numeroCheque;
    @Basic(optional = false)
    @NotNull
    @Column(name = "valor_total", nullable = false)
    private double valorTotal;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private boolean dolar;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private int estado;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pago", fetch = FetchType.LAZY)
    private List<PagoDetalle> pagoDetalleList;
    @JoinColumn(name = "cuenta", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CuentaBancaria cuenta;
    @JoinColumn(name = "factura", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Factura factura;
    @JoinColumn(name = "relacion", referencedColumnName = "id", nullable = true)
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private RelacionFactura relacionFactura;
    @Basic(optional = true)
    @Size(max = 30)
    @Column(length = 30, name = "orden_pago", nullable = true)
    private String ordenPago;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pago", fetch = FetchType.LAZY)
    private List<PagoDevolucion> pagoDevolucionList;

    public Pago() {
    }

    public Pago(Long id) {
        this.id = id;
    }

    public Pago(Long id, String ordenPago, int formaPago, Date fecha, double valorTotal, boolean dolar, int estado) {
        this.id = id;
        this.ordenPago = ordenPago;
        this.formaPago = formaPago;
        this.fecha = fecha;
        this.valorTotal = valorTotal;
        this.dolar = dolar;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrdenPago() {
        return ordenPago;
    }

    public void setOrdenPago(String ordenPago) {
        this.ordenPago = ordenPago;
    }

    public int getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(int formaPago) {
        this.formaPago = formaPago;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getNumeroCheque() {
        return numeroCheque;
    }

    public void setNumeroCheque(String numeroCheque) {
        this.numeroCheque = numeroCheque;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public boolean getDolar() {
        return dolar;
    }

    public void setDolar(boolean dolar) {
        this.dolar = dolar;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    @XmlTransient
    public List<PagoDetalle> getPagoDetalleList() {
        return pagoDetalleList;
    }

    public void setPagoDetalleList(List<PagoDetalle> pagoDetalleList) {
        this.pagoDetalleList = pagoDetalleList;
    }

    public CuentaBancaria getCuenta() {
        return cuenta;
    }

    public void setCuenta(CuentaBancaria cuenta) {
        this.cuenta = cuenta;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public RelacionFactura getRelacionFactura() {
        return relacionFactura;
    }

    public void setRelacionFactura(RelacionFactura relacionFactura) {
        this.relacionFactura = relacionFactura;
    }

    @XmlTransient
    public List<PagoDevolucion> getPagoDevolucionList() {
        return pagoDevolucionList;
    }

    public void setPagoDevolucionList(List<PagoDevolucion> pagoDevolucionList) {
        this.pagoDevolucionList = pagoDevolucionList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pago)) {
            return false;
        }
        Pago other = (Pago) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "entidades.Pago[ id=" + id + " ]";
    }
    
}
