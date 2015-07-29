package jp.facades;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import jp.entidades.CodigoDevolucion;
import jp.entidades.Producto;

@Stateless
public class CodigoDevolucionFacade extends AbstractFacade<CodigoDevolucion> {
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CodigoDevolucionFacade() {
        super(CodigoDevolucion.class);
    }
    
    @Override
    public List<CodigoDevolucion> findAll() {
        return super.findAll(true);
    }
    
    @Override
    public List<CodigoDevolucion> findAll(boolean asc) {
        return super.findAll(asc);
    }
    
}
