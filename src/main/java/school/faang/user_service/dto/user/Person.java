package school.faang.user_service.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Person {
        @JsonProperty("firstName")
        private String firstName;

        @JsonProperty("lastName")
        private String lastName;

        @JsonUnwrapped
        ContactInfo contactInfo;

        @JsonUnwrapped
        Education education;

        String employer;

        @Data
        public static class ContactInfo {
             String email;
             String phone;

             @JsonUnwrapped
             Address address;

             @Data
             public static class Address {
                     String street;
                     String city;
                     String state;
                     String country;
                     String postalCode;
             }
        }

        @Data
        public static class Education {
                String faculty;
                String yearsOfStudy;
                String major;
                String GPA;
        }
}
