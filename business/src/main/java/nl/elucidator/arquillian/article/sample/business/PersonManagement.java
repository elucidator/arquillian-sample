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

package nl.elucidator.arquillian.article.sample.business;

import nl.elucidator.arquillian.article.sample.model.Person;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Class personManagement
 */
@Stateless
public class PersonManagement {

    @PersistenceContext
    private EntityManager entityManager;

    public Integer countPersons() {
        final Query countPersons = entityManager.createNamedQuery("countPersons");
        return ((Long) countPersons.getSingleResult()).intValue();
    }

    @SuppressWarnings("unchecked")
    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<Person> findPersons(int startPosition, int maxResults, String sortFields,
                                    SortDirection sortDirections) {

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Person> criteriaQuery = criteriaBuilder.createQuery(Person.class);
        final Root<Person> from = criteriaQuery.from(Person.class);
        if (sortDirections.equals(SortDirection.ASCENDING)) {
            criteriaQuery.orderBy(criteriaBuilder.asc(from.get(sortFields)));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(from.get(sortFields)));
        }

        final TypedQuery<Person> query = entityManager.createQuery(criteriaQuery);

        query.setFirstResult(startPosition);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    public Person getPerson(final Long id) {
        return entityManager.find(Person.class, id);
    }

    public Person save(final Person person) {

        if (person.getId() == null) {
            Person personToSave = new Person();
            personToSave.setName(person.getName());
            personToSave.setDescription(person.getDescription());
            personToSave.setImageUrl(person.getImageUrl());
            entityManager.persist(personToSave);
            return personToSave;
        }

        Person personToUpdate = getPerson(person.getId());
        personToUpdate.setName(person.getName());
        personToUpdate.setDescription(person.getDescription());
        personToUpdate.setImageUrl(person.getImageUrl());

        return entityManager.merge(personToUpdate);

    }

    public void delete(final Long id) {
        entityManager.remove(getPerson(id));
    }
}
