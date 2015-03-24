package jp.facades;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Ingreso;
import jp.entidades.IngresoProducto;
import jp.entidades.Producto;

@Stateless
public class IngresoFacade extends AbstractFacade<Ingreso> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public IngresoFacade() {
        super(Ingreso.class);
    }

    public List<IngresoProducto> getProductosByIngreso(Ingreso ingreso) {
        try {
            Query query = getEntityManager().createQuery("SELECT i FROM IngresoProducto i WHERE i.ingreso.id = :ingre");
            query.setParameter("ingre", ingreso.getId());
            return query.getResultList();
        } catch (NoResultException e) {
        }
        return null;
    }

    public Long getCountIngresoByProducto(Producto producto) {
        try {
            Query q = getEntityManager().createQuery("SELECT SUM(ip.cantidad) FROM IngresoProducto ip WHERE ip.producto.id = :producto");
            q.setParameter("producto", producto.getId());
            return (Long) q.getSingleResult();
        } catch (Exception e) {
        }
        return 0L;
    }

}
