package org.randd.entity.library;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.Collection;

@Entity
@Audited
public class Book
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private boolean checkedOut;

    @ManyToMany(mappedBy = "booksWritten")
    private Collection<Author> authors;

    @ManyToMany(mappedBy = "books")
    private Collection<Library> libraries;

    @ManyToOne(optional = false)
    private Person personCheckingOut;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Collection<Library> getLibraries()
    {
        return this.libraries;
    }

    public void setLibraries(Collection<Library> pLibraries)
    {
        this.libraries = pLibraries;
    }

    public boolean isCheckedOut()
    {
        return checkedOut;
    }

    public void setCheckedOut(boolean checkedOut)
    {
        this.checkedOut = checkedOut;
    }


    public Collection<Author> getAuthors()
    {
        return authors;
    }

    public void setAuthors(Collection<Author> authors)
    {
        this.authors = authors;
    }

    public Person getPersonsCheckingOut()
    {
        return personCheckingOut;
    }

    public void setPersonsCheckingOut(Person personsCheckingOut)
    {
        this.personCheckingOut = personsCheckingOut;
    }
}
