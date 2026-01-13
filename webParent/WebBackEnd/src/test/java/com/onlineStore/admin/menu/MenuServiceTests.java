package com.onlineStore.admin.menu;

import com.onlineStoreCom.entity.menu.Menu;
import com.onlineStoreCom.entity.menu.MenuType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTests {

    @Mock
    private MenuRepository repo;

    @InjectMocks
    private MenuService service;

    @Test
    public void testListAll() {
        List<Menu> menus = new ArrayList<>();
        menus.add(new Menu());
        when(repo.findAllByOrderByTypeAscPositionAsc()).thenReturn(menus);

        List<Menu> result = service.listAll();

        assertThat(result).hasSize(1);
        verify(repo, times(1)).findAllByOrderByTypeAscPositionAsc();
    }

    @Test
    public void testSaveNewMenu() {
        Menu menu = new Menu();
        menu.setType(MenuType.HEADER);
        menu.setTitle("Test Menu");

        when(repo.countByType(MenuType.HEADER)).thenReturn(0L);
        when(repo.save(any(Menu.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.save(menu);

        assertThat(menu.getAlias()).isEqualTo("Test-Menu");
        assertThat(menu.getPosition()).isEqualTo(1);
        verify(repo, times(1)).save(menu);
    }

    @Test
    public void testSaveEditedMenu_TypeChanged() {
        Menu menu = new Menu();
        menu.setId(1);
        menu.setType(MenuType.FOOTER); // Changed type
        menu.setTitle("Test Menu");

        Menu existingMenu = new Menu();
        existingMenu.setId(1);
        existingMenu.setType(MenuType.HEADER);

        when(repo.findById(1)).thenReturn(Optional.of(existingMenu));
        when(repo.countByType(MenuType.FOOTER)).thenReturn(5L);
        when(repo.save(any(Menu.class))).thenReturn(menu);

        service.save(menu);

        assertThat(menu.getPosition()).isEqualTo(6);
    }

    @Test
    public void testGet_Found() throws MenuNotFoundException {
        Menu menu = new Menu();
        menu.setId(1);
        when(repo.findById(1)).thenReturn(Optional.of(menu));

        Menu result = service.get(1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    public void testGet_NotFound() {
        when(repo.findById(1)).thenReturn(Optional.empty());

        assertThrows(MenuNotFoundException.class, () -> {
            service.get(1);
        });
    }

    @Test
    public void testUpdateEnabledStatus_Found() throws MenuNotFoundException {
        when(repo.existsById(1)).thenReturn(true);
        doNothing().when(repo).updateEnabledStatus(1, true);

        service.updateEnabledStatus(1, true);

        verify(repo, times(1)).updateEnabledStatus(1, true);
    }

    @Test
    public void testUpdateEnabledStatus_NotFound() {
        when(repo.existsById(1)).thenReturn(false);

        assertThrows(MenuNotFoundException.class, () -> {
            service.updateEnabledStatus(1, true);
        });
    }

    @Test
    public void testDelete_Found() throws MenuNotFoundException {
        when(repo.existsById(1)).thenReturn(true);
        doNothing().when(repo).deleteById(1);

        service.delete(1);

        verify(repo, times(1)).deleteById(1);
    }

    @Test
    public void testDelete_NotFound() {
        when(repo.existsById(1)).thenReturn(false);

        assertThrows(MenuNotFoundException.class, () -> {
            service.delete(1);
        });
    }

    @Test
    public void testMoveMenu_Up() throws MenuNotFoundException {
        Menu current = new Menu();
        current.setId(2);
        current.setType(MenuType.HEADER);
        current.setPosition(2);

        Menu prev = new Menu();
        prev.setId(1);
        prev.setType(MenuType.HEADER);
        prev.setPosition(1);

        List<Menu> list = new ArrayList<>();
        list.add(prev);
        list.add(current);

        when(repo.findById(2)).thenReturn(Optional.of(current));
        when(repo.findByTypeOrderByPositionAsc(MenuType.HEADER)).thenReturn(list);

        service.moveMenu(2, MenuService.MenuMoveDirection.UP);

        assertThat(current.getPosition()).isEqualTo(1);
        assertThat(prev.getPosition()).isEqualTo(2);
        verify(repo, times(1)).saveAll(any());
    }

    @Test
    public void testMoveMenu_Down() throws MenuNotFoundException {
        Menu current = new Menu();
        current.setId(1);
        current.setType(MenuType.HEADER);
        current.setPosition(1);

        Menu next = new Menu();
        next.setId(2);
        next.setType(MenuType.HEADER);
        next.setPosition(2);

        List<Menu> list = new ArrayList<>();
        list.add(current);
        list.add(next);

        when(repo.findById(1)).thenReturn(Optional.of(current));
        when(repo.findByTypeOrderByPositionAsc(MenuType.HEADER)).thenReturn(list);

        service.moveMenu(1, MenuService.MenuMoveDirection.DOWN);

        assertThat(current.getPosition()).isEqualTo(2);
        assertThat(next.getPosition()).isEqualTo(1);
        verify(repo, times(1)).saveAll(any());
    }
}
