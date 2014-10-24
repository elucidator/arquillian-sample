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

package nl.elucidator.arquillian.article.sample.web.rest;

import nl.elucidator.arquillian.article.sample.business.PersonManagement;
import nl.elucidator.arquillian.article.sample.business.SortDirection;
import nl.elucidator.arquillian.article.sample.model.Person;
import nl.elucidator.arquillian.article.sample.web.pagination.PaginatedListWrapper;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

/**
 * REST Service to expose the data to display in the UI grid.
 *
 * @author Roberto Cortez
 */
@Stateless
@Path("/persons")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource extends Application {

    @Inject
    private PersonManagement personManagement;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public PaginatedListWrapper listPersons(@DefaultValue("1") @QueryParam("page") Integer page,
                                            @DefaultValue("id") @QueryParam("sortFields") String sortFields,
                                            @DefaultValue("asc") @QueryParam("sortDirections")
                                            SortDirection sortDirections) {
        PaginatedListWrapper paginatedListWrapper = new PaginatedListWrapper();
        paginatedListWrapper.setCurrentPage(page);
        paginatedListWrapper.setSortFields(sortFields);
        paginatedListWrapper.setSortDirections(sortDirections.toString());
        paginatedListWrapper.setPageSize(10);

        paginatedListWrapper.setTotalResults(personManagement.countPersons());
        int start = (paginatedListWrapper.getCurrentPage() - 1) * paginatedListWrapper.getPageSize();
        paginatedListWrapper.setList(
                personManagement.findPersons(start, paginatedListWrapper.getPageSize(), sortFields, sortDirections));
        return paginatedListWrapper;
    }

    @GET
    @Path("{id}")
    public Person getPerson(@PathParam("id") Long id) {
        return personManagement.getPerson(id);
    }

    @POST
    public Person savePerson(Person person) {
        return personManagement.save(person);
    }

    @DELETE
    @Path("{id}")
    public void deletePerson(@PathParam("id") Long id) {
        personManagement.delete(id);
    }
}
