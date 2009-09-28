/*
 * $Id$
 */
package org.randd.ui.bean;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.log.Log;
import org.randd.entity.library.Person;
import org.randd.ui.wrapper.PersonWrapper;

import javax.faces.event.ValueChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Name("selectionExample")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class SelectionExample
{
    @DataModel
    private List<PersonWrapper> people;

    private boolean allSelected;

    @Logger
    private Log log;

    public SelectionExample()
    {
        this.people = new ArrayList<PersonWrapper>();
    }

    @Factory("people")
    @Begin(join = true)
    public void init()
    {
        this.people.addAll(Arrays.asList(
            new PersonWrapper(new Person("John", "Doe")),
            new PersonWrapper(new Person("Jane", "Doe")),
            new PersonWrapper(new Person("Bill", "Doe")),
            new PersonWrapper(new Person("Mark", "Doe")),
            new PersonWrapper(new Person("Sandy", "Doe"))));
    }

    public void selectAll(ValueChangeEvent event)
    {
        for (PersonWrapper pw : this.people)
        {
            pw.setSelected(this.allSelected);
        }

    }

    public void setAllSelected(boolean allSelected)
    {
        this.allSelected = allSelected;
    }

    public boolean isAllSelected()
    {
        return allSelected;
    }
}
