/*
 * Copyright (c) 2014 Pieter van der Meer (pieter_at_elucidator_nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.elucidator.arquillian.article.sample.model;

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