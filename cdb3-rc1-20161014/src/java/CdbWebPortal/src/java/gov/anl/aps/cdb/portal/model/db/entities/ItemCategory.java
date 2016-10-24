/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.common.utilities.StringUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.io.Serializable;
import java.util.List;
import java.util.regex.Pattern;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author djarosz
 */
@Entity
@Table(name = "item_category")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ItemCategory.findAll", query = "SELECT i FROM ItemCategory i ORDER BY i.name ASC"),
    @NamedQuery(name = "ItemCategory.findById", query = "SELECT i FROM ItemCategory i WHERE i.id = :id"),
    @NamedQuery(name = "ItemCategory.findByName", query = "SELECT i FROM ItemCategory i WHERE i.name = :name"),
    @NamedQuery(name = "ItemCategory.findByDescription", query = "SELECT i FROM ItemCategory i WHERE i.description = :description"),
    @NamedQuery(name = "ItemCategory.findByDomainName", query = "SELECT i FROM ItemCategory i WHERE i.domain.name = :domainName ORDER BY i.name ASC")})
public class ItemCategory extends ItemTypeCategoryEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    private String name;
    @Size(max = 256)
    private String description;
    @ManyToMany(mappedBy = "itemCategoryList")
    private List<Item> itemList;
    @JoinColumn(name = "domain_id", referencedColumnName = "id")
    @ManyToOne
    private Domain domain;
    @JoinTable(name = "item_category_item_type", joinColumns = {
        @JoinColumn(name = "item_category_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "item_type_id", referencedColumnName = "id")})
    @ManyToMany
    @OrderBy("name ASC")
    private List<ItemType> itemTypeList;

    private transient String itemTypeString = null;

    public ItemCategory() {
    }

    public ItemCategory(Integer id) {
        this.id = id;
    }

    public ItemCategory(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public List<ItemType> getItemTypeList() {
        return itemTypeList;
    }

    public void setItemTypeList(List<ItemType> itemTypeList) {
        itemTypeString = null;
        this.itemTypeList = itemTypeList;
    }

    public String getItemTypeString() {
        if (itemTypeString == null) {
            itemTypeString = StringUtility.getStringifyCdbList(itemTypeList);
        }

        return itemTypeString;
    }

    public String getEditItemTypeString() {
        itemTypeString = getItemTypeString();
        if (itemTypeString.equals("-")) {
            return "Select Item " + getItemTypeTitle();
        } else {
            return itemTypeString;
        }
    }

    @XmlTransient
    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    @XmlTransient
    @Override
    public Domain getDomain() {
        return domain;
    }

    @Override
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    @Override
    public SearchResult search(Pattern searchPattern) {
        SearchResult searchResult = new SearchResult(id, name);

        searchResult.doesValueContainPattern("name", name, searchPattern);
        searchResult.doesValueContainPattern("description", description, searchPattern);
        if (domain != null) {
            searchResult.doesValueContainPattern("domain name", domain.getName(), searchPattern);
        }

        return searchResult;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ItemCategory)) {
            return false;
        }
        ItemCategory other = (ItemCategory) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

}
