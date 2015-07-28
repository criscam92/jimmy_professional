package jp.facades;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Promocion;
import jp.entidades.PromocionProducto;

@Stateless
public class PromocionProductoFacade extends AbstractFacade<PromocionProducto> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PromocionProductoFacade() {
        super(PromocionProducto.class);
    }

    public List<PromocionProducto> getPromocionProductoByProducto(Promocion promocion, EntityManager em) {
        if (em == null) {
            em = getEntityManager();
        }
        try {
            Query query = em.createQuery("SELECT pp FROM PromocionProducto pp WHERE pp.promocion.id = :prom");
            query.setParameter("prom", promocion.getId());
            return query.getResultList();
        } catch (Exception e) {
        }
        return null;
    }

}
