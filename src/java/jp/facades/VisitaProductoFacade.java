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
import jp.entidades.Producto;
import jp.entidades.Visita;
import jp.entidades.VisitaProducto;

/**
 *
 * @author CRISTIAN
 */
@Stateless
public class VisitaProductoFacade extends AbstractFacade<VisitaProducto> {
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VisitaProductoFacade() {
        super(VisitaProducto.class);
    }
    
    public List<Producto> getProductosByVisita(Visita visita){
        System.out.println("==="+visita.getObservacionesCliente()+"==========");
        List<Producto> productosTMP = new ArrayList<>();
        try {
            Query q = em.createQuery("SELECT vp.producto FROM VisitaProducto vp WHERE vp.visita.id= :visita");
            q.setParameter("visita", visita.getId());
            productosTMP = q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return productosTMP;
    }
    
}
