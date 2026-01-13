package com.onlineStore.admin.section;

import com.onlineStoreCom.entity.section.Section;
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
public class SectionServiceTests {

    @Mock
    private SectionRepository repo;

    @InjectMocks
    private SectionService service;

    @Test
    public void testListSections() {
        List<Section> sections = new ArrayList<>();
        sections.add(new Section());
        when(repo.findAllSectionsSortedByOrder()).thenReturn(sections);

        List<Section> result = service.listSections();

        assertThat(result).hasSize(1);
    }

    @Test
    public void testSaveSection_New() {
        Section section = new Section();
        section.setHeading("Test");

        when(repo.count()).thenReturn(2L);
        when(repo.save(section)).thenReturn(section);

        service.saveSection(section);

        assertThat(section.getSectionOrder()).isEqualTo(3);
        verify(repo).save(section);
    }

    @Test
    public void testSaveSection_Update() {
        Section section = new Section();
        section.setId(1);
        section.setHeading("Test");

        when(repo.save(section)).thenReturn(section);

        service.saveSection(section);

        verify(repo).save(section);
        verify(repo, times(0)).count(); // position not updated
    }

    @Test
    public void testGetSection_Found() throws SectionNotFoundException {
        Section section = new Section();
        section.setId(1);
        when(repo.findById(1)).thenReturn(Optional.of(section));

        Section result = service.getSection(1);

        assertThat(result).isEqualTo(section);
    }

    @Test
    public void testGetSection_NotFound() {
        when(repo.findById(1)).thenReturn(Optional.empty());

        assertThrows(SectionNotFoundException.class, () -> service.getSection(1));
    }

    @Test
    public void testDeleteSection_Found() throws SectionNotFoundException {
        when(repo.existsById(1)).thenReturn(true);
        doNothing().when(repo).deleteById(1);

        service.deleteSection(1);

        verify(repo).deleteById(1);
    }

    @Test
    public void testDeleteSection_NotFound() {
        when(repo.existsById(1)).thenReturn(false);

        assertThrows(SectionNotFoundException.class, () -> service.deleteSection(1));
    }

    @Test
    public void testUpdateSectionEnabledStatus_Found() throws SectionNotFoundException {
        when(repo.existsById(1)).thenReturn(true);
        doNothing().when(repo).updateEnabledStatus(1, true);

        service.updateSectionEnabledStatus(1, true);

        verify(repo).updateEnabledStatus(1, true);
    }

    @Test
    public void testUpdateSectionEnabledStatus_NotFound() {
        when(repo.existsById(1)).thenReturn(false);

        assertThrows(SectionNotFoundException.class, () -> service.updateSectionEnabledStatus(1, true));
    }

    @Test
    public void testMoveSectionUp_Simple() throws SectionNotFoundException, SectionUnmoveableException {
        Section s1 = new Section(1);
        s1.setSectionOrder(1);
        Section s2 = new Section(2);
        s2.setSectionOrder(2);

        List<Section> list = List.of(s1, s2);

        when(repo.getSimpleSectionById(2)).thenReturn(s2);
        when(repo.getOnlySectionIDsSortedByOrder()).thenReturn(list);

        service.moveSectionUp(2);

        assertThat(s2.getSectionOrder()).isEqualTo(1);
        assertThat(s1.getSectionOrder()).isEqualTo(2);
        verify(repo, times(2)).updateSectionPosition(any(Integer.class), any(Integer.class));
    }

    @Test
    public void testMoveSectionUp_First() {
        Section s1 = new Section(1);
        s1.setSectionOrder(1);
        List<Section> list = List.of(s1);

        when(repo.getSimpleSectionById(1)).thenReturn(s1);
        when(repo.getOnlySectionIDsSortedByOrder()).thenReturn(list);

        assertThrows(SectionUnmoveableException.class, () -> service.moveSectionUp(1));
    }

    @Test
    public void testMoveSectionDown_Simple() throws SectionNotFoundException, SectionUnmoveableException {
        Section s1 = new Section(1);
        s1.setSectionOrder(1);
        Section s2 = new Section(2);
        s2.setSectionOrder(2);

        List<Section> list = List.of(s1, s2);

        when(repo.getSimpleSectionById(1)).thenReturn(s1);
        when(repo.getOnlySectionIDsSortedByOrder()).thenReturn(list);

        service.moveSectionDown(1);

        assertThat(s1.getSectionOrder()).isEqualTo(2);
        assertThat(s2.getSectionOrder()).isEqualTo(1);
        verify(repo, times(2)).updateSectionPosition(any(Integer.class), any(Integer.class));
    }

    @Test
    public void testMoveSectionDown_Last() {
        Section s1 = new Section(1);
        s1.setSectionOrder(1);
        List<Section> list = List.of(s1);

        when(repo.getSimpleSectionById(1)).thenReturn(s1);
        when(repo.getOnlySectionIDsSortedByOrder()).thenReturn(list);

        assertThrows(SectionUnmoveableException.class, () -> service.moveSectionDown(1));
    }
}
