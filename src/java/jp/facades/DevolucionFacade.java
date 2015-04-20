package jp.facades;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Cliente;
import jp.entidades.Devolucion;
import jp.entidades.Factura;
import jp.entidades.Pago;
import jp.util.EstadoPagoFactura;

@Stateless
public class DevolucionFacade extends AbstractFacade<Devolucion> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DevolucionFacade() {
        super(Devolucion.class);
    }

    public Long getCantidadProductoByDevolucion(Devolucion d) {
        try {
            Query q = getEntityManager().createQuery("SELECT SUM(dp.cantidad) FROM DevolucionProducto dp WHERE dp.devolucion.id= :dev");
            q.setParameter("dev", d.getId());
            Long cantidad = (Long) q.getSingleResult();
            return cantidad != null ? cantidad : null;
        } catch (Exception e) {
            System.out.println("NO SE PUDO CONSULTAR EL NUMERO DE PRODBYDEVOLUCION");
            e.printStackTrace();
            return null;
        }
    }

}
