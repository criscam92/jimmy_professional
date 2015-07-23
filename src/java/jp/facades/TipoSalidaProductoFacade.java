package jp.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import jp.entidades.TipoSalidaProducto;

@Stateless
public class TipoSalidaProductoFacade extends AbstractFacade<TipoSalidaProducto> {
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoSalidaProductoFacade() {
        super(TipoSalidaProducto.class);
    }
    
}
