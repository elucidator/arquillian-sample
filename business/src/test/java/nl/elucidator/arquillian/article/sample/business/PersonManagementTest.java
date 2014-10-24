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
        final JavaArchive[] as = Maven.resolver().loadPomFromFile("pom.xml").resolve("nl.elucidator.arquillian.article.sample:model").withTransitivity().as(JavaArchive.class);
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