package jp.facades;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import jp.entidades.Concepto;
import jp.entidades.Producto;

@Stateless
public class ConceptoFacade extends AbstractFacade<Concepto> {
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConceptoFacade() {
        super(Concepto.class);
    }
    
    @Override
    public List<Concepto> findAll() {
        return super.findAll(true);
    }
    
    @Override
    public List<Concepto> findAll(boolean asc) {
        return super.findAll(asc);
    }
    
}
