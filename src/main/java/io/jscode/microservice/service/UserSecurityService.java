package io.jscode.microservice.service;


import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.jscode.db.entity.AdmiUsuario;
import io.jscode.db.service.DBAdmiUsuarioService;
import io.jscode.db.service.impl.DBAdmiUsuarioServiceImpl;

@Service
public class UserSecurityService implements UserDetailsService {

    @Autowired
    DBAdmiUsuarioService admiUsuarioService;

    @Autowired
    BeanFactory beanFactory;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        admiUsuarioService = (DBAdmiUsuarioServiceImpl) beanFactory.getBean(DBAdmiUsuarioServiceImpl.class);
        AdmiUsuario usuario = new AdmiUsuario();
        usuario.setUsuario(username);
        AdmiUsuario usuarioResponse = admiUsuarioService.getBy(usuario);

        return User.builder()
                   .username(usuarioResponse.getUsuario())
                   .password(usuarioResponse.getPassword())
                   .roles(usuarioResponse.getRol().getRol())
                   .accountLocked(usuarioResponse.isLocked())
                   .disabled(usuarioResponse.isDisabled())
                   .build();
    }
    
}
