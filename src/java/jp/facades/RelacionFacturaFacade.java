/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import jp.entidades.RelacionFactura;

/**
 *
 * @author CRISTIAN
 */
@Stateless
public class RelacionFacturaFacade extends AbstractFacade<RelacionFactura> {
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RelacionFacturaFacade() {
        super(RelacionFactura.class);
    }
    
}
