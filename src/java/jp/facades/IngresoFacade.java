package jp.facades;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Ingreso;
import jp.entidades.IngresoProducto;

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

}
