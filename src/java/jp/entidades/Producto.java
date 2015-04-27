package jp.entidades;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import jp.entidades.auxiliar.Codificable;

@Entity
@Table(catalog = "jimmy_professional", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"codigo"})})
@XmlRootElement
public class Producto implements Serializable, Codificable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(nullable = false, length = 100)
    private String descripcion;
    @Size(max = 100)
    @Column(length = 100)
    private String gramaje;
    @Basic(optional = false)
    @NotNull
    @Column(name = "valor_costo", nullable = false)
    private double valorCosto;
    @Basic(optional = false)
    @NotNull
    @Column(name = "valor_venta", nullable = false)
    private double valorVenta;
    @Column(name = "valor_venta_usd", precision = 17, scale = 17)
    private Double valorVentaUsd;
    @Basic(optional = false)
    @NotNull
    @Column(name = "venta_publico", nullable = false)
    private boolean ventaPublico;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(nullable = false, length = 45)
    private String codigo;
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private List<IngresoProducto> ingresoProductoList;
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private List<DevolucionProducto> devolucionProductoList;
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private List<VisitaProducto> visitaProductoList;
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private List<FacturaProducto> facturaProductoList;
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private List<DespachoFacturaProducto> despachoFacturaProductoList;
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private List<PromocionProducto> promocionProductoList;

    public Producto() {
    }

    public Producto(Long id) {
        this.id = id;
    }

    public Producto(Long id, String nombre, String descripcion, double valorCosto, double valorVenta, boolean ventaPublico, String codigo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.valorCosto = valorCosto;
        this.valorVenta = valorVenta;
        this.ventaPublico = ventaPublico;
        this.codigo = codigo;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getGramaje() {
        return gramaje;
    }

    public void setGramaje(String gramaje) {
        this.gramaje = gramaje;
    }

    public double getValorCosto() {
        return valorCosto;
    }

    public void setValorCosto(double valorCosto) {
        this.valorCosto = valorCosto;
    }

    public double getValorVenta() {
        return valorVenta;
    }

    public void setValorVenta(double valorVenta) {
        this.valorVenta = valorVenta;
    }

    public Double getValorVentaUsd() {
        return valorVentaUsd;
    }

    public void setValorVentaUsd(Double valorVentaUsd) {
        this.valorVentaUsd = valorVentaUsd;
    }

    public boolean getVentaPublico() {
        return ventaPublico;
    }

    public void setVentaPublico(boolean ventaPublico) {
        this.ventaPublico = ventaPublico;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @XmlTransient
    public List<IngresoProducto> getIngresoProductoList() {
        return ingresoProductoList;
    }

    public void setIngresoProductoList(List<IngresoProducto> ingresoProductoList) {
        this.ingresoProductoList = ingresoProductoList;
    }

    @XmlTransient
    public List<DevolucionProducto> getDevolucionProductoList() {
        return devolucionProductoList;
    }

    public void setDevolucionProductoList(List<DevolucionProducto> devolucionProductoList) {
        this.devolucionProductoList = devolucionProductoList;
    }

    @XmlTransient
    public List<VisitaProducto> getVisitaProductoList() {
        return visitaProductoList;
    }

    public void setVisitaProductoList(List<VisitaProducto> visitaProductoList) {
        this.visitaProductoList = visitaProductoList;
    }

    @XmlTransient
    public List<FacturaProducto> getFacturaProductoList() {
        return facturaProductoList;
    }

    public void setFacturaProductoList(List<FacturaProducto> facturaProductoList) {
        this.facturaProductoList = facturaProductoList;
    }

    @XmlTransient
    public List<DespachoFacturaProducto> getDespachoFacturaProductoList() {
        return despachoFacturaProductoList;
    }

    public void setDespachoFacturaProductoList(List<DespachoFacturaProducto> despachoFacturaProductoList) {
        this.despachoFacturaProductoList = despachoFacturaProductoList;
    }

    @XmlTransient
    public List<PromocionProducto> getPromocionProductoList() {
        return promocionProductoList;
    }

    public void setPromocionProductoList(List<PromocionProducto> promocionProductoList) {
        this.promocionProductoList = promocionProductoList;
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
        if (!(object instanceof Producto)) {
            return false;
        }
        Producto other = (Producto) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " " + (gramaje == null ? "" : gramaje);
    }

    @Override
    public String getTipo() {
        return null;
    }

}
