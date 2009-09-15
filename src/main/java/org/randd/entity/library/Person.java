package org.randd.entity.library;

import org.hibernate.envers.Audited;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Entity
@Audited
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Person
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_seq")
    @SequenceGenerator(name = "person_seq", sequenceName = "person_seq")
    protected Long id;

    @Embedded
    private Address address;

    private String firstName;

    private String lastName;

    @OneToMany(mappedBy = "personCheckingOut")
    private Set<Book> booksCheckedOut;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Address getAddress()
    {
        return address;
    }

    public void setAddress(Address address)
    {
        this.address = address;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public Collection<Book> getBooksCheckedOut()
    {
        return Collections.unmodifiableSet(booksCheckedOut);
    }

    public void setBooksCheckedOut(Set<Book> booksCheckedOut)
    {
        this.booksCheckedOut = booksCheckedOut;
    }
}
