package tn.esprit.studentmanagement.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.studentmanagement.entities.Department;
import tn.esprit.studentmanagement.services.IDepartmentService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentControllerTest {

    @Mock
    private IDepartmentService departmentService;

    @InjectMocks
    private DepartmentController controller;

    @Test
    void shouldReturnAllDepartments() {
        List<Department> expected = Arrays.asList(mock(Department.class));
        when(departmentService.getAllDepartments()).thenReturn(expected);

        List<Department> actual = controller.getAllDepartment();

        assertSame(expected, actual);
        verify(departmentService).getAllDepartments();
        verifyNoMoreInteractions(departmentService);
    }

    @Test
    void shouldReturnDepartmentById() {
        Department expected = mock(Department.class);
        when(departmentService.getDepartmentById(1L)).thenReturn(expected);

        Department actual = controller.getDepartment(1L);

        assertSame(expected, actual);
        verify(departmentService).getDepartmentById(1L);
        verifyNoMoreInteractions(departmentService);
    }

    @Test
    void shouldCreateDepartment() {
        Department toCreate = mock(Department.class);
        when(departmentService.saveDepartment(toCreate)).thenReturn(toCreate);

        Department actual = controller.createDepartment(toCreate);

        assertSame(toCreate, actual);
        verify(departmentService).saveDepartment(toCreate);
        verifyNoMoreInteractions(departmentService);
    }

    @Test
    void shouldUpdateDepartment() {
        Department toUpdate = mock(Department.class);
        when(departmentService.saveDepartment(toUpdate)).thenReturn(toUpdate);

        Department actual = controller.updateDepartment(toUpdate);

        assertSame(toUpdate, actual);
        verify(departmentService).saveDepartment(toUpdate);
        verifyNoMoreInteractions(departmentService);
    }

    @Test
    void shouldDeleteDepartment() {
        doNothing().when(departmentService).deleteDepartment(1L);

        controller.deleteDepartment(1L);

        verify(departmentService).deleteDepartment(1L);
        verifyNoMoreInteractions(departmentService);
    }
}