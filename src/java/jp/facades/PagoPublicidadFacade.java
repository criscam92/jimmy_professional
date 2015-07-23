/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.facades;

import jp.entidades.PagoPublicidad;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.PagoDetalle;

/**
 *
 * @author arturo
 */
@Stateless
public class PagoPublicidadFacade extends AbstractFacade<PagoPublicidad> {
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PagoPublicidadFacade() {
        super(PagoPublicidad.class);
    }

    public PagoPublicidad getPagoPublicidadByPagoDetalle(PagoDetalle pd) {
        PagoPublicidad pagoPublicidad;
        try {
            Query query = getEntityManager().createQuery("SELECT pp FROM PagoPublicidad pp WHERE pp.pagoDetalle.id = :pd");
            query.setParameter("pd", pd.getId());
            query.setMaxResults(1);
            pagoPublicidad = (PagoPublicidad) query.getSingleResult();
        } catch (Exception e) {
            pagoPublicidad = null;
        }
        return pagoPublicidad;
    }
    
}
