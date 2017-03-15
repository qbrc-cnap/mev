package edu.dfci.cccb.mev.web.test.subscriber.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.dfci.cccb.mev.annotation.server.configuration.AnnotationProjectManagerConfiguration;
import edu.dfci.cccb.mev.dataset.domain.contract.*;
import edu.dfci.cccb.mev.dataset.rest.configuration.DatasetRestConfiguration;
import edu.dfci.cccb.mev.presets.rest.configuration.PresetsRestConfiguration;
import edu.dfci.cccb.mev.test.annotation.server.configuration.ProbeAnnotationsPersistanceConfigTest;
import edu.dfci.cccb.mev.web.configuration.DispatcherConfiguration;
import edu.dfci.cccb.mev.web.configuration.PersistenceConfiguration;
import edu.dfci.cccb.mev.web.configuration.container.ContainerConfigurations;
import edu.dfci.cccb.mev.web.domain.Subscriber;
import lombok.extern.log4j.Log4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.inject.Inject;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Log4j
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {DispatcherConfiguration.class,
        PersistenceConfiguration.class,
        ContainerConfigurations.class,
        DatasetRestConfiguration.class,
        AnnotationProjectManagerConfiguration.class,
        PresetsRestConfiguration.class,
        ProbeAnnotationsPersistanceConfigTest.class})
public class TestSubscriberController {

    @Inject
    WebApplicationContext applicationContext;
    private MockMvc mockMvc;
    private
    @Inject
    ObjectMapper jsonObjectMapper;
    private MockHttpSession mockHttpSession;
    private Workspace workspace;
    private
    @Inject
    DatasetBuilder datasetBuilder;
    private Dataset mockDataset;


    @Before
    public void setup() throws DatasetBuilderException, InvalidDatasetNameException, InvalidDimensionTypeException {
        //create web applicatin context
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
        //create a new session object
        mockHttpSession = new MockHttpSession();
        //create a new request
        MockHttpServletRequest mockHttpRequest = new MockHttpServletRequest();
        //assign session to request
        mockHttpRequest.setSession(mockHttpSession);
        //bind current thread context to the request object
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpRequest));
        //touch the workspace bean to create new workspace incurrent session
        workspace = applicationContext.getBean(Workspace.class);
    }

    private List<Subscriber> getSubsctibers() throws Exception {
        MvcResult mvcResultGet = this.mockMvc.perform(
                get("/subscriber")
                        .param("format", "json")
                        .session(mockHttpSession)
                        .accept("application/json")
        )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        List<Subscriber> result = jsonObjectMapper.readValue(mvcResultGet.getResponse().getContentAsString(),
                new TypeReference<List<Subscriber>>() {
                });
        return result;
    }

    @Test
    public void testPutGetSubscriber() throws Exception {
        //ensure no subscribers exist
        assertThat(getSubsctibers().size(), equalTo(0));

        //create subscriber
        Subscriber subscriber = new Subscriber("x@z.com", "x");

        MvcResult mvcResult = this.mockMvc.perform(
                put("/subscriber")
                        .param("format", "json")
                        .content(jsonObjectMapper.writeValueAsString(subscriber))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(mockHttpSession)
                        .accept("application/json")
        )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        //ensure one subscriber added
        assertThat(getSubsctibers().size(), equalTo(1));
    }

}
