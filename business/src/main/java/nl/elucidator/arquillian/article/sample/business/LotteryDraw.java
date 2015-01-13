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

package nl.elucidator.arquillian.article.sample.business;

import nl.elucidator.arquillian.article.sample.model.User;

import javax.inject.Inject;
import java.security.SecureRandom;

/**
 * Class LotteryDraw.
 */
public class LotteryDraw {

    @Inject
    private SecureRandom random;

    public boolean isLucky(final User user, final int lotteryNumber) {
        return (random.nextInt() == lotteryNumber) && user.isAdult();
    }
}
