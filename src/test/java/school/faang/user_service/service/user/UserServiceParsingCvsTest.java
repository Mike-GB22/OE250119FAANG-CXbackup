package school.faang.user_service.service.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.Person;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.PersonUserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserServiceParsingCvsTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PersonUserMapper personUserMapper;
    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private UserService userService;
    @Spy
    private UserService userServiceMock = new UserService(userRepository, personUserMapper, countryRepository);

    @ParameterizedTest
    @CsvSource({"1", "12", "121"})
    @DisplayName("Positive. Create Users from List<Person>")
    public void  createUsersFromListPersonPositive(int size) {
        List<Person> persons = new ArrayList<>();
        for ( int i = 0; i < size; i++ ) {
            persons.add( new Person() );
        }

        Mockito.doReturn(new User()).when(userServiceMock).createUser(any(Person.class));
        List<User> users = userServiceMock.createUsers(persons);
        Mockito.verify(userServiceMock,Mockito.times(size)).createUser(any(Person.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Negative. Create Users from List<Person>, when list is null and empty")
    public void  createUsersFromListPersonWhenListIsNullOrEmpty(List<Person> persons) {
        List<User> users = userServiceMock.createUsers(persons);
        Mockito.verify(userServiceMock,Mockito.never()).createUser(any(Person.class));
    }


    @Test
    @DisplayName("Negative. Create User from Person, when Person is null")
    public void createUserFromPersonWhenPersonIsNull() {
        assertThrows(RuntimeException.class, () -> userService.createUser(null) );
    }

    @Test
    @DisplayName("Positive. Create User from Person, when Country is already in DB")
    public void createUserFromPersonWhenPersonExistCountry() {
        String countryName = "Test Country";
        Person person = getPersonWithCountry(countryName);
        Country country = new Country(1, countryName, List.of());

        Mockito.when(personUserMapper.toUser(any(Person.class))).thenReturn(new User());
        Mockito.when(countryRepository.findAll()).thenReturn(List.of(country));
        User result = userService.createUser(person);

        Mockito.verify(countryRepository, Mockito.never()).save(any(Country.class));
        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Positive. Create User from Person, when Country is NOT already in DB")
    public void createUserFromPersonWhenPersonNotExistCountry() {
        String countryNameInPerson = "Test Country";
        String countryNameInDB = "Other Country";
        Person person = getPersonWithCountry(countryNameInPerson);
        Country country = new Country(1, countryNameInDB, List.of());

        Mockito.when(personUserMapper.toUser(any(Person.class))).thenReturn(new User());
        Mockito.when(countryRepository.findAll()).thenReturn(List.of(country));
        User result = userService.createUser(person);

        Mockito.verify(countryRepository, Mockito.times(1)).save(any(Country.class));
        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
    }


    private Person getPersonWithCountry(String country) {
        Person.ContactInfo.Address address = new Person.ContactInfo.Address();
        address.setCountry(country);

        Person.ContactInfo contactInfo = new Person.ContactInfo();
        contactInfo.setAddress(address);

        Person person = new Person();
        person.setContactInfo(contactInfo);

        return person;
    }
}