/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.portal.controllers.settings.ItemSettings;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableCatalogFacade;
import gov.anl.aps.cdb.portal.model.db.beans.builder.ItemDomainCableCatalogQueryBuilder;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author darek
 */
public class ItemDomainCableCatalogLazyDataModel extends ItemLazyDataModel<ItemDomainCableCatalogFacade, ItemDomainCableCatalogQueryBuilder> {

    public ItemDomainCableCatalogLazyDataModel(ItemDomainCableCatalogFacade facade, Domain itemDomain, ItemSettings settings) {
        super(facade, itemDomain, settings);
    }

    @Override
    protected ItemDomainCableCatalogQueryBuilder getQueryBuilder(Map filterMap, String sortField, SortOrder sortOrder) {
        return new ItemDomainCableCatalogQueryBuilder(itemDomain.getId(), filterMap, sortField, sortOrder, settings); 
    }
}
