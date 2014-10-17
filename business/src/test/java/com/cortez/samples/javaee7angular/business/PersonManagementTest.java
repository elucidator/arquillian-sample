package com.cortez.samples.javaee7angular.business;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class PersonManagementTest {

    @Deployment
    public static Archive<?> createDeployment() {
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class).addPackage(PersonManagement.class.getPackage());
        final JavaArchive[] as = Maven.resolver().loadPomFromFile("pom.xml").resolve("com.cortez.samples.javaee7-angular:model").withTransitivity().as(JavaArchive.class);
        for (JavaArchive a : as) {
            archive.merge(a);
        }
        archive.addAsManifestResource("test-persistence.xml", "persistence.xml");

        System.out.println("archive = " + archive.toString(true));
        return archive;
    }

    @Test
    public void xx() {

    }

}