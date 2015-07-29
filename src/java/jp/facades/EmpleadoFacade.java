package jp.facades;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Empleado;
import jp.entidades.Producto;

@Stateless
public class EmpleadoFacade extends AbstractFacade<Empleado> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EmpleadoFacade() {
        super(Empleado.class);
    }

    public List<Empleado> getEmpleadoByQuery(String query) {
        try {
            Query q = getEntityManager().createQuery("SELECT e FROM Empleado e WHERE UPPER(CONCAT(e.codigo,' - ',e.nombre)) LIKE UPPER(:param)");
            q.setParameter("param", "%" + query + "%");
            q.setFirstResult(0);
            q.setMaxResults(10);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean existeDocumento(Empleado empleado){
        try {
            String jpql = "SELECT COUNT(e.id) FROM Empleado e WHERE e.documento = :documento";
            if(empleado.getId()!=null){
                jpql += " AND e.id != :id";
            }
            Query query = getEntityManager().createQuery(jpql);
            query.setParameter("documento", empleado.getDocumento());
            if(empleado.getId()!=null){
                query.setParameter("id", empleado.getId());
            }
            Long result = (Long) query.getSingleResult();
            if(result>0l){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public List<Empleado> findAll() {
        return super.findAll(true);
    }
    
    @Override
    public List<Empleado> findAll(boolean asc) {
        return super.findAll(asc);
    }

}
