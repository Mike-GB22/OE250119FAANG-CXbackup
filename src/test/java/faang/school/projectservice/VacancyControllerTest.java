package faang.school.projectservice;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.controller.VacancyController;
import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.mapper.VacancyMapperImpl;
import faang.school.projectservice.model.*;
import faang.school.projectservice.service.VacancyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Collections;
import org.springframework.http.MediaType;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class VacancyControllerTest {
    private MockMvc mockMvc;
    @Mock
    private VacancyService vacancyService;
    @Spy
    private VacancyMapperImpl vacancyMapper;
    @InjectMocks
    private VacancyController vacancyController;
    VacancyServiceTest service = new VacancyServiceTest();
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(vacancyController).build();
    }
    @Test
    void shouldThrowNullPointerExceptionWhenPositionIsNull() {
        VacancyDto vacancyDto = new VacancyDto();
        service.dtoInitializer(vacancyDto);
        vacancyDto.setPositionId(null);
        vacancyDto.setProjectId(1L);
        vacancyDto.setCuratorRoleId(0);
        assertThrows(NullPointerException.class, () -> vacancyController.isDataValid(vacancyDto));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenCuratorRoleIsInvalid() {
        VacancyDto vacancyDto = new VacancyDto();
        service.dtoInitializer(vacancyDto);
        vacancyDto.setPositionId(1);
        vacancyDto.setProjectId(1L);
        vacancyDto.setCuratorRoleId(2);

        assertThrows(IllegalArgumentException.class, () -> vacancyController.isDataValid(vacancyDto));
    }
    @Test
    public void testCreateVacancy() throws Exception {
        VacancyDto vacancyDto = new VacancyDto();
        service.dtoInitializer(vacancyDto);
        Vacancy vacancy = new Vacancy();
        vacancyInitializer(vacancy);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(vacancyDto);
        mockMvc.perform(post("/api/create_vacancy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isOk());
        verify(vacancyMapper, times(1)).toEntity(vacancyDto);
        verify(vacancyService, times(1)).createVacancy(any(Vacancy.class));
    }
    @Test
    public void testUpdateVacancy() throws Exception {
        VacancyDto vacancyDto1 = new VacancyDto();
        service.dtoInitializer(vacancyDto1);
        Vacancy vacancy = new Vacancy();
        vacancyInitializer(vacancy);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(vacancyDto1);
        mockMvc.perform(put("/api/update_vacancy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isOk());
        verify(vacancyMapper, times(1)).toEntity(vacancyDto1);
        verify(vacancyService, times(1)).updateVacancy(any(Vacancy.class));
        }
    @Test
    public void testDeleteVacancy() throws Exception {
        long vacancyId = 5L;
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(vacancyId);

        mockMvc.perform(delete("/api/delete_vacancy/{vacancyId}", vacancyId)
                        .contentType("application/json")
                        .content(jsonContent))
                .andExpect(status().isOk());

        verify(vacancyService, times(1)).deleteVacancy(vacancyId);
    }


        public void vacancyInitializer (Vacancy vacancy) {
            vacancy.setCuratorRole(TeamRole.OWNER);
            vacancy.setProject(new Project());
            vacancy.setPosition(TeamRole.DEVELOPER);
            vacancy.setId(5L);
            vacancy.setName("Java Developer");
            vacancy.setCuratorId(2L);
            vacancy.setCount(1);
            vacancy.setCandidates(Collections.singletonList(new Candidate()));
            vacancy.setStatus(VacancyStatus.OPEN);
            vacancy.setDescription("Java Developer");
            vacancy.setSalary(1000.0);
            vacancy.setCoverImageKey("Java Developer");
            vacancy.setRequiredSkillIds(Collections.singletonList(1L));
        }
}
