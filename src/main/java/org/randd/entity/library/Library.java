package org.randd.entity.library;

import org.hibernate.envers.Audited;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Audited
public class Library
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToMany
    private Set<Book> books;

    @Embedded
    private Address address;

    public Long getId()
    {
        return id;
    }

    public void setId(Long pId)
    {
        this.id = pId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String pName)
    {
        this.name = pName;
    }

    public Collection<Book> getBooks()
    {
        return Collections.unmodifiableSet(books);
    }

    public void addBook(Book pBook)
    {
        if (this.books == null)
            this.books = new HashSet<Book>();

        this.books.add(pBook);
    }

    public Address getAddress()
    {
        return address;
    }

    public void setAddress(Address pAddress)
    {
        this.address = pAddress;
    }
}
