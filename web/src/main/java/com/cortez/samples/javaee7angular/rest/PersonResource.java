package com.cortez.samples.javaee7angular.rest;

import com.cortez.samples.javaee7angular.business.PersonManagement;
import com.cortez.samples.javaee7angular.business.SortDirection;
import com.cortez.samples.javaee7angular.data.Person;
import com.cortez.samples.javaee7angular.pagination.PaginatedListWrapper;

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
