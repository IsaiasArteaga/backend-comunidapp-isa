package com.pa.comunidapp_backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pa.comunidapp_backend.models.Menu;
import com.pa.comunidapp_backend.repositories.MenuRepository;

@Service
@Transactional
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    public List<Menu> getMenusByRolId(Long rolId) {
        return menuRepository.findMenusByRolId(rolId);
    }
}
