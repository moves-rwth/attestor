package de.rwth.i2.attestor.automata;

import de.rwth.i2.attestor.main.settings.Settings;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNotEquals;

public class AssignmentIteratorTest {

    @AfterClass
    public static void tearDownClass() {
        Settings.getInstance().resetAllSettings();
    }

    @Test
    public void testAssignmentIterator() {

        List<List<Integer>> choices = new ArrayList<>();

        List<Integer> t1 = new ArrayList<>();
        t1.add(0);
        t1.add(1);

        List<Integer> t2 = new ArrayList<>();
        t2.add(0);
        t2.add(1);

        choices.add(t1);
        choices.add(t2);

        AssignmentIterator<Integer> iterator = new AssignmentIterator<>(choices);

        assert(iterator.hasNext());
        List<Integer> choice = iterator.next();
        assertNotNull(choice);
        assertEquals(0, choice.get(0).intValue());
        assertEquals(0, choice.get(1).intValue());

        assert(iterator.hasNext());
        choice = iterator.next();
        assertNotNull(choice);
        assertEquals(1, choice.get(0).intValue());
        assertEquals(0, choice.get(1).intValue());

        assert(iterator.hasNext());
        choice = iterator.next();
        assertNotNull(choice);
        assertEquals(0, choice.get(0).intValue());
        assertEquals(1, choice.get(1).intValue());

        assert(iterator.hasNext());
        choice = iterator.next();
        assertNotNull(choice);
        assertEquals(1, choice.get(0).intValue());
        assertEquals(1, choice.get(1).intValue());

    }
}
