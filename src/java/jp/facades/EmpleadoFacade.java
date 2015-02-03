/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.facades;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Empleado;

/**
 *
 * @author CRISTIAN
 */
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

    public Boolean getEmpleadoByCodigo(Empleado empleado) {
        try {
            
            String sql = "SELECT e FROM Empleado e WHERE e.codigo = :codigoNuevo";
            String codigoViejo = null;
            if(empleado.getId()!=null){
                 Empleado empleadoTMP = em.find(Empleado.class, empleado.getId());
                 sql += " AND e.codigo <> :codigoViejo";
                 codigoViejo = empleadoTMP.getCodigo();
            }
            
            Query query = em.createQuery(sql);
            query.setParameter("codigoNuevo", empleado.getCodigo());
            if(empleado.getId()!=null){
                query.setParameter("codigoViejo", codigoViejo);
            }
            query.setMaxResults(1);
            Empleado empleadoTMP = (Empleado) query.getSingleResult();
            if (empleadoTMP != null) {
                return true;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
