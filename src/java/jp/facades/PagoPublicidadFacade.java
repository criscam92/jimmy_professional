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
    
}
