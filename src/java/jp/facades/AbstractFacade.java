/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.facades;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Query;
import jp.entidades.Empleado;
import jp.entidades.aux.Codificable;

/**
 *
 * @author CRISTIAN
 */
public abstract class AbstractFacade<T> {
    private Class<T> entityClass;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    public void create(T entity) {
        getEntityManager().persist(entity);
    }

    public void edit(T entity) {
        getEntityManager().merge(entity);
    }

    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    public List<T> findAll() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<T> findRange(int[] range) {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public int count() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }
    
    public <U extends Codificable> Boolean getEntityByCodigo(U entity) {
        try {
            
            String sql = "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e.codigo = :codigoNuevo";
            String codigoViejo = null;
            
            if(entity.getId()!=null){
                 U entityTMP = (U)getEntityManager().find(entityClass, entity.getId());
                 sql += " AND e.codigo <> :codigoViejo";
                 codigoViejo = entityTMP.getCodigo();
            }
            
            Query query = getEntityManager().createQuery(sql);
            query.setParameter("codigoNuevo", entity.getCodigo());
            if(entity.getId()!=null){
                query.setParameter("codigoViejo", codigoViejo);
            }
            query.setMaxResults(1);
            T empleadoTMP = (T) query.getSingleResult();
            if (empleadoTMP != null) {
                return true;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
}
