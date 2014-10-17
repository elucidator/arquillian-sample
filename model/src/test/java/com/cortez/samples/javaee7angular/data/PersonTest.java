package com.cortez.samples.javaee7angular.data;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.*;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(Arquillian.class)
public class PersonTest {

    @Inject
    UserTransaction utx;
    @PersistenceContext
    private EntityManager em;

    @Deployment
    public static Archive<?> createArchive() {
        final JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class).addClass(Person.class);

        javaArchive.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        javaArchive.addAsManifestResource("test-persistence.xml", "persistence.xml");

        System.out.println("javaArchive.toString(true) = " + javaArchive.toString(true));
        return javaArchive;
    }

    @Test
    public void store() throws HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {
        assertThat(em, notNullValue());
        assertThat(utx, notNullValue());

        utx.begin();
        em.joinTransaction();
        Person person = new Person();
        person.setDescription("The Description");
        person.setName("The Name");
        person.setImageUrl("http://google.com");

        em.persist(person);
        utx.commit();

        final Person personFound = em.find(Person.class, person.getId());

        assertThat(personFound, notNullValue());
        assertThat(personFound.getDescription(), is(person.getDescription()));
    }

}