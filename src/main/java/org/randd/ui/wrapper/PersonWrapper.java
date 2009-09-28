/*
 * $Id$
 */
package org.randd.ui.wrapper;

import org.randd.entity.library.Person;

/**
 * Wraps a {@link org.randd.entity.library.Person} to be used on the UI.
 */
public class PersonWrapper
{
    private Person person;
    private boolean selected;

    public PersonWrapper(Person person)
    {
        this.person = person;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("PersonWrapper");
        sb.append("{person=").append(person);
        sb.append(", selected=").append(selected);
        sb.append('}');
        return sb.toString();
    }

    public Person getPerson()
    {
        return person;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }
}
