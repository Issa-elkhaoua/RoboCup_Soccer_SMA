/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Arthur Lefebvre
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

import fstt.sim.modele.Position;
import fstt.sim.modele.PositionHelper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PositionTest {

    @Test
    public void approcherFuir(){
        Position p1 = new Position(0,0);
        Position p2 = new Position(0,0);

        double d1 = PositionHelper.distance(p1,p2);

        p1.Fuir(p2);
        p1.Fuir(p2);
        p2.approcher(p1);
        p2.approcher(p1);

        double d2 = PositionHelper.distance(p1,p2);

        assertEquals(d1,d2,1e-15);
    }

    @Test
    public void millieu(){
        Position p1 = new Position(0,0);
        Position p2 = new Position(2,2);

       Position p3 = PositionHelper.milieu(p1,p2);

        Position valeurAttendue = new Position(1,1);

        assertEquals(p3,valeurAttendue);
    }
}
