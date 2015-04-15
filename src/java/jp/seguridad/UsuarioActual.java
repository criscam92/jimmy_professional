/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.seguridad;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import jp.controllers.LoginController;
import jp.entidades.Usuario;

/**
 *
 * @author gurzaf
 */
@ManagedBean(name = "currentUser")
@SessionScoped
public class UsuarioActual {
    
    private Usuario usuario;
    
    public Usuario get(){
        if(usuario==null){
            usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext()
                    .getSessionMap().get(LoginController.AUTH_KEY);
        }
        return usuario;
    }
    
}
