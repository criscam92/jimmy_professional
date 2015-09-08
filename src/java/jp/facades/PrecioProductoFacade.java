package jp.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import jp.entidades.PrecioProducto;

@Stateless
public class PrecioProductoFacade extends AbstractFacade<PrecioProducto> {
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PrecioProductoFacade() {
        super(PrecioProducto.class);
    }
    
}
