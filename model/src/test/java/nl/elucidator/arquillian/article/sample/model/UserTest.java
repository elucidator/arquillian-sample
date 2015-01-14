/*
 * Copyright (c) 2015 Pieter van der Meer (pieter_at_elucidator_nl)
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

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UserTest {

    public static final String USER_NAME = "Pieter";
    public static final int USER_AGE = 44;

    @Test
    public void isCOnstructedCorrectly() {
        User user = new User(USER_NAME, USER_AGE);
        assertThat(user.getName(), is(USER_NAME));
        assertThat(user.getAge(), is(USER_AGE));
        assertThat(user.isAdult(), is(true));
    }

}