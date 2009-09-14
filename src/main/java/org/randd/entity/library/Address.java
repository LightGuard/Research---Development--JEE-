package org.randd.entity.library;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.Embeddable;

@Embeddable
public class Address
{
    private String line1;
    private String line2;
    private String line3;
    private String city;
    private String state;
    private String zipCode;

    @Override
    public boolean equals(Object pO)
    {
        if (this == pO)
        {
            return true;
        }
        if (!(pO instanceof Address))
        {
            return false;
        }

        Address pAddress = (Address) pO;

        EqualsBuilder eb = new EqualsBuilder();
        eb.append(this.line1, pAddress.line1)
          .append(this.line2, pAddress.line2)
          .append(this.line3, pAddress.line3)
          .append(this.city, pAddress.city)
          .append(this.state, pAddress.state)
          .append(this.zipCode, pAddress.zipCode);

        return eb.isEquals();
    }

    @Override
    public int hashCode()
    {
        HashCodeBuilder hcb = new HashCodeBuilder(7, 37);
        hcb.append(this.line1)
           .append(this.line2)
           .append(this.line3)
           .append(this.city)
           .append(this.state)
           .append(this.zipCode);
        return hcb.toHashCode();
    }

    public String getLine1()
    {
        return line1;
    }

    public void setLine1(String pLine1)
    {
        this.line1 = pLine1;
    }

    public String getLine2()
    {
        return line2;
    }

    public void setLine2(String pLine2)
    {
        this.line2 = pLine2;
    }

    public String getLine3()
    {
        return line3;
    }

    public void setLine3(String pLine3)
    {
        this.line3 = pLine3;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String pLity)
    {
        this.city = pLity;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String pState)
    {
        this.state = pState;
    }

    public String getZipCode()
    {
        return zipCode;
    }

    public void setZipCode(String pZipCode)
    {
        this.zipCode = pZipCode;
    }
}
