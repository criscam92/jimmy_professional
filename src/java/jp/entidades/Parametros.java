package jp.entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(catalog = "jimmy_professional", schema = "public")
@XmlRootElement
public class Parametros implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "recargo_local", nullable = false)
    private Float recargoLocal;
    @Basic(optional = false)
    @NotNull
    @Column(name = "recargo_nacional", nullable = false)
    private Float recargoNacional;
    @Basic(optional = false)
    @NotNull
    @Column(name = "recargo_internacional", nullable = false)
    private Float recargoInternacional;
    @Basic(optional = false)
    @NotNull
    @Column(name = "porcentaje_publicidad", nullable = false)
    private Float porcentajePublicidad;
    @Basic(optional = false)
    @NotNull
    @Column(name = "porcentaje_venta_public", nullable = false)
    private Float porcentajeVentaPublic;
    @Basic(optional = false)
    @NotNull
    @Column(name = "precio_dolar", nullable = false)
    private Float precioDolar;
    @Basic(optional = false)
    @NotNull
    @Column(name = "valor_pronto_pago", nullable = false)
    private Float valorProntoPago;
    @Basic(optional = false)
    @NotNull
    @Column(name = "porcentaje_pronto_pago", nullable = false)
    private Float porcentajeProntoPago;
    @Basic(optional = false)
    @NotNull
    @Column(name = "dias_descuento_pronto_pago", nullable = false)
    private int diasDescuentoProntoPago;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(nullable = false, length = 30)
    private String correo;
    @JoinColumn(name = "ciudad", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Ciudad ciudad;

    public Parametros() {
    }

    public Parametros(Integer id) {
        this.id = id;
    }

    public Parametros(Integer id, Float recargoLocal, Float recargoNacional, Float recargoInternacional, Float porcentajePublicidad, Float porcentajeVentaPublic, Float precioDolar, Float valorProntoPago, Float porcentajeProntoPago, int diasDescuentoProntoPago, String correo) {
        this.id = id;
        this.recargoLocal = recargoLocal;
        this.recargoNacional = recargoNacional;
        this.recargoInternacional = recargoInternacional;
        this.porcentajePublicidad = porcentajePublicidad;
        this.porcentajeVentaPublic = porcentajeVentaPublic;
        this.precioDolar = precioDolar;
        this.valorProntoPago = valorProntoPago;
        this.porcentajeProntoPago = porcentajeProntoPago;
        this.diasDescuentoProntoPago = diasDescuentoProntoPago;
        this.correo = correo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getRecargoLocal() {
        return recargoLocal;
    }

    public void setRecargoLocal(Float recargoLocal) {
        this.recargoLocal = recargoLocal;
    }

    public Float getRecargoNacional() {
        return recargoNacional;
    }

    public void setRecargoNacional(Float recargoNacional) {
        this.recargoNacional = recargoNacional;
    }

    public Float getRecargoInternacional() {
        return recargoInternacional;
    }

    public void setRecargoInternacional(Float recargoInternacional) {
        this.recargoInternacional = recargoInternacional;
    }

    public Float getPorcentajePublicidad() {
        return porcentajePublicidad;
    }

    public void setPorcentajePublicidad(Float porcentajePublicidad) {
        this.porcentajePublicidad = porcentajePublicidad;
    }

    public Float getPorcentajeVentaPublic() {
        return porcentajeVentaPublic;
    }

    public void setPorcentajeVentaPublic(Float porcentajeVentaPublic) {
        this.porcentajeVentaPublic = porcentajeVentaPublic;
    }

    public Float getPrecioDolar() {
        return precioDolar;
    }

    public void setPrecioDolar(Float precioDolar) {
        this.precioDolar = precioDolar;
    }

    public Float getValorProntoPago() {
        return valorProntoPago;
    }

    public void setValorProntoPago(Float valorProntoPago) {
        this.valorProntoPago = valorProntoPago;
    }

    public Float getPorcentajeProntoPago() {
        return porcentajeProntoPago;
    }

    public void setPorcentajeProntoPago(Float porcentajeProntoPago) {
        this.porcentajeProntoPago = porcentajeProntoPago;
    }

    public int getDiasDescuentoProntoPago() {
        return diasDescuentoProntoPago;
    }

    public void setDiasDescuentoProntoPago(int diasDescuentoProntoPago) {
        this.diasDescuentoProntoPago = diasDescuentoProntoPago;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Ciudad getCiudad() {
        return ciudad;
    }

    public void setCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Parametros)) {
            return false;
        }
        Parametros other = (Parametros) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "entidades.Parametros[ id=" + id + " ]";
    }
    
}
