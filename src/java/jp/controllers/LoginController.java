package jp.controllers;

import java.io.IOException;
import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import jp.entidades.Usuario;
import jp.facades.UsuarioFacade;
import jp.util.JsfUtil;

@ManagedBean(name = "loginController")
@SessionScoped
public class LoginController implements Serializable {
    
    public static final String AUTH_KEY = "jp.usuario";
    public static final String LOGOUT_PARAM = "logout";
    
    @EJB
    private UsuarioFacade ejbFacade;
    
    public static Usuario user;
    
    private String userName;
    private String password;
    
    public LoginController() {
    }
    
    public Usuario getUser() {
        if (user == null && FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get(AUTH_KEY) != null) {
            user = (Usuario) FacesContext.getCurrentInstance().getExternalContext()
                    .getSessionMap().get(AUTH_KEY);
        }
        return user;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    private UsuarioFacade getFacade() {
        return ejbFacade;
    }
    
    public void login() {
        try {
            user = getFacade().login(userName, password);
            if (user != null) {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(AUTH_KEY, user);
                JsfUtil.addSuccessMessage("Se ingreso correctamente a la aplicación");
            } else {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(AUTH_KEY);
                JsfUtil.addWarnMessage("No se encontró el usuario y contraseña");
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            JsfUtil.addErrorMessage(e, JsfUtil.getMessageBundle("PersistenceErrorOccured"));
        }
        
    }
    
    public boolean isAdmin() {
        if (user != null) {
            return user.isAdmin();
        }
        return false;
    }
    
    public void logoutLink() {
        
        String url = ((HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest()).getContextPath()
                .concat("/faces/content/content/?").concat(LOGOUT_PARAM);
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        } catch (IOException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, "Error...");
        }
    }
    
}
