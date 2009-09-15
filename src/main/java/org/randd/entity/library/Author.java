package org.randd.entity.library;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
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
            this.booksWritten = new HashSet<Book>();
        this.booksWritten.addAll(pBooksWritten);
    }
}
