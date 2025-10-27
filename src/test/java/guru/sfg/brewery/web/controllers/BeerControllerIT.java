package guru.sfg.brewery.web.controllers;

import guru.sfg.brewery.repositories.BeerInventoryRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.services.BeerService;
import guru.sfg.brewery.services.BreweryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BeerControllerIT {

    @Autowired
    WebApplicationContext wac;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserDetailsService uds;

    @Autowired
    PasswordEncoder encoder;

    @MockBean
    BeerRepository beerRepository;

    @MockBean
    BeerInventoryRepository beerInventoryRepository;

    @MockBean
    BreweryService breweryService;

    @MockBean
    CustomerRepository customerRepository;

    @MockBean
    BeerService beerService;

    @BeforeEach
    void setUp() {
        // If you prefer builder style, uncomment the next 3 lines and remove field injection for mockMvc
        // mockMvc = MockMvcBuilders.webAppContextSetup(wac)
        //         .apply(springSecurity())
        //         .build();
    }

    @Test
    void scottUsesBcrypt15AndMatches() {
        UserDetails scott = uds.loadUserByUsername("scott");
        assertTrue(scott.getPassword().startsWith("{bcrypt15}"),
                "Scott's stored hash should be prefixed with {bcrypt15}");
        assertTrue(encoder.matches("tiger", scott.getPassword()),
                "Raw password 'tiger' must match Scott's hash");
    }

    @Test
    void initCreationForm() throws Exception {
        mockMvc.perform(get("/beers/new")
                        .with(httpBasic("admin", "2003")))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/createBeer"))
                .andExpect(model().attributeExists("beer"));
    }

    @WithMockUser(username = "spring")
    @Test
    void findBeers() throws Exception {
        mockMvc.perform(get("/beers/find"))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/findBeers"))
                .andExpect(model().attributeExists("beer"));
    }

    @Test
    void findBeersWithHttp() throws Exception {
        mockMvc.perform(get("/beers/find")
                        .with(httpBasic("Hoss", "2003")))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/findBeers"))
                .andExpect(model().attributeExists("beer"));
    }
}
