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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LotteryDrawTest {

    public static final int MAGIC_NUMBER = 42;
    public static final int UNLUCKY_NUMBER = -1;
    private User adultUser = new User("Pieter", 44);
    private User underAgeUser = new User("Tycho", 7);
    @InjectMocks
    private LotteryDraw lotteryDraw = new LotteryDraw();
    @Mock
    private WinningNumberGenerator winningNumberGenerator;

    @Test
    public void adultUser() throws Exception {
        when(winningNumberGenerator.getWinningNumber()).thenReturn(MAGIC_NUMBER);
        assertThat(lotteryDraw.isLucky(adultUser, MAGIC_NUMBER), is(true));
        assertThat(lotteryDraw.isLucky(adultUser, UNLUCKY_NUMBER), is(false));
    }

    @Test
    public void underAgeCanNotWin() {
        when(winningNumberGenerator.getWinningNumber()).thenReturn(MAGIC_NUMBER);
        assertThat(lotteryDraw.isLucky(underAgeUser, MAGIC_NUMBER), is(false));
        assertThat(lotteryDraw.isLucky(underAgeUser, UNLUCKY_NUMBER), is(false));

    }

}