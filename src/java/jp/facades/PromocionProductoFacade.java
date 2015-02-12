package jp.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import jp.entidades.PromocionProducto;

/**
 *
 * @author CRISTIAN
 */
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
    
}
