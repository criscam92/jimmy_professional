package jp.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import jp.entidades.Visita;

@Stateless
public class VisitaFacade extends AbstractFacade<Visita> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VisitaFacade() {
        super(Visita.class);
        
    }

    

}
