package org.randd.entity.library;

import org.hibernate.envers.Audited;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("A")
@Audited
public class Author extends Person
{
   @ManyToMany
   private Set<Book> booksWritten;

    public Collection<Book> getBooksWritten()
    {
        return Collections.unmodifiableSet(booksWritten);
    }

    public void setBooksWritten(Collection<Book> pBooksWritten)
    {
        if (this.booksWritten == null)
            this.booksWritten = new HashSet();
        this.booksWritten.addAll(pBooksWritten);
    }
}
