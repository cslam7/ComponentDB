/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.utilities.EntityInfoUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.primefaces.model.TreeNode;

/**
 *
 * @author djarosz
 */
@Entity
@Table(name = "item")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Item.findAll",
            query = "SELECT i FROM Item i"),
    @NamedQuery(name = "Item.findAllWithName",
            query = "SELECT i FROM Item i WHERE i.name != NULL"),
    @NamedQuery(name = "Item.findById",
            query = "SELECT i FROM Item i WHERE i.id = :id"),
    @NamedQuery(name = "Item.findByDerivedFromItemId",
            query = "SELECT i FROM Item i WHERE i.derivedFromItem.id = :id"),
    @NamedQuery(name = "Item.findByName",
            query = "SELECT i FROM Item i WHERE i.name = :name"),
    @NamedQuery(name = "Item.findByItemIdentifier1",
            query = "SELECT i FROM Item i WHERE i.itemIdentifier1 = :itemIdentifier1"),
    @NamedQuery(name = "Item.findByItemIdentifier2",
            query = "SELECT i FROM Item i WHERE i.itemIdentifier2 = :itemIdentifier2"),
    @NamedQuery(name = "Item.findByQrId",
            query = "SELECT i FROM Item i WHERE i.qrId = :qrId"),
    @NamedQuery(name = "Item.findByDomainName",
            query = "SELECT DISTINCT(i) FROM Item i JOIN i.entityTypeList etl WHERE i.domain.name = :domainName and etl.name = :entityTypeName"),
    @NamedQuery(name = "Item.findByDomainNameOderByQrId",
            query = "SELECT DISTINCT(i) FROM Item i JOIN i.entityTypeList etl WHERE i.domain.name = :domainName and etl.name = :entityTypeName ORDER BY i.qrId DESC")
})
public class Item extends CdbDomainEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @Size(max = 64)
    private String name;
    @Size(max = 32)
    @Column(name = "item_identifier1")
    private String itemIdentifier1;
    @Size(max = 32)
    @Column(name = "item_identifier2")
    private String itemIdentifier2;
    @Column(name = "qr_id")
    private Integer qrId;
    @JoinTable(name = "item_entity_type", joinColumns = {
        @JoinColumn(name = "item_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "entity_type_id", referencedColumnName = "id")})
    @ManyToMany
    private List<EntityType> entityTypeList;
    @JoinTable(name = "item_item_category", joinColumns = {
        @JoinColumn(name = "item_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "item_category_id", referencedColumnName = "id")})
    @ManyToMany
    private List<ItemCategory> itemCategoryList;
    @JoinTable(name = "item_item_type", joinColumns = {
        @JoinColumn(name = "item_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "item_type_id", referencedColumnName = "id")})
    @ManyToMany
    private List<ItemType> itemTypeList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentItem")
    private List<ItemElement> fullItemElementList;
    @OneToMany(mappedBy = "containedItem")
    private List<ItemElement> itemElementMemberList;
    @JoinColumn(name = "domain_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Domain domain;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "derivedFromItem")
    private List<Item> derivedFromItemList;
    @JoinColumn(name = "derived_from_item_id", referencedColumnName = "id")
    @ManyToOne
    private Item derivedFromItem;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item")
    private List<ItemConnector> itemConnectorList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item")
    private List<ItemSource> itemSourceList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item")
    private List<ItemResource> itemResourceList;

    private transient ItemElement selfItemElement = null;
    private transient String itemTypeString = null;
    private transient String itemCategoryString = null;
    private transient String itemSourceString = null;
    private transient String qrIdDisplay = null;
    private transient TreeNode locationTree = null;
    private transient String itemType = null;

    private transient String locationDetails = null;
    private transient Item location;

    private transient boolean isCloned = false;

    private transient List<ItemElement> itemElementDisplayList;

    public Item() {
    }

    public void init() {
        ItemElement selfElement = new ItemElement();
        EntityInfo entityInfo = EntityInfoUtility.createEntityInfo();
        selfElement.setEntityInfo(entityInfo);
        selfElement.setParentItem(this);
        this.fullItemElementList = new ArrayList<>();
        this.fullItemElementList.add(selfElement);
    }

    public void init(Domain domain) {
        init();

        this.domain = domain;
    }

    public Item(Integer id) {
        this.id = id;
    }

    public Item(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public void resetAttributesToNullIfEmpty() {
        if (itemIdentifier1 != null && itemIdentifier1.isEmpty()) {
            itemIdentifier1 = null;
        }
        if (itemIdentifier2 != null && itemIdentifier2.isEmpty()) {
            itemIdentifier2 = null;
        }

    }

    @Override
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

    public String getItemIdentifier1() {
        return itemIdentifier1;
    }

    public void setItemIdentifier1(String itemIdentifier1) {
        this.itemIdentifier1 = itemIdentifier1;
    }

    public String getItemIdentifier2() {
        return itemIdentifier2;
    }

    public void setItemIdentifier2(String itemIdentifier2) {
        this.itemIdentifier2 = itemIdentifier2;
    }

    public Integer getQrId() {
        return qrId;
    }

    public void setQrId(Integer qrId) {
        this.qrId = qrId;
    }

    public static String formatQrIdDisplay(Integer qrId) {
        String qrIdDisplay = null;
        if (qrId != null) {
            qrIdDisplay = String.format("%09d", qrId);
            qrIdDisplay = qrIdDisplay.substring(0, 3) + " " + qrIdDisplay.substring(3, 6) + " " + qrIdDisplay.substring(6, 9);
        }
        return qrIdDisplay;
    }

    public String getQrIdDisplay() {
        if (qrId != null && qrIdDisplay == null) {
            qrIdDisplay = formatQrIdDisplay(qrId);
        }
        if (qrIdDisplay == null) {
            qrIdDisplay = "-";
        }
        return qrIdDisplay;
    }

    public String getDescription() {
        return getSelfElement().getDescription();
    }

    public void setDescription(String description) {
        this.getSelfElement().setDescription(description);
    }

    @Override
    public List<Log> getLogList() {
        return getSelfElement().getLogList();
    }

    public void setLogList(List<Log> logList) {
        getSelfElement().setLogList(logList);
    }

    @XmlTransient
    public List<EntityType> getEntityTypeList() {
        return entityTypeList;
    }

    public void setEntityTypeList(List<EntityType> entityTypeList) {
        this.entityTypeList = entityTypeList;
    }

    @XmlTransient
    public List<ItemCategory> getItemCategoryList() {
        return itemCategoryList;
    }

    public String getItemCategoryString() {
        if (itemCategoryString == null) {
            itemCategoryString = "";
            if (itemCategoryList != null) {
                itemCategoryList.stream().forEach((itemCategory) -> {
                    if (itemCategoryList.indexOf(itemCategory) == itemCategoryList.size() - 1) {
                        itemCategoryString += itemCategory.getName();
                    } else {
                        itemCategoryString += itemCategory.getName() + " | ";
                    }
                });
            }
        }

        return itemCategoryString;
    }

    public void setItemCategoryList(List<ItemCategory> itemCategoryList) {
        this.itemCategoryList = itemCategoryList;
    }

    @XmlTransient
    public List<ItemType> getItemTypeList() {
        return itemTypeList;
    }

    public void setItemTypeList(List<ItemType> itemTypeList) {
        this.itemTypeList = itemTypeList;
    }

    public String getItemTypeString() {
        if (itemTypeString == null) {
            itemTypeString = "";
            if (itemTypeList != null) {
                itemTypeList.stream().forEach((itemType) -> {
                    if (itemTypeList.indexOf(itemType) == itemTypeList.size() - 1) {
                        itemTypeString += itemType.getName();
                    } else {
                        itemTypeString += itemType.getName() + " | ";
                    }
                });
            }
        }

        return itemTypeString;
    }

    @XmlTransient
    public List<ItemElement> getFullItemElementList() {
        return fullItemElementList;
    }

    public void setFullItemElementList(List<ItemElement> fullItemElementList) {
        this.fullItemElementList = fullItemElementList;
    }

    public List<ItemElement> getItemElementDisplayList() {
        if (itemElementDisplayList == null) {
            itemElementDisplayList = new ArrayList<>(fullItemElementList);

            for (ItemElement itemElement : itemElementDisplayList) {
                if (itemElement.getName() == null) {
                    itemElementDisplayList.remove(itemElement);
                    break;
                }
            }
        }
        return itemElementDisplayList;
    }

    public void updateDynamicProperties(UserInfo enteredByUser, Date enteredOnDateTime) {
        if (isCloned) {
            // Only update properties for non-cloned instances
            return;
        }
        List<PropertyValue> itemPropertyList = getPropertyValueList();
        if (itemPropertyList == null) {
            itemPropertyList = new ArrayList<>();
        }
        List<PropertyValue> parentItemPropertyList = derivedFromItem.getPropertyValueList();
        for (PropertyValue propertyValue : parentItemPropertyList) {
            if (propertyValue.getIsDynamic()) {
                PropertyValue propertyValue2 = propertyValue.copyAndSetUserInfoAndDate(enteredByUser, enteredOnDateTime);
                itemPropertyList.add(propertyValue2);
            }
        }
        setPropertyValueList(itemPropertyList);
    }

    public void setItemElementList(List<ItemElement> itemElementDisplayList) {
        this.itemElementDisplayList = itemElementDisplayList;
    }

    @XmlTransient
    public List<ItemElement> getItemElementMemberList() {
        return itemElementMemberList;
    }

    public void setItemElementMemberList(List<ItemElement> itemElementMemberList) {
        this.itemElementMemberList = itemElementMemberList;
    }

    @XmlTransient
    public List<Item> getDerivedFromItemList() {
        return derivedFromItemList;
    }

    public void setDerivedFromItemList(List<Item> derivedFromItemList) {
        this.derivedFromItemList = derivedFromItemList;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public String getItemType() {
        if (itemType == null) {
            String entityTypeString = null;
            for (EntityType entityType : entityTypeList) {
                if (entityTypeString == null) {
                    entityTypeString = entityType.getName();
                } else {
                    entityTypeString += ", " + entityType.getName();
                }
            }

            itemType = entityTypeString + " | " + domain.getName();
        }
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Item getDerivedFromItem() {
        return derivedFromItem;
    }

    public void setDerivedFromItem(Item derivedFromItem) {
        this.derivedFromItem = derivedFromItem;
    }

    @Override
    public EntityInfo getEntityInfo() {
        return getSelfElement().getEntityInfo();
    }

    public void setEntityInfo(EntityInfo entityInfo) {
        this.getSelfElement().setEntityInfo(entityInfo);
    }

    @XmlTransient
    public List<ItemConnector> getItemConnectorList() {
        return itemConnectorList;
    }

    public void setItemConnectorList(List<ItemConnector> itemConnectorList) {
        this.itemConnectorList = itemConnectorList;
    }

    @XmlTransient
    public List<ItemSource> getItemSourceList() {
        return itemSourceList;
    }

    public void setItemSourceList(List<ItemSource> itemSourceList) {
        this.itemSourceList = itemSourceList;
    }

    public String getItemSourceString() {
        if (itemSourceString == null) {
            itemSourceString = "";
            itemSourceList.stream().forEach((itemSource) -> {
                itemSourceString += " " + itemSource.getSource().getName();
            });
        }

        return itemSourceString;
    }

    public TreeNode getLocationTree() {
        return locationTree;
    }

    public void setLocationTree(TreeNode locationTree) {
        this.locationTree = locationTree;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(String locationDetails) {
        this.locationDetails = locationDetails;
    }

    public Item getLocation() {
        return location;
    }

    public void setLocation(Item location) {
        this.location = location;
    }

    @XmlTransient
    public List<ItemResource> getItemResourceList() {
        return itemResourceList;
    }

    public void setItemResourceList(List<ItemResource> itemResourceList) {
        this.itemResourceList = itemResourceList;
    }

    public ItemElement getSelfElement() {
        if (selfItemElement == null) {
            for (ItemElement ie : this.fullItemElementList) {
                if (ie.getName() == null) {
                    selfItemElement = ie;
                    break;
                }
            }
        }

        return selfItemElement;
    }

    @Override
    public List<PropertyValue> getPropertyValueList() {
        return this.getSelfElement().getPropertyValueList();
    }

    public void setPropertyValueList(List<PropertyValue> propertyValueList) {
        this.getSelfElement().setPropertyValueList(propertyValueList);
    }

    @Override
    public void setImagePropertyList(List<PropertyValue> imageList) {
        this.getSelfElement().setImagePropertyList(imageList);
    }

    @Override
    public List<PropertyValue> getImagePropertyList() {
        return this.getSelfElement().getImagePropertyList();
    }

    public CdbDomainEntity.PropertyValueInformation getPropertyValueInformation(int propertyTypeId) {
        return this.getSelfElement().getPropertyValueInformation(propertyTypeId);
    }

    @Override
    public String getPropertyValue1() {
        return getSelfElement().getPropertyValue1();
    }

    @Override
    public void setPropertyValue1(String propertyValue1) {
        getSelfElement().setPropertyValueByIndex(1, propertyValue1);
    }

    @Override
    public String getPropertyValue2() {
        return getSelfElement().getPropertyValue2();
    }

    @Override
    public void setPropertyValue2(String propertyValue2) {
        getSelfElement().setPropertyValueByIndex(2, propertyValue2);
    }

    @Override
    public String getPropertyValue3() {
        return getSelfElement().getPropertyValue3();
    }

    @Override
    public void setPropertyValue3(String propertyValue3) {
        getSelfElement().setPropertyValueByIndex(3, propertyValue3);
    }

    @Override
    public String getPropertyValue4() {
        return getSelfElement().getPropertyValue4();
    }

    @Override
    public void setPropertyValue4(String propertyValue4) {
        getSelfElement().setPropertyValueByIndex(4, propertyValue4);
    }

    @Override
    public String getPropertyValue5() {
        return getSelfElement().getPropertyValue5();
    }

    @Override
    public void setPropertyValue5(String propertyValue5) {
        getSelfElement().setPropertyValueByIndex(5, propertyValue5);
    }
    
    @Override
    public SearchResult search(Pattern searchPattern) {
        SearchResult searchResult;
        
        if (name != null) {
            searchResult = new SearchResult(id, name);
            searchResult.doesValueContainPattern("name", name, searchPattern);
        } else if (derivedFromItem != null && derivedFromItem.getName() != null) {
            String title = "Derived from: " + derivedFromItem.getName(); 
            if (qrId != null) {
                title += " (QRID: " + getQrIdDisplay() + ")";  
            }
            searchResult = new SearchResult(id, title);
        } else {
            searchResult = new SearchResult(id, "Item");
        }
        
        if (derivedFromItem != null) {
            searchResult.doesValueContainPattern("derived from name", derivedFromItem.getName(), searchPattern);
        }
        
        searchResult.doesValueContainPattern("created by", getEntityInfo().getCreatedByUser().getUsername(), searchPattern); 
        searchResult.doesValueContainPattern("last modified by", getEntityInfo().getLastModifiedByUser().getUsername(), searchPattern);
        searchResult.doesValueContainPattern("owned by", getEntityInfo().getOwnerUser().getUsername(), searchPattern);
        searchResult.doesValueContainPattern("description", getDescription(), searchPattern);
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
        if (!(object instanceof Item)) {
            return false;
        }
        Item other = (Item) object;
        
        return (Objects.equals(other.getItemIdentifier1(), itemIdentifier1)
                && Objects.equals(other.getItemIdentifier2(), itemIdentifier2)
                && Objects.equals(other.getDerivedFromItem(), derivedFromItem)
                && Objects.equals(other.getDomain(), domain));
    }

    @Override
    public String toString() {
        if (getName() != null) {
            return getName();
        } else if (getDerivedFromItem() != null && getDerivedFromItem().getName() != null) {
            return "derived from " + getDerivedFromItem().getName(); 
        }
        else if (getId() != null) {
            return getId().toString();
        } else {
            return "New Item";
        }
    }

}
