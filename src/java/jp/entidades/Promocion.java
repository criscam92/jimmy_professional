package jp.entidades;

import java.io.Serializable;
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
@NamedQueries({
    @NamedQuery(name = "Promocion.findAll", query = "SELECT p FROM Promocion p"),
    @NamedQuery(name = "Promocion.findById", query = "SELECT p FROM Promocion p WHERE p.id = :id"),
    @NamedQuery(name = "Promocion.findByDescripcion", query = "SELECT p FROM Promocion p WHERE p.descripcion = :descripcion"),
    @NamedQuery(name = "Promocion.findByValor", query = "SELECT p FROM Promocion p WHERE p.valor = :valor"),
    @NamedQuery(name = "Promocion.findByCodigo", query = "SELECT p FROM Promocion p WHERE p.codigo = :codigo")})
public class Promocion implements Serializable, Codificable {

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
    private String descripcion;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private double valor;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private double valorVentaUsd;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(nullable = false, length = 45)
    private String codigo;
    @JoinColumn(name = "categoria", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Categoria categoria;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "promocion", fetch = FetchType.LAZY)
    private List<FacturaPromocion> facturaPromocionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "promocion", fetch = FetchType.LAZY)
    private List<PromocionProducto> promocionProductoList;

    public Promocion() {
    }

    public Promocion(Long id) {
        this.id = id;
    }

    public Promocion(Long id, String descripcion, double valor, String codigo, Double valorVentaUSD) {
        this.id = id;
        this.descripcion = descripcion;
        this.valor = valor;
        this.codigo = codigo;
        this.valorVentaUsd = valorVentaUSD;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Double getValorVentaUsd() {
        return valorVentaUsd;
    }

    public void setValorVentaUsd(Double valorVentaUsd) {
        this.valorVentaUsd = valorVentaUsd;
    }

    @Override
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    @XmlTransient
    public List<FacturaPromocion> getFacturaPromocionList() {
        return facturaPromocionList;
    }

    public void setFacturaPromocionList(List<FacturaPromocion> facturaPromocionList) {
        this.facturaPromocionList = facturaPromocionList;
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
        if (!(object instanceof Promocion)) {
            return false;
        }
        Promocion other = (Promocion) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "entidades.Promocion[ id=" + id + " ]";
    }

    @Override
    public String getTipo() {
        return null;
    }

    @Override
    public String getNombre() {
        return null;
    }

}
