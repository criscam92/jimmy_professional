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
 * @author CRISTIAN
 */
@Entity
@Table(catalog = "jimmy_professional", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Factura.findAll", query = "SELECT f FROM Factura f"),
    @NamedQuery(name = "Factura.findById", query = "SELECT f FROM Factura f WHERE f.id = :id"),
    @NamedQuery(name = "Factura.findByFecha", query = "SELECT f FROM Factura f WHERE f.fecha = :fecha"),
    @NamedQuery(name = "Factura.findByFechaVencimiento", query = "SELECT f FROM Factura f WHERE f.fechaVencimiento = :fechaVencimiento"),
    @NamedQuery(name = "Factura.findByTotalUnidadVentas", query = "SELECT f FROM Factura f WHERE f.totalUnidadVentas = :totalUnidadVentas"),
    @NamedQuery(name = "Factura.findByTotalUnidadBonificaciones", query = "SELECT f FROM Factura f WHERE f.totalUnidadBonificaciones = :totalUnidadBonificaciones"),
    @NamedQuery(name = "Factura.findByTotalBruto", query = "SELECT f FROM Factura f WHERE f.totalBruto = :totalBruto"),
    @NamedQuery(name = "Factura.findByDescuento", query = "SELECT f FROM Factura f WHERE f.descuento = :descuento"),
    @NamedQuery(name = "Factura.findBySubtotal", query = "SELECT f FROM Factura f WHERE f.subtotal = :subtotal"),
    @NamedQuery(name = "Factura.findByTotalPagar", query = "SELECT f FROM Factura f WHERE f.totalPagar = :totalPagar"),
    @NamedQuery(name = "Factura.findByObservaciones", query = "SELECT f FROM Factura f WHERE f.observaciones = :observaciones")})
public class Factura implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_vencimiento", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaVencimiento;
    @Basic(optional = false)
    @NotNull
    @Column(name = "total_unidad_ventas", nullable = false)
    private int totalUnidadVentas;
    @Basic(optional = false)
    @NotNull
    @Column(name = "total_unidad_bonificaciones", nullable = false)
    private int totalUnidadBonificaciones;
    @Basic(optional = false)
    @NotNull
    @Column(name = "total_bruto", nullable = false)
    private double totalBruto;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(precision = 17, scale = 17)
    private Double descuento;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private double subtotal;
    @Basic(optional = false)
    @NotNull
    @Column(name = "total_pagar", nullable = false)
    private double totalPagar;
    @Size(max = 200)
    @Column(length = 200)
    private String observaciones;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "factura", fetch = FetchType.LAZY)
    private List<Pago> pagoList;
    @JoinColumn(name = "cliente", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Cliente cliente;
    @JoinColumn(name = "empleado", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Empleado empleado;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "factura", fetch = FetchType.LAZY)
    private List<FacturaProducto> facturaProductoList;

    public Factura() {
    }

    public Factura(Long id) {
        this.id = id;
    }

    public Factura(Long id, Date fecha, Date fechaVencimiento, int totalUnidadVentas, int totalUnidadBonificaciones, double totalBruto, double subtotal, double totalPagar) {
        this.id = id;
        this.fecha = fecha;
        this.fechaVencimiento = fechaVencimiento;
        this.totalUnidadVentas = totalUnidadVentas;
        this.totalUnidadBonificaciones = totalUnidadBonificaciones;
        this.totalBruto = totalBruto;
        this.subtotal = subtotal;
        this.totalPagar = totalPagar;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public int getTotalUnidadVentas() {
        return totalUnidadVentas;
    }

    public void setTotalUnidadVentas(int totalUnidadVentas) {
        this.totalUnidadVentas = totalUnidadVentas;
    }

    public int getTotalUnidadBonificaciones() {
        return totalUnidadBonificaciones;
    }

    public void setTotalUnidadBonificaciones(int totalUnidadBonificaciones) {
        this.totalUnidadBonificaciones = totalUnidadBonificaciones;
    }

    public double getTotalBruto() {
        return totalBruto;
    }

    public void setTotalBruto(double totalBruto) {
        this.totalBruto = totalBruto;
    }

    public Double getDescuento() {
        return descuento;
    }

    public void setDescuento(Double descuento) {
        this.descuento = descuento;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(double totalPagar) {
        this.totalPagar = totalPagar;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @XmlTransient
    public List<Pago> getPagoList() {
        return pagoList;
    }

    public void setPagoList(List<Pago> pagoList) {
        this.pagoList = pagoList;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    @XmlTransient
    public List<FacturaProducto> getFacturaProductoList() {
        return facturaProductoList;
    }

    public void setFacturaProductoList(List<FacturaProducto> facturaProductoList) {
        this.facturaProductoList = facturaProductoList;
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
        if (!(object instanceof Factura)) {
            return false;
        }
        Factura other = (Factura) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.entidades.Factura[ id=" + id + " ]";
    }
    
}
