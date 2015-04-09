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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(catalog = "jimmy_professional", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Factura.findAll", query = "SELECT f FROM Factura f"),
    @NamedQuery(name = "Factura.findById", query = "SELECT f FROM Factura f WHERE f.id = :id"),
    @NamedQuery(name = "Factura.findByFecha", query = "SELECT f FROM Factura f WHERE f.fecha = :fecha"),
    @NamedQuery(name = "Factura.findByTipoPago", query = "SELECT f FROM Factura f WHERE f.tipoPago = :tipoPago"),
    @NamedQuery(name = "Factura.findByObservaciones", query = "SELECT f FROM Factura f WHERE f.observaciones = :observaciones"),
    @NamedQuery(name = "Factura.findByTotalBruto", query = "SELECT f FROM Factura f WHERE f.totalBruto = :totalBruto"),
    @NamedQuery(name = "Factura.findByDescuento", query = "SELECT f FROM Factura f WHERE f.descuento = :descuento"),
    @NamedQuery(name = "Factura.findByTotalPagar", query = "SELECT f FROM Factura f WHERE f.totalPagar = :totalPagar"),
    @NamedQuery(name = "Factura.findByEstado", query = "SELECT f FROM Factura f WHERE f.estado = :estado"),
    @NamedQuery(name = "Factura.findByDolar", query = "SELECT f FROM Factura f WHERE f.dolar = :dolar")})
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
    @Column(name = "tipo_pago", nullable = false)
    private int tipoPago;
    @Size(max = 200)
    @Column(length = 200)
    private String observaciones;
    @Basic(optional = false)
    @NotNull
    @Column(name = "total_bruto", nullable = false)
    private double totalBruto;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(precision = 17, scale = 17)
    private Double descuento;
    @Basic(optional = false)
    @NotNull
    @Column(name = "total_pagar", nullable = false)
    private double totalPagar;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private int estado;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private boolean dolar;
    @Size(max = 30)
    @Column(length = 30, name = "orden_pedido")
    private String ordenPedido;
    @Basic(optional = true)
    @Column(name = "dolar_actual", nullable = true)
    private float dolarActual;
    @JoinColumn(name = "cliente", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Cliente cliente;
    @JoinColumn(name = "empleado", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Empleado empleado;
    @JoinColumn(name = "usuario", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Usuario usuario;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "factura", fetch = FetchType.LAZY)
    private List<FacturaPromocion> facturaPromocionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "factura", fetch = FetchType.LAZY)
    private List<CambioDevolucion> cambioDevolucionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "factura", fetch = FetchType.LAZY)
    private List<FacturaProducto> facturaProductoList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "factura", fetch = FetchType.LAZY)
    private List<Pago> pagoList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "factura", fetch = FetchType.LAZY)
    private List<DespachoFactura> despachoFacturaList;
    @Transient
    Double saldo;
    @Transient
    Double saldoPagado;
    
    public Factura() {
    }

    public Factura(Long id) {
        this.id = id;
    }

    public Factura(Long id, Date fecha, int tipoPago, double totalBruto, double totalPagar, int estado, boolean dolar) {
        this.id = id;
        this.fecha = fecha;
        this.tipoPago = tipoPago;
        this.totalBruto = totalBruto;
        this.totalPagar = totalPagar;
        this.estado = estado;
        this.dolar = dolar;
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

    public int getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(int tipoPago) {
        this.tipoPago = tipoPago;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
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

    public double getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(double totalPagar) {
        this.totalPagar = totalPagar;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public boolean getDolar() {
        return dolar;
    }

    public void setDolar(boolean dolar) {
        this.dolar = dolar;
    }

    public String getOrdenPedido() {
        return ordenPedido;
    }

    public void setOrdenPedido(String ordenPedido) {
        this.ordenPedido = ordenPedido;
    }

    public float getDolarActual() {
        return dolarActual;
    }

    public void setDolarActual(float dolarActual) {
        this.dolarActual = dolarActual;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public Double getSaldoCancelado() {
        return saldoPagado;
    }

    public void setSaldoCancelado(Double saldoCancelado) {
        this.saldoPagado = saldoCancelado;
    }

    @XmlTransient
    public List<FacturaPromocion> getFacturaPromocionList() {
        return facturaPromocionList;
    }

    public void setFacturaPromocionList(List<FacturaPromocion> facturaPromocionList) {
        this.facturaPromocionList = facturaPromocionList;
    }

    @XmlTransient
    public List<CambioDevolucion> getCambioDevolucionList() {
        return cambioDevolucionList;
    }

    public void setCambioDevolucionList(List<CambioDevolucion> cambioDevolucionList) {
        this.cambioDevolucionList = cambioDevolucionList;
    }

    @XmlTransient
    public List<FacturaProducto> getFacturaProductoList() {
        return facturaProductoList;
    }

    public void setFacturaProductoList(List<FacturaProducto> facturaProductoList) {
        this.facturaProductoList = facturaProductoList;
    }

    @XmlTransient
    public List<Pago> getPagoList() {
        return pagoList;
    }

    public void setPagoList(List<Pago> pagoList) {
        this.pagoList = pagoList;
    }

    @XmlTransient
    public List<DespachoFactura> getDespachoFacturaList() {
        return despachoFacturaList;
    }

    public void setDespachoFacturaList(List<DespachoFactura> despachoFacturaList) {
        this.despachoFacturaList = despachoFacturaList;
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
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "entidades.Factura[ id=" + id + " ]";
    }

}
