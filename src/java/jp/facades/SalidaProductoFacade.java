package jp.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import jp.entidades.SalidaProducto;

@Stateless
public class SalidaProductoFacade extends AbstractFacade<SalidaProducto> {
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SalidaProductoFacade() {
        super(SalidaProducto.class);
    }
}
