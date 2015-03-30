package jp.facades;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.IngresoProducto;
import jp.entidades.Producto;

@Stateless
public class IngresoProductoFacade extends AbstractFacade<IngresoProducto> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public IngresoProductoFacade() {
        super(IngresoProducto.class);
    }

    public List<IngresoProducto> getIngresoProductoByProducto(Producto producto) {
        try {
            Query query = getEntityManager().createQuery("SELECT ip FROM IngresoProducto ip WHERE ip.producto.id = :prod");
            query.setParameter("prod", producto.getId());
            return query.getResultList();
        } catch (Exception e) {
        }
        return null;
    }

}
