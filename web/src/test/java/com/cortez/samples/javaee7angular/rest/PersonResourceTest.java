package com.cortez.samples.javaee7angular.rest;

import com.cortez.samples.javaee7angular.data.Person;
import com.cortez.samples.javaee7angular.pagination.PaginatedListWrapper;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Class PersonResourceTest
 */
@RunWith(Arquillian.class)
public class PersonResourceTest {
    private WebTarget target;
    @ArquillianResource
    private URL base;

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        final File[] model = Maven.resolver().loadPomFromFile("pom.xml").resolve(
                "com.cortez.samples.javaee7-angular:model").withoutTransitivity().asFile();

        final File[] business = Maven.resolver().loadPomFromFile("pom.xml").resolve(
                "com.cortez.samples.javaee7-angular:business").withoutTransitivity().asFile();

        final WebArchive webArchive =
                ShrinkWrap.create(WebArchive.class).addClasses(JavaEE7AngularApplication.class, PersonResource.class,
                        PaginatedListWrapper.class).addAsLibraries(
                        model).addAsLibraries(business);

        return webArchive;
    }

    @Before
    public void setUp() throws MalformedURLException {
        Client client = ClientBuilder.newClient();
        target = client.target(URI.create(new URL(base, "resources/persons").toExternalForm()));
        target.register(Person.class);
    }

    @Test
    public void findPerson() {
        final Person person =
                target.path("{id}").resolveTemplate("id", 1).request(MediaType.APPLICATION_JSON).get(
                        Person.class);

        assertThat(person, notNullValue());
        assertThat(person.getName(), is("Uzumaki Naruto"));
    }

    @Test
    public void listPersonsDefault() {
        final PaginatedListWrapper paginatedListWrapper =
                target.request(MediaType.APPLICATION_JSON).get(PaginatedListWrapper.class);

        assertThat(paginatedListWrapper, notNullValue());
        assertThat(paginatedListWrapper.getCurrentPage(), is(1));
        assertThat(paginatedListWrapper.getPageSize(), is(10));
        assertThat(paginatedListWrapper.getSortFields(), is("id"));
        assertThat(paginatedListWrapper.getTotalResults(), is(21));
        assertThat(paginatedListWrapper.getSortDirections(), is("ASC"));
    }

    @Test
    public void savePerson() {
        Person person = new Person();
        person.setDescription("The Description");
        person.setImageUrl("http://google.com");
        person.setName("Jhon Doe");
        final Person post =
                target.request(MediaType.APPLICATION_JSON).post(Entity.entity(person, MediaType.APPLICATION_JSON),
                        Person.class);
        assertThat(post, notNullValue());
        assertThat(post.getName(), is("Jhon Doe"));
        assertThat(post.getDescription(), is("The Description"));
        assertThat(post.getImageUrl(), is("http://google.com"));
        assertThat(post.getId(), Matchers.notNullValue());
        assertThat(post.getId(), not(0L));
    }

    @Test
    public void deletePerson() {
        Person person = new Person();
        person.setDescription("The Description");
        person.setImageUrl("http://google.com");
        person.setName("Jhon Doe");
        final Person post =
                target.request(MediaType.APPLICATION_JSON).post(Entity.entity(person, MediaType.APPLICATION_JSON),
                        Person.class);

        target.path("{id}").resolveTemplate("id", post.getId()).request().delete();

        final Person person1 =
                target.path("{id}").resolveTemplate("id", post.getId()).request(MediaType.APPLICATION_JSON).get(
                        Person.class);

        assertThat(person1, nullValue());
    }
}
