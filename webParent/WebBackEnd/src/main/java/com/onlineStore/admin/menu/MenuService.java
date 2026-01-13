package com.onlineStore.admin.menu;

import com.onlineStoreCom.entity.menu.Menu;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class MenuService {

    @Autowired
    private MenuRepository repo;

    public List<Menu> listAll() {
        return repo.findAllByOrderByTypeAscPositionAsc();
    }

    public void save(Menu menu) {
        setDefaultAlias(menu);

        if (menu.getId() == null) {
            setPositionForNewMenu(menu);
        } else {
            setPositionForEditedMenu(menu);
        }

        repo.save(menu);
    }

    private void setPositionForEditedMenu(Menu menu) {
        Menu existMenu = repo.findById(menu.getId()).get();

        if (!existMenu.getType().equals(menu.getType())) {
            // if the menu type changed, then set its position at the last
            setPositionForNewMenu(menu);
        }
    }

    private void setPositionForNewMenu(Menu newMenu) {
        // newly added menu always has position at the last
        Long newPosition = repo.countByType(newMenu.getType()) + 1;
        newMenu.setPosition(newPosition.intValue());
    }

    private void setDefaultAlias(Menu menu) {
        if (menu.getAlias() == null || menu.getAlias().isEmpty()) {
            menu.setAlias(menu.getTitle().replaceAll(" ", "-"));
        }
    }

    public Menu get(Integer id) throws MenuNotFoundException {
        try {
            return repo.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new MenuNotFoundException("Could not find any menu item with ID " + id);
        }
    }

    public void updateEnabledStatus(Integer id, boolean enabled) throws MenuNotFoundException {
        if (!repo.existsById(id)) {
            throw new MenuNotFoundException("Could not find any menu item with ID " + id);
        }
        repo.updateEnabledStatus(id, enabled);
    }

    public void delete(Integer id) throws MenuNotFoundException {
        if (!repo.existsById(id)) {
            throw new MenuNotFoundException("Could not find any menu item with ID " + id);
        }
        repo.deleteById(id);
    }

    public void moveMenu(Integer id, MenuMoveDirection direction) throws MenuNotFoundException, MenuNotFoundException {
        if (direction.equals(MenuMoveDirection.UP)) {
            moveMenuUp(id);
        } else {
            moveMenuDown(id);
        }
    }

    private void moveMenuUp(Integer id) throws MenuNotFoundException, MenuNotFoundException {
        Optional<Menu> findById = repo.findById(id);
        if (!findById.isPresent()) {
            throw new MenuNotFoundException("Could not find any menu item with ID " + id);
        }

        Menu currentMenu = findById.get();
        List<Menu> allMenusOfSameType = repo.findByTypeOrderByPositionAsc(currentMenu.getType());
        int currentMenuIndex = allMenusOfSameType.indexOf(currentMenu);

        if (currentMenuIndex == 0) {
            throw new MenuNotFoundException("The menu ID " + id + " is already in the first position");
        }

        // swap current menu item with the previous one, thus the given menu is moved up
        int previousMenuIndex = currentMenuIndex - 1;
        Menu previousMenu = allMenusOfSameType.get(previousMenuIndex);

        currentMenu.setPosition(previousMenuIndex + 1);
        allMenusOfSameType.set(previousMenuIndex, currentMenu);

        previousMenu.setPosition(currentMenuIndex + 1);
        allMenusOfSameType.set(currentMenuIndex, previousMenu);

        // update all menu items of the same type
        repo.saveAll(Arrays.asList(currentMenu, previousMenu));
    }

    private void moveMenuDown(Integer id) throws MenuNotFoundException, MenuNotFoundException {
        Optional<Menu> findById = repo.findById(id);
        if (!findById.isPresent()) {
            throw new MenuNotFoundException("Could not find any menu item with ID " + id);
        }

        Menu currentMenu = findById.get();
        List<Menu> allMenusOfSameType = repo.findByTypeOrderByPositionAsc(currentMenu.getType());
        int currentMenuIndex = allMenusOfSameType.indexOf(currentMenu);

        if (currentMenuIndex == allMenusOfSameType.size() - 1) {
            throw new MenuNotFoundException("The menu ID " + id + " is already in the last position");
        }

        // swap current menu item with the next one, thus the given menu is moved down
        int nextMenuIndex = currentMenuIndex + 1;
        Menu nextMenu = allMenusOfSameType.get(nextMenuIndex);

        currentMenu.setPosition(nextMenuIndex + 1);
        allMenusOfSameType.set(nextMenuIndex, currentMenu);

        nextMenu.setPosition(currentMenuIndex + 1);
        allMenusOfSameType.set(currentMenuIndex, nextMenu);

        // update all menu items of the same type
        repo.saveAll(Arrays.asList(currentMenu, nextMenu));
    }

    public enum MenuMoveDirection {
        UP, DOWN
    }
}
