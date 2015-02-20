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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Ciudad;
import jp.entidades.Pais;

/**
 *
 * @author CRISTIAN
 */
@Stateless
public class CiudadFacade extends AbstractFacade<Ciudad> {
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CiudadFacade() {
        super(Ciudad.class);
    }

    public List<Ciudad> getCiudadesByPais(Pais pais) {
        try {
            Query query = getEntityManager().createQuery("SELECT c FROM Ciudad c WHERE c.pais.id = :pais");
            query.setParameter("pais", pais.getId());
            
            List<Ciudad> ciudades = query.
            
        } catch (NoResultException e) {
        }
        return new ArrayList<>();
    }
    
}
