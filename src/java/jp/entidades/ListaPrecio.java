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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import jp.entidades.auxiliar.Codificable;

@Entity
@Table(name = "lista_precio", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"codigo"})})
@NamedQueries({
    @NamedQuery(name = "ListaPrecio.findAll", query = "SELECT l FROM ListaPrecio l")})
public class ListaPrecio implements Serializable, Codificable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(nullable = false, length = 45)
    private String codigo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;
    @OneToMany(mappedBy = "listaPrecio", fetch = FetchType.LAZY)
    private List<Factura> facturaList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "listaPrecio", fetch = FetchType.LAZY)
    private List<PrecioProducto> precioProductoList;

    public ListaPrecio() {
    }

    public ListaPrecio(Integer id) {
        this.id = id;
    }

    public ListaPrecio(Integer id, String codigo, String nombre) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @XmlTransient
    public List<Factura> getFacturaList() {
        return facturaList;
    }

    public void setFacturaList(List<Factura> facturaList) {
        this.facturaList = facturaList;
    }

    public List<PrecioProducto> getPrecioProductoList() {
        return precioProductoList;
    }

    public void setPrecioProductoList(List<PrecioProducto> precioProductoList) {
        this.precioProductoList = precioProductoList;
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
        if (!(object instanceof ListaPrecio)) {
            return false;
        }
        ListaPrecio other = (ListaPrecio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getCodigo() + " - " + this.getNombre();
    }

    @Override
    public String getTipo() {
        return null;
    }

}
