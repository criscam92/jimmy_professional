package jp.facades;

import java.util.List;
import jp.entidades.Zona;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import jp.entidades.Producto;

@Stateless
public class ZonaFacade extends AbstractFacade<Zona> {
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ZonaFacade() {
        super(Zona.class);
    }
    
    @Override
    public List<Zona> findAll() {
        return super.findAll(true);
    }
    
    @Override
    public List<Zona> findAll(boolean asc) {
        return super.findAll(asc);
    }
    
}
