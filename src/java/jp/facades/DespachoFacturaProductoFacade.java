package jp.facades;

import java.util.List;
import jp.entidades.DespachoFacturaProducto;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.DespachoFactura;
import jp.entidades.Producto;


@Stateless
public class DespachoFacturaProductoFacade extends AbstractFacade<DespachoFacturaProducto> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DespachoFacturaProductoFacade() {
        super(DespachoFacturaProducto.class);
    }

    public List<DespachoFacturaProducto> getDespachosFacturaProductosByProducto(Producto producto) {
        try {
            Query query = getEntityManager().createQuery("SELECT dfp FROM DespachoFacturaProducto dfp WHERE dfp.producto.id = :prod");
            query.setParameter("prod", producto.getId());
            return query.getResultList();
        } catch (Exception e) {
        }
        return null;
    }

    public List<DespachoFacturaProducto> getDespachosFacturaProductoByDespachoFactura(DespachoFactura df) {
        try {
            Query query = getEntityManager().createQuery("SELECT dfp FROM DespachoFacturaProducto dfp WHERE dfp.despachoFactura.id = :dFactura");
            query.setParameter("dFactura", df.getId());
            return query.getResultList();
        } catch (Exception e) {
        }
        return null;
    }

}
