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
@Table(name = "codigo_devolucion", catalog = "jimmy_professional", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"codigo"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CodigoDevolucion.findAll", query = "SELECT c FROM CodigoDevolucion c"),
    @NamedQuery(name = "CodigoDevolucion.findById", query = "SELECT c FROM CodigoDevolucion c WHERE c.id = :id"),
    @NamedQuery(name = "CodigoDevolucion.findByCodigo", query = "SELECT c FROM CodigoDevolucion c WHERE c.codigo = :codigo"),
    @NamedQuery(name = "CodigoDevolucion.findByDescripcion", query = "SELECT c FROM CodigoDevolucion c WHERE c.descripcion = :descripcion")})
public class CodigoDevolucion implements Serializable, Codificable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(nullable = false, length = 45)
    private String codigo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(nullable = false, length = 100)
    private String descripcion;
    @OneToMany(mappedBy = "codigoDevolucion", fetch = FetchType.LAZY)
    private List<DevolucionProducto> devolucionProductoList;

    public CodigoDevolucion() {
    }

    public CodigoDevolucion(Long id) {
        this.id = id;
    }

    public CodigoDevolucion(Long id, String codigo, String descripcion) {
        this.id = id;
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @XmlTransient
    public List<DevolucionProducto> getDevolucionProductoList() {
        return devolucionProductoList;
    }

    public void setDevolucionProductoList(List<DevolucionProducto> devolucionProductoList) {
        this.devolucionProductoList = devolucionProductoList;
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
        if (!(object instanceof CodigoDevolucion)) {
            return false;
        }
        CodigoDevolucion other = (CodigoDevolucion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getCodigo()+" - "+this.getDescripcion();
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
