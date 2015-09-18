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
import jp.util.TipoUsuario;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"usuario"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u"),
    @NamedQuery(name = "Usuario.findById", query = "SELECT u FROM Usuario u WHERE u.id = :id"),
    @NamedQuery(name = "Usuario.findByUsuario", query = "SELECT u FROM Usuario u WHERE u.usuario = :usuario"),
    @NamedQuery(name = "Usuario.findByContrasena", query = "SELECT u FROM Usuario u WHERE u.contrasena = :contrasena"),
    @NamedQuery(name = "Usuario.findByTipo", query = "SELECT u FROM Usuario u WHERE u.tipo = :tipo")})
public class Usuario implements Serializable {

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
    private String usuario;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(nullable = false, length = 200)
    private String contrasena;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private long tipo;
    @JoinColumn(name = "empleado", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Empleado empleado;
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Ingreso> ingresoList;
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Factura> facturaList;
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<DespachoFactura> despachoFacturaList;

    public Usuario() {
    }

    public Usuario(Long id) {
        this.id = id;
    }

    public Usuario(Long id, String usuario, String contrasena, long tipo) {
        this.id = id;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public long getTipo() {
        return tipo;
    }

    public void setTipo(long tipo) {
        this.tipo = tipo;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    @XmlTransient
    public List<Ingreso> getIngresoList() {
        return ingresoList;
    }

    public void setIngresoList(List<Ingreso> ingresoList) {
        this.ingresoList = ingresoList;
    }

    @XmlTransient
    public List<Factura> getFacturaList() {
        return facturaList;
    }

    public void setFacturaList(List<Factura> facturaList) {
        this.facturaList = facturaList;
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
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return this.getUsuario();
    }

    public boolean isAdmin() {
        return getTipo() == TipoUsuario.Administrador.getValor();
    }

}
