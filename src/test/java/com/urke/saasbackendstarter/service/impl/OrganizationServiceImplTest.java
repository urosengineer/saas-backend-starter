package com.urke.saasbackendstarter.service.impl;

import com.urke.saasbackendstarter.domain.Organization;
import com.urke.saasbackendstarter.dto.organization.OrganizationCreateRequest;
import com.urke.saasbackendstarter.events.OrganizationEvent;
import com.urke.saasbackendstarter.exception.OrganizationAlreadyExistsException;
import com.urke.saasbackendstarter.exception.OrganizationNotFoundException;
import com.urke.saasbackendstarter.repository.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class OrganizationServiceImplTest {

    @Mock private OrganizationRepository organizationRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private MessageSource messageSource;

    @InjectMocks
    private OrganizationServiceImpl organizationService;

    private Organization org;
    private OrganizationCreateRequest request;

    @BeforeEach
    void setUp() {
        request = OrganizationCreateRequest.builder()
                .name("Test Org")
                .build();

        org = Organization.builder()
                .id(1L)
                .name("Test Org")
                .slug("test-org")
                .deleted(false)
                .build();
    }

    @Test
    void create_success() {
        when(organizationRepository.existsByNameAndDeletedFalse(request.getName())).thenReturn(false);
        when(organizationRepository.existsBySlugAndDeletedFalse("test-org")).thenReturn(false);
        when(organizationRepository.save(any(Organization.class))).thenReturn(org);

        Organization result = organizationService.create(request);

        assertThat(result.getName()).isEqualTo("Test Org");
        assertThat(result.getSlug()).isEqualTo("test-org");
        verify(eventPublisher).publishEvent(any(OrganizationEvent.class));
    }

    @Test
    void create_duplicateName_shouldThrowException() {
        when(organizationRepository.existsByNameAndDeletedFalse(request.getName())).thenReturn(true);
        when(messageSource.getMessage(eq("org.exists"), any(), any())).thenReturn("Org already exists!");

        assertThatThrownBy(() -> organizationService.create(request))
            .isInstanceOf(OrganizationAlreadyExistsException.class)
            .hasMessageContaining("Org already exists!");
    }

    @Test
    void create_duplicateSlug_shouldThrowException() {
        when(organizationRepository.existsByNameAndDeletedFalse(request.getName())).thenReturn(false);
        when(organizationRepository.existsBySlugAndDeletedFalse("test-org")).thenReturn(true);
        when(messageSource.getMessage(eq("org.exists"), any(), any())).thenReturn("Org already exists!");

        assertThatThrownBy(() -> organizationService.create(request))
            .isInstanceOf(OrganizationAlreadyExistsException.class)
            .hasMessageContaining("Org already exists!");
    }

    @Test
    void findAll_shouldReturnList() {
        List<Organization> orgList = List.of(org);
        when(organizationRepository.findAllByDeletedFalse()).thenReturn(orgList);

        List<Organization> result = organizationService.findAll();

        assertThat(result).containsExactly(org);
    }

    @Test
    void findById_found() {
        when(organizationRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(org));
        Optional<Organization> result = organizationService.findById(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Org");
    }

    @Test
    void findById_notFound() {
        when(organizationRepository.findByIdAndDeletedFalse(2L)).thenReturn(Optional.empty());
        Optional<Organization> result = organizationService.findById(2L);
        assertThat(result).isEmpty();
    }

    @Test
    void findByName_found() {
        when(organizationRepository.findByNameAndDeletedFalse("Test Org")).thenReturn(Optional.of(org));
        Optional<Organization> result = organizationService.findByName("Test Org");
        assertThat(result).isPresent();
        assertThat(result.get().getSlug()).isEqualTo("test-org");
    }

    @Test
    void findBySlug_found() {
        when(organizationRepository.findBySlugAndDeletedFalse("test-org")).thenReturn(Optional.of(org));
        Optional<Organization> result = organizationService.findBySlug("test-org");
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Org");
    }

    @Test
    void deleteOrganization_success() {
        when(organizationRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(org));
        when(organizationRepository.save(any(Organization.class))).thenReturn(org);

        organizationService.deleteOrganization(1L);

        assertThat(org.isDeleted()).isTrue();
        verify(organizationRepository).save(org);
        verify(eventPublisher).publishEvent(any(OrganizationEvent.class));
    }

    @Test
    void deleteOrganization_notFound_shouldThrow() {
        when(organizationRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("org.notfound"), any(), any())).thenReturn("Not found");

        assertThatThrownBy(() -> organizationService.deleteOrganization(1L))
            .isInstanceOf(OrganizationNotFoundException.class)
            .hasMessageContaining("Not found");
    }

    @Test
    void findAll_withPageable() {
        Page<Organization> page = new PageImpl<>(List.of(org));
        Pageable pageable = PageRequest.of(0, 10);
        when(organizationRepository.findAllByDeletedFalse(pageable)).thenReturn(page);

        Page<Organization> result = organizationService.findAll(pageable);

        assertThat(result.getContent()).contains(org);
    }

    @Test
    void findAllByNameFilter_shouldReturnPage() {
        Page<Organization> page = new PageImpl<>(List.of(org));
        Pageable pageable = PageRequest.of(0, 10);
        when(organizationRepository.findByNameContainingIgnoreCaseAndDeletedFalse("Test", pageable)).thenReturn(page);

        Page<Organization> result = organizationService.findAllByNameFilter("Test", pageable);

        assertThat(result.getContent()).contains(org);
    }
}